package me.autobot.playerdoll;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class Messenger implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("playerdoll:doll")) {
            return;
        }
        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        int id = input.readInt();
        if (id == 1) {
            // Find Doll by UUID
            String uuid = input.readUTF();
            boolean exist = Bukkit.getPlayer(UUID.fromString(uuid)) != null;
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeInt(2);
            output.writeUTF(uuid);
            output.writeBoolean(exist);
            Bukkit.getServer().sendPluginMessage(PlayerDoll.getPlugin(),"playerdoll:doll", output.toByteArray());
        } else if (id == 2) {
            //
            boolean exist = input.readBoolean();
            if (exist) {
                return;
            }

            String dollUUID = input.readUTF(); // doll UUID
            String dollName = input.readUTF(); // doll Name
            String callerUUID = input.readUTF(); // caller UUID
            success(dollUUID, dollName, callerUUID);
        }
    }

    private void success(String dollUUID, String dollName, String callerUUID) {
        Player sender = Bukkit.getPlayer(UUID.fromString(callerUUID));
        if (sender == null) {
            return;
        }
        if (Bukkit.hasWhitelist()) {
            Bukkit.getOfflinePlayer(UUID.fromString(dollUUID)).setWhitelisted(true);
            Bukkit.reloadWhitelist();
        }
        DollManager.getInstance().spawnDoll(dollName, UUID.fromString(dollUUID),sender);
    }
}
