package me.autobot.playerdoll;

import java.lang.reflect.InvocationTargetException;

public class DollSpawnHelper {
    public static Object callSpawn(Object serverPlayer, String name, String version) {
        try {
            Class<?> abstractDoll = Class.forName("me.autobot.playerdoll." + version + ".Dolls.AbstractDoll");
            return abstractDoll.getMethod("callSpawn", Object.class, String.class).invoke(null,serverPlayer,name);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
