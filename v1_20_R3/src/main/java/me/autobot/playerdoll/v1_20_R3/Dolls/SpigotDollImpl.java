package me.autobot.playerdoll.v1_20_R3.Dolls;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class SpigotDollImpl extends AbstractDoll {
    public SpigotDollImpl(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile, ServerPlayer player) {
        super(minecraftserver, worldserver, gameprofile, player);
    }
    @Override
    public void spawnToWorld() {
        //this.connection = new DollNetworkHandler(server,dollNetworkManager,this);
        super.spawnToWorld();
        spawnPacketTask.run();
    }
    /*
    @Override
    public void tick() {
        nonFoliaTickCount = this.getServer().getTickCount();
        super.tick();
    }

     */
    @Override
    public void disconnect() {
        super.disconnect();
        this.serverLevel().removePlayerImmediately(this, RemovalReason.DISCARDED);
        connection.onDisconnect(Component.literal("Disconnected"));
    }
}
