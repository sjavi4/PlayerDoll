package me.autobot.playerdoll.api.connection;

import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import me.autobot.playerdoll.api.PlayerDollAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Connection {

    protected String IP = Bukkit.getIp();
    protected int PORT = Bukkit.getPort();
    public InetSocketAddress HOST = new InetSocketAddress(IP, PORT);
    public static final Map<UUID, Channel> DOLL_CONNECTIONS = new ConcurrentHashMap<>();

    public void connect(String name, UUID uuid, Player caller) {
        connect(new GameProfile(uuid, name), caller);
    }
    public abstract void connect(GameProfile profile, Player caller);

    public abstract void shutDown();
    public boolean pre_1204() {
        return PlayerDollAPI.getServerVersion().registerVersion().matches("v1_20_R2|v1_20_R3");
    }
    public int protocolNumber() {
        return PlayerDollAPI.getServerVersion().getProtocol();
    }
}
