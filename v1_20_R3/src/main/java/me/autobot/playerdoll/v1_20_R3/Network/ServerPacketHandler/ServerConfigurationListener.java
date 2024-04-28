package me.autobot.playerdoll.v1_20_R3.Network.ServerPacketHandler;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R3.player.ServerDoll;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.bukkit.entity.Player;

public class ServerConfigurationListener extends ServerConfigurationPacketListenerImpl {
    private final Player caller;
    public ServerConfigurationListener(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer player, Player caller) {
        super(minecraftserver, networkmanager, CommonListenerCookie.createInitial(player.getGameProfile()), player);
        handleClientInformation(new ServerboundClientInformationPacket(ClientInformation.createDefault()));
        this.caller = caller;
    }

    @Override
    public void handleConfigurationFinished(ServerboundFinishConfigurationPacket serverboundfinishconfigurationpacket) {
        super.handleConfigurationFinished(serverboundfinishconfigurationpacket);
        Runnable task = () -> {
            this.connection.suspendInboundAfterProtocolChange();
            ServerGamePlayListener gamePlayListener = new ServerGamePlayListener((ServerDoll) this.player, this.connection, playerProfile());
            this.connection.setListener(gamePlayListener);
            ((ServerDoll) this.player).serverConnection = this.connection;
            ((ServerDoll) this.player).setup(caller);
            this.player.connection = gamePlayListener;
            this.connection.resumeInboundAfterProtocolChange();
        };

        PlayerDoll.getScheduler().globalTaskDelayed(task, 5);
        /*
        if (PlayerDoll.isFolia) {
            PlayerDoll.getFoliaHelper().globalTaskDelayed(task,5);
        } else {
            task.run();
        }

         */
    }

}
