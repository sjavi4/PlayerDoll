package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Command.Helper.ArgumentHelper;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class exp extends SubCommand {
    Player player;
    Player doll;
    String[] args;
    public exp() {
    }
    public exp(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);

        if (!checkPermission(sender, dollName, "Exp")) return;
        player = (Player) sender;
        this.doll = Bukkit.getPlayer(dollName);
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        this.args = args == null? new String[]{"1"} : (String[]) args;
        execute();
    }

    @Override
    public void execute() {
        if (args[0].equalsIgnoreCase("all")) {
            while (true) {
                if (!getExp()) break;
            }
        } else {
            ArgumentHelper<Integer> argumentHelper = new ArgumentHelper<>();
            ArrayList<Integer> intArgs = argumentHelper.castTo(args, Integer.class);
            int loop = Math.max(0,intArgs.get(0));
            for (int i = 0; i < loop; i++) {
                if (!getExp()) break;
            }
        }
    }
    private boolean getExp() {
        if (doll.getLevel() <= 0) return false;
        float sumPoints = doll.getExp() * doll.getExpToLevel() + player.getExp() * player.getExpToLevel();
        doll.setExp(0);
        player.setExp(0);

        doll.setLevel(doll.getLevel() - 1);
        sumPoints += doll.getExpToLevel();

        while (sumPoints >= player.getExpToLevel()) {
            sumPoints -= player.getExpToLevel();
            player.setLevel(player.getLevel()+1);
        }
        if (sumPoints > 0) {
            player.setExp(sumPoints/player.getExpToLevel());
        }
        return true;
    }

}
