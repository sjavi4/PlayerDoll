package me.autobot.addonDoll.player;

import com.mojang.authlib.GameProfile;
import me.autobot.addonDoll.action.PackPlayerImpl;
import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.action.pack.ActionPack;
import me.autobot.playerdoll.api.doll.BaseEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;

public abstract class ExtServerPlayer extends ServerPlayer implements BaseEntity {

    protected final ActionPack actionPack = new ActionPack(this, new PackPlayerImpl(this));

    public ExtServerPlayer(MinecraftServer server, ServerLevel level, GameProfile profile, ServerPlayer serverPlayer) {
        super(server, level, profile, ClientInformation.createDefault());
        ReflectionUtil.setConvertPlayerPermBase(ReflectionUtil.NMSToBukkitPlayer(serverPlayer), getBukkitPlayer());
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
        return ReflectionUtil.NMSToBukkitPlayer(this);
    }

    @Override
    public ActionPack getActionPack() {
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
