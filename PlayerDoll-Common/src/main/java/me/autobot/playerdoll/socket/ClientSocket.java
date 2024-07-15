package me.autobot.playerdoll.socket;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.socket.io.SocketReader;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class ClientSocket {
    public Socket socket;
    private boolean connected;

    private final GameProfile profile;
    private final Player caller;
    private SocketReader socketReader;
    public ClientSocket(String dollName, UUID dollUUID, Player caller) {
        this(new GameProfile(dollUUID, dollName), caller);
    }

    public ClientSocket(GameProfile profile, Player caller) {
        this.profile = profile;
        this.caller = caller;
        startListen();
        if (connected) {
            SocketHelper.DOLL_CLIENTS.put(profile.getId(), this);
            socketReader = new SocketReader(this);
            socketReader.start();
        }
    }


    private void startListen() {
        socket = new Socket();
        try {
            socket.connect(SocketHelper.HOST, 3000);
            connected = true;
        } catch (IOException e) {
            connected = false;
        }
    }


    public GameProfile getProfile() {
        return profile;
    }
    public Player getCaller() {
        return caller;
    }
    public SocketReader getSocketReader() {
        return socketReader;
    }

}
