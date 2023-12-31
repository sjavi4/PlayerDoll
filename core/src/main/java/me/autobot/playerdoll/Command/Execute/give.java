package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.PermissionManager;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

public class give extends SubCommand {
    Player player;
    OfflinePlayer target;
    YAMLManager dollConfig;
    String dollName;
    public give() {}
    public give(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Owner,false);
        dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName, "Give")) return;

        player = (Player) sender;
        if (args == null) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("CommandMissingTarget",'&'));
            return;
        }
        String[] strArgs = (String[]) args;
        target = Bukkit.getOfflinePlayer(strArgs[0]);
        PermissionManager perm = PermissionManager.getOfflinePlayerPermission(target);
        if (!target.hasPlayedBefore() || perm == null) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerNotExist",'&'));
            return;
        }
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollConfigNotExist()) return;
        //if (validator.isDollBeingTarget(target.getName().startsWith("-"))) return;
        if (validator.isOfflineOperationWhenDollOnline()) return;
        dollConfig = YAMLManager.loadConfig(dollName,false);
        if (dollConfig == null) return;
        var config = dollConfig.getConfig();
        if (validator.isExecutionWhenDollRemoved(config)) return;

        // TODO maxDollPerPlayer of target

        int max = perm.maxDollCreation;
        if (PlayerDoll.playerDollCountMap.get(target.getName()) >= max) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateTooMuchDoll", '&', new Pair<>( "%a%", Integer.toString(max))));
            return;
        }
        /*

        int max = ConfigManager.getConfig().getInt("Global.MaxDollPerPlayer");

        if (PlayerDoll.playerDollCountMap.get(target.getName()) >= max) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateTooMuchDoll", '&', new Pair<>( "%a%", Integer.toString(max))));
            return;

        }

         */
        execute();

    }
    @Override
    public void execute() {
        YamlConfiguration config = dollConfig.getConfig();
        config.set("Owner.Name",target.getName());
        config.set("Owner.UUID",target.getUniqueId().toString());
        dollConfig.saveConfig();
        player.sendMessage(LangFormatter.YAMLReplaceMessage("DollGiver",'&', new Pair<>("%a%", target.getName())));
        if (target.isOnline()) {
            ((Player)target).sendMessage(LangFormatter.YAMLReplaceMessage("DollGetter", '&', new Pair<>("%a%", player.getName())));
        }
    }

}
