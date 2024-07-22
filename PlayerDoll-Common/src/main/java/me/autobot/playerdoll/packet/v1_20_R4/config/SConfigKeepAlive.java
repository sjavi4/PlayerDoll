package me.autobot.playerdoll.packet.v1_20_R4.config;


public class SConfigKeepAlive extends me.autobot.playerdoll.packet.v1_20_R2.config.SConfigKeepAlive {

    public SConfigKeepAlive(long keepAliveId) {
        super(keepAliveId);
    }
    @Override
    protected int getPacketID() {
        return 0x04;
    }
}
