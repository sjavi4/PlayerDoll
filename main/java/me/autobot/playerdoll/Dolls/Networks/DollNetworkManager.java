package me.autobot.playerdoll.Dolls.Networks;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;

public class DollNetworkManager extends Connection {
    public DollNetworkManager(PacketFlow enumprotocoldirection) {
        super(enumprotocoldirection);
    }
}
