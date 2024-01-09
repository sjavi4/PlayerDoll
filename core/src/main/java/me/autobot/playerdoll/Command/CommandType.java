package me.autobot.playerdoll.Command;

import me.autobot.playerdoll.Command.SubCommands.*;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.Util.PermissionManager;
import me.autobot.playerdoll.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import me.autobot.playerdoll.Util.Pair;


public enum CommandType {
    ATTACK(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder builder = SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
            CommandType.buildAction(builder);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Attack(sender, CommandType.getDollName(dollName,true), args);
        }

    }, COPY(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL).addChild(ArgumentType.ONLINE_DOLL);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Copy(sender,dollName,args);
        }
    }, CREATE(false, OnlineStatus.NOT_EXIST) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ANY).addChild(ArgumentType.ONLINE_PLAYER);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return onlineStatus.valid(dollName);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Create(sender, CommandType.getDollName(dollName,true), args);
        }
    }, DESPAWN(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Despawn(sender, CommandType.getDollName(dollName,true));
        }
    }, DISMOUNT(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Dismount(sender, CommandType.getDollName(dollName,true));
        }
    }, DROP(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder builder = SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL).addChild(ArgumentType.STACK);
            CommandType.buildAction(builder);
            builder.addChild(ArgumentType.INVENTORY_SLOT);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Drop(sender, CommandType.getDollName(dollName,true), args);
        }
    }, ECHEST(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Echest(sender, CommandType.getDollName(dollName,true));
        }
    }, EXP(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder builder = SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
            builder.addChild("all",true);
            builder.addChild(ArgumentType.POSITIVE_INTEGER);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Exp(sender, CommandType.getDollName(dollName,true), args);
        }
    }, GIVE(true, OnlineStatus.MUST_OFFLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ALL_DOLL).addChild(ArgumentType.ONLINE_PLAYER);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Give(sender, CommandType.getDollName(dollName,true), args);
        }
    }, GSET(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ALL_DOLL);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Gset(sender, CommandType.getDollName(dollName,true));
        }
    }, INFO(true, OnlineStatus.ONLINE_OFFLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ALL_DOLL);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Info(sender, CommandType.getDollName(dollName,true), args);
        }
    }, INV(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ALL_DOLL);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Inv(sender, CommandType.getDollName(dollName,true));
        }
    }, JUMP(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder builder = SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
            CommandType.buildAction(builder);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Jump(sender, CommandType.getDollName(dollName,true), args);
        }
    }, LOOK(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder builder = SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
            builder.addChild(ArgumentType.SIGNED_FLOAT).addChild(ArgumentType.SIGNED_FLOAT);
            builder.addChild(ArgumentType.DIRECTION);
            builder.addChild(ArgumentType.ONLINE_PLAYER);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Look(sender, CommandType.getDollName(dollName,true), args);
        }
    }, LOOKAT(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder builder = SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
            builder.addChild(ArgumentType.COORDINATE).addChild(ArgumentType.COORDINATE).addChild(ArgumentType.COORDINATE);
            builder.addChild(ArgumentType.ONLINE_PLAYER);
            builder = builder.addChild("target",true);
            CommandType.buildAction(builder);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Lookat(sender, CommandType.getDollName(dollName,true), args);
        }
    }, MENU(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Menu(sender, CommandType.getDollName(dollName,true));
        }
    }, MOUNT(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Mount(sender, CommandType.getDollName(dollName,true));
        }
    }, MOVE(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder builder = SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
            builder.addChild("forward",true);
            builder.addChild("backward",true);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Move(sender, CommandType.getDollName(dollName,true), args);
        }
    }, PSET(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ALL_DOLL).addChild(ArgumentType.ONLINE_PLAYER);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Pset(sender, CommandType.getDollName(dollName,true), args);
        }
    }, REMOVE(true, OnlineStatus.ONLINE_OFFLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ALL_DOLL);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return onlineStatus.valid(dollName); // checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Remove(sender, CommandType.getDollName(dollName,true));
        }
    }, RENAME(true, OnlineStatus.MUST_OFFLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ALL_DOLL).addChild(ArgumentType.ANY);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Rename(sender, CommandType.getDollName(dollName,true), args);
        }
    }, SET(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ALL_DOLL);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Set(sender, CommandType.getDollName(dollName,true));
        }
    }, SLOT(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder builder = SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
            builder.addChild(ArgumentType.HOTBAR_SLOT);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Slot(sender, CommandType.getDollName(dollName,true), args);
        }
    }, SNEAK(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL).addChild(ArgumentType.BOOLEAN);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Sneak(sender, CommandType.getDollName(dollName,true), args);
        }
    }, SPAWN(true, OnlineStatus.MUST_OFFLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ALL_DOLL).addChild(ArgumentType.ALIGN_IN_GRID);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Spawn(sender, CommandType.getDollName(dollName,true), args);
        }
    }, SPRINT(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL).addChild(ArgumentType.BOOLEAN);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Sprint(sender, CommandType.getDollName(dollName,true), args);
        }
    }, STOP(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder builder = SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
            builder.addChild("all",true);
            builder.addChild("movement",true);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Stop(sender, CommandType.getDollName(dollName,true), args);
        }
    }, STRAFE(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder builder = SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
            builder.addChild("left",true);
            builder.addChild("right",true);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Strafe(sender, CommandType.getDollName(dollName,true), args);
        }
    }, SWAP(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder builder = SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
            CommandType.buildAction(builder);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Swap(sender, CommandType.getDollName(dollName,true), args);
        }
    }, TP(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL).addChild(ArgumentType.ALIGN_IN_GRID);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Tp(sender, CommandType.getDollName(dollName,true), args);
        }
    }, TURN(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL).addChild(ArgumentType.PITCH_YAW).addChild(ArgumentType.PITCH_YAW);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Turn(sender, CommandType.getDollName(dollName,true), args);
        }
    }, USE(true, OnlineStatus.MUST_ONLINE) {
        @Override
        void buildSuggestion() {
            SuggestionBuilder builder = SuggestionBuilder.create(this).addChild(ArgumentType.ONLINE_DOLL);
            CommandType.buildAction(builder);
        }
        @Override
        boolean checkPermission(Player sender, String dollName) {
            return checkSenderPermission(sender, dollName, this);
        }

        @Override
        void execute(Player sender, String dollName, String[] args) {
            new Use(sender, CommandType.getDollName(dollName,true), args);
        }
    };

    public final boolean allowConsole;
    public final OnlineStatus onlineStatus;
    CommandType(boolean allowConsole, OnlineStatus onlineStatus) {
        this.allowConsole = allowConsole;
        this.onlineStatus = onlineStatus;
    }

    abstract void buildSuggestion();
    //abstract void execute();
    abstract boolean checkPermission(Player sender, String dollName);
    abstract void execute(Player sender, String dollName, String[] args);
    private static void buildAction(SuggestionBuilder builder) {
        builder.addChild("once", true);
        builder.addChild("continuous", true);
        builder.addChild("interval", true).addChild(ArgumentType.POSITIVE_INTEGER).addChild(ArgumentType.POSITIVE_INTEGER);
    }
    public static String getDollName(String dollName, boolean fullName) {
        if (fullName) {
            return dollName.startsWith("-") ? dollName : "-" + dollName;
        } else {
            return dollName.startsWith("-") ? dollName.substring(1) : dollName;
        }
    }
    private static boolean checkSenderPermission(Player sender, String dollName, CommandType commandType) {
        return commandType.checkSenderPermission(sender, dollName);
    }
    // Doll must exist when doing check.
    // Check action commands
    private boolean checkSenderPermission(Player sender, String dollName) {
        if (!this.onlineStatus.valid(dollName)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage(switch (this.onlineStatus) {
                case MUST_ONLINE -> "OnlineStatus.Must_Online";
                case MUST_OFFLINE -> "OnlineStatus.Must_Offline";
                case ONLINE_OFFLINE -> "OnlineStatus.Online_Offline";
                case NOT_EXIST -> "OnlineStatus.Not_Exist";
            }));
            return false;
        }
        String fullDollName = getDollName(dollName, true);
        String commandName = this.toString();
        String senderUUID = sender.getUniqueId().toString();
        YamlConfiguration dollConfig = null;
        if (PlayerDoll.dollManagerMap.containsKey(fullDollName)) {
            dollConfig = PlayerDoll.dollManagerMap.get(fullDollName).getConfigManager().config;
        } else {
            YAMLManager yamlManager = YAMLManager.loadConfig(fullDollName,false);
            if (yamlManager != null) {
                dollConfig = yamlManager.getConfig();
            }
        }
        if (dollConfig == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CannotObtainDollConfig"));
            return false;
        }
        PermissionManager permissionManager = PermissionManager.getInstance(dollConfig.getString("Owner.Perm"));
        if (permissionManager == null) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CannotObtainDollPermission"));
            return false;
        }

        // 1. check owner's permission group settings
        // 2. check is owner
        // 3. check has pset permission (has set before)
        // 4. check has gset permission (has set before)
        // 5. check permission group default permission toggle
        String CommandExecutorIsNotPermit = LangFormatter.YAMLReplaceMessage("CommandExecutorIsNotPermit");

        String playerSettingPath = "playerSetting." + senderUUID + "." + commandName;
        String generalSettingPath = "generalSetting." + commandName;
        if (!permissionManager.flagPersonalDisplays.get(commandName)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("CommandDisabledFromOwnerPermissionGroup",new Pair<>("%a%",commandName)));
            return false;
        } else if (dollConfig.getString("Owner.UUID", "").equals(senderUUID)) {
            return true;
        } else if (dollConfig.contains(playerSettingPath)) {
            if (!dollConfig.getBoolean(playerSettingPath)) {
                sender.sendMessage(CommandExecutorIsNotPermit);
                return false;
            }
            return true;
        } else if (dollConfig.contains(generalSettingPath)) {
            if (!dollConfig.getBoolean(generalSettingPath)) {
                sender.sendMessage(CommandExecutorIsNotPermit);
                return false;
            }
            return true;
        } else if (permissionManager.flagPersonalToggles.containsKey(commandName)) {
            if (!permissionManager.flagPersonalToggles.get(commandName)) {
                sender.sendMessage(CommandExecutorIsNotPermit);
                return false;
            }
            return true;
        }
        sender.sendMessage(CommandExecutorIsNotPermit);
        return false;
    }
    public static class FilterSuggestion {

    }
    
    private enum OnlineStatus {
        MUST_ONLINE {
            @Override
            boolean valid(String dollName) {
                String fullDollName = getDollName(dollName, true);
                return PlayerDoll.dollManagerMap.containsKey(fullDollName);
            }
        }, MUST_OFFLINE {
            @Override
            boolean valid(String dollName) {
                String fullDollName = getDollName(dollName, true);
                boolean exist = YAMLManager.loadConfig(fullDollName, false) != null;
                return !MUST_ONLINE.valid(dollName) && exist;
            }
        }, ONLINE_OFFLINE {
            @Override
            boolean valid(String dollName) {
                return MUST_ONLINE.valid(dollName) || MUST_OFFLINE.valid(dollName);
            }
        }, NOT_EXIST {
            @Override
            boolean valid(String dollName) {
                String fullDollName = getDollName(dollName, true);
                return YAMLManager.loadConfig(fullDollName, false) == null;
            }
        };
        
        abstract boolean valid(String dollName);
    }
}
