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

                int sum = sumExpFromLevel(less, level);

                less.setLevel(more.getLevel());
                less.setExp(more.getExp());
                more.setLevel(0);
                more.setExp(0);

                loadExp(less, sum);
                return;
            }
        }
        int sum = sumExpFromLevel(target, level);

        int playerProgress = Math.round(sender.getExp() * sender.getExpToLevel());
        sender.setExp(0);

        loadExp(sender, sum);
        loadExp(sender, playerProgress);
    }

    private void spawnExp(int level) {
        if (target.getLevel() <= 0) {
            return;
        }

        if (level == -1) {
            level = target.getLevel();
        }

        int sum = sumExpFromLevel(target, level);
        ExperienceOrb orb = (ExperienceOrb) sender.getWorld().spawnEntity(sender.getLocation(), EntityType.EXPERIENCE_ORB);
        orb.setExperience(sum);
    }

    private int sumExpFromLevel(Player who, int levels) {
        int lv = Math.min(levels, who.getLevel());
        int sum = Math.round(who.getExp() * who.getExpToLevel());
        who.setExp(0);
        for (int i = lv; i > 0; --i) {
            who.setLevel(who.getLevel() - 1);
            int expToLevel = who.getExpToLevel();
            if (sum + expToLevel <= sum) {
                // INT Overflow
                who.setLevel(who.getLevel() + 1);
                sender.sendMessage(LangFormatter.YAMLReplaceMessage("exp-overflow"));
                break;
            }
            sum += expToLevel;
        }
        return sum;
    }

    private void loadExp(Player who, int exp) {
        while (exp >= who.getExpToLevel()) {
            exp -= who.getExpToLevel();
            who.setLevel(who.getLevel() + 1);
        }
        if (exp > 0) {
            who.setExp((float) exp / who.getExpToLevel());
        }
    }
}
