package me.autobot.playerdoll.v1_20_R2.connection.login;

import me.autobot.playerdoll.doll.PlayerConvertInjector;
import me.autobot.playerdoll.listener.bukkit.AsyncPlayerPreLogin;
import me.autobot.playerdoll.util.ReflectionUtil;
import me.autobot.playerdoll.v1_20_R2.player.TransPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.handshake.ClientIntent;
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

//        ConvertPlayerConnection.checkProtocol = (listener) -> listener != null &&((ServerLoginPacketListenerImpl)listener).connection.channel.attr(Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL).get() == ClientIntent.LOGIN.protocol().codec(PacketFlow.CLIENTBOUND);
        AsyncPlayerPreLogin.checkProtocol = (listener) -> listener != null &&((ServerLoginPacketListenerImpl)listener).connection.channel.attr(Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL).get() == ClientIntent.LOGIN.protocol().codec(PacketFlow.CLIENTBOUND);

        PlayerConvertInjector.swapListenerFunc = (oldListener) -> {
            ServerLoginPacketListenerImpl l = (ServerLoginPacketListenerImpl) oldListener;
            Connection c = l.connection;
            //c.suspendInboundAfterProtocolChange();
            c.channel.attr(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.LOGIN.codec(PacketFlow.SERVERBOUND));
            c.setListener(new PlayerLoginListener((MinecraftServer) ReflectionUtil.getDedicatedServerInstance(), c, getPlayer(l)));
            //l = null;
            c.channel.attr(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.CONFIGURATION.codec(PacketFlow.SERVERBOUND));
            //c.resumeInboundAfterProtocolChange();
        };
    }
    public PlayerLoginListener(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer player) {
        super(minecraftserver, networkmanager);
        this.server = minecraftserver;
        this.player = player;
        // Async getEntity Call!
        transPlayer = new TransPlayer(player.getBukkitEntity());
    }

    @Override
    public void handleLoginAcknowledgement(ServerboundLoginAcknowledgedPacket serverboundloginacknowledgedpacket) {
        CommonListenerCookie commonlistenercookie = CommonListenerCookie.createInitial(player.getGameProfile());
        ServerConfigurationPacketListenerImpl serverconfigurationpacketlistenerimpl = new ServerConfigurationPacketListenerImpl(this.server, this.connection, commonlistenercookie, transPlayer);
        this.connection.setListener(serverconfigurationpacketlistenerimpl);
        serverconfigurationpacketlistenerimpl.startConfiguration();
    }

    public static ServerPlayer getPlayer(ServerLoginPacketListenerImpl instance) {
        return ReflectionUtil.getField(ServerPlayer.class, serverPlayerField, instance);
    }
}
