package me.autobot.playerdoll.v1_20_R4.player;

import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.ExtendPlayer;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;

public class TransPlayer extends ExtServerPlayer implements ExtendPlayer {

    public TransPlayer(CraftPlayer craftPlayer) {
        super(craftPlayer.getHandle().server, craftPlayer.getHandle().serverLevel(), craftPlayer.getProfile());
        DollManager.ONLINE_PLAYERS.put(craftPlayer.getUniqueId(), this);
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
