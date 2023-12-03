package me.autobot.playerdoll.newCommand;

import me.autobot.playerdoll.CarpetMod.IEntityPlayerActionPack;
import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.YAMLManager;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public abstract class SubCommand {

    public enum MinPermission {
        Player(0), Share(1), Owner(2), Admin(3);
        final int level;
        MinPermission(int level) {
            this.level = level;
        }
    }
    private MinPermission permission;
    private boolean allowConsole;

    public void setPermission(MinPermission permission , boolean allowConsole) {
        this.permission = permission;
        this.allowConsole = allowConsole;
    }
    public boolean checkPermission(CommandSender sender, String stripedDollName) {
        //get Doll config
        int level = -1;
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            var config = YAMLManager.loadConfig(stripedDollName,false);
            if (config == null) {
                return true;
            } else {
                var dollConfig = config.getConfig();
                if (player.isOp()) {
                    level = 3;
                } else if (dollConfig.getString("Owner.UUID").equalsIgnoreCase(player.getUniqueId().toString())) {
                    level = 2;
                } else if (dollConfig.getStringList("Share").contains(player.getUniqueId().toString())) {
                    level = 1;
                } else {
                    level = 0;
                }
            }
        }
        boolean pass = level >= permission.level || allowConsole;
        if (!pass && player != null) {
            player.sendMessage(LangFormatter.YAMLReplace("PlayerNoPermission",'&'));
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
        public static Object action(IEntityPlayerActionPack actionPack, String actionType, ArrayList<Integer> args) {
            return switch (actionType) {
                case "continuous" -> actionPack.Action_continuous();
                case "interval" -> {
                    if (args.size() >= 2) yield actionPack.Action_interval(args.get(0),args.get(1));
                    yield actionPack.Action_interval(args.get(0));
                }
                default -> actionPack.Action_once();
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
        return list;
    }
}
