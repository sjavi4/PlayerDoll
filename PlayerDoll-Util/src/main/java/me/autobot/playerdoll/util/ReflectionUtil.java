package me.autobot.playerdoll.util;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class ReflectionUtil {
    private static final String CB_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();
    private static final String NMS_PACKAGE = "net.minecraft.server";

    private static Class<?> C_FOLIA_REGIONIZED_SERVER;
    private static Object FOLIA_REGOINIZED_SERVER;
    private static Method M_REGOINIZED_SERVER_ADDTASK;
    private static final Object DEDICATED_SERVER;
    private static final Class<?> C_CRAFT_SERVER = getCBClass("CraftServer");
    private static final Object CRAFT_SERVER;

    private static Method getBukkitPlayerMethod;
    public static String gameVersion;

    static {
        try {
            Server server = Bukkit.getServer();
            Field dedicatedServerField = server.getClass().getDeclaredField("console");
            dedicatedServerField.setAccessible(true);
            DEDICATED_SERVER = dedicatedServerField.get(server);

            CRAFT_SERVER = C_CRAFT_SERVER.cast(server);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invokeMethod(Method method, Object instance, Object... args) {
        try {
            return method.invoke(instance, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static Object getField(Field field, Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T invokeMethod(Class<T> clazz, Method method, Object instance, Object... args) {
        try {
            return clazz.cast(method.invoke(instance, args));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T getField(Class<T> clazz, Field field, Object instance) {
        try {
            return clazz.cast(field.get(instance));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static void setField(Field field, Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static Object newInstance(Constructor<?> constructor, Object... args) {
        try {
            return constructor.getDeclaringClass().cast(constructor.newInstance(args));
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean hasClass(String className) {
        return getClass(className) != null;
    }
    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    public static Class<?> getNMSClass(String shortName) {
        return getClass(NMS_PACKAGE + "." + shortName);
    }

    public static Class<?> getCBClass(String shortName) {
        return getClass(CB_PACKAGE + "." + shortName);
    }

    public static Object getDedicatedServerInstance() {
        return DEDICATED_SERVER;
    }
    public static Class<?> getDedicatedServerClass() {
        return DEDICATED_SERVER.getClass();
    }

    public static void initialFoliaRegionizedServer() {
        C_FOLIA_REGIONIZED_SERVER = getClass("io.papermc.paper.threadedregions.RegionizedServer");
        if (C_FOLIA_REGIONIZED_SERVER == null) {
            throw new UnsupportedOperationException("Not Running on Folia Server");
        }
        Method getRegionizedServerMethod = Arrays.stream(C_FOLIA_REGIONIZED_SERVER.getMethods())
                .filter(method -> method.getReturnType() == C_FOLIA_REGIONIZED_SERVER && method.getName().equals("getInstance"))
                .findFirst()
                .orElseThrow();

        M_REGOINIZED_SERVER_ADDTASK = Arrays.stream(C_FOLIA_REGIONIZED_SERVER.getMethods())
                .filter(method -> method.getReturnType() == void.class && method.getParameterCount() == 1 && method.getName().equals("addTask"))
                .findFirst()
                .orElseThrow();
        try {
            FOLIA_REGOINIZED_SERVER = getRegionizedServerMethod.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getCraftServerClass() {
        return C_CRAFT_SERVER;
    }
    public static Object getCraftServerInstance() {
        return CRAFT_SERVER;
    }
    public static Object getFoliaRegoinizedServerInstance() {
        return FOLIA_REGOINIZED_SERVER;
    }
    public static Class<?> getFoliaRegoinizedServerClass() {
        return C_FOLIA_REGIONIZED_SERVER;
    }

    public static Player getBukkitPlayer(Object nmsPlayer) {
        if (getBukkitPlayerMethod == null) {
            try {
                getBukkitPlayerMethod = nmsPlayer.getClass().getMethod("getBukkitEntity");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return invokeMethod(Player.class, getBukkitPlayerMethod, nmsPlayer);
    }

    public static Class<?> getPluginNMSClass(String className) {
        return getClass("me.autobot.playerdoll." + gameVersion + "." + className);
    }
    public static Class<?> getPluginClass(String className) {
        return getClass("me.autobot.playerdoll." + className);
    }
}
