package me.autobot.playerdoll.netty.decoder.play;

import me.autobot.playerdoll.netty.decoder.Pre_1_20_R4_AbstractPacketDecoder;
import me.autobot.playerdoll.netty.protocol.Pre1_20_R4_Protocols;

public class Pre1_20_R4_PlayDecoder extends Pre_1_20_R4_AbstractPacketDecoder {
    @Override
    protected Object getProtocol() {
        return Pre1_20_R4_Protocols.CLIENT_PLAY_PROTOCOL;
    }
}
