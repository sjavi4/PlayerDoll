package me.autobot.playerdoll.util;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface PacketUtil {

    static void writeVarInt(ByteBuf buf, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                buf.writeByte(paramInt);
                return;
            }

            buf.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    static int readVarInt(ByteBuf buf) throws IOException {
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
