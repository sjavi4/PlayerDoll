package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class copy implements SubCommandHandler {
    @Override
    public void perform(Player player, String dollName, String[] args) {
        String[] _args = Arrays.copyOf(args,2);

        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}
        if (!(Boolean) PlayerDoll.dollManagerMap.get(PlayerDoll.getDollPrefix() + dollName).configManager.getData().get("setting.Copy")) {
            player.sendMessage(TranslateFormatter.stringConvert("DisabledCommand",'&'));
            return;
        }
        //Check permission
        if (!PlayerDoll.dollManagerMap.containsKey("BOT-"+_args[1])) {return;}
        doll.getActionPack().copyFrom(PlayerDoll.dollManagerMap.get("BOT-"+_args[1]).getActionPack());
    }

    @Override
    public List<List<String>> commandList() {
        return List.of(PlayerDoll.dollManagerMap.keySet().stream().toList());
    }

}
