package me.autobot.playerdoll.packet.v1_20_R4.play;

public class SRequestRespawn extends me.autobot.playerdoll.packet.v1_20_R2.play.SRequestRespawn {
    @Override
    protected int getPacketID() {
        return 0x09;
    }

}
