package me.autobot.addonDoll.connection;

import me.autobot.addonDoll.player.TransPlayer;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.connection.ConnectionFetcher;
import me.autobot.playerdoll.api.constant.AbsServerBranch;
import me.autobot.playerdoll.api.doll.PlayerConvertInjector;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import java.lang.reflect.Field;
import java.util.Arrays;

public class PlayerLoginListener extends ServerLoginPacketListenerImpl {
    private final MinecraftServer server;
    private final ServerPlayer player;
    private final TransPlayer transPlayer;
    private static final Field serverPlayerField;

    static {
        serverPlayerField = Arrays.stream(ServerLoginPacketListenerImpl.class.getDeclaredFields())
                .filter(field -> field.getType() == ServerPlayer.class)
                .findFirst()
                .orElseThrow();
        serverPlayerField.setAccessible(true);

        PlayerConvertInjector.swapListenerFunc = (oldListener) -> {
            ServerLoginPacketListenerImpl l = (ServerLoginPacketListenerImpl) oldListener;
            ConnectionFetcher.setPacketListener(l.connection, new PlayerLoginListener((MinecraftServer) ReflectionUtil.getDedicatedServerInstance(), l.connection, getPlayer(l), l.isTransferred()));
        };

    }
    public PlayerLoginListener(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer player, boolean transfer) {
        super(minecraftserver, networkmanager, transfer);
        this.server = minecraftserver;
        this.player = player;
        transPlayer = new TransPlayer(player);
    }

    @Override
    public void handleLoginAcknowledgement(ServerboundLoginAcknowledgedPacket serverboundloginacknowledgedpacket) {
        if (PlayerDollAPI.getServerBranch() != AbsServerBranch.FOLIA) {
            PacketUtils.ensureRunningOnSameThread(serverboundloginacknowledgedpacket, this, this.server);
        }
        this.connection.setupOutboundProtocol(ConfigurationProtocols.CLIENTBOUND);
        CommonListenerCookie commonlistenercookie = CommonListenerCookie.createInitial(player.getGameProfile(), false);
        ServerConfigurationPacketListenerImpl serverconfigurationpacketlistenerimpl = new ServerConfigurationPacketListenerImpl(this.server, this.connection, commonlistenercookie, transPlayer);
        this.connection.setupInboundProtocol(ConfigurationProtocols.SERVERBOUND, serverconfigurationpacketlistenerimpl);
        serverconfigurationpacketlistenerimpl.startConfiguration();
    }

    public static ServerPlayer getPlayer(ServerLoginPacketListenerImpl instance) {
        return ReflectionUtil.getField(ServerPlayer.class, serverPlayerField, instance);
    }
}
