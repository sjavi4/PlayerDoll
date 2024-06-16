package me.autobot.playerdoll.v1_20_R3.player;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.carpetmod.EntityPlayerActionPack;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.v1_20_R3.carpetmod.NMSActionPackPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;

public abstract class ExtServerPlayer extends ServerPlayer implements BaseEntity {

    protected final EntityPlayerActionPack actionPack = new EntityPlayerActionPack(this, new NMSActionPackPlayer(this));

    public ExtServerPlayer(MinecraftServer server, ServerLevel level, GameProfile profile) {
        super(server, level, profile, ClientInformation.createDefault());
    }

    @Override
    public boolean isDoll() {
        return false;
    }
    @Override
    public boolean isPlayer() {
        return false;
    }
    @Override
    public Player getBukkitPlayer() {
        return getBukkitEntity();
    }

    @Override
    public EntityPlayerActionPack getActionPack() {
        return actionPack;
    }
    @Override
    public void updateActionPack() {
        actionPack.onUpdate();
    }


    @Override
    public void tick() {
        try {
            beforeTick();
            super.tick();
            afterTick();
        } catch (NullPointerException ignored) {
        }
    }

    abstract void beforeTick();
    abstract void afterTick();
}
