package me.autobot.playerdoll.Dolls.Folia;

import net.minecraft.nbt.CompoundTag;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class v1_20_R2_HandleAcceptedLogin extends Abstract_HandleAcceptedLogin {
    private final Object commonListenerCookie;
    public v1_20_R2_HandleAcceptedLogin(Object connection, Object serverPlayer, Object commonListenerCookie, Object playerList, Object chunkSource, Runnable packetTask) {
        super(connection, serverPlayer, playerList, chunkSource);
        this.commonListenerCookie = commonListenerCookie;
        setup_PlaceNewPlayer();
        setup_LoadSpawnForNewPlayer();
        callSpawn(packetTask);
    }
    @Override
    void setup_PlaceNewPlayer() {
        consumer_PlaceNewPlayer = (location) -> {
            try {
                Method placeNewPlayerFolia = playerList.getClass().getMethod("placeNewPlayer", connection.getClass().getSuperclass(), serverPlayer.getClass().getSuperclass().getSuperclass(), commonListenerCookie.getClass() , CompoundTag.class, String.class, Location.class);
                placeNewPlayerFolia.invoke(playerList, connection, serverPlayer, commonListenerCookie, data.getValue(), lastKnownName.getValue(), location);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    void setup_LoadSpawnForNewPlayer() {
        runnable_LoadSpawnForNewPlayer = () -> {
            try {
                Method loadSpawnForNewPlayerFolia = playerList.getClass().getMethod("loadSpawnForNewPlayer", connection.getClass().getSuperclass(), serverPlayer.getClass().getSuperclass().getSuperclass(), commonListenerCookie.getClass() , data.getClass(), lastKnownName.getClass(), completable);
                loadSpawnForNewPlayerFolia.invoke(playerList, connection, serverPlayer, commonListenerCookie, data, lastKnownName, toComplete);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
