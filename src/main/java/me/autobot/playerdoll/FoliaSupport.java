package me.autobot.playerdoll;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FoliaSupport {
    public static void globalTask(Runnable task) {
        if (PlayerDoll.isFolia) {
            try {
                Object globalRegionScheduler = Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler").invoke(Bukkit.getServer());
                Method execute = globalRegionScheduler.getClass().getMethod("execute", Plugin.class, Runnable.class);
                execute.invoke(globalRegionScheduler, PlayerDoll.getPlugin(),task);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException ignored) {
                throw new RuntimeException(ignored);
            }
        }
    }
    public static void regionTask(Location location, Runnable task) {
        if (PlayerDoll.isFolia) {
            try {
                Object regionScheduler = Bukkit.getServer().getClass().getMethod("getRegionScheduler").invoke(Bukkit.getServer());
                Method execute = regionScheduler.getClass().getMethod("execute", Plugin.class, Location.class, Runnable.class);
                execute.invoke(regionScheduler, PlayerDoll.getPlugin(), location, task);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException ignored) {
                throw new RuntimeException(ignored);
            }
        }
    }
}
