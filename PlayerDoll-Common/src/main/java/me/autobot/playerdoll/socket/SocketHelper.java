package me.autobot.playerdoll.socket;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.config.BasicConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SocketHelper {
    public static String IP = Bukkit.getIp();
    public static int PORT = Bukkit.getPort();
    public static InetSocketAddress HOST = new InetSocketAddress(IP, PORT);
    public static final Map<UUID, ClientSocket> DOLL_CLIENTS = new ConcurrentHashMap<>();

    static {
        BasicConfig basicConfig = BasicConfig.get();
        if (PlayerDoll.BUNGEECORD || basicConfig.forceProxyIP.getValue()) {
            IP = basicConfig.proxyIP.getValue();
            PORT = basicConfig.proxyPort.getValue();
            HOST = new InetSocketAddress(IP, PORT);
        }
    }

//    public static void createConnection(GameProfile profile, Player caller) {
//        new ClientSocket(profile, caller);
//    }
    public static void createConnection(String dollName, UUID dollUUID, Player caller) {
        new ClientSocket(dollName, dollUUID, caller);
    }
}
