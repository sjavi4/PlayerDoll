package me.autobot.playerdoll.v1_20_R4.Network.ClientPacketHandler;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R4.Network.CursedConnections;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.configuration.*;

import java.util.logging.Level;

public class ClientConfigurationListener extends ClientCommonListenerAbs implements TickablePacketListener, ClientConfigurationPacketListener {

    private final GameProfile profile;
    public ClientConfigurationListener(Connection connection, GameProfile profile) {
        super(connection);
        this.profile = profile;
        this.connection.channel.attr(Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(ConnectionProtocol.CONFIGURATION.codec(PacketFlow.CLIENTBOUND));
    }

    @Override
    public void handleRegistryData(ClientboundRegistryDataPacket clientboundRegistryDataPacket) {
        // Not Handle This
    }
    @Override
    public void handleCustomPayload(ClientboundCustomPayloadPacket clientboundCustomPayloadPacket) {
        // Not Handle This
    }
    @Override
    public void handleConfigurationFinished(ClientboundFinishConfigurationPacket clientboundFinishConfigurationPacket) {
        // Handle This
        PlayerDoll.getPluginLogger().log(Level.INFO,"Handle Fake Client Configuration");
        this.connection.suspendInboundAfterProtocolChange();
        if (!PlayerDoll.isFolia) {
            PacketUtils.ensureRunningOnSameThread(clientboundFinishConfigurationPacket, this, CursedConnections.server);
        }
        this.connection.setListener(new ClientPacketListener(connection, profile));
        this.connection.resumeInboundAfterProtocolChange();
        this.connection.send(new ServerboundFinishConfigurationPacket());
    }

    @Override
    public void handleEnabledFeatures(ClientboundUpdateEnabledFeaturesPacket clientboundUpdateEnabledFeaturesPacket) {
        // Not Handle This
    }

    @Override
    public void tick() {
    }

    @Override
    public void handleDisconnect(ClientboundDisconnectPacket clientboundDisconnectPacket) {
        this.connection.disconnect(clientboundDisconnectPacket.getReason());
    }

    @Override
    public void onDisconnect(Component component) {
        super.onDisconnect(component);
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }
}
