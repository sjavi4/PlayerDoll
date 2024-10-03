package me.autobot.playerdoll.v1_20_R2.connection.configuration;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.netty.ConnectionFetcher;
import me.autobot.playerdoll.scheduler.SchedulerHelper;
import me.autobot.playerdoll.v1_20_R2.connection.play.ServerGamePlayListener;
import me.autobot.playerdoll.v1_20_R2.player.ServerDoll;
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
            //((ServerDoll) this.player).serverConnection = this.connection;
//            ((ServerDoll) this.player).setup(caller);
            ((ServerDoll) this.player).callDollJoinEvent();
            this.player.connection = gamePlayListener;
            this.connection.resumeInboundAfterProtocolChange();
        };
        if (PlayerDoll.serverBranch == PlayerDoll.ServerBranch.FOLIA) {
            SchedulerHelper.scheduler.entityTask(task, this.getCraftPlayer());
        } else {
            task.run();
        }
    }
}
