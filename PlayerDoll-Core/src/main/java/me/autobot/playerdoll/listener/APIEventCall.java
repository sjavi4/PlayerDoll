package me.autobot.playerdoll.listener;

import me.autobot.playerdoll.api.event.CommandRegisterEvent;
import me.autobot.playerdoll.command.BuiltinCommandBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class APIEventCall implements Listener {

    @EventHandler
    private void onAPIRegisterCommand(CommandRegisterEvent event) {
        new BuiltinCommandBuilder(event.getRoot());
    }
}
