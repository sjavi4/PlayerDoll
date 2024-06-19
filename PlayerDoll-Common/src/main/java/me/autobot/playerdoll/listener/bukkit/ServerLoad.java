package me.autobot.playerdoll.listener.bukkit;

import me.autobot.playerdoll.brigadier.CommandBuilder;
import me.autobot.playerdoll.brigadier.CommandRegister;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerLoad implements Listener {
    @EventHandler
    public void onReload(ServerLoadEvent event) {
        // Register and unregister command here (brigadier command)
        // Or else crash with Linkage Error
        CommandBuilder.COMMANDS.forEach(CommandRegister::registerCommand);
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }
}
