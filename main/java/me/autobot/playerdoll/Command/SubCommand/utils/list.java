package me.autobot.playerdoll.Command.SubCommand.utils;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.PlayerDoll;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class list implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        if (PlayerDoll.dollManagerMap.isEmpty()) {
            player.sendMessage(TranslateFormatter.stringTranslate("NoDollOnline",'&'));
            return;
        }
        PlayerDoll.dollManagerMap.forEach( (n,p) -> {
            TextComponent hoverText = new TextComponent(ChatColor.GREEN + n);
            hoverText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Owner:"+p.getOwner().getName()+"\n")
                    .append("UUID:"+p.getStringUUID()+"\n")
                    .append("Level:"+p.experienceLevel+"\n")
                    .append("World:"+p.getBukkitEntity().getWorld().getName())
                    .create()));
            player.spigot().sendMessage(hoverText);
        } );
    }

    @Override
    public List<List<String>> commandList() {
        return null;
    }
}
