package me.autobot.playerdoll.doll;

import me.autobot.playerdoll.packet.PlayerToBungeeListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class DollData {
    public static final List<DollData> DOLL_DATA_LIST = new CopyOnWriteArrayList<>();
    private final String address;
    private final UUID uuid;
    private final String fullName;
    private final String stripName;
    private ProxiedPlayer dollPlayer;
    private final ServerInfo targetServer;
    private PlayerToBungeeListener listener;

    public DollData(String address, UUID dollUUID, String dollName, UUID callerUUID) {
        this.address = address;
        uuid = dollUUID;
        fullName = dollName;
        stripName = dollName.substring(1);
        this.targetServer = ProxyServer.getInstance().getPlayer(callerUUID).getServer().getInfo();
    }

    public DollData(String address, UUID dollUUID, String dollName, String serverName) {
        this.address = address;
        uuid = dollUUID;
        fullName = dollName;
        stripName = dollName.substring(1);
        this.targetServer = ProxyServer.getInstance().getServerInfo(serverName);
    }

    public String getAddress() {
        return address;
    }
    public UUID getUuid() {
        return uuid;
    }
    public String getFullName() {
        return fullName;
    }

    public String getStripName() {
        return stripName;
    }

    public void setPacketListener(PlayerToBungeeListener listener) {
        this.listener = listener;
    }
    public PlayerToBungeeListener getListener() {
        return listener;
    }

    public ServerInfo getTargetServer() {
        return targetServer;
    }

//    public void setTargetServer(ServerInfo targetServer) {
//        this.targetServer = targetServer;
//    }

    public ProxiedPlayer getDollPlayer() {
        return dollPlayer;
    }

    public void setDollPlayer(ProxiedPlayer dollPlayer) {
        this.dollPlayer = dollPlayer;
    }
}
