package me.autobot.playerdoll.api.doll;

import me.autobot.playerdoll.api.action.pack.ActionPack;
import org.bukkit.entity.Player;

public interface BaseEntity {
    boolean isPlayer();
    boolean isDoll();
    Player getBukkitPlayer();
    ActionPack getActionPack();
    void updateActionPack();
}
