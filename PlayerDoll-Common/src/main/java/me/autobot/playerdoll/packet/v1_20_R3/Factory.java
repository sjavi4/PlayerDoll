package me.autobot.playerdoll.packet.v1_20_R3;

import me.autobot.playerdoll.packet.v1_20_R2.CDisconnect;
import me.autobot.playerdoll.packet.v1_20_R2.config.CConfigFinish;
import me.autobot.playerdoll.packet.v1_20_R2.config.CConfigKeepAlive;
import me.autobot.playerdoll.packet.v1_20_R2.play.CCombatDeath;
import me.autobot.playerdoll.packet.v1_20_R2.play.CGameEvent;
import me.autobot.playerdoll.packet.v1_20_R2.play.CSyncPosition;
import me.autobot.playerdoll.packet.v1_20_R3.config.CConfigResourcePackPush;
import me.autobot.playerdoll.packet.v1_20_R3.play.CPlayKeepAlive;
import me.autobot.playerdoll.socket.io.SocketReader;

public class Factory extends me.autobot.playerdoll.packet.v1_20_R2.Factory {

    public Factory(SocketReader socketReader) {
        super(socketReader);
    }

    @Override
    protected int getProtocol() {
        return 765;
    }


    @Override
    protected void registerConfigPackets() {
        configPackets.put(0x01, new CDisconnect());
        configPackets.put(0x02, new CConfigFinish());
        configPackets.put(0x03, new CConfigKeepAlive());
        configPackets.put(0x07, new CConfigResourcePackPush());
    }

    @Override
    protected void registerPlayPackets() {
        playPackets.put(0x1B, new CDisconnect());
        playPackets.put(0x20, new CGameEvent());
        playPackets.put(0x24, new CPlayKeepAlive());
        playPackets.put(0x3A, new CCombatDeath());
        playPackets.put(0x3E, new CSyncPosition());
    }
}
