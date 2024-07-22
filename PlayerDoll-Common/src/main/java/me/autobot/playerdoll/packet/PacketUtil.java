package me.autobot.playerdoll.packet;

import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public interface PacketUtil {
    enum protocolNumber {
        v1_20_R2,
        v1_20_R3,
        v1_20_R4,
        v1_21_R1;
        public IPacketFactory getFactory(SocketReader reader) {
            return switch (this) {
                case v1_20_R2 -> new me.autobot.playerdoll.packet.v1_20_R2.Factory(reader);
                case v1_20_R3 -> new me.autobot.playerdoll.packet.v1_20_R3.Factory(reader);
                case v1_20_R4 -> new me.autobot.playerdoll.packet.v1_20_R4.Factory(reader);
                case v1_21_R1 -> new me.autobot.playerdoll.packet.v1_21_R1.Factory(reader);
            };
        }
    }
    static void writeString(DataOutputStream out, String string, Charset charset) throws IOException {
        byte [] bytes = string.getBytes(charset);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }
    static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }
    static void writeVarLong(DataOutputStream out, long paramLong) throws IOException {
        while (true) {
            if ((paramLong & ~((long) 0x7F)) == 0) {
                out.writeByte((int) paramLong);
                return;
            }

            out.writeByte((int) ((paramLong & 0x7F) | 0x80));

            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            paramLong >>>= 7;
        }
    }

    static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;

        byte b;
        do {
            b = in.readByte();
            i |= (b & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while(hasContinuationBit(b));

        return i;
    }
    static UUID readUUID(DataInputStream in) throws IOException {
        long m = in.readLong();
        long l = in.readLong();

        return new UUID(m, l);
    }

    static String readString(DataInputStream in) throws IOException {
        int l = readVarInt(in);
        byte[] b = new byte[l];
        in.read(b);
        return new String(b);
    }
    static boolean hasContinuationBit(byte b) {
        return (b & 128) == 128;
    }

    static byte[] decompress(byte[] input, boolean noWrap) throws DataFormatException {
        Inflater inflater = new Inflater(noWrap);
        inflater.setInput(input);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];

        while (!inflater.finished()) {
            int decompressedSize = inflater.inflate(buffer);
            if (decompressedSize == 0) {
                break;
            }
            outputStream.write(buffer, 0, decompressedSize);
        }

        return outputStream.toByteArray();
    }
    static byte[] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            if (count == 0) {
                break;
            }
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }

    static int getVarIntLength(int i) {
        for(int j = 1; j < 5; ++j) {
            if ((i & -1 << j * 7) == 0) {
                return j;
            }
        }

        return 5;
    }
}
