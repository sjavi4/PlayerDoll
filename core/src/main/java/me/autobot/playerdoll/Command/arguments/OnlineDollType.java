package me.autobot.playerdoll.Command.arguments;

import me.autobot.playerdoll.Command.CommandType;
import me.autobot.playerdoll.Dolls.DollManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class OnlineDollType extends AbstractType {

    @Override
    boolean validate(String s) {
        Player player = Bukkit.getPlayer(CommandType.getDollName(s,true));
        return player != null && DollManager.ONLINE_DOLL_MAP.containsKey(player.getUniqueId());
    }

    @Override
    List<String> suggestions() {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.startsWith("-"))
                .map(s -> s.substring(1))
                .toList();
    }
}
