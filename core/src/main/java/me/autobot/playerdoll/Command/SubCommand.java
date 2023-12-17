package me.autobot.playerdoll.Command;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.DollConfigManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public abstract class SubCommand {
    public enum MinPermission {
        Player {
            @Override
            boolean canExecute(Player sender, String dollName, String command) {
                if (command.equalsIgnoreCase("Create")) return true;
                if (Owner.canExecute(sender,dollName,command)) {
                    return true;
                } else {
                    YamlConfiguration config = getConfig(dollName);
                    if (config == null) return false;
                    if (config.getBoolean("generalSetting.Admin")) return true;
                    return config.getBoolean("generalSetting." + command);
                }
            }
        }, Share {
            @Override
            boolean canExecute(Player sender, String dollName, String command) {
                if (Owner.canExecute(sender,dollName,command)) {
                    return true;
                } else {
                    YamlConfiguration config = getConfig(dollName);
                    if (config == null) return false;
                    if (config.getBoolean("playerSetting." + sender.getUniqueId() + ".Admin")) return true;
                    if (config.contains("playerSetting." + sender.getUniqueId() + "." + command)) {
                        return config.getBoolean("playerSetting." + sender.getUniqueId() + "." + command);
                    } else {
                        return Player.canExecute(sender,dollName,command);
                    }
                }
            }
        }, Owner {
            @Override
            boolean canExecute(Player sender, String dollName, String command) {
                if (Admin.canExecute(sender,dollName,command)) {
                    return true;
                } else {
                    YamlConfiguration config = getConfig(dollName);
                    if (config == null) return false;
                    return config.getString("Owner.UUID").equalsIgnoreCase(sender.getUniqueId().toString());
                }
            }
        }, Admin {
            @Override
            boolean canExecute(Player sender, String dollName, String command) {
                return sender.isOp();
            }
        };
        abstract boolean canExecute(Player sender, String dollName, String command);
        YamlConfiguration getConfig(String dollName) {
            if (PlayerDoll.dollManagerMap.containsKey(dollName)) {
                return DollConfigManager.getConfigManager(dollName).config;
            }
            YAMLManager config = YAMLManager.loadConfig(dollName, false);
            if (config == null) return null;
            return config.getConfig();
        }
    }
    private MinPermission permission;
    private boolean allowConsole;

    public void setPermission(MinPermission permission , boolean allowConsole) {
        this.permission = permission;
        this.allowConsole = allowConsole;
    }
    public boolean checkPermission(CommandSender sender, String dollName, String command) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        boolean pass = permission.canExecute(player,dollName,command) || allowConsole;
        if (!pass && player != null) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerNoPermission",'&'));
        }
        return pass;
    }

    public String checkDollName(Object dollName) {
        String name = (String) dollName;
        String identifier = PlayerDoll.dollIdentifier;
        if (!name.startsWith(identifier)) {
            return identifier + name;
        }
        return name;
    }

    public abstract void execute();
    public abstract ArrayList<String> targetSelection(UUID uuid);
    public abstract List<Object> tabSuggestion();

    public static class ActionHandler {
        public static EntityPlayerActionPack.Action action(String actionType, ArrayList<Integer> args) {
            return switch (actionType) {
                case "continuous" -> EntityPlayerActionPack.Action.continuous();
                case "interval" -> {
                    if (args.size() >= 2) yield EntityPlayerActionPack.Action.interval(args.get(0),args.get(1));
                    else if (args.size() == 1) yield EntityPlayerActionPack.Action.interval(args.get(0));
                    else yield EntityPlayerActionPack.Action.once();
                }
                default -> EntityPlayerActionPack.Action.once();
            };
        }
    }

    public static ArrayList<String> getOnlineDoll() {
        return new ArrayList<>() {{addAll(PlayerDoll.dollManagerMap.keySet().stream().map(s -> s.substring(1)).toList());}};
    }
    public static ArrayList<String> getAllDoll() {
        ArrayList<String> list = new ArrayList<>();
        File[] dollFiles = new File(PlayerDoll.getDollDirectory()).listFiles();
        if (dollFiles != null) {
            for (File file : dollFiles) {
                String f = file.getName();
                list.add(f.substring(1, f.lastIndexOf("."))); // remove -
            }
        }
        return list;
    }
    public static ArrayList<String> getOwnedDoll(UUID uuid) {
        ArrayList<String> list = new ArrayList<>();
        File[] dollFiles = new File(PlayerDoll.getDollDirectory()).listFiles();
        if (dollFiles != null) {
            for (File file : dollFiles) {
                String f = file.getName();
                String stripedDollName = f.substring(1, f.lastIndexOf("."));
                try {
                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.equalsIgnoreCase("Owner:")) {
                            line = scanner.nextLine();
                            if (line.startsWith("Name: ", 2)) line = scanner.nextLine();
                            if (line.startsWith("UUID: ", 2)) {
                                String owner = line.split(": ")[1];
                                if (uuid.toString().equalsIgnoreCase(owner)) {
                                    list.add(stripedDollName); //remove -
                                }
                                break;
                            }
                        }
                    }
                } catch (FileNotFoundException ignored) {
                }
            }
        }
        return list;
    }
    public static ArrayList<String> getSharedDoll(UUID uuid) {
        ArrayList<String> list = new ArrayList<>();
        /*
        File[] dollFiles = new File(PlayerDoll.getDollDirectory()).listFiles();
        if (dollFiles != null) {
            for (File file : dollFiles) {
                String f = file.getName();
                String stripedDollName = f.substring(1, f.lastIndexOf("."));
                try {
                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.equalsIgnoreCase("Share:")) {
                            while (scanner.hasNextLine()) {
                                line = scanner.nextLine();
                                if (line.startsWith("- ")) {
                                    String share = line.split(" ")[1];
                                    if (uuid.toString().equalsIgnoreCase(share)) {
                                        list.add(stripedDollName); //remove -
                                    }
                                    break;
                                }
                            }
                        }
                    }
                } catch (FileNotFoundException ignored) {
                }
            }
        }

         */
        return list;
    }
}
