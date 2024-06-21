package me.autobot.playerdoll.socket.io;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.connection.CursedConnection;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.packet.IPacketFactory;
import me.autobot.playerdoll.packet.Packets;
import me.autobot.playerdoll.socket.ClientSocket;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.entity.Player;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.zip.DataFormatException;

public class SocketReader extends Thread {
    private final ClientSocket clientSocket;
    private final Socket socket;
    private final String localAddress;
    private IPacketFactory packetFactory;

    private DataOutputStream output;
    private DataInputStream input;

    private int compressionThreshold = -1;
    private boolean endStream = false;
    private ConnectionState currentState;

    private Packets.protocolNumber protocol;

    public SocketReader(ClientSocket clientSocket) {
        this.clientSocket = clientSocket;
        this.socket = clientSocket.socket;
        localAddress = socket.getLocalAddress().toString() + ":" + socket.getLocalPort();
        try {
            protocol = Packets.protocolNumber.valueOf(PlayerDoll.INTERNAL_VERSION);
        } catch (IllegalArgumentException e) {
            PlayerDoll.LOGGER.severe("Not supported Game Version");
            throw new IllegalArgumentException(e);
        }
        if (PlayerDoll.BUNGEECORD) {
            setupBungeeDollData();
        }
        packetFactory = protocol.getFactory(this);
        currentState = ConnectionState.HANDSHAKE;
    }
    @Override
    public void run() {
        try {
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            packetFactory = protocol.getFactory(this);

            startHandshake();
            // BungeeCord: wait until packet Received (Timed out 10s)

            //PlayerDoll.LOGGER.info("Send the bungeeCord Make login 1");
//            if (PlayerDoll.BUNGEECORD) {
//                //PlayerDoll.LOGGER.info("Send the bungeeCord Make login 2");
//                //long delay = clientSocket.getCaller() == null ? 200 : 0;
//                Thread.sleep(400);
//                ByteArrayDataOutput output = ByteStreams.newDataOutput();
//                // Send Socket Address
//                output.writeInt(2);
//                output.writeUTF(localAddress);
//                GameProfile profile = clientSocket.getProfile();
//                output.writeUTF(profile.getId().toString());
//                output.writeUTF(profile.getName());
//                PlayerDoll.sendBungeeCordMessage(output.toByteArray());
//
//                synchronized (this) {
//                    PlayerDoll.LOGGER.info("Start Wait Thread");
//                    currentThread().wait(10000);
//                }
//            }
            boolean passLogin = captureLogin();
            if (!passLogin) {
                endStream = true;
            }
            while (!endStream) {
                if (enableCompression()) {
                    readPacketCompressed();
                } else {
                    readPacketUncompressed();
                }
            }
            if (getCurrentState() != ConnectionState.PLAY) {
                Player caller = clientSocket.getCaller();
                if (caller != null) {
                    caller.sendMessage(LangFormatter.YAMLReplaceMessage("spawn-error", clientSocket.getProfile().getName()));
                } else {
                    PlayerDoll.LOGGER.warning(String.format("Doll %s failed to Join, Please Try again Later", clientSocket.getProfile().getName()));
                }
            }
            PlayerDoll.LOGGER.info("Client Connection Closed");
            close();
        } catch (InterruptedException | IOException e) {
            PlayerDoll.LOGGER.warning("Error caught on Client");
            e.printStackTrace();
            close();

        }
        PlayerDoll.LOGGER.info("Client Thread End");
    }

    public int getCompressionThreshold() {
        return compressionThreshold;
    }

    public DataOutputStream getOutput() {
        return output;
    }

    public void close() {
        try {
            //Close socket
            socket.shutdownInput();
            socket.shutdownOutput();
            input.close();
            output.close();
            socket.close();
            PlayerDoll.LOGGER.info("Connection Closed Successfully");
            //System.out.println("Closed connections for Client.");
        } catch (IOException e) {
            e.printStackTrace();
            PlayerDoll.LOGGER.warning("Error caught on Closing Connection");
        }
    }

    public void endStream() {
        endStream = true;
    }
    public boolean enableCompression() {
        return compressionThreshold >= 0;
    }

    private void setupBungeeDollData() {
        GameProfile profile = clientSocket.getProfile();

        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeInt(0);
        output.writeUTF(localAddress);
        output.writeUTF(profile.getId().toString());
        output.writeUTF(profile.getName());

        Player caller = clientSocket.getCaller();
        output.writeBoolean(caller == null);
        if (caller == null) {
            DollConfig dollConfig = DollConfig.getTemporaryConfig(profile.getName());
            output.writeUTF(dollConfig.dollLastJoinServer.getValue());
        } else {
            output.writeUTF(caller.getUniqueId().toString());
        }
        PlayerDoll.sendBungeeCordMessage(output.toByteArray());

    }

