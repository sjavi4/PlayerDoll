package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public class FoliaDollHelper {
    public static Class<?> regionizedServerFolia;
    public static Object regionizedServer;
    public FoliaDollHelper() {
        try {
            regionizedServerFolia = Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            regionizedServer = regionizedServerFolia.getMethod("getInstance").invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }
    }
    public final void handleAcceptedLogin(Object connection, Object serverPlayer, Object playerList, Object chunkSource, Runnable packetTask) {
        try {
            MutableObject<CompoundTag> data = new MutableObject<>();
            MutableObject<String> lastKnownName = new MutableObject<>();
            Class<?> completable = Class.forName("ca.spottedleaf.concurrentutil.completable.Completable");
            Object toComplete = completable.getConstructor().newInstance();

            TicketType<?> loginType = (TicketType<?>) TicketType.class.getField("LOGIN").get(null);
            Object taskQueue = regionizedServerFolia.getField("taskQueue").get(regionizedServer);
            Class<?> priorityClass = Class.forName("ca.spottedleaf.concurrentutil.executor.standard.PrioritisedExecutor$Priority");
            Object[] priority = priorityClass.getEnumConstants();

            Method qQ = taskQueue.getClass().getMethod("queueTickTaskQueue", ServerLevel.class, int.class, int.class, Runnable.class, priorityClass);
            Method placeNewPlayerFolia = playerList.getClass().getMethod("placeNewPlayer", Connection.class, ServerPlayer.class, CompoundTag.class, String.class, Location.class);

            BiConsumer<Location, Throwable> waiter = (loc, t) -> {
                int chunkX = loc.getX() < (double)(int)loc.getX() ? (int)loc.getX() -1 >> 4 : (int)loc.getX() >> 4;
                int chunkZ = loc.getZ() < (double)(int)loc.getZ() ? (int)loc.getZ() -1 >> 4 : (int)loc.getZ() >> 4;
                try {
                    Class<?> craftWorldClass = Class.forName("org.bukkit.craftbukkit."+ PlayerDoll.version +".CraftWorld");
                    ServerLevel serverLevel = (ServerLevel) loc.getWorld().getClass().asSubclass(craftWorldClass).getDeclaredMethod("getHandle").invoke(loc.getWorld());
                    Method addTicket = chunkSource.getClass().getMethod("addTicketAtLevel", TicketType.class, ChunkPos.class, int.class, Object.class);
                    addTicket.invoke(chunkSource,loginType, new ChunkPos(chunkX,chunkZ), 33, Unit.INSTANCE);

                    Runnable r = () -> {
                        try {
                            if (t == null) {
                                placeNewPlayerFolia.invoke(playerList, (Connection)connection, (ServerPlayer)serverPlayer, data.getValue(), lastKnownName.getValue(), loc);
                                packetTask.run();
                            }
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    };

                    qQ.invoke(taskQueue, serverLevel, chunkX, chunkZ, r, priority[2]);
                } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            };


            toComplete.getClass().getMethod("addWaiter", BiConsumer.class).invoke(toComplete,waiter);

            Method loadSpawnForNewPlayerFolia = playerList.getClass().getMethod("loadSpawnForNewPlayer", Connection.class, ServerPlayer.class, MutableObject.class, MutableObject.class, completable);
            loadSpawnForNewPlayerFolia.invoke(playerList, (Connection)connection, (ServerPlayer)serverPlayer, data, lastKnownName, toComplete);


        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException | InstantiationException | NoSuchFieldException e) {
            System.out.println("Exception while invoking Folia's PlaceNewPlayer");
            e.printStackTrace();
        }
    }
    public final void teleportTo(Player player, Location location) {
        FoliaSupport.entityTask(player,()->{
            try {
                Method teleportAsync = player.getClass().getMethod("teleportAsync", Location.class, PlayerTeleportEvent.TeleportCause.class);
                teleportAsync.invoke(player,location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                System.out.println("Error while TeleportAsync.");
            }},20);
    }
    public final void setDollLookAt(int tickoffset, Runnable task) {
        try {
            regionizedServerFolia.getMethod("addTask", Runnable.class).invoke(regionizedServer, new TickTask(Math.toIntExact(getCurrentTick())+tickoffset, task));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public final long getCurrentTick() {
        try {
            return (long) regionizedServerFolia.getMethod("getCurrentTick").invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            return 0;
        }
    }
}
