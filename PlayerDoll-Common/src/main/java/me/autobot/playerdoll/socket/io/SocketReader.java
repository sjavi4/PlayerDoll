package me.autobot.playerdoll.socket.io;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.connection.CursedConnection;
import me.autobot.playerdoll.packet.IPacketFactory;
import me.autobot.playerdoll.packet.Packets;
import me.autobot.playerdoll.socket.ClientSocket;

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
            boolean pass = captureLogin();
            if (!pass) {
                close();
            }
            while (!endStream) {
                if (enableCompression()) {
                    readPacketCompressed();
                } else {
                    readPacketUncompressed();
                }
            }
            PlayerDoll.LOGGER.info("Client Connection Closed");
            //System.out.println("Client Is Closed.");
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
    private void startHandshake() {
        try {
            byte[] handshakeMessage = packetFactory.clientIntent();

            Packets.writeVarInt(output, handshakeMessage.length);
            output.write(handshakeMessage);

            nextState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean captureLogin() throws InterruptedException {
        int asks = 0;
        while (getCurrentState() == ConnectionState.LOGIN) {
            if (CursedConnection.startCursedConnection(localAddress, clientSocket.getProfile(), clientSocket.getCaller())) {
                System.out.println("Succeed to login");
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

//    private void readLogin(int id, DataInputStream in, int size) throws IOException {
//        switch (id) {
//            // Disconnect Packet
//            case 0x00 -> {
//                System.out.println("disconnected");
//                endStream = true;
//            }
//            // Encryption Packet
//            case 0x01 -> {
//                System.out.println("Encrypt");
//            }
//            // Login Success (Profile)
//            case 0x02 -> {
//                System.out.println("Login Success");
//                output.write(OldPacketFactory.serverLoginAck());
//                nextState();
//            }
//            // Setup Compression
//            case 0x03 -> {
//                System.out.println("Setup Compression");
//                compressionThreshold = Packets.readVarInt(in);
//                System.out.println("Compression Threshold: " + compressionThreshold);
//            }
//            // Plugin Message
//            case 0x04 -> System.out.println("Login Plugin Message");
//            default -> System.out.println("Unknown Login Packet ID: " + id);
//        }
//    }
//    private void readConfiguration(int id, DataInputStream in, int size) throws IOException {
//        switch (id) {
//            // Plugin Message
//            case 0x00 -> System.out.println("Config Plugin message");
//            // Disconnect
//            case 0x01 -> {
//                System.out.println("Config Disconnect");
//                endStream = true;
//            }
//            // Finish Configuration
//            case 0x02 -> {
//                System.out.println("Finish Config");
//                output.write(OldPacketFactory.serverConfigAck());
//                nextState();
//            }
//            // Keep Alive
//            //case 0x03 -> output.write(OldPacketFactory.keepAlive(getCurrentState(), in.readLong()));
//            // Pong
//            case 0x04 -> System.out.println("Pong Packet");
//            // Registry
//            case 0x05 -> System.out.println("Registry");
//            // Remove Resource Pack (resourcePack Pop) Config
//            case 0x06 -> System.out.println("Resource Pack Pop (config)");
//            // Add Resource Pack (resource Pack Push) Config
//            case 0x07 -> System.out.println("Resource Pack Push (config)");
//            // Feature Flags
//            case 0x08 -> System.out.println("data packs");
//            // Tags
//            case 0x09 -> System.out.println("tags");
//            default -> System.out.println("Unknown Config Packet ID: " + id);
//        }
//    }
//    private void readPlay(int id, DataInputStream in, int size) throws IOException {
//        switch (id) {
//            // Disconnect
//            case 0x1B -> {
//                System.out.println("Play Disconnect Packet");
//                endStream = true;
//            }
//            // Keep Alive
//            //case 0x24 -> output.write(OldPacketFactory.keepAlive(getCurrentState(), in.readLong()));
//            case 0x43 -> System.out.println("ResourcePack Pop (Play)");
//            case 0x44 -> {
//                // Weird problem when sending resource pack respond
//                // Skip using other way
//                System.out.println("ResourcePack Push (Play)");
//                UUID uuid = Packets.readUUID(in);
//                String url = Packets.readString(in);
//                String hash = Packets.readString(in);
//                boolean forced = in.readBoolean();
//                if (forced) {
//                    System.out.println("It is a forced Resource Pack");
//                    System.out.println("Not implemented, not respond to this");
////                    byte[] accept = PacketFactory.replyResourcePackPush(clientSocket.getCurrentState(), uuid, PacketFactory.resourcePackStatus.ACCEPTED);
////                    byte[] downloaded = PacketFactory.replyResourcePackPush(clientSocket.getCurrentState(), uuid, PacketFactory.resourcePackStatus.DOWNLOADED);
////                    byte[] loaded = PacketFactory.replyResourcePackPush(clientSocket.getCurrentState(), uuid, PacketFactory.resourcePackStatus.DOWNLOAD_SUCCESSFULLY);
////
////                    output.write(accept);
////                    //output.write(downloaded);
////                    final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
////                    executor.schedule(() -> {
////                                try {
////                                    output.write(loaded);
////                                } catch (IOException e) {
////                                    throw new RuntimeException(e);
////                                }
////                            },
////                            2, TimeUnit.SECONDS);
////                    //PacketFactory.replyResourcePackPush(clientSocket.getCurrentState(), uuid, PacketFactory.resourcePackStatus.DOWNLOAD_SUCCESSFULLY);
//                } else {
//                    System.out.println("It is an optional Resource Pack, Skip");
//                    //OldPacketFactory.replyResourcePackPush(getCurrentState(), uuid, OldPacketFactory.resourcePackStatus.DECLINED);
//                }
//            }
//            default -> System.out.println("Unknown Play Packet ID: " + id);
//        }
//    }

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
