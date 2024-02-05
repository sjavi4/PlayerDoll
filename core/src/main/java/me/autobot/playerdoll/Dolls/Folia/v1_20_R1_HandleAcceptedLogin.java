package me.autobot.playerdoll.Dolls.Folia;


import net.minecraft.nbt.CompoundTag;
import org.bukkit.Location;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class v1_20_R1_HandleAcceptedLogin extends Abstract_HandleAcceptedLogin {
    public v1_20_R1_HandleAcceptedLogin(Object connection, Object serverPlayer, Object playerList, Object chunkSource, Runnable packetTask) {
        super(connection, serverPlayer, playerList, chunkSource);
        setup_PlaceNewPlayer();
        setup_LoadSpawnForNewPlayer();
        callSpawn(packetTask);
    }

    @Override
    void setup_PlaceNewPlayer() {
        consumer_PlaceNewPlayer = (location) -> {
            try {
                Method placeNewPlayerFolia = playerList.getClass().getMethod("placeNewPlayer", connection.getClass().getSuperclass(), serverPlayer.getClass().getSuperclass().getSuperclass(), CompoundTag.class, String.class, Location.class);
                placeNewPlayerFolia.invoke(playerList, connection, serverPlayer, data.getValue(), lastKnownName.getValue(), location);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    void setup_LoadSpawnForNewPlayer() {
        runnable_LoadSpawnForNewPlayer = () -> {
            try {
                Method loadSpawnForNewPlayerFolia = playerList.getClass().getMethod("loadSpawnForNewPlayer", connection.getClass().getSuperclass(), serverPlayer.getClass().getSuperclass().getSuperclass(), data.getClass(), lastKnownName.getClass(), completable);
                loadSpawnForNewPlayerFolia.invoke(playerList, connection, serverPlayer, data, lastKnownName, toComplete);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
