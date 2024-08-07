package me.autobot.playerdoll.listener;

import me.autobot.playerdoll.DollProxy;
import me.autobot.playerdoll.doll.DollData;
import me.autobot.playerdoll.wrapper.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Optional;

public class DisconnectListener implements Listener {
    @EventHandler
    public void onServerDisconnect(ServerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        PendingConnection connection = player.getPendingConnection();
        Optional<DollData> optionalDollData = DollData.DOLL_DATA_LIST.stream().filter(data -> data.getUuid().equals(player.getUniqueId()))
                .findFirst();
        if (optionalDollData.isEmpty()) {
            return;
        }
        DollProxy.PLUGIN.getLogger().warning("DollData Found in Disconnect Event. Cleanup Doll connection Error");
        DollData dollData = optionalDollData.get();
        if (!player.getName().equals(connection.getName())) {
            ServerConnectListener.modifyDollName(connection, dollData.getFullName());
            ServerConnectListener.modifyDollName(player, dollData.getFullName());
        }
        DollData.DOLL_DATA_LIST.remove(dollData);
        BungeeCord bungeeCord = new BungeeCord(ProxyServer.getInstance());
        bungeeCord.removeConnection(player);
        DollProxy.PLUGIN.getLogger().info(String.format("DollData Removed, Doll %s can be re-connect again now", dollData.getFullName()));
    }
}
