package me.autobot.playerdoll.v1_20_R4.Network;

import net.minecraft.network.Connection;

public class DollPacketInjector {
    public final Connection serverConnection;
    public final DollPacketHandler dollPacketHandler;
    public DollPacketInjector(Connection connection) {
        this.serverConnection = connection;
        this.dollPacketHandler = new DollPacketHandler();
        serverConnection.channel.pipeline().addBefore("packet_handler", "doll_packet_injector", dollPacketHandler);
    }
}
