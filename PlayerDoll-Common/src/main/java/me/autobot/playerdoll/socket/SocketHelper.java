package me.autobot.playerdoll.socket;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.config.BasicConfig;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SocketHelper {
    public static final String IP = BasicConfig.get().serverIP.getValue();
    public static final int PORT = BasicConfig.get().serverPort.getValue();
    public static final InetSocketAddress HOST = new InetSocketAddress(IP, PORT);
    public static final Map<UUID, ClientSocket> DOLL_CLIENTS = new ConcurrentHashMap<>();

    public static void createConnection(GameProfile profile, Player caller) {
        new ClientSocket(profile, caller);
    }
    public static void createConnection(String dollName, UUID dollUUID, Player caller) {
        new ClientSocket(dollName, dollUUID, caller);
    }
}
