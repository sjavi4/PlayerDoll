package me.autobot.playerdoll.netty.encoder.login;

import me.autobot.playerdoll.netty.encoder.Post_1_20_R4_AbstractPacketEncoder;
import me.autobot.playerdoll.netty.protocol.Post1_20_R4_Protocols;

public class Post1_20_R4_LoginEncoder extends Post_1_20_R4_AbstractPacketEncoder {
    @Override
    protected Object getProtocol() {
        return Post1_20_R4_Protocols.SERVER_LOGIN_PROTOCOL;
    }
}
