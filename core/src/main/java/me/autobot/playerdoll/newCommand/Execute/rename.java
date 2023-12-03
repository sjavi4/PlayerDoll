package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.Helper.DollDataValidator;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class rename extends SubCommand {
    Player player;
    String dollName;
    File dollFile;
    String[] args;
    public rename() {}
    public rename(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Owner,false);
        dollName = checkDollName(doll);
        player = (Player) sender;
        if (!checkPermission(sender, dollName)) return;

        DollDataValidator oldNameValidator = new DollDataValidator(player, dollName, dollName.substring(1));

        if (oldNameValidator.isOfflineOperationWhenDollOnline()) return;
        dollFile = new File(PlayerDoll.getDollDirectory(),dollName+".yml");

        if (oldNameValidator.isDollConfigNotExist(dollFile)) return;

        /*
        if (!dollFile.exists()) {
            player.sendMessage(LangFormatter.YAMLReplace("DollNotExist",'&'));
            return;
        }

         */
        var yaml = YAMLManager.loadConfig(dollName,false);
        if (yaml == null) return;
        var config = yaml.getConfig();
        if (oldNameValidator.isExecutionWhenDollRemoved(config)) return;
        this.args = args == null? new String[]{""} : (String[]) args;
        this.args[0] = checkDollName(this.args[0]);
        DollDataValidator newNameValidator = new DollDataValidator(player, this.args[0], this.args[0].substring(1));

        if (newNameValidator.isDollNameIllegal()) return;
        if (newNameValidator.isDollNamePreserved()) return;
        if (newNameValidator.isDollNameTooLong()) return;

        if (new File(PlayerDoll.getDollDirectory(),this.args[0]+".yml").exists()) {
            player.sendMessage(LangFormatter.YAMLReplace("RepeatDollName",'&', new Pair<>("%a%",this.args[0])));
            return;
        }
        execute();
    }
    @Override
    public void execute() {
        var oldConfig = new File(PlayerDoll.getDollDirectory(), this.dollName+".yml");
        var newConfig = new File(PlayerDoll.getDollDirectory(), this.args[0]+".yml");
        if (oldConfig.exists() && !newConfig.exists()) {
            String oldUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.dollName).getBytes(StandardCharsets.UTF_8)).toString();
            String newUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.args[0]).getBytes(StandardCharsets.UTF_8)).toString();
            File dat = new File(Bukkit.getServer().getWorldContainer()+File.separator+"world"+File.separator+"playerdata"+File.separator+oldUUID+".dat");

            boolean flag1 = dat.renameTo(new File(dat.getParentFile(),newUUID+".dat"));
            boolean flag2 = dollFile.renameTo(new File(PlayerDoll.getDollDirectory(), this.args[0]+".yml"));
            if (flag1 && flag2) {
                var config = YAMLManager.loadConfig(newConfig.getName().substring(0, newConfig.getName().lastIndexOf(".")),false);
                config.getConfig().set("UUID", newUUID);
                config.saveConfig();
                player.sendMessage(LangFormatter.YAMLReplace("PlayerRenameSucceed",'&', new Pair<>("%a%",this.dollName), new Pair<>("%b%",this.args[0])));
            } else {
                player.sendMessage(LangFormatter.YAMLReplace("PlayerRenameFailed",'&'));
                if (!flag2) new File(dat.getParentFile(),newUUID+".dat").renameTo(dat);
                if (!flag1) new File(PlayerDoll.getDollDirectory(),this.args[0]+".yml").renameTo(dollFile);
            }
        } else {
            player.sendMessage(LangFormatter.YAMLReplace("PlayerRenameFailed",'&'));
        }
    }
    @Override
    public final ArrayList<String> targetSelection(UUID uuid) {
        Set<String> set = new HashSet<>();
        set.addAll(getOwnedDoll(uuid));
        set.addAll(getSharedDoll(uuid));
        set.retainAll(getOnlineDoll());
        return new ArrayList<>(){{addAll(set);}};
    }
    @Override
    public List<Object> tabSuggestion() {
        return List.of("<new_name>");
    }
}
