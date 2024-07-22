package me.autobot.playerdoll.packet.v1_20_R2;

import me.autobot.playerdoll.packet.PacketFactory;
import me.autobot.playerdoll.packet.v1_20_R2.config.CConfigFinish;
import me.autobot.playerdoll.packet.v1_20_R2.config.CConfigKeepAlive;
import me.autobot.playerdoll.packet.v1_20_R2.config.CConfigResourcePackPush;
import me.autobot.playerdoll.packet.v1_20_R2.login.CLoginSuccess;
import me.autobot.playerdoll.packet.v1_20_R2.login.CSetCompression;
import me.autobot.playerdoll.packet.v1_20_R2.play.CCombatDeath;
import me.autobot.playerdoll.packet.v1_20_R2.play.CGameEvent;
import me.autobot.playerdoll.packet.v1_20_R2.play.CPlayKeepAlive;
import me.autobot.playerdoll.packet.v1_20_R2.play.CSyncPosition;
import me.autobot.playerdoll.socket.io.SocketReader;

public class Factory extends PacketFactory {

    public Factory(SocketReader socketReader) {
        super(socketReader);
    }

    @Override
    protected int getProtocol() {
        return 764;
    }

    @Override
    protected void registerLoginPackets() {
        loginPackets.put(0x00, new CDisconnect());
        loginPackets.put(0x02, new CLoginSuccess());
        loginPackets.put(0x03, new CSetCompression());
    }

    @Override
    protected void registerConfigPackets() {
        configPackets.put(0x01, new CDisconnect());
        configPackets.put(0x02, new CConfigFinish());
        configPackets.put(0x03, new CConfigKeepAlive());
        configPackets.put(0x06, new CConfigResourcePackPush());
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
