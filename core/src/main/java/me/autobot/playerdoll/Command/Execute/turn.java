package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.ArgumentHelper;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class turn extends SubCommand {
    Player player;
    IDoll doll;
    String[] args;
    private static final String[] directions = new String[]{"north", "east", "south", "west", "up", "down"};
    public turn() {}
    public turn(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName, "Turn")) return;
        player = (Player) sender;
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        this.args = args == null? new String[]{""} : (String[]) args;
        execute();
    }

    @Override
    public void execute() {
        if (args.length >= 2) {
            ArgumentHelper<Float> argumentHelper = new ArgumentHelper<>();
            ArrayList<Float> floatArgs = argumentHelper.castTo(args, Float.class);
            doll.getActionPack().turn(floatArgs.get(0), floatArgs.get(1));
        }

    }
}