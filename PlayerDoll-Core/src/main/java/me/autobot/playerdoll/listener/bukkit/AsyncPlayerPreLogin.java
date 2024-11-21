package me.autobot.playerdoll.listener.bukkit;

import io.netty.channel.Channel;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.connection.Connection;
import me.autobot.playerdoll.api.connection.ConnectionFetcher;
import me.autobot.playerdoll.api.doll.PlayerConvertInjector;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Function;

public class AsyncPlayerPreLogin implements Listener {

    private static final Class<?> loginListenerClass = ReflectionUtil.getClass("net.minecraft.server.network.LoginListener");
    // Just trigger the static block
    //private static final Class<?> playerLoginListenerClass = ReflectionUtil.getPluginNMSClass("connection.login.PlayerLoginListener");
    // Must be set
    public static Function<Object, Boolean> checkProtocol;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (Connection.DOLL_CONNECTIONS.containsKey(event.getUniqueId())) {
            PlayerDollAPI.getScheduler().globalTask(() -> Bukkit.getOfflinePlayer(event.getUniqueId()).setOp(true));
            return;
        }
        if (!PlayerDollAPI.getConfigLoader().getBasicConfig().convertPlayer.getValue()) {
            return;
        }
        String address = event.getAddress().getHostAddress();
        try {
            // Prevent player logging-in too fast
            // Delay a bit to let the ConnectionList to update
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<Object> connectionList = ConnectionFetcher.getServerConnectionList();
        synchronized (connectionList) {
            for (Object connections : connectionList) {
                Channel channel = ConnectionFetcher.getChannel(connections);
                if (channel == null) {
                    continue;
                }
                // Only IP are provided from the event. Not sure 100% match
                if (!((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress().equals(address)) {
                    continue;
                }
                Object packetListener = ConnectionFetcher.getPacketListener(connections);
                if (loginListenerClass.equals(packetListener.getClass())) {
                    if (checkProtocol.apply(packetListener)) {
                        if (channel.pipeline().get("packet_handler") != null && channel.pipeline().get("player_convert_injector") == null) {
                            PlayerDollAPI.getLogger().info("Capture Player Connection");
                            new PlayerConvertInjector(connections, channel);
                            break;
                        }
                    }
                }
            }
        }
    }
}
