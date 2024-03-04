package me.autobot.playerdoll.v1_20_R4.Network.ServerPacketHandler;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R4.Dolls.UniversalDollImpl;
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
            ServerGamePlayListener gamePlayListener = new ServerGamePlayListener((UniversalDollImpl) this.player, this.connection, playerProfile());
            this.connection.setListener(gamePlayListener);
            ((UniversalDollImpl) this.player).serverConnection = this.connection;
            ((UniversalDollImpl) this.player).setup(caller);
            this.player.connection = gamePlayListener;
            this.player.joining = false;
            this.connection.resumeInboundAfterProtocolChange();
        };

        if (PlayerDoll.isFolia) {
            try {
                Thread.sleep(300L);
                task.run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            task.run();
        }
    }

}
