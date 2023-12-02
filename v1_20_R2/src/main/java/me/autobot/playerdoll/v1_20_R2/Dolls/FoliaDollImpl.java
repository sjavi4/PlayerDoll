package me.autobot.playerdoll.v1_20_R2.Dolls;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.ChunkPos;
import org.apache.commons.lang3.mutable.MutableObject;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public class FoliaDollImpl extends AbstractDoll {
    Class<?> regionizedServerFolia;
    public FoliaDollImpl(MinecraftServer minecraftserver, ServerLevel worldserver, GameProfile gameprofile, ServerPlayer player) {
        super(minecraftserver, worldserver, gameprofile, player);

    }
    @Override
    public void spawnToWorld() {
        //for (var clazz : this.server.getPlayerList().getClass().getMethods()) {
        //    System.out.println(clazz + " -- " + Arrays.toString(clazz.getParameters()));
        //}
        try {

            MutableObject<CompoundTag> data = new MutableObject<>();
            MutableObject<String> lastKnownName = new MutableObject<>();
            Class<?> completable = Class.forName("ca.spottedleaf.concurrentutil.completable.Completable");
            Object toComplete = completable.getConstructor().newInstance();

            TicketType<?> loginType = (TicketType<?>) TicketType.class.getField("LOGIN").get(null);
            Object rServer = regionizedServerFolia.getMethod("getInstance").invoke(null);
            Object taskQueue = regionizedServerFolia.getField("taskQueue").get(rServer);
            Class<?> priorityClass = Class.forName("ca.spottedleaf.concurrentutil.executor.standard.PrioritisedExecutor$Priority");
            Object[] priority = priorityClass.getEnumConstants();
            Method qQ = taskQueue.getClass().getMethod("queueTickTaskQueue", ServerLevel.class, int.class, int.class, Runnable.class, priorityClass);
            Method placeNewPlayerFolia = this.server.getPlayerList().getClass().getMethod("placeNewPlayer", Connection.class, ServerPlayer.class, CompoundTag.class, String.class, Location.class);

            BiConsumer<Location, Throwable> waiter = (loc, t) -> {
                int chunkX = Mth.floor(loc.getX()) >> 4;
                int chunkZ = Mth.floor(loc.getZ()) >> 4;
                try {
                    Class<?> craftWorldClass = Class.forName("org.bukkit.craftbukkit."+ PlayerDoll.version +".CraftWorld");
                    ServerLevel serverLevel = (ServerLevel) loc.getWorld().getClass().asSubclass(craftWorldClass).getDeclaredMethod("getHandle").invoke(loc.getWorld());
                    Method addTicket = serverLevel.getChunkSource().getClass().getMethod("addTicketAtLevel", TicketType.class, ChunkPos.class, int.class, Object.class);
                    addTicket.invoke(serverLevel.getChunkSource(),loginType, new ChunkPos(chunkX,chunkZ), 33, Unit.INSTANCE);

                    Runnable r = () -> {
                        try {
                            if (t == null && this.dollNetworkManager.isConnected()) {
                                placeNewPlayerFolia.invoke(this.server.getPlayerList(), this.dollNetworkManager, this, data.getValue(), lastKnownName.getValue(), loc);
                                this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this));
                                this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this));
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



            //Method addTicket = serverLevel().getChunkSource().getClass().getMethod("addTicketAtLevel", TicketType.class, ChunkPos.class, int.class, Object.class);


            Method loadSpawnForNewPlayerFolia = this.server.getPlayerList().getClass().getMethod("loadSpawnForNewPlayer", Connection.class, ServerPlayer.class, MutableObject.class, MutableObject.class, completable);
            loadSpawnForNewPlayerFolia.invoke(this.server.getPlayerList(), this.dollNetworkManager, this, data, lastKnownName, toComplete);

        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException | InstantiationException | NoSuchFieldException e) {
            System.out.println("Exception while invoking Folia's PlaceNewPlayer");
            e.printStackTrace();
        }


    }
    @Override
    public void teleportTo() {
        Runnable t = () -> {
            /*
            try {
                Method teleportToFolia = this.getClass().getMethod("teleportTo", ServerLevel.class, double.class, double.class, double.class, Set.class, float.class, float.class, PlayerTeleportEvent.TeleportCause.class);
                var pos = this.player.position();
                teleportToFolia.invoke(this, this.player.serverLevel(), pos.x, pos.y, pos.z, Set.of(), TPYaw, TPPitch, PlayerTeleportEvent.TeleportCause.PLUGIN);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }

             */
            try {
                Method teleportAsyncFolia = this.getBukkitEntity().getClass().getMethod("teleportAsync", Location.class, PlayerTeleportEvent.TeleportCause.class);
                teleportAsyncFolia.invoke(this.getBukkitEntity(), this.player.getBukkitEntity().getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        };
        FoliaSupport.entityTask(this.getBukkitEntity(),t,20);
    }
    @Override
    public void setDollLookAt() {
        Runnable r = () -> {
            this.server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(this, PacketYaw));
            this.server.getPlayerList().broadcastAll(new ClientboundMoveEntityPacket.Rot(this.getId(), PacketYaw, PacketPitch, true));
        };
        try {
            long tick = getCurrentTickFolia();
            Object rServer = regionizedServerFolia.getMethod("getInstance").invoke(null);
            regionizedServerFolia.getMethod("addTask", Runnable.class).invoke(rServer, new TickTask(Math.toIntExact(tick) + 2, r));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tick() {
        long tickCount = getCurrentTickFolia();
        if (tickCount % 10 == 0) {
            connection.resetPosition();
            this.serverLevel().getChunkSource().move(this);
            if (noPhantom) this.getBukkitEntity().setStatistic(Statistic.TIME_SINCE_REST,0);
        }
        super.tick();
    }

    @Override
    public void getFoliaRegionizedServer() {
        try {
            regionizedServerFolia = Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
        } catch (ClassNotFoundException ignored) {
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.checkBlocking(damageSource)) {
            //this.attackBlocked = true;
            this.playSound(SoundEvents.SHIELD_BLOCK, 0.8F, 0.8F + this.serverLevel().random.nextFloat() * 0.4F);
        }
        boolean damaged = super.hurt(damageSource, f);
        if (damaged) {
            if (this.hurtMarked) {
                this.hurtMarked = false;
                Runnable r = () -> this.hurtMarked = true;
                FoliaSupport.regionTask(this.getBukkitEntity().getLocation(), r);
            }
        }
        return damaged;
    }

    @Override
    public void disconnect() {
        super.disconnect();
        this.connection.onDisconnect(Component.literal("Disconnected"));
    }
    private long getCurrentTickFolia() {
        try {
            return (long) regionizedServerFolia.getMethod("getCurrentTick").invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
            return 0;
        }
    }

    public void foliaDisconnect(boolean remove) {
        if (remove) {
            FoliaSupport.entityTask(this.getBukkitEntity(), this::kill, 1);
        } else {
            FoliaSupport.entityTask(this.getBukkitEntity(), this::disconnect, 1);
            //FoliaSupport.regionTask(this.getBukkitEntity().getLocation(), ()->this.connection.disconnect(Component.literal("despawn")));
        }
    }
}
