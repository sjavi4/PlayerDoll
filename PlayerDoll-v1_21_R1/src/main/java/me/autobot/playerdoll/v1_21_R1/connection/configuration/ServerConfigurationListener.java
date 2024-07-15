package me.autobot.playerdoll.v1_21_R1.connection.configuration;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.connection.CursedConnection;
import me.autobot.playerdoll.v1_21_R1.connection.play.ServerGamePlayListener;
import me.autobot.playerdoll.v1_21_R1.player.ServerDoll;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.network.protocol.configuration.ServerboundSelectKnownPacks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.packs.repository.KnownPack;
import org.bukkit.entity.Player;

import java.util.List;

public class ServerConfigurationListener extends ServerConfigurationPacketListenerImpl {
    private final Player caller;
    public ServerConfigurationListener(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer player, Player caller) {
        super(minecraftserver, networkmanager, CommonListenerCookie.createInitial(player.getGameProfile(), false), player);
        handleClientInformation(new ServerboundClientInformationPacket(ClientInformation.createDefault()));
        this.caller = caller;
    }

    @Override
    public void startConfiguration() {
        super.startConfiguration();
        List<KnownPack> list = this.server.getResourceManager().listPacks().flatMap((iresourcepack) -> iresourcepack.location().knownPackInfo().stream()).toList();
        handleSelectKnownPacks(new ServerboundSelectKnownPacks(list));
    }
    @Override
    public void handleConfigurationFinished(ServerboundFinishConfigurationPacket serverboundfinishconfigurationpacket) {
        super.handleConfigurationFinished(serverboundfinishconfigurationpacket);
        Runnable task = () -> {
            //this.connection.suspendInboundAfterProtocolChange();
            ServerGamePlayListener gamePlayListener = new ServerGamePlayListener((ServerDoll) this.player, this.connection, playerProfile());
            CursedConnection.setPacketListener(this.connection, gamePlayListener);
            //((ServerDoll) this.player).serverConnection = this.connection;
            ((ServerDoll) this.player).setup(caller);
            this.player.connection = gamePlayListener;
            //this.connection.resumeInboundAfterProtocolChange();
        };
        if (PlayerDoll.serverBranch == PlayerDoll.ServerBranch.FOLIA) {
            PlayerDoll.scheduler.globalTaskDelayed(task, 5);
        } else {
            task.run();
        }
    }
}