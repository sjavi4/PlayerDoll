package me.autobot.addonDoll.connection;

import com.mojang.authlib.GameProfile;
import me.autobot.addonDoll.player.ServerDoll;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.constant.AbsServerBranch;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class DollLoginListener extends ServerLoginPacketListenerImpl implements Listener {
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
        super(minecraftserver, networkmanager);
        networkmanager.channel.attr(Connection.ATTRIBUTE_SERVERBOUND_PROTOCOL).set(ConnectionProtocol.LOGIN.codec(PacketFlow.SERVERBOUND));
        this.profile = profile;
        this.caller = caller;
        handleHello(new ServerboundHelloPacket(profile.getName(), profile.getId()));
    }
    @Override
    public void handleHello(ServerboundHelloPacket packetlogininstart) {
        callPreLogin();
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
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getUniqueId() == profile.getId()) {
            PlayerDollAPI.getScheduler().globalTaskDelayed(() -> ReflectionUtil.invokeMethod(startClientVerificationMethod, this, profile), 5);
            AsyncPlayerPreLoginEvent.getHandlerList().unregister(this);
        }
    }


    @Override
    public void handleLoginAcknowledgement(ServerboundLoginAcknowledgedPacket serverboundloginacknowledgedpacket) {
        if (PlayerDollAPI.getServerBranch() == AbsServerBranch.SPIGOT) {
            // Avoid IllegalStateException "Asynchronous Chunk getEntities call!"
            PacketUtils.ensureRunningOnSameThread(serverboundloginacknowledgedpacket, this, (MinecraftServer) ReflectionUtil.getDedicatedServerInstance());
        }
        ServerPlayer oldPlayer = PlayerLoginListener.getPlayer(this);
        ServerDoll player = ServerDoll.callSpawn(profile, oldPlayer);
        player.setup(caller);
        ServerConfigurationPacketListenerImpl serverconfigurationpacketlistenerimpl = new ServerConfigurationListener(player.server, this.connection, player);
        this.connection.setListener(serverconfigurationpacketlistenerimpl);
        serverconfigurationpacketlistenerimpl.startConfiguration();
    }
}
