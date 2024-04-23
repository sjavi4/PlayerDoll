package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public interface IServerDoll extends IServerPlayerExt {
    DollConfig getDollConfig();
    void setDollConfig(DollConfig dollConfig);
    void setDollMaxUpStep(float h);
    Player getCaller();
    OfflinePlayer getOwner();
    void dollDisconnect();
    void dollKill();
    boolean getDollHurtMarked();
    void setDollHurtMarked(boolean b);
    static void resetPhantomStatistic(Player player) {
        player.setStatistic(Statistic.TIME_SINCE_REST,0);
    }
    static boolean executeHurt(IServerDoll iDoll, Player doll, boolean damaged) {
        if (damaged) {
            if (iDoll.getDollHurtMarked()) {
                iDoll.setDollHurtMarked(false);
                PlayerDoll.getScheduler().entityTask(doll, () -> iDoll.setDollHurtMarked(true), 1);
            }
        }
        return damaged;
    }
}
