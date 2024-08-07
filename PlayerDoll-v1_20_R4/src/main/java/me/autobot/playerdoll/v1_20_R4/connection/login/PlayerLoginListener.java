package me.autobot.playerdoll.v1_20_R4.connection.login;

import me.autobot.playerdoll.doll.PlayerConvertInjector;
import me.autobot.playerdoll.listener.bukkit.AsyncPlayerPreLogin;
import me.autobot.playerdoll.netty.ConnectionFetcher;
import me.autobot.playerdoll.util.ReflectionUtil;
import me.autobot.playerdoll.v1_20_R4.player.TransPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
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
    private static final Field serverPlayerField;
    private final TransPlayer transPlayer;
    static {
        serverPlayerField = Arrays.stream(ServerLoginPacketListenerImpl.class.getDeclaredFields())
                .filter(field -> field.getType() == ServerPlayer.class)
                .findFirst()
                .orElseThrow();
        serverPlayerField.setAccessible(true);

//        ConvertPlayerConnection.checkProtocol = (listener) -> listener != null && ((ServerLoginPacketListenerImpl)listener).protocol() == ConnectionProtocol.LOGIN;
        AsyncPlayerPreLogin.checkProtocol = (listener) -> listener != null && ((ServerLoginPacketListenerImpl)listener).protocol() == ConnectionProtocol.LOGIN;

        PlayerConvertInjector.swapListenerFunc = (oldListener) -> {
            ServerLoginPacketListenerImpl l = (ServerLoginPacketListenerImpl) oldListener;
            ConnectionFetcher.setPacketListener(l.connection, new PlayerLoginListener((MinecraftServer) ReflectionUtil.getDedicatedServerInstance(), l.connection, getPlayer(l), l.isTransferred()));
        };
    }
    public PlayerLoginListener(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer player, boolean transfer) {
        super(minecraftserver, networkmanager, transfer);
        this.server = minecraftserver;
        this.player = player;
        transPlayer = new TransPlayer(player.getBukkitEntity());
    }

    @Override
    public void handleLoginAcknowledgement(ServerboundLoginAcknowledgedPacket serverboundloginacknowledgedpacket) {
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
