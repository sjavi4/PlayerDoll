package me.autobot.playerdoll.Command.SubCommand.actions;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Configs.TranslateFormatter;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;

import java.util.List;

public class dismount implements SubCommandHandler {

    @Override
    public void perform(Player player, String dollName, String[] args) {
        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}

        if (!(Boolean) PlayerDoll.dollManagerMap.get(PlayerDoll.getDollPrefix() + dollName).configManager.getData().get("setting.Dismount")) {
            player.sendMessage(TranslateFormatter.stringConvert("DisabledCommand",'&'));
            return;
        }

        doll.getActionPack().dismount();
    }

    @Override
    public List<List<String>> commandList() {
        return null;
    }

}
