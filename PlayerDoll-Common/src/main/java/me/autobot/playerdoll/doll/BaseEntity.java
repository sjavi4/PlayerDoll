package me.autobot.playerdoll.doll;

import me.autobot.playerdoll.carpetmod.EntityPlayerActionPack;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public interface BaseEntity {
    boolean isPlayer();
    boolean isDoll();
    Player getBukkitPlayer();
    EntityPlayerActionPack getActionPack();
    void updateActionPack();
}