    private void startHandshake() throws InterruptedException {
        if (PlayerDoll.BUNGEECORD) {
            // Make sure no packet delay
            synchronized (this) {
                Thread.currentThread().wait(10000);
            }
        }
        try {
            byte[] handshakeMessage = packetFactory.clientIntent();

            Packets.writeVarInt(output, handshakeMessage.length);
            output.write(handshakeMessage);

            nextState();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (PlayerDoll.BUNGEECORD) {
            synchronized (this) {
                Thread.currentThread().wait(10000);
                //Thread.sleep(2000);
            }
        }
    }

    private boolean captureLogin() throws InterruptedException {
        int asks = 0;
        while (getCurrentState() == ConnectionState.LOGIN) {
            if (CursedConnection.startCursedConnection(localAddress, clientSocket.getProfile(), clientSocket.getCaller())) {
                //System.out.println("Succeed to login");
                return true;
            } else {
                if (asks == 10) {
                    PlayerDoll.LOGGER.info("Client wait Too Long");
                    //System.out.println("Wait Too long");
                    return false;
                }
                PlayerDoll.LOGGER.info("Wait for Login Listener");
                //System.out.println("Wait for Login Listener");
                asks++;
                Thread.sleep(500);
            }
        }
        return false;
    }

    private void readPacketUncompressed() throws IOException {
        //System.out.println("Reading packet disabled compression");
        int packetLength = Packets.readVarInt(input);
        int packetID = Packets.readVarInt(input);
        int shouldReadLength = packetLength - Packets.getVarIntLength(packetID);
        byte[] packetData = new byte[shouldReadLength];
        readActualData(packetData);
        processPacket(packetID, new DataInputStream(new ByteArrayInputStream(packetData)), packetData.length);
    }
    // Cursed but it works
    private void readPacketCompressed() throws IOException {
        //System.out.println("Reading packet enabled compression");
        int packetLength = Packets.readVarInt(input);
        int decompressedDataLength = Packets.readVarInt(input);
        //System.out.println("PacketLength: " + packetLength);
        //System.out.println("Decompressed Data Length: " + decompressedDataLength);
        //System.out.println("Size of decompressed Data: " + Packets.getVarIntLength(decompressedDataLength));
        int packetID = -2;
        int shouldReadLength = packetLength - Packets.getVarIntLength(decompressedDataLength);
        byte[] packetData;
        DataInputStream packetDataInputStream = null;
        int finalDataLength = -1;
        if (decompressedDataLength == 0) {
            //System.out.println("Uncompressed Packet");
            packetID = Packets.readVarInt(input);
            // packet ID consumed size
            packetData = new byte[shouldReadLength - Packets.getVarIntLength(packetID)];
            readActualData(packetData);
            packetDataInputStream = new DataInputStream(new ByteArrayInputStream(packetData));
            finalDataLength = packetData.length;
        } else {
            //System.out.println("Compressed Packet");
            byte[] compressedPacketIDWithData = new byte[shouldReadLength];
            readActualData(compressedPacketIDWithData);
            try {
                byte[] decompressedRawData = packetData = Packets.decompress(compressedPacketIDWithData, false);
                //System.out.println("Decompressed Raw Data: " + decompressedRawData.length);
                //System.out.println("Expected Decompressed:" + decompressedDataLength);
                //if (decompressedRawData.length != decompressedDataLength) {
                //    System.out.println("Decompressed size Not Match expected");
                //}
                if (decompressedRawData.length < decompressedDataLength) {
                    //System.out.println("Reading the rest data");
                    input.read(new byte[decompressedDataLength - decompressedRawData.length]);
                }
                packetDataInputStream = new DataInputStream(new ByteArrayInputStream(decompressedRawData));
                packetID = Packets.readVarInt(packetDataInputStream);
                finalDataLength = packetData.length - Packets.getVarIntLength(packetID);
            } catch (DataFormatException e) {
                e.printStackTrace();
                PlayerDoll.LOGGER.warning("Error while Decompressing Packet");
                //System.out.println("Error while Decompressing Packet");
            }
        }

        processPacket(packetID, packetDataInputStream, finalDataLength);
    }
    private void readActualData(byte[] data) throws IOException {
        int readCount = input.read(data);
        //if (readCount == -1) {
        //    System.out.println("read end of stream");
        //} else if (readCount != data.length) {
        //    System.out.println("actually Read is less then expected");
        //}
    }

    private void processPacket(int packetID, DataInputStream data, int dataLength) throws IOException {
        if (packetID == -2 || data == null || dataLength == -1) {
            //System.out.println("Decoded invalid packet data. Skipping");
            return;
        }
        if (packetID == -1) {
            //System.out.println("End Stream Packet -1");
            endStream = true;
            return;
        }
        switch (getCurrentState()) {
            case LOGIN -> packetFactory.processLogin(packetID, data, dataLength);
            case CONFIGURATION -> packetFactory.processConfiguration(packetID, data, dataLength);
            case PLAY -> packetFactory.processPlay(packetID, data, dataLength);

//            case LOGIN -> readLogin(packetID, data, dataLength);
//            case CONFIGURATION -> readConfiguration(packetID, data, dataLength);
//            case PLAY -> readPlay(packetID, data, dataLength);
            default -> {
                PlayerDoll.LOGGER.warning("Client Switch to Unknown State");
                //System.out.println("Unknown State");
            }
        }
    }

    public void setCompressionThreshold(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }

    public ConnectionState getCurrentState() {
        return currentState;
    }
    public void nextState() {
        currentState = currentState.nextState;
        //System.out.println("State switched to " + currentState);
    }
    public enum ConnectionState {
        PLAY(null), CONFIGURATION(PLAY), LOGIN(CONFIGURATION), HANDSHAKE(LOGIN);

        private final ConnectionState nextState;

        ConnectionState(ConnectionState nextState) {
            this.nextState = nextState;
        }

        public ConnectionState getNextState() {
            return nextState;
        }
    }
}
