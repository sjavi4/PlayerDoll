package me.autobot.playerdoll.packet.v1_20_R4;

import me.autobot.playerdoll.packet.v1_20_R2.CDisconnect;
import me.autobot.playerdoll.packet.v1_20_R2.play.CSyncPosition;
import me.autobot.playerdoll.packet.v1_20_R4.config.CConfigFinish;
import me.autobot.playerdoll.packet.v1_20_R4.config.CConfigKeepAlive;
import me.autobot.playerdoll.packet.v1_20_R4.config.CConfigResourcePackPush;
import me.autobot.playerdoll.packet.v1_20_R4.play.CCombatDeath;
import me.autobot.playerdoll.packet.v1_20_R4.play.CGameEvent;
import me.autobot.playerdoll.packet.v1_20_R4.play.CPlayKeepAlive;
import me.autobot.playerdoll.socket.io.SocketReader;

public class Factory extends me.autobot.playerdoll.packet.v1_20_R3.Factory {

    public Factory(SocketReader socketReader) {
        super(socketReader);
    }

    @Override
    protected int getProtocol() {
        return 766;
    }


    @Override
    protected void registerConfigPackets() {
        configPackets.put(0x02, new CDisconnect());
        configPackets.put(0x03, new CConfigFinish());
        configPackets.put(0x04, new CConfigKeepAlive());
        configPackets.put(0x09, new CConfigResourcePackPush());
    }

    @Override
    protected void registerPlayPackets() {
        playPackets.put(0x1D, new CDisconnect());
        playPackets.put(0x22, new CGameEvent());
        playPackets.put(0x26, new CPlayKeepAlive());
        playPackets.put(0x3C, new CCombatDeath());
        playPackets.put(0x40, new CSyncPosition());
    }
}
