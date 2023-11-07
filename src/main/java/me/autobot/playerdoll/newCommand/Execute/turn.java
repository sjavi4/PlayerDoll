package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import net.minecraft.core.Direction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class turn extends SubCommand {
    Player player;
    DollManager doll;
    String[] args;
    private static final String[] directions = new String[]{"north", "east", "south", "west", "up", "down"};
    public turn(CommandSender sender, Object doll, Object args) {
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
        if (args.length >= 2) {
            try {
                //if (args[0].matches("[-+]?[0-9]*\\.?[0-9]+") && args[1].matches("[-+]?[0-9]*\\.?[0-9]+")) {
                doll.getActionPack().turn(Float.parseFloat(args[0]), Float.parseFloat(args[1]));
            } catch (NumberFormatException ignored) {
                player.sendMessage(LangFormatter.YAMLReplace("WrongArgument",'&'));
            }
        }

    }
    public static List<Object> tabSuggestion() {
        return List.of("<rotation:X> <rotation:Y>");
    }
}