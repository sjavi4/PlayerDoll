package me.autobot.playerdoll.api.connection;

import io.netty.buffer.ByteBuf;

public interface PacketVarIntHelper {

    static void writeVarInt(ByteBuf buf, int paramInt) {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                buf.writeByte(paramInt);
                return;
            }

            buf.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    static int readVarInt(ByteBuf buf) {
        int i = 0;
        int j = 0;

        byte b;
        do {
            b = buf.readByte();
            i |= (b & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while(hasContinuationBit(b));

        return i;
    }

    static boolean hasContinuationBit(byte b) {
        return (b & 128) == 128;
    }
}
