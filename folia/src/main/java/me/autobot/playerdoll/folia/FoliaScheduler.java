package me.autobot.playerdoll.folia;

import me.autobot.playerdoll.Util.scheduler.BukkitScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class FoliaScheduler extends BukkitScheduler {
    public FoliaScheduler(Plugin plugin) {
        super(plugin);
    }
    @Override
    public void globalTask(Runnable task) {
        Bukkit.getServer().getGlobalRegionScheduler().execute(plugin, task);
    }
    @Override
    public void globalTaskDelayed(Runnable task, long delay) {
        Bukkit.getServer().getGlobalRegionScheduler().runDelayed(plugin, (t)->task.run(),delay);
    }

    @Override
    public void entityTask(Entity entity, Runnable task, long delay) {
        entity.getScheduler().execute(plugin, task, null, delay);
    }
}
