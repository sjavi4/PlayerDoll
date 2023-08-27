package me.autobot.playerdoll.Command.SubCommand.operations;

import me.autobot.playerdoll.Command.SubCommandHandler;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.GUI.Menus.MainMenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.entity.Player;

import java.util.List;

public class set implements SubCommandHandler {

    @Override
    public void perform(Player player, String dollName, String[] args) {
        DollManager doll = SubCommandHandler.checkDoll(player, dollName);
        if (doll == null) {return;}
        //Check permission
        doll.getActionPack().stopAll();
        PlayerDoll.getGuiManager().openGUI(new MainMenu(doll.getBukkitEntity()),player);
    }

    @Override
    public List<List<String>> commandList() {
        return null;
    }


}
