package me.autobot.playerdoll.command.subcommand.actionpack;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.brigadier.CommandBuilder;
import me.autobot.playerdoll.carpetmod.EntityPlayerActionPack;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class ActionCommand extends SubCommand implements DollCommandExecutor {

    private Player sender;
    private final EntityPlayerActionPack.ActionType type;
    private final EntityPlayerActionPack.Action actionMode;
    private final FlagConfig.PersonalFlagType flagType;
    private BaseEntity targetEntity;
    public ActionCommand(Player target, EntityPlayerActionPack.ActionType type, EntityPlayerActionPack.Action actionMode, FlagConfig.PersonalFlagType flagType) {
        super(target);
        this.type = type;
        this.actionMode = actionMode;
        this.flagType = flagType;
    }

    @Override
    public void execute() {
        if (type == EntityPlayerActionPack.ActionType.JUMP && targetEntity.isPlayer()) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("self-jump"));
            return;
        }
        if (type == EntityPlayerActionPack.ActionType.LOOK_AT) {
            Location loc = sender.getEyeLocation();
            RayTraceResult result = sender.getWorld().rayTraceEntities(loc,loc.getDirection(), 3.5, entity -> !(entity instanceof Player));
            if (result == null || result.getHitEntity() == null) {
                sender.sendMessage(LangFormatter.YAMLReplaceMessage("empty-lookat"));
                return;
            }
            targetEntity.getActionPack().setLookingAtEntity(result.getHitEntity().getUniqueId());
        }
        targetEntity.getActionPack().start(type, actionMode);
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
            return 0;
        }
        boolean self = context.getInput().split(" ")[2].equals(CommandBuilder.SELF_INDICATION); // doll <action> <target> ...
        this.sender = playerSender;
        if (target == null) {
            if (self) {
                targetEntity = DollManager.ONLINE_PLAYERS.get(playerSender.getUniqueId());
            } else {
                playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
                return 0;
            }
        } else {
            targetEntity = DollManager.ONLINE_DOLLS.get(target.getUniqueId());
        }
        if (targetEntity == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }

        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        // Converted Player does not have config
        if (targetEntity.isDoll()) {
            if (!outputHasPerm(playerSender, DollConfig.getOnlineDollConfig(target.getUniqueId()), flagType)) {
                return 0;
            }
        }

        execute();
        return 1;
    }
}
