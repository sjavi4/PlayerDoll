package me.autobot.playerdoll.Dolls;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class DollHelper {

    public static void registerDollEvent(String version) {
        if (version.matches("v1_20_R3|v1_20_R4")) {
            version = "v1_20_R4";
        }
        try {
            //Trigger Static block to initialize itself
            Class.forName("me.autobot.playerdoll." + version + ".Event.SettingChangeEvent");
            //Class.forName("me.autobot.playerdoll." + version + ".Event.TransformEvent");
            Class.forName("me.autobot.playerdoll." + version + ".Network.ConnectionManager");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }
    public static Object callSpawn(Player player, String name, UUID configUUID , String version) {
        if (version.matches("v1_20_R3|v1_20_R4")) {
            version = "v1_20_R4";
        }
        try {
            Class<?> abstractDoll = Class.forName("me.autobot.playerdoll." + version + ".Network.CursedConnections");
            return abstractDoll.getMethod("doConnection", Player.class, String.class, UUID.class).invoke(null, player, name, configUUID);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException |
                 NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
            /*
            try {
                Class<?> abstractDoll = Class.forName("me.autobot.playerdoll." + version + ".Dolls.AbstractDoll");
                return abstractDoll.getMethod("callSpawn", Object.class, String.class, UUID.class).invoke(null, serverPlayer, name, configUUID);
            } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException |
                     NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }

             */
    }
}
