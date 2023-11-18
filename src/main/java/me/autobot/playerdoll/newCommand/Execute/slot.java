package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Dolls.AbstractDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class slot extends SubCommand {
    Player player;
    AbstractDoll doll;
    String[] args;
    public slot(CommandSender sender, Object doll, Object args) {
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
        try {
            doll.getActionPack().setSlot(args[0] == null ? 1 : Integer.parseInt(args[0]));
        } catch (NumberFormatException ignored) {
            player.sendMessage(LangFormatter.YAMLReplace("WrongArgument",'&'));
        }
    }
    public static List<Object> tabSuggestion() {
        return List.of(List.of("1","2","3","4","5","6","7","8","9"));
    }
}