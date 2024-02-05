package me.autobot.playerdoll.Dolls.Folia;

import me.autobot.playerdoll.Folia.FoliaHelper;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class Abstract_HandleAcceptedLogin {

    static final Class<?> regionizedServerFolia = FoliaHelper.FOLIA_REGIONIZED_SERVER;
    static final Object regionizedServer = FoliaHelper.REGOINIZED_SERVER;
    static final MutableObject<CompoundTag> data = new MutableObject<>();
    static final MutableObject<String> lastKnownName = new MutableObject<>();
    static final Class<?> completable;
    static final Object toComplete;
    private static final TicketType<?> loginType;
    private static final Object taskQueue;
    private static final Class<?> priorityClass;
    private static final Object[] priority;
    private static final Method qQ;
    private static final Class<?> craftWorldClass;
    Consumer<Location> consumer_PlaceNewPlayer = (location) -> {};
    Runnable runnable_LoadSpawnForNewPlayer = () -> {};
    final Object connection;
    final Object serverPlayer;
    final Object playerList;
    final Object chunkSource;

    static {
        try {
            completable = Class.forName("ca.spottedleaf.concurrentutil.completable.Completable");
            toComplete = completable.getConstructor().newInstance();
            loginType = (TicketType<?>) TicketType.class.getField("LOGIN").get(null);
            taskQueue = regionizedServerFolia.getField("taskQueue").get(regionizedServer);
            priorityClass = Class.forName("ca.spottedleaf.concurrentutil.executor.standard.PrioritisedExecutor$Priority");
            priority = priorityClass.getEnumConstants();
            qQ = taskQueue.getClass().getMethod("queueTickTaskQueue", ServerLevel.class, int.class, int.class, Runnable.class, priorityClass);
            craftWorldClass = Class.forName("org.bukkit.craftbukkit."+ PlayerDoll.version +".CraftWorld");
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    public Abstract_HandleAcceptedLogin(Object connection, Object serverPlayer, Object playerList, Object chunkSource) {
        this.connection = connection;
        this.serverPlayer = serverPlayer;
        this.playerList = playerList;
        this.chunkSource = chunkSource;
    }

    abstract void setup_PlaceNewPlayer();
    abstract void setup_LoadSpawnForNewPlayer();

    protected void callSpawn(Runnable packetTask) {
        try {
            BiConsumer<Location, Throwable> waiter = (loc, t) -> {
                int chunkX = loc.getX() < (double)(int)loc.getX() ? (int)loc.getX() -1 >> 4 : (int)loc.getX() >> 4;
                int chunkZ = loc.getZ() < (double)(int)loc.getZ() ? (int)loc.getZ() -1 >> 4 : (int)loc.getZ() >> 4;
                try {
                    ServerLevel serverLevel = (ServerLevel) loc.getWorld().getClass().asSubclass(craftWorldClass).getDeclaredMethod("getHandle").invoke(loc.getWorld());
                    Method addTicket = chunkSource.getClass().getMethod("addTicketAtLevel", TicketType.class, ChunkPos.class, int.class, Object.class);
                    addTicket.invoke(chunkSource,loginType, new ChunkPos(chunkX,chunkZ), 33, Unit.INSTANCE);

                    Runnable r = () -> {
                        if (t == null) {
                            consumer_PlaceNewPlayer.accept(loc);
                            packetTask.run();
                        }
                    };

                    qQ.invoke(taskQueue, serverLevel, chunkX, chunkZ, r, priority[2]);
                } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            };

            toComplete.getClass().getMethod("addWaiter", BiConsumer.class).invoke(toComplete,waiter);

            runnable_LoadSpawnForNewPlayer.run();

        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            System.out.println("Exception while invoking Folia's PlaceNewPlayer");
            e.printStackTrace();
        }
    }
}
