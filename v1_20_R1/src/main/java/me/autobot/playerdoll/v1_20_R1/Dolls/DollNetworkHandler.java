package me.autobot.playerdoll.v1_20_R1.Dolls;

import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class DollNetworkHandler extends ServerGamePacketListenerImpl {
    public DollNetworkHandler(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
    }

    @Override
    public void tick() {
    }
}
