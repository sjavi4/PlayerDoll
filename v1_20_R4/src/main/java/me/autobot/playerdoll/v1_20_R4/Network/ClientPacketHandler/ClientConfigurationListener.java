package me.autobot.playerdoll.v1_20_R4.Network.ClientPacketHandler;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R4.Network.CursedConnections;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.network.protocol.configuration.*;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.game.GameProtocols;

import java.util.logging.Level;

public class ClientConfigurationListener extends ClientCommonListenerAbs implements TickablePacketListener, ClientConfigurationPacketListener {

    private final GameProfile profile;
    public ClientConfigurationListener(Connection connection, GameProfile profile) {
        super(connection);
        this.profile = profile;
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
        if (!PlayerDoll.isFolia) {
            PacketUtils.ensureRunningOnSameThread(clientboundFinishConfigurationPacket, this, CursedConnections.server);
        }
        this.connection.setupInboundProtocol(GameProtocols.CLIENTBOUND.bind(RegistryFriendlyByteBuf.decorator(CursedConnections.server.registryAccess())), new ClientPacketListener(this.connection, profile));
        this.connection.send(ServerboundFinishConfigurationPacket.INSTANCE);
        this.connection.setupOutboundProtocol(GameProtocols.SERVERBOUND.bind(RegistryFriendlyByteBuf.decorator(CursedConnections.server.registryAccess())));
    }

    @Override
    public void handleEnabledFeatures(ClientboundUpdateEnabledFeaturesPacket clientboundUpdateEnabledFeaturesPacket) {
        // Not Handle This
    }

    @Override
    public void handleSelectKnownPacks(ClientboundSelectKnownPacks clientboundSelectKnownPacks) {
        // 1.20.5
        send(new ServerboundSelectKnownPacks(clientboundSelectKnownPacks.knownPacks()));
    }

    @Override
    public void handleResetChat(ClientboundResetChatPacket clientboundResetChatPacket) {

    }

    @Override
    public void tick() {
    }

    @Override
    public void handleDisconnect(ClientboundDisconnectPacket clientboundDisconnectPacket) {
        this.connection.disconnect(clientboundDisconnectPacket.reason());
    }

    @Override
    public void handleStoreCookie(ClientboundStoreCookiePacket clientboundStoreCookiePacket) {

    }

    @Override
    public void handleTransfer(ClientboundTransferPacket clientboundTransferPacket) {

    }

    @Override
    public void onDisconnect(Component component) {
        super.onDisconnect(component);
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected();
    }

    @Override
    public void handleRequestCookie(ClientboundCookieRequestPacket clientboundCookieRequestPacket) {

    }
}
