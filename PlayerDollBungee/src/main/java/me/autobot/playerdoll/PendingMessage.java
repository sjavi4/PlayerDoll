package me.autobot.playerdoll;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PendingMessage {
    public final ProxiedPlayer player;
    public final String dollUUID;
    public final String dollName;
    public PendingMessage(ProxiedPlayer player, String dollUUID, String dollName) {
        this.player = player;
        this.dollName = dollName;
        this.dollUUID = dollUUID;
    }
}
