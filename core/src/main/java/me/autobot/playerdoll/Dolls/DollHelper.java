package me.autobot.playerdoll.Dolls;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class DollHelper {
    public static Object callSpawn(Object serverPlayer, String name, UUID configUUID , String version) {
        if (version.equalsIgnoreCase("v1_20_R4")) {
            version = "v1_20_R3";
        }
        try {
            Class<?> abstractDoll = Class.forName("me.autobot.playerdoll." + version + ".Dolls.AbstractDoll");
            return abstractDoll.getMethod("callSpawn", Object.class, String.class, UUID.class).invoke(null,serverPlayer,name, configUUID);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
