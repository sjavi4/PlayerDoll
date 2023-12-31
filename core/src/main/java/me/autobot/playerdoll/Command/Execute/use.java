package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Configs.PermissionManager;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.ArgumentHelper;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class use extends SubCommand {
    Player player;
    IDoll doll;
    String[] args;
    public use() {}

    public use(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName, "Use")) return;
        player = (Player) sender;
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        this.args = args == null? new String[]{""} : (String[]) args;
        execute();
    }

    @Override
    public void execute() {
        //IEntityPlayerActionPack actionPack = doll.getActionPack();
        ArgumentHelper<Integer> argumentHelper = new ArgumentHelper<>();
        ArrayList<Integer> intArgs = argumentHelper.castTo(args, Integer.class);
        if (!player.isOp() && intArgs.size() > 0) {
            PermissionManager perm = PermissionManager.getInstance(player);
            intArgs.set(0,Math.max(perm.minUseInterval,intArgs.get(0)));
        }
        EntityPlayerActionPack.Action action = ActionHandler.action(args[0].toLowerCase(), intArgs);
        doll.getActionPack().start(EntityPlayerActionPack.ActionType.USE, action);
    }

}