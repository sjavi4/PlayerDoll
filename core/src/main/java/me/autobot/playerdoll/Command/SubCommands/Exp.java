package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.entity.Player;

public class Exp extends SubCommand {
    public Exp(Player sender, String dollName, String[] args) {
        super(sender, dollName);
        if (args == null || args.length == 0) {
            getExp();
            return;
        }
        if (args[0].equalsIgnoreCase("all")) {
            while (true) {
                if (!getExp()) break;
            }
        } else {
            if (ArgumentType.checkArgumentValid(ArgumentType.POSITIVE_INTEGER,args[0])) {
                int loop = Math.max(0,castArgument(args[0], Integer.class));
                for (int i = 0; i < loop; i++) {
                    if (!getExp()) break;
                }
            } else if (args[0].equalsIgnoreCase("all")) {
                while (true) {
                    if (!getExp()) break;
                }
            } else {
                getExp();
            }
        }
    }
    private boolean getExp() {
        if (this.dollPlayer.getLevel() <= 0) return false;
        float sumPoints = this.dollPlayer.getExp() * this.dollPlayer.getExpToLevel() + player.getExp() * player.getExpToLevel();
        this.dollPlayer.setExp(0);
        player.setExp(0);

        this.dollPlayer.setLevel(this.dollPlayer.getLevel() - 1);
        sumPoints += this.dollPlayer.getExpToLevel();

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
