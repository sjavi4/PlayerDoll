package me.autobot.playerdoll.api.scheduler;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface SchedulerAPI {
    void globalTask(Runnable r);
    void regionTask(Runnable r, Location l);
    void entityTask(Runnable r, Entity e);
    void globalTaskDelayed(Runnable r, long d);
    void regionTaskDelayed(Runnable r, Location l, long d);
    void entityTaskDelayed(Runnable r, Entity e, long d);
    void foliaTeleportAync(Entity entity, Location location);
}
