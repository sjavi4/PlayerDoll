package me.autobot.playerdoll.Util;

import org.bukkit.ChatColor;


public class LangFormatter {
    public static String[] splitter(String s) {
        return s.split("\\$n");
    }

    public static String YAMLReplaceMessage(String path) {
        return YAMLFormat("Message." + path, null);
    }
    public static String YAMLReplaceMessage(String path, String... variables) {
        return YAMLFormat("Message." + path, variables);
    }
    @SafeVarargs
    public static <T> String YAMLReplaceMessage(String path, T... variables) {
        return YAMLFormat("Message." + path, variables);
    }

    public static String YAMLReplace(String path) {
        return YAMLFormat(path, null);
    }

    public static String YAMLReplace(String path, String... variables) {
        return YAMLFormat(path, variables);
    }

    @SafeVarargs
    public static <T> String YAMLReplace(String path, T... variables) {
        return YAMLFormat(path, variables);
    }

    @SafeVarargs
    private static <T> String YAMLFormat(String path, T... variables) {
        var config = ConfigLoader.get().getConfig(ConfigLoader.ConfigType.CUSTOM_LANGUAGE);
        if (config == null) return "CONFIG_NOT_FOUND";
        String str = config.getString(path);
        if (str == null) return path;
        if (variables != null) {
            for (T value : variables) {
                if (value instanceof String string) {
                    str = str.replaceFirst("%[a-zA-z]%", string);
                } else {
                    str = str.replaceFirst("%[a-zA-z]%", String.valueOf(value));
                }
            }
        }
        if (path.startsWith("Message")) str = config.getString("MessagePrefix") + " " + str;
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
