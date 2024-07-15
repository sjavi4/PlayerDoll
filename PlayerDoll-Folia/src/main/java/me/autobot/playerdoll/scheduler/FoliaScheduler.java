package me.autobot.playerdoll.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class FoliaScheduler implements Scheduler {
    private final Plugin plugin;
    public FoliaScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void globalTask(Runnable r) {
        Bukkit.getServer().getGlobalRegionScheduler().execute(plugin, r);
    }

    @Override
    public void regionTask(Runnable r, Location l) {
        Bukkit.getServer().getRegionScheduler().execute(plugin, l, r);
    }

    @Override
    public void entityTask(Runnable r, Entity e) {
        e.getScheduler().execute(plugin, r, null, 1);
    }

    @Override
    public void globalTaskDelayed(Runnable r, long d) {
        Bukkit.getServer().getGlobalRegionScheduler().runDelayed(plugin, (c)->r.run(), checkDelay(d));
    }

    @Override
    public void regionTaskDelayed(Runnable r, Location l, long d) {
        Bukkit.getServer().getRegionScheduler().runDelayed(plugin, l, (c)->r.run(), checkDelay(d));
    }

    @Override
    public void entityTaskDelayed(Runnable r, Entity e, long d) {
        e.getScheduler().runDelayed(plugin, (c)->r.run(), null, checkDelay(d));
    }

    @Override
    public void foliaTeleportAync(Entity e, Location l) {
        entityTaskDelayed(() -> e.teleportAsync(l), e, 1);
    }

    private long checkDelay(long d) {
        return Math.max(d, 1);
    }
}
