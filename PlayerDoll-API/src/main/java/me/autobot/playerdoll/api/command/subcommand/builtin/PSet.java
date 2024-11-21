package me.autobot.playerdoll.api.command.subcommand.builtin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.FileUtil;
import me.autobot.playerdoll.api.LangFormatter;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.command.DollCommandExecutor;
import me.autobot.playerdoll.api.command.subcommand.SubCommand;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.doll.DollNameUtil;
import me.autobot.playerdoll.api.doll.DollStorage;
import me.autobot.playerdoll.api.inv.DollMenuHolder;
import me.autobot.playerdoll.api.inv.button.InvButton;
import me.autobot.playerdoll.api.inv.button.PersonalFlagButton;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class PSet extends SubCommand implements DollCommandExecutor {
    private CommandSender sender;
    private final GameProfile profile;
    private final PersonalFlagButton flagType;
    private final boolean toggle;
    public PSet(Player target, Collection<GameProfile> profiles) {
        super(target);
        profile = profiles.stream().findFirst().orElseThrow();
        flagType = null;
        toggle = false;
    }
    public PSet(Player target, Collection<GameProfile> profiles, PersonalFlagButton flagType, boolean toggle) {
        super(target);
        profile = profiles.stream().findFirst().orElseThrow();
        this.flagType = flagType;
        this.toggle = toggle;
    }

    @Override
    public void execute() {
        if (flagType == null) {
            openGUI();
            return;
        }
        DollConfig config = DollConfig.getOnlineConfig(target.getUniqueId());
        Map<PersonalFlagButton, Boolean> flagMap = config.playerSetting.get(profile.getId());
        if (flagMap == null) {
            // Setup all default value for new player
            LinkedHashMap<PersonalFlagButton, Boolean> defaultMap = new LinkedHashMap<>();
            var list = InvButton.getButtons().values().stream().filter(b -> b instanceof PersonalFlagButton).map(b -> (PersonalFlagButton)b).toList();
            list.forEach(b -> defaultMap.put(b, false));
            config.playerSetting.put(profile.getId(), defaultMap);

            DollMenuHolder.HOLDERS.get(target.getUniqueId()).getPSetMenu(Bukkit.getOfflinePlayer(profile.getId()));
            flagMap = config.playerSetting.get(profile.getId());
        }
        flagMap.put(flagType, toggle);

        if (flagType == PersonalFlagButton.HIDDEN) {
            Player psetPlayer = Bukkit.getPlayer(profile.getId());
            if (psetPlayer == null) {
                return;
            }
            if (!psetPlayer.isOp() || (psetPlayer.isOp() && !PlayerDollAPI.getConfigLoader().getBasicConfig().opCanSeeHiddenDoll.getValue())) {
                if (toggle) {
                    psetPlayer.hidePlayer(PlayerDollAPI.getInstance(), target);
                } else {
                    psetPlayer.showPlayer(PlayerDollAPI.getInstance(), target);
                }
            }
        }
    }

    private void openGUI() {
        if (sender instanceof Player player) {
            player.openInventory(DollMenuHolder.HOLDERS.get(target.getUniqueId()).getPSetMenu(Bukkit.getOfflinePlayer(profile.getId())).getInventory());
        }
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
//        if (!(sender instanceof Player playerSender)) {
//            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
//            return 0;
//        }
        this.sender = sender;
        if (target == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        BaseEntity targetEntity = DollStorage.ONLINE_DOLLS.get(target.getUniqueId());
        if (targetEntity == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        String[] splitInput = context.getInput().split(" ");
        if (splitInput.length == 4) {
            // Player have skin permission & inputted skin
            if (splitInput[3].startsWith("@")) { // doll pset <name> <player (profile)>
                sender.sendMessage(LangFormatter.YAMLReplaceMessage("multi-select"));
                return 0;
            }
        }
        if (!outputValidProfile(sender, profile)) {
            return 0;
        }
        // Not allow Doll pset
        FileUtil fileUtil = PlayerDollAPI.getFileUtil();
        if (fileUtil.getFile(fileUtil.getDollDir(), DollNameUtil.dollShortName(profile.getName()) + ".yml").exists()) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("doll-pset"));
            return 0;
        }

        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (!outputHasPerm(sender, DollConfig.getOnlineConfig(target.getUniqueId()), PersonalFlagButton.PSET)) {
            return 0;
        }

        execute();
        return 1;
    }
}
