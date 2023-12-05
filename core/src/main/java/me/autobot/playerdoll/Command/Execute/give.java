package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

import java.util.*;

public class give extends SubCommand {
    Player player;
    Player target;
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
        target = Bukkit.getPlayer(strArgs[0]);
        if (target == null) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerNotExist",'&'));
            return;
        }
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollConfigNotExist()) return;
        if (validator.isDollBeingTarget(target)) return;
        if (validator.isOfflineOperationWhenDollOnline()) return;
        dollConfig = YAMLManager.loadConfig(dollName,false);
        if (dollConfig == null) return;
        var config = dollConfig.getConfig();
        if (validator.isExecutionWhenDollRemoved(config)) return;

        int max = ConfigManager.getConfig().getInt("Global.MaxDollPerPlayer");
        if (PlayerDoll.playerDollCountMap.get(target.getName()) >= max) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerCreateTooMuchDoll", '&', new Pair<>( "%a%", Integer.toString(max))));
            return;
        }
        execute();

    }
    @Override
    public void execute() {
        YamlConfiguration config = dollConfig.getConfig();
        config.set("Owner.Name",target.getName());
        config.set("Owner.UUID",target.getUniqueId().toString());
        dollConfig.saveConfig();
        player.sendMessage(LangFormatter.YAMLReplaceMessage("DollGiver",'&', new Pair<>("%a%", target.getName())));
        target.sendMessage(LangFormatter.YAMLReplaceMessage("DollGetter",'&', new Pair<>("%a%", player.getName())));
    }

    @Override
    public ArrayList<String> targetSelection(UUID uuid) {
        Set<String> set = new HashSet<>(getOwnedDoll(uuid));
        getOnlineDoll().forEach(set::remove);
        return new ArrayList<>(){{addAll(set);}};
    }

    @Override
    public List<Object> tabSuggestion() {
        return List.of(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
    }
}
