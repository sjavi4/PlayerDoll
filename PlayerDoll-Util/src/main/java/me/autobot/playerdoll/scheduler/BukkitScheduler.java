package me.autobot.playerdoll.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class BukkitScheduler implements Scheduler {
    private final Plugin plugin;
    public BukkitScheduler(Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public void globalTask(Runnable r) {
        Bukkit.getScheduler().runTask(plugin, r);
    }

    @Override
    public void regionTask(Runnable r, Location l) {
        globalTask(r);
    }

    @Override
    public void entityTask(Runnable r, Entity e) {
        globalTask(r);
    }

    @Override
    public void globalTaskDelayed(Runnable r, long d) {
        Bukkit.getScheduler().runTaskLater(plugin, r, d);
    }

    @Override
    public void regionTaskDelayed(Runnable r, Location l, long d) {
        globalTaskDelayed(r, d);
    }

    @Override
    public void entityTaskDelayed(Runnable r, Entity e, long d) {
        globalTaskDelayed(r, d);
    }

    @Override
    public void foliaTeleportAync(Entity entity, Location location) {
        throw new UnsupportedOperationException("Must be Called at Folia Server");
    }
}
