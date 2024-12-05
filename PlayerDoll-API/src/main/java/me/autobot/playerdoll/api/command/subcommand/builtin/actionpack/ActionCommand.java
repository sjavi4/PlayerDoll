package me.autobot.playerdoll.api.command.subcommand.builtin.actionpack;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.action.Action;
import me.autobot.playerdoll.api.action.ActionTypeHelper;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class ActionCommand extends SubCommand implements DollCommandExecutor {

    private CommandSender sender;
    private final ActionTypeHelper.Defaults type;
    private final Action actionMode;
    private final PersonalFlagButton flagType;
    private BaseEntity targetEntity;
    public ActionCommand(Player target, ActionTypeHelper.Defaults type, Action actionMode, PersonalFlagButton flagType) {
        super(target);
        this.type = type;
        this.actionMode = actionMode;
        this.flagType = flagType;
    }

    @Override
    public void execute() {
        if (type == ActionTypeHelper.Defaults.JUMP && targetEntity.isPlayer()) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("self-jump"));
            return;
        }
        if (type == ActionTypeHelper.Defaults.LOOK_AT && sender instanceof Player player) {
            Location loc = player.getEyeLocation();
            RayTraceResult result = player.getWorld().rayTraceEntities(loc,loc.getDirection(), 3.5, entity -> !(entity instanceof Player));
            if (result == null || result.getHitEntity() == null) {
                sender.sendMessage(LangFormatter.YAMLReplaceMessage("empty-lookat"));
                return;
            }
            targetEntity.getActionPack().setLookingAtEntity(result.getHitEntity().getUniqueId());
        }
        targetEntity.getActionPack().start(type.get(), actionMode);
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
//        if (!(sender instanceof Player playerSender)) {
//            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
//            return 0;
//        }
        boolean self = context.getInput().split(" ")[2].equals(PlayerDollAPI.getCommandBuilder().getSelfIndicator()); // doll <action> <target> ...
        this.sender = sender;
        if (target == null) {
            if (self && sender instanceof Player playerSender) {
                targetEntity = DollStorage.ONLINE_TRANSFORMS.get(playerSender.getUniqueId());
            } else {
                sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
                return 0;
            }
        } else {
            targetEntity = DollStorage.ONLINE_DOLLS.get(target.getUniqueId());
        }
        if (targetEntity == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }

        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        // Converted Player does not have config
        if (targetEntity.isDoll()) {
            if (!outputHasPerm(sender, DollConfig.getOnlineConfig(target.getUniqueId()), flagType)) {
                return 0;
            }
        }

        execute();
        return 1;
    }
}
