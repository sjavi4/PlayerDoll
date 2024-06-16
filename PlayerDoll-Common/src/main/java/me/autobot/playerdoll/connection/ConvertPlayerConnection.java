package me.autobot.playerdoll.connection;

import io.netty.channel.Channel;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.packet.PlayerConvertInjector;
import me.autobot.playerdoll.util.ReflectionUtil;

import java.util.List;
import java.util.function.Function;

public class ConvertPlayerConnection extends Thread {
    private final Class<?> loginListenerClass = ReflectionUtil.getClass("net.minecraft.server.network.LoginListener");
    // Just trigger the static block
    private static final Class<?> playerLoginListenerClass = ReflectionUtil.getPluginNMSClass("connection.login.PlayerLoginListener");
    public static Function<Object, Boolean> checkProtocol;

    @Override
    public void run() {
        PlayerDoll.LOGGER.info("Start Listening Player Connection...");
        while (!Thread.currentThread().isInterrupted()) {
            List<Object> connectionList = CursedConnection.getServerConnectionList();
            synchronized (connectionList) {
                for (Object connections : connectionList) {
                    Channel channel = CursedConnection.getChannel(connections);
                    if (channel == null) {
                        continue;
                    }
                    Object packetListener = CursedConnection.getPacketListener(connections);
                    if (loginListenerClass.equals(packetListener.getClass())) {
                        if (checkProtocol.apply(packetListener)) {
                            if (channel.pipeline().get("packet_handler") != null && channel.pipeline().get("player_convert_injector") == null) {
                                PlayerDoll.LOGGER.info("Capture Player Connection");
                                new PlayerConvertInjector(connections, channel);
                            }
                        }
                    }
                }
            }
        }
    }
}
