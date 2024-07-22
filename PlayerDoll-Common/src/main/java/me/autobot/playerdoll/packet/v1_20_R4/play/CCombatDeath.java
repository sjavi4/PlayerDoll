package me.autobot.playerdoll.packet.v1_20_R4.play;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.event.DollRespawnEvent;
import me.autobot.playerdoll.socket.io.SocketReader;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.function.Consumer;

public class CCombatDeath extends me.autobot.playerdoll.packet.v1_20_R2.play.CCombatDeath {

    @Override
    public Consumer<SocketReader> reply() {
        return socketReader -> {
            Player player = DollManager.ONLINE_DOLLS.get(socketReader.profile.getId()).getBukkitPlayer();
            if (entityId == player.getEntityId()) {
                try {
                    socketReader.getOutput().write(new SRequestRespawn().write());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                PlayerDoll.scheduler.globalTaskDelayed(() -> PlayerDoll.callSyncEvent(new DollRespawnEvent(player)), 5);
            }
        };
    }
}
