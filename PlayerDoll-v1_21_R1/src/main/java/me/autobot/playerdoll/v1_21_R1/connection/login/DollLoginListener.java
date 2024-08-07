package me.autobot.playerdoll.v1_21_R1.connection.login;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.util.ReflectionUtil;
import me.autobot.playerdoll.v1_21_R1.connection.configuration.ServerConfigurationListener;
import me.autobot.playerdoll.v1_21_R1.player.ServerDoll;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class DollLoginListener extends ServerLoginPacketListenerImpl {
    private final GameProfile profile;
    private final Player caller;
    private static final Method startClientVerificationMethod;

    static {
        startClientVerificationMethod = Arrays.stream(ServerLoginPacketListenerImpl.class.getDeclaredMethods())
                .filter(method -> method.getModifiers() == 0)
                .filter(method -> method.getReturnType() == void.class && method.getParameterCount() == 1 && method.getParameterTypes()[0] == GameProfile.class)
                .findFirst()
                .orElseThrow();
        startClientVerificationMethod.setAccessible(true);
    }
    public DollLoginListener(MinecraftServer minecraftserver, Connection networkmanager, GameProfile profile, Player caller) {
        super(minecraftserver, networkmanager, false);
        //networkmanager.channel.attr(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.LOGIN.codec(PacketFlow.SERVERBOUND));
        this.profile = profile;
        this.caller = caller;
        handleHello(new ServerboundHelloPacket(profile.getName(), profile.getId()));
    }
    @Override
    public void handleHello(ServerboundHelloPacket packetlogininstart) {
        callPreLogin();
        ReflectionUtil.invokeMethod(startClientVerificationMethod, this, profile);
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
        // Avoid IllegalStateException "Asynchronous Chunk getEntities call!"
        PacketUtils.ensureRunningOnSameThread(serverboundloginacknowledgedpacket, this, (MinecraftServer) ReflectionUtil.getDedicatedServerInstance());

        this.connection.setupOutboundProtocol(ConfigurationProtocols.CLIENTBOUND);

        ServerDoll player = ServerDoll.callSpawn(profile);
        player.setup(caller);
        ServerConfigurationPacketListenerImpl serverconfigurationpacketlistenerimpl = new ServerConfigurationListener(player.server, this.connection, player);
        this.connection.setupInboundProtocol(ConfigurationProtocols.SERVERBOUND, serverconfigurationpacketlistenerimpl);
        serverconfigurationpacketlistenerimpl.startConfiguration();
    }
}
