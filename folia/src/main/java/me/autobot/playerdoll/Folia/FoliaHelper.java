package me.autobot.playerdoll.Folia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class FoliaHelper {
    public static final Class<?> FOLIA_REGIONIZED_SERVER;
    public static final Object REGOINIZED_SERVER;
    private final Plugin plugin;
    static {
        try {
            FOLIA_REGIONIZED_SERVER = Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            REGOINIZED_SERVER = FOLIA_REGIONIZED_SERVER.getMethod("getInstance").invoke(null);
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
    public void globalTask(Runnable task) {
        Bukkit.getServer().getGlobalRegionScheduler().execute(plugin, task);
    }
    public void globalTaskDelayed(Runnable task,long delay) {
        Bukkit.getServer().getGlobalRegionScheduler().runDelayed(plugin, (t)->task.run(),delay);
    }
    public void regionTask(Location location, Runnable task) {
        Bukkit.getServer().getRegionScheduler().execute(plugin, location, task);
    }
    public void entityTeleportTo(Entity entity, Location location) {
        entityTask(entity, () -> entity.teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN), 10);
    }
    public void setDollLookAt(Entity entity, Runnable task) {
        entityTask(entity, task, 2);
    }

    public int getTick() {
        return Bukkit.getServer().getCurrentTick();
    }
}
