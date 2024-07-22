package me.autobot.playerdoll.packet.v1_20_R4.config;

import java.util.UUID;

public class SConfigResourcePackResponse extends me.autobot.playerdoll.packet.v1_20_R3.config.SConfigResourcePackResponse {

    public SConfigResourcePackResponse(UUID uuid, boolean forced) {
        super(uuid, forced);
    }

    @Override
    protected int getPacketID() {
        return 0x06;
    }

}
