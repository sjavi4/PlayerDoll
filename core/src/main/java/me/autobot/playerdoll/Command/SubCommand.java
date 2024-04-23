package me.autobot.playerdoll.Command;


import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Dolls.DollConfig;
import me.autobot.playerdoll.Dolls.DollManager;

import me.autobot.playerdoll.Dolls.IServerDoll;
import me.autobot.playerdoll.Util.Configs.BasicConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public abstract class SubCommand {
    public Player player;
    public Player dollPlayer;
    public IServerDoll doll;
    public EntityPlayerActionPack actionPack;
    //public DollConfigManager dollConfigManager;
    //public YAMLManager dollYAML;
    //public YamlConfiguration dollConfig;
    public DollConfig dollConfig;
    public DollDataValidator validator;
    public SubCommand(Player sender, String dollName) {
        this.player = sender;
        this.dollPlayer = Bukkit.getPlayer(dollName);

        if (DollManager.dollShortName(dollName).equals("!") && BasicConfig.get().convertPlayer.getValue()) {
            this.actionPack = DollManager.ONLINE_PLAYER_MAP.get(sender.getUniqueId()).getActionPack();
            return;
        }

        if (dollPlayer == null) {
            dollConfig = DollConfig.getOfflineDollConfig(dollName);
        } else {
            this.doll = DollManager.ONLINE_DOLL_MAP.get(dollPlayer.getUniqueId());
            dollConfig = DollConfig.getOnlineDollConfig(dollPlayer.getUniqueId());
            this.actionPack = doll.getActionPack();
            //dollConfigManager = doll.getConfigManager();
            //dollConfig = dollConfigManager.config;
            //permissionManager = PermissionManager.getPlayerPermission(doll.getOwner().getUniqueId());
        }
        /*
        if (dollConfig != null) {
            permissionManager = PermissionManager.getPermissionGroup(dollConfig.getString("Owner.Perm","default"));
        }

         */
        if (sender != null) {
            validator = new DollDataValidator(sender, dollName);
        }
    }
    /*
    private YamlConfiguration getOfflineDollConfig() {
        dollYAML = YAMLManager.loadConfig(dollName,false, true);
        return dollYAML == null ? null : dollYAML.getConfig();
    }

     */
    public void executeAction(String[] args, int startIndex, EntityPlayerActionPack.ActionType actionType) {
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
                        action = EntityPlayerActionPack.Action.interval(integer1);
                    }
                }
            }
            case 3 -> {
                String value1 = args[1+startIndex];
                String value2 = args[2+startIndex];
                if (checkArgumentValid(argumentType,value1) && checkArgumentValid(argumentType,value2)) {
                    int integer1 = castArgument(value1, Integer.class);
                    int integer2 = castArgument(value2, Integer.class);
                    action = EntityPlayerActionPack.Action.interval(integer1,integer2);
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
