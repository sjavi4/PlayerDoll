package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class lookat extends SubCommand {
    Player player;
    DollManager doll;
    String[] args;
    public lookat(CommandSender sender, Object doll, Object args) {
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
        if (Bukkit.getPlayer(args[0]) != null) {
            doll.getActionPack().lookAt(doll.server.getPlayerList().getPlayerByName(args[0]).getEyePosition());
        } else if (args.length >= 3) {
            try {
                doll.getActionPack().lookAt(new Vec3(Float.parseFloat(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2])));
                //args[0].matches("[-+]?[0-9]*\\.?[0-9]+") && args[1].matches("[-+]?[0-9]*\\.?[0-9]+")
            } catch (NumberFormatException ignored) {
                player.sendMessage(LangFormatter.YAMLReplace("WrongArgument",'&'));
            }
        }
    }
    public static List<Object> tabSuggestion() {
        return List.of(new ArrayList<>(){{
            add("<coord:X> <coord:Y> <coord:Z>");
            addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
        }});
    }
}