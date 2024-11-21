package me.autobot.playerdoll.netty.decoder.configuration;

import me.autobot.playerdoll.netty.decoder.Post_1_20_R4_AbstractPacketDecoder;
import me.autobot.playerdoll.netty.protocol.Post1_20_R4_Protocols;

public class Post1_20_R4_ConfigurationDecoder extends Post_1_20_R4_AbstractPacketDecoder {
    @Override
    protected Object getProtocol() {
        return Post1_20_R4_Protocols.CLIENT_CONFIGURATION_PROTOCOL;
    }
}
