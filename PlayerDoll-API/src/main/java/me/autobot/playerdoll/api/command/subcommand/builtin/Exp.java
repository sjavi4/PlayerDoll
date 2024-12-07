package me.autobot.playerdoll.api.command.subcommand.builtin;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

public class Exp extends SubCommand implements DollCommandExecutor {
    private final int level;
    private Player sender;
    private final boolean asExpOrb;

    public Exp(Player target, int level, boolean asExpOrb) {
        super(target);
        this.level = level;
        this.asExpOrb = asExpOrb;
    }
    @Override
    public void execute() {
        if (asExpOrb) {
            spawnExp(level);
        } else {
            getExp(level);
        }
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
            return 0;
        }
        this.sender = playerSender;
        if (target == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        BaseEntity targetEntity = DollStorage.ONLINE_DOLLS.get(target.getUniqueId());
        if (targetEntity == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (!outputHasPerm(playerSender, DollConfig.getOnlineConfig(target.getUniqueId()), PersonalFlagButton.EXP)) {
            return 0;
        }

        execute();
        return 1;
    }

    private void getExp(int level) {
        if (target.getLevel() <= 0) {
            return;
        }

        if (level == -1) {
            level = target.getLevel();

            // Should improve performance
            if (target.getLevel() > sender.getLevel()) {
                Player less = sender;
                Player more = target;

                int lessPlayerSum = Math.round(less.getExp() * less.getExpToLevel());
                for (int i = less.getLevel(); i > 0; --i) {
                    less.setLevel(less.getLevel() - 1);
                    int expToLevel = less.getExpToLevel();
                    lessPlayerSum += expToLevel;
                }

                less.setLevel(more.getLevel());
                less.setExp(more.getExp());
                more.setLevel(0);
                more.setExp(0);

                while (lessPlayerSum >= less.getExpToLevel()) {
                    lessPlayerSum -= less.getExpToLevel();
                    less.setLevel(less.getLevel() + 1);
                }
                if (lessPlayerSum > 0) {
                    less.setExp((float) lessPlayerSum / less.getExpToLevel());
                }
                return;
            }
        }

        int sumPoints = Math.round(target.getExp() * target.getExpToLevel() + sender.getExp() * sender.getExpToLevel());
        target.setExp(0);
        sender.setExp(0);

        for (int i = 0; i < level; ++i) {
            target.setLevel(target.getLevel() - 1);
            int expToLevel = target.getExpToLevel();
            while (expToLevel >= sender.getExpToLevel()) {
                expToLevel -= sender.getExpToLevel();
                sender.setLevel(sender.getLevel() + 1);
            }
            sumPoints += expToLevel;
        }

        while (sumPoints >= sender.getExpToLevel()) {
            sumPoints -= sender.getExpToLevel();
            sender.setLevel(sender.getLevel() + 1);
        }
        if (sumPoints > 0) {
            sender.setExp((float) sumPoints / sender.getExpToLevel());
        }
    }

    private void spawnExp(int level) {
        if (target.getLevel() <= 0) {
            return;
        }

        if (level == -1) {
            level = target.getLevel();
        }

        int sumPoints = Math.round(target.getExp() * target.getExpToLevel());
        target.setExp(0);

        for (int i = 0; i < level; ++i) {
            target.setLevel(target.getLevel() - 1);
            int expToLevel = target.getExpToLevel();
            if (sumPoints + expToLevel < sumPoints) {
                // INT Overflow
                target.setLevel(target.getLevel() + 1);
                sender.sendMessage(LangFormatter.YAMLReplaceMessage("exp-overflow"));
                break;
            }
            sumPoints += expToLevel;
        }
        ExperienceOrb orb = (ExperienceOrb) sender.getWorld().spawnEntity(sender.getLocation(), EntityType.EXPERIENCE_ORB);
        orb.setExperience(sumPoints);
    }
}
