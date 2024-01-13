package me.autobot.playerdoll.Util;

import org.bukkit.ChatColor;

import javax.annotation.Nullable;

public class LangFormatter {
    public static String[] splitter(String s) {
        return s.split("\\$n");
    }

    public static String YAMLReplaceMessage(String path) {
        return YAMLFormat("Message." + path, null);
    }
    @SafeVarargs
    public static String YAMLReplaceMessage(String path, Pair<CharSequence,CharSequence>... variables) {
        return YAMLFormat("Message." + path, variables);
    }

    public static String YAMLReplace(String path) {
        return YAMLFormat(path, null);
    }
    @SafeVarargs
    public static String YAMLReplace(String path, Pair<CharSequence,CharSequence>... variables) {
        return YAMLFormat(path, variables);
    }


    @SafeVarargs
    private static String YAMLFormat(String path, @Nullable Pair<CharSequence,CharSequence>... variables) {
        var config = ConfigManager.getLanguage();
        if (config == null) return "LANGNOTFOUND";
        String str = config.getString(path);
        if (str == null) return path;
        if (variables != null) {
            for (Pair<CharSequence,CharSequence> pair : variables) {
                CharSequence a = pair.getA() == null ? "A NOTFOUND" : pair.getA();
                CharSequence b = pair.getB() == null ? "B NOTFOUND" : pair.getB();
                str = str.replace(a,b);
            }
        }
        if (path.startsWith("Message")) str = config.getString("MessagePrefix") + " " + str;
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
