package me.autobot.playerdoll.v1_20_R3.connection.play;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.config.DollConfig;
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
        if (doll.dollConfig.dollRealPlayerTickUpdate.getValue()) {
            doll.doTick();
        }
        this.keepConnectionAlive();
    }
}
