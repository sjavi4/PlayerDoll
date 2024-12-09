package me.autobot.addonDoll.player;

import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.doll.ExtendPlayer;
import net.minecraft.server.level.ServerPlayer;

public class TransPlayer extends ExtServerPlayer implements ExtendPlayer {

    public TransPlayer(ServerPlayer serverPlayer) {
        super(serverPlayer.server, serverPlayer.serverLevel(), serverPlayer.getGameProfile());
        DollStorage.ONLINE_TRANSFORMS.put(serverPlayer.getUUID(), this);
        ReflectionUtil.setConvertPlayerPermBase(ReflectionUtil.NMSToBukkitPlayer(serverPlayer), getBukkitPlayer());
    }

    @Override
    public boolean isPlayer() {
        return true;
    }
    @Override
    void beforeTick() {
        updateActionPack();
    }

    @Override
    void afterTick() {
    }
}
