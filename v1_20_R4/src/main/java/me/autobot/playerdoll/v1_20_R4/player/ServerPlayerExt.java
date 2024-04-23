package me.autobot.playerdoll.v1_20_R4.player;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Dolls.IServerPlayerExt;
import me.autobot.playerdoll.v1_20_R4.CarpetMod.NMSPlayerEntityActionPack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public abstract class ServerPlayerExt extends ServerPlayer implements IServerPlayerExt {
    NMSPlayerEntityActionPack actionPack;
    final Runnable updateActionTask = () -> actionPack.onUpdate();

    public ServerPlayerExt(CraftPlayer bukkitPlayer) {
        this(bukkitPlayer.getHandle().server, bukkitPlayer.getHandle().serverLevel(), bukkitPlayer.getProfile(), bukkitPlayer.getHandle().clientInformation());
    }
    public ServerPlayerExt(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile, ClientInformation clientinformation) {
        super(minecraftserver, worldserver, gameprofile, clientinformation);
    }

    // Vanilla

    @Override
    public void tick() {
        try {
            beforeTick();
            super.tick();
            afterTick();
        } catch (NullPointerException ignored) {
        }
    }

    // Vanilla

    // ServerPlayerExt
    protected abstract void beforeTick();
    protected abstract void afterTick();
    protected void sendPacket(Packet<?> packet) {
        this.server.getPlayerList().broadcastAll(packet);
    }
    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean isDoll() {
        return false;
    }
    @Override
    public Player getBukkitPlayer() {
        return this.getBukkitEntity();
    }
    // ServerPlayerExt

    // Action Pack
    @Override
    public EntityPlayerActionPack getActionPack() {
        return this.actionPack;
    }

    @Override
    public void _resetLastActionTime() {
        this.resetLastActionTime();
    }

    @Override
    public void _resetAttackStrengthTicker() {
        this.resetAttackStrengthTicker();
    }

    @Override
    public void _setJumping(boolean b) {
        this.setJumping(b);
    }

    @Override
    public void _jumpFromGround() {
        this.jumpFromGround();
    }

    // Action Pack
}
