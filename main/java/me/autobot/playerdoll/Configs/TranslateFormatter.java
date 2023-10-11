package me.autobot.playerdoll.Configs;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;

public class TranslateFormatter {
    public static FileConfiguration getDollConfig(String dollName) {

        return YAMLManager.getConfig(dollName);
        //File file = new File(PlayerDoll.getPlugin().getDataFolder() + File.separator  +"doll", PlayerDoll.dollManagerMap.get(dollName).getDollName() +".yml");
        //return file.exists() ? YamlConfiguration.loadConfiguration(file) : null;
    }
    public static String stringConvert(String yaml, char colorCode) {
        return process(yaml, colorCode, null, null, null,null,null,null);
    }
    public static String stringConvert(String yaml, char colorCode, CharSequence replace1, CharSequence var1) {
        return process(yaml, colorCode, replace1, var1, null,null,null,null);
    }
    public static String stringConvert(String yaml, char colorCode, CharSequence replace1, CharSequence var1, CharSequence replace2, CharSequence var2) {
        return process(yaml, colorCode, replace1, var1, replace2,var2,null,null);
    }

    public static String stringConvert(String yaml, char colorCode, CharSequence replace1, CharSequence var1, CharSequence replace2, CharSequence var2, CharSequence replace3, CharSequence var3) {
        return process(yaml, colorCode, replace1, var1, replace2,var2,replace3,var3);
    }

    private static String process(String yaml, char colorCode, CharSequence replace1, CharSequence var1, @Nullable CharSequence replace2, @Nullable CharSequence var2, @Nullable CharSequence replace3, @Nullable CharSequence var3) {
        Object a = YAMLManager.getConfig("lang").get(yaml);
        if (a == null) {
            return "NOTFOUND";
        }
        String b = a.toString();
        CharSequence[] c = {replace1,replace2,replace3};
        CharSequence[] d = {var1,var2,var3};
        for (int i = 0; i < c.length; i++) {
            if (c[i] == null) {break;}
            b = b.replace(c[i],d[i]);
        }

        return ChatColor.translateAlternateColorCodes(colorCode, b);
    }
    public static String stringTranslate(String string, char colorCode) {
        return process2(string, colorCode, null, null);
    }
    public static String stringTranslate(String string, char colorCode, CharSequence replace, CharSequence var) {
        return process2(string, colorCode, replace, var);
    }
    private static String process2(String string, char colorCode, @Nullable CharSequence replace, @Nullable CharSequence var) {
        if (string == null) {
            return "NOTFOUND";
        }
        if (replace != null && var != null) {
            string = string.replace(replace,var);
        }
        return ChatColor.translateAlternateColorCodes(colorCode, string);
    }
}
