package me.autobot.playerdoll.Command.SubCommand.utils;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class helps implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        YamlConfiguration langFile = YAMLManager.getConfig("lang");
        langFile.getConfigurationSection("helpCommand").getValues(false).forEach((k,v) -> {
            String desc = langFile.getString("helpCommand."+k+".desc");
            TextComponent hoverText = new TextComponent(ChatColor.GREEN + k + ": " + TranslateFormatter.stringTranslate(desc,'&'));
            hoverText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(TranslateFormatter.stringTranslate(langFile.getString("help."+k+".usage"),'&')).create()));
            player.spigot().sendMessage(hoverText);
        });
    }

    @Override
    public List<List<String>> commandList() {
        return null;
    }
}
