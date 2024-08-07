package me.autobot.playerdoll.command.subcommand;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.gui.DollGUIHolder;
import me.autobot.playerdoll.util.FileUtil;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;

public class PSet extends SubCommand implements DollCommandExecutor {
    private Player sender;
    private final GameProfile profile;
    private final FlagConfig.PersonalFlagType flagType;
    private final boolean toggle;
    public PSet(Player target, Collection<GameProfile> profiles) {
        super(target);
        profile = profiles.stream().findFirst().orElseThrow();
        flagType = null;
        toggle = false;
    }
    public PSet(Player target, Collection<GameProfile> profiles, FlagConfig.PersonalFlagType flagType, boolean toggle) {
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
        DollConfig config = DollConfig.getOnlineDollConfig(target.getUniqueId());
        EnumMap<FlagConfig.PersonalFlagType, Boolean> flagMap = config.playerSetting.get(profile.getId());
        if (flagMap == null) {
            // Setup all default value for new player
            EnumMap<FlagConfig.PersonalFlagType, Boolean> enumMap = new EnumMap<>(FlagConfig.PersonalFlagType.class);
            Arrays.stream(FlagConfig.PersonalFlagType.values())
                    .forEach(personalFlagType -> enumMap.put(personalFlagType, false));
            config.playerSetting.put(profile.getId(), enumMap);

            DollGUIHolder.DOLL_GUI_HOLDERS.get(target.getUniqueId()).getPSetMenu(Bukkit.getOfflinePlayer(profile.getId()));
            flagMap = config.playerSetting.get(profile.getId());
        }
        flagMap.put(flagType, toggle);

        if (flagType == FlagConfig.PersonalFlagType.HIDDEN) {
            Player psetPlayer = Bukkit.getPlayer(profile.getId());
            if (psetPlayer == null) {
                return;
            }
            if (!psetPlayer.isOp() || (psetPlayer.isOp() && !BasicConfig.get().opCanSeeHiddenDoll.getValue())) {
                if (toggle) {
                    psetPlayer.hidePlayer(PlayerDoll.PLUGIN, target);
                } else {
                    psetPlayer.showPlayer(PlayerDoll.PLUGIN, target);
                }
            }
        }
    }

    private void openGUI() {
        sender.openInventory(DollGUIHolder.DOLL_GUI_HOLDERS.get(target.getUniqueId()).getPSetMenu(Bukkit.getOfflinePlayer(profile.getId())).getInventory());
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
        BaseEntity targetEntity = DollManager.ONLINE_DOLLS.get(target.getUniqueId());
        if (targetEntity == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        String[] splitInput = context.getInput().split(" ");
        if (splitInput.length == 4) {
            // Player have skin permission & inputted skin
            if (splitInput[3].startsWith("@")) { // doll pset <name> <player (profile)>
                playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("multi-select"));
                return 0;
            }
        }
        if (!outputValidProfile(playerSender, profile)) {
            return 0;
        }
        // Not allow Doll pset
        FileUtil fileUtil = FileUtil.INSTANCE;
        if (fileUtil.getFile(fileUtil.getDollDir(), DollManager.dollShortName(profile.getName()) + ".yml").exists()) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("doll-pset"));
            return 0;
        }

        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (!outputHasPerm(playerSender, DollConfig.getOnlineDollConfig(target.getUniqueId()), FlagConfig.PersonalFlagType.PSET)) {
            return 0;
        }

        execute();
        return 1;
    }
}
