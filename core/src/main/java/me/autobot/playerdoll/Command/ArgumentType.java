package me.autobot.playerdoll.Command;

import me.autobot.playerdoll.YAMLManager;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public enum ArgumentType {
    ONLINE_DOLL {
        @Override
        List<String> get() {
            return PlayerDoll.dollManagerMap.keySet().stream().map(s -> s.substring(1)).toList();
        }
        @Override
        boolean argumentValid(String s) {
            return PlayerDoll.dollManagerMap.containsKey(CommandType.getDollName(s,true));
        }
    }, ALL_DOLL {
        @Override
        List<String> get() {
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
        @Override
        boolean argumentValid(String s) {
            return YAMLManager.loadConfig(CommandType.getDollName(s,true),false) != null;
        }
    }, ONLINE_PLAYER {
        @Override
        List<String> get() {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        @Override
        boolean argumentValid(String s) {
            return Bukkit.getPlayer(s) != null;
        }
    }, NONE {
        @Override
        List<String> get() {
            return Collections.singletonList("");
        }
        @Override
        boolean argumentValid(String s) {
            return false;
        }
    }, ANY {
        @Override
        List<String> get() {
            return Collections.singletonList("?");
        }
        @Override
        boolean argumentValid(String s) {
            return !s.isBlank();
        }
    }, BOOLEAN {
        @Override
        List<String> get() {
            return List.of("true","false");
        }
        @Override
        boolean argumentValid(String s) {
            return s.matches("true|false");
        }
    }, POSITIVE_INTEGER {
        @Override
        List<String> get() {
            return List.of("<POSITIVE_INTEGER>");
        }
        @Override
        boolean argumentValid(String s) {
            Integer i = isInteger(s);
            return i != null && i > 0;
        }
    }, SIGNED_FLOAT {
        @Override
        List<String> get() {
            return List.of("<SIGNED_FLOAT>");
        }
        @Override
        boolean argumentValid(String s) {
            return isFloat(s) != null;
        }
    }, COORDINATE {
        @Override
        List<String> get() {
            return List.of("<COORDINATE>");
        }
        @Override
        boolean argumentValid(String s) {
            return isFloat(s) != null;
        }
    }, PITCH_YAW {
        @Override
        List<String> get() {
            return List.of("<PITCH_YAW>");
        }
        @Override
        boolean argumentValid(String s) {
            return isFloat(s) != null;
        }
    }, ALIGN_IN_GRID {
        @Override
        List<String> get() {
            return Collections.singletonList("inGrid");
        }
        @Override
        boolean argumentValid(String s) {
            return s.equalsIgnoreCase("inGrid");
        }
    }, INVENTORY_SLOT {
        @Override
        List<String> get() {
            return List.of("<INV_SLOT>","helmet","chestplate","leggings","boots","offhand","everything");
        }
        @Override
        boolean argumentValid(String s) {
            Integer slot = isInteger(s);
            if (slot != null) {
                return slot >= 1 && slot <= 36;
            } else {
                return s.matches("helmet|chestplate|leggings|boots|offhand|everything");
            }
        }
    }, HOTBAR_SLOT {
        @Override
        List<String> get() {
            return List.of("1","2","3","4","5","6","7","8","9");
        }
        @Override
        boolean argumentValid(String s) {
            Integer slot = isInteger(s);
            return slot != null && slot >= 1 && slot <= 9;
        }
    }, STACK {
        @Override
        List<String> get() {
            return List.of("stack","single");
        }
        @Override
        boolean argumentValid(String s) {
            return s.matches("stack|single");
        }
    }, DIRECTION {
        @Override
        List<String> get() {
            return List.of("north", "east", "south", "west", "up", "down");
        }
        @Override
        boolean argumentValid(String s) {
            return s.matches("north|east|south|west|up|down");
        }
    };

    abstract boolean argumentValid(String s);
    abstract List<String> get();
    public static boolean checkArgumentValid(ArgumentType type, String value) {
        return type.argumentValid(value);
    }
    Integer isInteger(String s) {
        try {
            return Integer.parseInt(s);
        } catch(NumberFormatException | NullPointerException e) {
            return null;
        }
    }
    Float isFloat(String s) {
        try {
            return Float.parseFloat(s);
        } catch(NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    static List<String> union(ArgumentType A, ArgumentType B) {
        Set<String> set = new HashSet<>();
        set.addAll(A.get());
        set.addAll(B.get());
        return new ArrayList<>(set);
    };
    static List<String> intersection(ArgumentType A, ArgumentType B) {
        Set<String> set = new HashSet<>(A.get());
        set.retainAll(B.get());
        return new ArrayList<>(set);
    };

}
