package me.autobot.playerdoll.Command;


import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Util.PermissionManager;
import me.autobot.playerdoll.YAMLManager;
import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public abstract class SubCommand {
    public Player player;
    public Player dollPlayer;
    public IDoll doll;
    public EntityPlayerActionPack actionPack;
    public PermissionManager permissionManager;
    public DollConfigManager dollConfigManager;
    public YAMLManager dollYAML;
    public YamlConfiguration dollConfig;
    public DollDataValidator validator;
    private final String dollName;
    public SubCommand(Player sender, String dollName) {
        this.player = sender;
        this.dollName = dollName;
        this.doll = getOnlineDoll();

        if (this.doll == null) {
            dollConfig = getOfflineDollConfig();

        } else {
            this.actionPack = doll.getActionPack();
            this.dollPlayer = Bukkit.getPlayer(dollName);
            dollConfigManager = doll.getConfigManager();
            dollConfig = dollConfigManager.config;
            permissionManager = PermissionManager.getOfflinePlayerPermission(doll.getOwner());
        }
        if (dollConfig != null) {
            permissionManager = PermissionManager.getInstance(dollConfig.getString("Owner.Perm","default"));
        }
        if (sender != null) {
            validator = new DollDataValidator(sender, dollName);
        }
    }
    private IDoll getOnlineDoll() {
        return PlayerDoll.dollManagerMap.getOrDefault(dollName, null);
    }
    private YamlConfiguration getOfflineDollConfig() {
        dollYAML = YAMLManager.loadConfig(dollName,false);
        return dollYAML == null ? null : dollYAML.getConfig();
    }
    public void executeAction(String[] args, int startIndex, EntityPlayerActionPack.ActionType actionType, int permissionMin) {
        EntityPlayerActionPack.Action action = EntityPlayerActionPack.Action.once();
        ArgumentType argumentType = ArgumentType.POSITIVE_INTEGER;
        switch (args.length-startIndex) {
            case 1 -> {
                if (args[startIndex].equalsIgnoreCase("continuous")) {
                    action = EntityPlayerActionPack.Action.continuous();
                }
            }
            case 2 -> {
                if (args[startIndex].equalsIgnoreCase("interval")) {
                    String value1 = args[1+startIndex];
                    if (checkArgumentValid(argumentType,value1)) {
                        int integer1 = castArgument(value1, Integer.class);
                        action = EntityPlayerActionPack.Action.interval(Math.max(integer1, permissionMin));
                    }
                }
            }
            case 3 -> {
                String value1 = args[1+startIndex];
                String value2 = args[2+startIndex];
                if (checkArgumentValid(argumentType,value1) && checkArgumentValid(argumentType,value2)) {
                    int integer1 = castArgument(value1, Integer.class);
                    int integer2 = castArgument(value2, Integer.class);
                    action = EntityPlayerActionPack.Action.interval(Math.max(integer1, permissionMin),integer2);
                }
            }
        };
        actionPack.start(actionType,action);
    }
    public boolean checkArgumentValid(ArgumentType argumentType, String value) {
        return ArgumentType.checkArgumentValid(argumentType, value);
    }
    @SuppressWarnings("unchecked")
    public <T> T castArgument(String value, Class<T> type) {
        try {
            return (T) type.getDeclaredMethod("valueOf", String.class).invoke(null, value);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
