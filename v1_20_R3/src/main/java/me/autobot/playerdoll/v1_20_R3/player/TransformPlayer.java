package me.autobot.playerdoll.v1_20_R3.player;

import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.v1_20_R3.CarpetMod.NMSPlayerEntityActionPack;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TransformPlayer extends ServerPlayerExt {
    public TransformPlayer(Player bukkitPlayer) {
        super((CraftPlayer) bukkitPlayer);
        this.actionPack = new NMSPlayerEntityActionPack(this);
        DollManager.ONLINE_PLAYER_MAP.put(this.uuid, this);
    }

    @Override
    protected void beforeTick() {
        updateActionTask.run();
    }

    @Override
    protected void afterTick() {
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

}
