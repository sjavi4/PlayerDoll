package me.autobot.playerdoll.folia;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class FoliaHelper {
    public static final Class<?> FOLIA_REGIONIZED_SERVER;
    public static final Object REGOINIZED_SERVER;
    private static final Method REGOINIZED_SERVER_ADDTASK;
    private final Plugin plugin;
    static {
        try {
            FOLIA_REGIONIZED_SERVER = Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            REGOINIZED_SERVER = FOLIA_REGIONIZED_SERVER.getMethod("getInstance").invoke(null);
            REGOINIZED_SERVER_ADDTASK = FOLIA_REGIONIZED_SERVER.getMethod("addTask", Runnable.class);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    public FoliaHelper(Plugin plugin) {
        this.plugin = plugin;
    }
    public void entityTask(Entity entity, Runnable task, long delay) {
        entity.getScheduler().execute(plugin, task, null, delay);
    }
    public void entityTeleportTo(Entity entity, Location location) {
        entityTask(entity, () -> entity.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN), 10);
    }
    public void setDollLookAt(Entity entity, Runnable task) {
        entityTask(entity, task, 2);
    }
    public void addTask(Runnable task) {
        try {
            REGOINIZED_SERVER_ADDTASK.invoke(REGOINIZED_SERVER, task);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public int getTick() {
        return Bukkit.getServer().getCurrentTick();
    }
}
