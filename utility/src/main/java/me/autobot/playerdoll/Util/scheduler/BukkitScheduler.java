package me.autobot.playerdoll.Util.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class BukkitScheduler {

    public final Plugin plugin;
    public BukkitScheduler(Plugin plugin) {
        this.plugin = plugin;
    }
    public void globalTask(Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }
    public void globalTaskDelayed(Runnable task, long delay) {
        Bukkit.getScheduler().runTaskLater(plugin, task, delay);
    }

    public void entityTask(Entity entity, Runnable task, long delay) {
        globalTaskDelayed(task, delay);
    }
}
