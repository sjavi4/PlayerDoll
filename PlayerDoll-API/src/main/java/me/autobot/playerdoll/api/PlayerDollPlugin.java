package me.autobot.playerdoll.api;

import me.autobot.playerdoll.api.command.CommandBuilder;
import me.autobot.playerdoll.api.config.ConfigLoader;
import me.autobot.playerdoll.api.connection.Connection;
import me.autobot.playerdoll.api.constant.AbsServerBranch;
import me.autobot.playerdoll.api.constant.AbsServerVersion;
import me.autobot.playerdoll.api.registry.AddonRegistry;
import org.bukkit.plugin.Plugin;

public interface PlayerDollPlugin extends Plugin {

    FileUtil getFileUtil();
    ConfigLoader getConfigLoader();
    int getOriginalMaxPlayer();
    AbsServerVersion getServerVersion();
    AbsServerBranch getServerBranch();
    AddonRegistry getAddonRegistry();
    Connection getConnection();
    CommandBuilder getCommandBuilder();

}
