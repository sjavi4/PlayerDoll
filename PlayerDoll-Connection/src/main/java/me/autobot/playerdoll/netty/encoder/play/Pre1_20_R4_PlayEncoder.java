package me.autobot.playerdoll.netty.encoder.play;

import me.autobot.playerdoll.netty.encoder.Pre_1_20_R4_AbstractPacketEncoder;
import me.autobot.playerdoll.netty.protocol.Pre1_20_R4_Protocols;

public class Pre1_20_R4_PlayEncoder extends Pre_1_20_R4_AbstractPacketEncoder {
    @Override
    protected Object getProtocol() {
        return Pre1_20_R4_Protocols.SERVER_PLAY_PROTOCOL;
    }
}
