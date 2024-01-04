package me.autobot.playerdoll.Dolls;

import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public class DollHelper {
    public static Object callSpawn(Object serverPlayer, String name, String version) {
        if (version.equalsIgnoreCase("v1_20_R4")) {
            version = "v1_20_R3";
        }
        try {
            Class<?> abstractDoll = Class.forName("me.autobot.playerdoll." + version + ".Dolls.AbstractDoll");
            return abstractDoll.getMethod("callSpawn", Object.class, String.class).invoke(null,serverPlayer,name);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void FoliaTeleportTo(Player player, Location location) {
        try {
            Method teleportAsync = player.getClass().getMethod("teleportAsync", Location.class, PlayerTeleportEvent.TeleportCause.class);
            teleportAsync.invoke(player,location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("Error while TeleportAsync.");
        }
    }
    public static void FoliaHandleAcceptedLogin(Class<?> compoundTag, Class<?> ticketType, Class<?> regionizedServerFolia, Class<?> _serverLevel, Class<?> connectionClass ,Object connection, Class<?> serverPlayerClass, Object serverPlayer, Object playerList, String version, Object chunkSource, Class<?> chunkPos, Object unit, Runnable packetTask) {
        try {
            MutableObject<Object> data = new MutableObject<>();
            MutableObject<String> lastKnownName = new MutableObject<>();
            Class<?> completable = Class.forName("ca.spottedleaf.concurrentutil.completable.Completable");
            Object toComplete = completable.getConstructor().newInstance();

            Object loginType = ticketType.getField("LOGIN").get(null);
            Object rServer = regionizedServerFolia.getMethod("getInstance").invoke(null);
            Object taskQueue = regionizedServerFolia.getField("taskQueue").get(rServer);
            Class<?> priorityClass = Class.forName("ca.spottedleaf.concurrentutil.executor.standard.PrioritisedExecutor$Priority");
            Object[] priority = priorityClass.getEnumConstants();

            Method qQ = taskQueue.getClass().getMethod("queueTickTaskQueue", _serverLevel, int.class, int.class, Runnable.class, priorityClass);
            Method placeNewPlayerFolia = playerList.getClass().getMethod("placeNewPlayer", connectionClass, serverPlayerClass, compoundTag, String.class, Location.class);

            BiConsumer<Location, Throwable> waiter = (loc, t) -> {
                int chunkX = loc.getX() < (double)(int)loc.getX() ? (int)loc.getX() -1 >> 4 : (int)loc.getX() >> 4;
                int chunkZ = loc.getZ() < (double)(int)loc.getZ() ? (int)loc.getZ() -1 >> 4 : (int)loc.getZ() >> 4;
                try {
                    Class<?> craftWorldClass = Class.forName("org.bukkit.craftbukkit."+ version +".CraftWorld");
                    Object serverLevel = loc.getWorld().getClass().asSubclass(craftWorldClass).getDeclaredMethod("getHandle").invoke(loc.getWorld());
                    Method addTicket = chunkSource.getClass().getMethod("addTicketAtLevel", ticketType, chunkPos, int.class, Object.class);
                    addTicket.invoke(chunkSource,loginType, chunkPos.getConstructor(int.class,int.class).newInstance(chunkX,chunkZ), 33, unit);

                    Runnable r = () -> {
                        try {
                            if (t == null) {
                                placeNewPlayerFolia.invoke(playerList, connection, serverPlayer, data.getValue(), lastKnownName.getValue(), loc);
                                packetTask.run();
                            }
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    };

                    qQ.invoke(taskQueue, serverLevel, chunkX, chunkZ, r, priority[2]);
                } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException |
                         NoSuchMethodException | InstantiationException e) {
                    e.printStackTrace();
                }
            };


            toComplete.getClass().getMethod("addWaiter", BiConsumer.class).invoke(toComplete,waiter);



            //Method addTicket = serverLevel().getChunkSource().getClass().getMethod("addTicketAtLevel", TicketType.class, ChunkPos.class, int.class, Object.class);


            Method loadSpawnForNewPlayerFolia = playerList.getClass().getMethod("loadSpawnForNewPlayer", connectionClass, serverPlayerClass, MutableObject.class, MutableObject.class, completable);
            loadSpawnForNewPlayerFolia.invoke(playerList, connection, serverPlayer, data, lastKnownName, toComplete);


        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException | InstantiationException | NoSuchFieldException e) {
            System.out.println("Exception while invoking Folia's PlaceNewPlayer");
            e.printStackTrace();
        }
    }
    public static void newTickTask(Class<?> regionizedServerFolia, Class<? extends Runnable> tickTaskClass, int tick, Runnable task) {
        try {
            Object rServer = regionizedServerFolia.getMethod("getInstance").invoke(null);
            regionizedServerFolia.getMethod("addTask", Runnable.class).invoke(rServer, tickTaskClass.getConstructor(int.class, Runnable.class).newInstance(tick, task));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
    public static long getCurrentTickFolia(Class<?> regionizedServerFolia) {
        try {
            return (long) regionizedServerFolia.getMethod("getCurrentTick").invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            return 0;
        }
    }
}
