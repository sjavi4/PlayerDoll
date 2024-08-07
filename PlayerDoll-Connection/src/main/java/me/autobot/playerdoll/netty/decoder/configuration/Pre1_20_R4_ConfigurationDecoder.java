package me.autobot.playerdoll.netty.decoder.configuration;

import me.autobot.playerdoll.netty.decoder.Pre_1_20_R4_AbstractPacketDecoder;
import me.autobot.playerdoll.netty.protocol.Pre1_20_R4_Protocols;

public class Pre1_20_R4_ConfigurationDecoder extends Pre_1_20_R4_AbstractPacketDecoder {
    @Override
    protected Object getProtocol() {
        return Pre1_20_R4_Protocols.CLIENT_CONFIGURATION_PROTOCOL;
    }
}
