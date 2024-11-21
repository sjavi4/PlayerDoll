package me.autobot.playerdoll.api.command.subcommand;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollNameUtil;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public abstract class SubCommand {
    protected final Player target;
    protected final String targetString;
    public SubCommand(Player target) {
        this.target = target;
        this.targetString = null;
    }
    public SubCommand(String targetString) {
        this.target = null;
        String unquoted = targetString.replaceAll("^\"|\"$", "");
        this.targetString = DollNameUtil.dollShortName(unquoted);
    }

    public abstract void execute();

    protected boolean fromManageCommand(String input) {
        return input.split(" ")[0].endsWith("dollmanage");
    }
    protected boolean outputValidProfile(CommandSender sender, GameProfile profile) {
        //if (PlayerDoll.BUNGEECORD) {
        //    PlayerDollAPI.getLogger().warning("This command is not yet tested in BungeeCord");
        //}
        if (!validProfile(profile)) {
            ComponentBuilder builder = new ComponentBuilder();
            builder.color(ChatColor.RED).append(new TranslatableComponent("argument.player.unknown"));
            sender.spigot().sendMessage(builder.create());
            return false;
        }
        return true;
    }
    protected boolean validProfile(GameProfile profile) {
        // Already processed by brigadier
        if (Bukkit.getOnlineMode()) {
            return true;
        }
        // If offlinePlayer is null, the player is never joined before;
        return Bukkit.getOfflinePlayer(profile.getId()).getName() != null;
    }
    protected boolean executeIfManage(String input) {
        if (fromManageCommand(input)) {
            execute();
            return true;
        }
        return false;
    }

    protected boolean outputHasPerm(CommandSender sender, DollConfig config, PersonalFlagButton flagType) {
        if (!hasDollPermission(sender, config, flagType)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-permission", flagType.registerName(), config.dollName.getValue()));
            return false;
        }
        return true;
    }
    public static boolean hasDollPermission(CommandSender sender, DollConfig config, PersonalFlagButton flagType) {
        // Owner check
        if (isOwnerOrOp(sender, config)) {
            return true;
        }
        if (!(sender instanceof Player player)) {
            return true;
        }
        UUID senderUUID = player.getUniqueId();
        boolean generalAdmin = config.generalSetting.get(PersonalFlagButton.ADMIN);
        boolean hasPSet = config.playerSetting.containsKey(senderUUID);
        // check player has been PSet from the doll
        if (!hasPSet) {
            // if general Admin is true, pass the check
            // else check the actual general Flag
            return generalAdmin || config.generalSetting.get(flagType);
        }
        Map<PersonalFlagButton, Boolean> pSetMap = config.playerSetting.get(senderUUID);
        boolean personalAdmin = pSetMap.get(PersonalFlagButton.ADMIN);

        return personalAdmin || pSetMap.get(flagType);

    }
    public static boolean isOwnerOrOp(CommandSender sender, DollConfig config) {
        if (sender instanceof Player player) {
            UUID senderUUID = player.getUniqueId();
            // Owner check
            return sender.isOp() || senderUUID.toString().equals(config.ownerUUID.getValue());
        }
        // Non player command sender
        return true;
    }
}
