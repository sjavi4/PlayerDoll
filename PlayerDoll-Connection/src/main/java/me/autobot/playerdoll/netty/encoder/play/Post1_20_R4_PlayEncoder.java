package me.autobot.playerdoll.netty.encoder.play;

import me.autobot.playerdoll.netty.encoder.Post_1_20_R4_AbstractPacketEncoder;
import me.autobot.playerdoll.netty.protocol.Post1_20_R4_Protocols;

public class Post1_20_R4_PlayEncoder extends Post_1_20_R4_AbstractPacketEncoder {
    @Override
    protected Object getProtocol() {
        return Post1_20_R4_Protocols.SERVER_PLAY_PROTOCOL;
    }
}
