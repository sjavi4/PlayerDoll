package me.autobot.playerdoll.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.autobot.playerdoll.DollProxy;
import me.autobot.playerdoll.doll.DollData;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LoginListener implements Listener {
    @EventHandler
    public void onLogin(LoginEvent event) {
        String address = event.getConnection().getSocketAddress().toString();
        DollData.DOLL_DATA_LIST.stream().filter(dollData -> dollData.getAddress().equals(address))
                .findFirst()
                .ifPresent(dollData -> {
                    ByteArrayDataOutput output = ByteStreams.newDataOutput();
                    // Start capture Login Listener
                    DollProxy.PLUGIN.getLogger().info(String.format("Doll %s is ready to Join server", dollData.getFullName()));
                    output.writeInt(0);
                    output.writeUTF(dollData.getUuid().toString()); // doll UUID
                    dollData.getTargetServer().sendData("playerdoll:doll", output.toByteArray());
                });
    }
}
