package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
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
        EntityPlayerActionPack.Action action = ActionHandler.action(args[0].toLowerCase(), intArgs);
        doll.getActionPack().start(EntityPlayerActionPack.ActionType.USE, action);
    }
    @Override
    public final ArrayList<String> targetSelection(UUID uuid) {
        Set<String> set = new HashSet<>();
        set.addAll(getOwnedDoll(uuid));
        set.addAll(getSharedDoll(uuid));
        set.retainAll(getOnlineDoll());
        return new ArrayList<>(){{addAll(set);}};
    }

    @Override
    public List<Object> tabSuggestion() {
        return List.of(
                List.of("once","interval","continuous"),
                List.of("<[tick:interval]>"),
                List.of("<[tick:offset]>")
        );
    }
}