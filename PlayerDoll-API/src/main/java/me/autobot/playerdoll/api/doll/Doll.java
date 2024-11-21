package me.autobot.playerdoll.api.doll;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public interface Doll extends BaseEntity {
    void dollDisconnect();
//    void dollKill();
    void setDollMaxUpStep(double d);
    Player getCaller();
    static void resetPhantomStatistic(Player player) {
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);
    }
    static long getTickCount(Player player) {
        return player.getWorld().getGameTime();
    }
}
