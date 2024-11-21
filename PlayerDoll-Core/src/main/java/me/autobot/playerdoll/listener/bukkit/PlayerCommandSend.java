package me.autobot.playerdoll.listener.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class PlayerCommandSend implements Listener {
    @EventHandler
    public void onSendCommand(PlayerCommandSendEvent event) {
        // Remove minecraft: prefixed commands
        event.getCommands().removeIf(s -> s.equals("minecraft:doll") || s.equals("minecraft:playerdoll:doll"));
        event.getCommands().removeIf(s -> s.equals("minecraft:dollmanage") || s.equals("minecraft:playerdoll:dollmanage"));
    }
}
