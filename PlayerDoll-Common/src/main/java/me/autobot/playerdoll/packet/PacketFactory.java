package me.autobot.playerdoll.packet;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.autobot.playerdoll.socket.SocketHelper;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class PacketFactory implements IPacketFactory {
    protected final SocketReader socketReader;
    protected final DataOutputStream output;

    protected final Map<Integer, CPackets> loginPackets = new Int2ObjectOpenHashMap<>();
    protected final Map<Integer, CPackets> configPackets = new Int2ObjectOpenHashMap<>();
    protected final Map<Integer, CPackets> playPackets = new Int2ObjectOpenHashMap<>();
    public PacketFactory(SocketReader socketReader) {
        this.socketReader = socketReader;
        this.output = socketReader.getOutput();
        registerLoginPackets();
        registerConfigPackets();
        registerPlayPackets();
    }
    
    protected abstract int getProtocol();

    @Override
    public byte[] clientIntent() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream handshake = new DataOutputStream(buffer);

        handshake.writeByte(0x00); //packet id for handshake
        PacketUtil.writeVarInt(handshake, getProtocol()); //protocol version
        PacketUtil.writeString(handshake, SocketHelper.IP, StandardCharsets.UTF_8);
        handshake.writeShort(SocketHelper.PORT); //port
        PacketUtil.writeVarInt(handshake, 2); //state (2 for login)

        return buffer.toByteArray();
    }


    @Override
    public void processLogin(int packetID, DataInputStream data, int dataLength) throws IOException {
        CPackets packets = loginPackets.get(packetID);
        if (packets != null) {
            packets.handle(data);
            packets.reply().accept(socketReader);
        }
    }

    @Override
    public void processConfiguration(int packetID, DataInputStream data, int dataLength) throws IOException {
        CPackets packets = configPackets.get(packetID);
        if (packets != null) {
            packets.handle(data);
            packets.reply().accept(socketReader);
        }
    }

    @Override
    public void processPlay(int packetID, DataInputStream data, int dataLength) throws IOException {
        CPackets packets = playPackets.get(packetID);
        if (packets != null) {
            packets.handle(data);
            packets.reply().accept(socketReader);
        }
    }

    protected abstract void registerLoginPackets();
    protected abstract void registerConfigPackets();
    protected abstract void registerPlayPackets();

}
