package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.CarpetMod.IEntityPlayerActionPack;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class attack extends SubCommand {
    Player player;
    IDoll doll;
    String[] args;
    public attack(CommandSender sender, Object doll, Object args) {
        super(MinPermission.Share, false);
        String dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName)) return;
        player = (Player) sender;
        if (!isOnline(dollName)) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        this.args = args == null? new String[]{""} : (String[]) args;
        execute();
    }

    @Override
    public void execute() {
        IEntityPlayerActionPack actionPack = doll.getActionPack();
        int[] para = null;
        try {
            if (args.length >= 3) para = new int[]{Integer.parseInt(args[1]),Integer.parseInt(args[2])};
            else if (args.length >= 2) para = new int[]{Integer.parseInt(args[1])};
        } catch (NumberFormatException ignored) {
            player.sendMessage(LangFormatter.YAMLReplace("WrongArgument",'&'));
        }
        Object action = ActionHandler.action(actionPack, args[0].toLowerCase(), para);
        doll.getActionPack().start(actionPack.ActionType_attack(), action);

    }
    public static List<Object> tabSuggestion() {
        return List.of(
                List.of("once","interval","continuous"),
                List.of("<[tick:interval]>"),
                List.of("<[tick:offset]>")
        );
    }
}