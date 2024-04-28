package me.autobot.playerdoll.v1_20_R3.Network.ServerPacketHandler;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.v1_20_R3.player.ServerDoll;
import net.minecraft.network.Connection;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerGamePlayListener extends ServerGamePacketListenerImpl {
    private final ServerDoll doll;

    public ServerGamePlayListener(ServerDoll doll, Connection networkmanager, GameProfile profile) {
        super(doll.server, networkmanager, doll, CommonListenerCookie.createInitial(profile));
        this.doll = doll;
    }

    @Override
    public void tick() {
        if (doll.getDollConfig().dollRealPlayerTickUpdate.getValue()) {
            doll.doTick();
        }
        this.keepConnectionAlive();
    }
}
