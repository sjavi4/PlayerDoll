package me.autobot.playerdoll;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

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

    public static void globalTaskDelayed(Runnable task, long tick) {
        if (PlayerDoll.isFolia) {
            try {
                Object globalRegionScheduler = Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler").invoke(Bukkit.getServer());
                Method execute = globalRegionScheduler.getClass().getMethod("runDelayed", Plugin.class, Consumer.class, long.class);
                Consumer<?> consumer = (t) -> task.run();
                execute.invoke(globalRegionScheduler, PlayerDoll.getPlugin(),consumer, tick);
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

    public static void entityTask(Entity entity, Runnable task, long delay) {
        if (PlayerDoll.isFolia) {
            try {
                Object entityScheduler = entity.getClass().getMethod("getScheduler").invoke(entity);
                Method execute = entityScheduler.getClass().getMethod("execute", Plugin.class, Runnable.class, Runnable.class , long.class);
                execute.invoke(entityScheduler, PlayerDoll.getPlugin(), task, null, delay);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException ignored) {
                throw new RuntimeException(ignored);
            }
        }
    }
}
