package me.autobot.playerdoll.v1_20_R4.Network.ServerPacketHandler;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.v1_20_R4.Dolls.UniversalDollImpl;
import net.minecraft.network.Connection;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerGamePlayListener extends ServerGamePacketListenerImpl {
    private final UniversalDollImpl doll;

    public ServerGamePlayListener(UniversalDollImpl doll, Connection networkmanager, GameProfile profile) {
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
