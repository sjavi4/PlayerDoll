package me.autobot.playerdoll.Command.Utils;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.Pair;
import me.autobot.playerdoll.Util.PermissionManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUpgrade implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player p)) return false;

        if (args == null || args.length == 0) {
            String playerPermission = PermissionManager.playerPermissionGroup.get(p);
            PermissionManager perm = PermissionManager.permissionGroupMap.get(playerPermission);

            ComponentBuilder confirmMessage = new ComponentBuilder();
            confirmMessage.append(LangFormatter.YAMLReplaceMessage("upgradeConfirm", new Pair<>("%a%", Double.toString(perm.costForUpgrade))));
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dollupgrade confirm");
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder().append("Click to Confirm").create());
            confirmMessage.event(clickEvent);
            confirmMessage.event(hoverEvent);
            p.spigot().sendMessage(confirmMessage.create());
            return true;
        }
        if (args[0].equalsIgnoreCase("confirm")) {
            PermissionManager permissionManager = PermissionManager.getInstance(p);
            if (permissionManager.nextGroup == null || permissionManager.nextGroup.isBlank()) {
                p.sendMessage(LangFormatter.YAMLReplaceMessage("noFurtherUpgrade"));
                return false;
            }

            boolean success = PlayerDoll.getVaultHelper().playerUpgrade(p);
            if (!success) {
                return true;
            }

            if (PermissionManager.upgrade(p)) {
                p.sendMessage(LangFormatter.YAMLReplaceMessage("upgradeSuccess"));
            } else {
                return true;
            }
            PlayerDoll.dollManagerMap.values().forEach(d -> {
                if (d.getOwner().getUniqueId() == p.getUniqueId()) {
                    d.getConfigManager().config.set("Owner.Perm", permissionManager.nextGroup);
                }
            });

            return true;

        }
        return true;
    }
}