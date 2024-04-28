package me.autobot.playerdoll.v1_20_R4.Network.ServerPacketHandler;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R4.Network.CursedConnections;
import me.autobot.playerdoll.v1_20_R4.player.ServerDoll;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.bukkit.entity.Player;

public class ServerConfigurationListener extends ServerConfigurationPacketListenerImpl {
    private final Player caller;
    public ServerConfigurationListener(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer player, Player caller) {
        super(minecraftserver, networkmanager, CommonListenerCookie.createInitial(player.getGameProfile(), false), player);
        handleClientInformation(new ServerboundClientInformationPacket(ClientInformation.createDefault()));
        this.caller = caller;
    }

    @Override
    public void handleConfigurationFinished(ServerboundFinishConfigurationPacket serverboundfinishconfigurationpacket) {
        super.handleConfigurationFinished(serverboundfinishconfigurationpacket);
        Runnable task = () -> {
            ServerGamePlayListener gamePlayListener = new ServerGamePlayListener((ServerDoll) this.player, this.connection, playerProfile());
            CursedConnections.setPacketListener(connection, gamePlayListener);
            //this.connection.setupInboundProtocol(GameProtocols.SERVERBOUND.bind(RegistryFriendlyByteBuf.decorator(this.server.registryAccess())), gamePlayListener);
            ((ServerDoll) this.player).serverConnection = this.connection;
            ((ServerDoll) this.player).setup(caller);
            this.player.connection = gamePlayListener;
        };

        PlayerDoll.getScheduler().globalTaskDelayed(task, 5);

    }
}
