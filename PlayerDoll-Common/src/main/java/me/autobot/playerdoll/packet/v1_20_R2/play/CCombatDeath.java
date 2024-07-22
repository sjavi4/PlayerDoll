package me.autobot.playerdoll.packet.v1_20_R2.play;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.event.DollRespawnEvent;
import me.autobot.playerdoll.packet.CPackets;
import me.autobot.playerdoll.packet.PacketUtil;
import me.autobot.playerdoll.socket.io.SocketReader;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class CCombatDeath extends CPackets {

    protected int entityId;
    @Override
    public void handle(DataInputStream data) throws IOException {
        entityId = PacketUtil.readVarInt(data);
    }

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
