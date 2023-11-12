package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Dolls.AbstractDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import net.minecraft.core.Direction;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class look extends SubCommand {
    Player player;
    AbstractDoll doll;
    String[] args;
    private static final String[] directions = new String[]{"north", "east", "south", "west", "up", "down"};
    public look(CommandSender sender, Object doll, Object args) {
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
        if (Arrays.stream(directions).anyMatch(d -> d.equalsIgnoreCase(args[0]))) {
            doll.getActionPack().look(Direction.byName(args[0].toLowerCase()));
        } else if (Bukkit.getPlayer(args[0]) != null) {
            var p = doll.server.getPlayerList().getPlayerByName(args[0]);
            doll.getActionPack().look(p.getYRot(),p.getXRot());
        } else if (args.length >= 2) {
            try {
                doll.getActionPack().look(Float.parseFloat(args[0]), Float.parseFloat(args[1]));
                //args[0].matches("[-+]?[0-9]*\\.?[0-9]+") && args[1].matches("[-+]?[0-9]*\\.?[0-9]+")
            } catch (NumberFormatException ignored) {
                player.sendMessage(LangFormatter.YAMLReplace("WrongArgument",'&'));
            }
        }

    }
    public static List<Object> tabSuggestion() {
        return List.of(new ArrayList<>(){{
            add("<rotation:X> <rotation:Y>");
            addAll(Arrays.stream(directions).toList());
            addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }});
    }
}