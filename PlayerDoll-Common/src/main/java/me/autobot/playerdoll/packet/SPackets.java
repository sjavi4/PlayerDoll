package me.autobot.playerdoll.packet;

import java.io.IOException;

public abstract class SPackets {

    protected abstract int getPacketID();

    public abstract byte[] write() throws IOException;

}
