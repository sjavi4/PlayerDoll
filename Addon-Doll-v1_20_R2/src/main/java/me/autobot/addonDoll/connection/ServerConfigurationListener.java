package me.autobot.addonDoll.connection;

import me.autobot.addonDoll.player.ServerDoll;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.connection.ConnectionFetcher;
import me.autobot.playerdoll.api.constant.AbsServerBranch;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public class ServerConfigurationListener extends ServerConfigurationPacketListenerImpl {
    public ServerConfigurationListener(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer player) {
        super(minecraftserver, networkmanager, CommonListenerCookie.createInitial(player.getGameProfile()), player);
        handleClientInformation(new ServerboundClientInformationPacket(ClientInformation.createDefault()));
    }

    @Override
    public void handleConfigurationFinished(ServerboundFinishConfigurationPacket serverboundfinishconfigurationpacket) {
        super.handleConfigurationFinished(serverboundfinishconfigurationpacket);
        Runnable task = () -> {
            this.connection.suspendInboundAfterProtocolChange();
            ServerGamePlayListener gamePlayListener = new ServerGamePlayListener((ServerDoll) this.player, this.connection, playerProfile());
            ConnectionFetcher.setPacketListener(this.connection, gamePlayListener);
            ((ServerDoll) this.player).callDollJoinEvent();
            this.player.connection = gamePlayListener;
            this.connection.resumeInboundAfterProtocolChange();
        };
        if (PlayerDollAPI.getServerBranch() == AbsServerBranch.FOLIA) {
            PlayerDollAPI.getScheduler().entityTask(task, this.getCraftPlayer());
        } else {
            task.run();
        }
    }
}
