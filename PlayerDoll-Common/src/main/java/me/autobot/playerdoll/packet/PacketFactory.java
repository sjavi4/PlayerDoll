package me.autobot.playerdoll.packet;

import me.autobot.playerdoll.socket.SocketHelper;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public abstract class PacketFactory implements IPacketFactory {
    protected final SocketReader socketReader;
    protected final DataOutputStream output;
    public PacketFactory(SocketReader socketReader) {
        this.socketReader = socketReader;
        this.output = socketReader.getOutput();
    }
    
    protected abstract int getProtocol();


    public byte[] clientIntent() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream handshake = new DataOutputStream(buffer);

        handshake.writeByte(0x00); //packet id for handshake
        Packets.writeVarInt(handshake, getProtocol()); //protocol version
        Packets.writeString(handshake, SocketHelper.IP, StandardCharsets.UTF_8);
        handshake.writeShort(SocketHelper.PORT); //port
        Packets.writeVarInt(handshake, 2); //state (2 for login)

        return buffer.toByteArray();
    }

    protected byte[] loginAck() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream ack = new DataOutputStream(buffer);

        Packets.writeVarInt(ack, 0x02); // length of compressed size + packet id
        Packets.writeVarInt(ack, 0x00); // no compression
        Packets.writeVarInt(ack, 0x03); // login ack id

        return buffer.toByteArray();

    }

    protected abstract int getConfigAckId();
    protected byte[] configAck() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream ack = new DataOutputStream(buffer);

        Packets.writeVarInt(ack, 0x02); // length of compressed size + packet id
        Packets.writeVarInt(ack, 0x00); // no compression
        Packets.writeVarInt(ack, getConfigAckId()); // login ack id

        return buffer.toByteArray();

    }

    protected abstract int getKeepAlivePacketId(SocketReader.ConnectionState state);
    protected byte[] keepAlive(SocketReader.ConnectionState state, long id) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream respond = new DataOutputStream(buffer);

        Packets.writeVarInt(respond, 0x0A); // length of compressed size + packet id + packet data
        Packets.writeVarInt(respond, 0x00); // no compression
        Packets.writeVarInt(respond, getKeepAlivePacketId(state)); // id
        // id
        respond.writeLong(id);

        return buffer.toByteArray();
    }

    protected byte[] resourcePackPush(SocketReader.ConnectionState state, UUID packID, ResourcePackStatus status) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream respond = new DataOutputStream(buffer);

        Packets.writeVarInt(respond, 0x19); // length of compressed size + packet id + packet data
        Packets.writeVarInt(respond, 0x00); // no compression
        Packets.writeVarInt(respond, state == SocketReader.ConnectionState.CONFIGURATION ? 0x06 : 0x28); // id
        // UUID
        respond.writeLong(packID.getMostSignificantBits());
        respond.writeLong(packID.getLeastSignificantBits());
        // reply
        Packets.writeVarInt(respond, status.id);


        return buffer.toByteArray();

    }

//    protected byte[] selectKnownPacks(DataInputStream in) throws IOException {
//        int knownPackCount = Packets.readVarInt(in);
//        String[][] knownPacks = new String[knownPackCount][3];
//        for (int i = 0; i < knownPackCount; i++) {
//            String[] pack = new String[3];
//            for (int j = 0; j < pack.length; j++) {
//                pack[j] = Packets.readString(in);
//            }
//            knownPacks[i] = pack;
//        }
//        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//        DataOutputStream respond = new DataOutputStream(buffer);
//
//
//        ByteArrayOutputStream temp = new ByteArrayOutputStream();
//        DataOutputStream raw = new DataOutputStream(temp);
//
//        Packets.writeVarInt(raw, knownPackCount);
//        for (String[] pack : knownPacks) {
//            for (String data : pack) {
//                Packets.writeVarInt(raw, data.length());
//                Packets.writeString(raw, data, StandardCharsets.UTF_8);
//            }
//        }
//
//        byte[] uncompressedByte = temp.toByteArray();
//
//        if (uncompressedByte.length >= socketReader.getCompressionThreshold()) {
//            ByteArrayOutputStream rawWithPacketId = new ByteArrayOutputStream();
//            DataOutputStream other = new DataOutputStream(rawWithPacketId);
//
//            Packets.writeVarInt(other, 0x07);
//            other.write(uncompressedByte);
//
//            byte[] compressedByte = Packets.compress(rawWithPacketId.toByteArray());
//            Packets.writeVarInt(respond, Packets.getVarIntLength(uncompressedByte.length) + Packets.getVarIntLength(compressedByte.length)); // length of compressed size + packet id + packet data
//            Packets.writeVarInt(respond, uncompressedByte.length); // compression
//            respond.write(compressedByte);
//        } else {
//            Packets.writeVarInt(respond, 0x02 + Packets.getVarIntLength(uncompressedByte.length)); // length of compressed size + packet id + packet data
//            Packets.writeVarInt(respond, 0x00); // no compression
//            Packets.writeVarInt(respond, 0x07); // id
//            respond.write(uncompressedByte);
//        }
//        Packets.writeVarInt(respond, 0x07); // id
//
//        Packets.writeVarInt(respond, 0x02 + Packets.getVarIntLength(compressedByte.length)); // length of compressed size + packet id + packet data
//        Packets.writeVarInt(respond, Packets.getVarIntLength(uncompressedByte.length) >= socketReader.getCompressionThreshold() ? ); // no compression
//        Packets.writeVarInt(respond, 0x07); // id
//
//        respond.write(compressedByte);
//
//        return buffer.toByteArray();
//    }

    public enum ResourcePackStatus {
        DOWNLOAD_SUCCESSFULLY(0), ACCEPTED(3), DOWNLOADED(4), DECLINED(1);

        private final int id;
        ResourcePackStatus(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }
    }
}
