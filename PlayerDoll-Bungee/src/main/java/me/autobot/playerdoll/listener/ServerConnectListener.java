package me.autobot.playerdoll.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.autobot.playerdoll.DollProxy;
import me.autobot.playerdoll.doll.DollData;
import me.autobot.playerdoll.wrapper.InitialHandler;
import me.autobot.playerdoll.wrapper.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class ServerConnectListener implements Listener {
    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        DollData.DOLL_DATA_LIST.stream().filter(dollData -> dollData.getUuid().equals(player.getUniqueId()))
                .findFirst()
                .ifPresent(dollData -> {
                    //dollData.getListener().close();
                    dollData.setDollPlayer(player);
                    modifyDollName(player, dollData.getStripName());
                    modifyDollName(event.getPlayer().getPendingConnection(), dollData.getFullName());

                    // Change Server
                    event.setTarget(dollData.getTargetServer());

                    // Send Last Join Server
                    ByteArrayDataOutput output = ByteStreams.newDataOutput();
                    output.writeInt(1);
                    output.writeUTF(dollData.getUuid().toString());
                    output.writeUTF(dollData.getTargetServer().getName()); // target
                    dollData.getTargetServer().sendData("playerdoll:doll", output.toByteArray());

                    ProxyServer.getInstance().getScheduler().schedule(DollProxy.PLUGIN, () -> {
                        modifyDollName(player, dollData.getFullName());
                    }, 1, TimeUnit.SECONDS);
                });
    }

    public static void modifyDollName(ProxiedPlayer player, String name) {
        UserConnection connection = new UserConnection(player);
        connection.setName(name);
    }
    public static void modifyDollName(PendingConnection connection, String name) {
        InitialHandler initialHandler = new InitialHandler(connection);
        initialHandler.setName(name);
    }
}
