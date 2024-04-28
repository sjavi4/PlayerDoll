package me.autobot.playerdoll.v1_20_R3.Network.ServerPacketHandler;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R3.player.ServerDoll;
import me.autobot.playerdoll.v1_20_R3.Network.CursedConnections;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class ServerLoginListener extends ServerLoginPacketListenerImpl {
    private final GameProfile profile;
    private final Player caller;
    private static Method startClientVerificationMethod;

    static {
        for (Method method : ServerLoginListener.class.getSuperclass().getDeclaredMethods()) {
            if (method.getReturnType() == void.class && method.getParameterCount() == 1 && method.getParameterTypes()[0] == GameProfile.class) {
                if (method.getModifiers() == 0) {
                    method.setAccessible(true);
                    startClientVerificationMethod = method;
                    break;
                }
            }
        }
    }
    public ServerLoginListener(MinecraftServer minecraftserver, Connection networkmanager, GameProfile profile, Player caller) {
        super(minecraftserver, networkmanager);
        networkmanager.channel.attr(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.LOGIN.codec(PacketFlow.SERVERBOUND));
        this.profile = profile;
        this.caller = caller;
    }
    @Override
    public void handleHello(ServerboundHelloPacket packetlogininstart) {
        callPreLogin();
        startClientVerificationMethod.setAccessible(true);
        try {
            startClientVerificationMethod.invoke(this, profile);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private void callPreLogin() {
        Thread preLogin = new Thread(() -> {
            AsyncPlayerPreLoginEvent preLoginEvent = new AsyncPlayerPreLoginEvent(profile.getName(), ((InetSocketAddress)this.connection.getRemoteAddress()).getAddress(), profile.getId());
            preLoginEvent.setLoginResult(AsyncPlayerPreLoginEvent.Result.ALLOWED);
            preLoginEvent.setKickMessage("PlayerDoll");
            Bukkit.getPluginManager().callEvent(preLoginEvent);
        });
        preLogin.start();
    }

    @Override
    public void handleLoginAcknowledgement(ServerboundLoginAcknowledgedPacket serverboundloginacknowledgedpacket) {
        if (!PlayerDoll.isFolia) {
            PacketUtils.ensureRunningOnSameThread(serverboundloginacknowledgedpacket, this, CursedConnections.server);
        }
        ServerPlayer player = ServerDoll.callSpawn(profile.getName(), profile.getId());
        ServerConfigurationPacketListenerImpl serverconfigurationpacketlistenerimpl = new ServerConfigurationListener(MinecraftServer.getServer(), this.connection, player, caller);
        this.connection.setListener(serverconfigurationpacketlistenerimpl);
        serverconfigurationpacketlistenerimpl.startConfiguration();
    }
}
