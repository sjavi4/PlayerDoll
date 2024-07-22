package me.autobot.playerdoll.packet.v1_20_R4.play;

public class SPlayKeepAlive extends me.autobot.playerdoll.packet.v1_20_R3.play.SPlayKeepAlive {
    public SPlayKeepAlive(long keepAliveId) {
        super(keepAliveId);
    }
    @Override
    protected int getPacketID() {
        return 0x18;
    }
}
