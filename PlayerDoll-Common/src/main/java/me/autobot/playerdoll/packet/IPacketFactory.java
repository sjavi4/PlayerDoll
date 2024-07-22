package me.autobot.playerdoll.packet;


import java.io.DataInputStream;
import java.io.IOException;

public interface IPacketFactory {
      byte[] clientIntent() throws IOException;
//    byte[] loginAck() throws IOException;
//    byte[] configAck() throws IOException;
//    byte[] keepAlive(ClientSocket.ConnectionState state, long id) throws IOException;
//
//    byte[] resourcePackPush(ClientSocket.ConnectionState state, UUID packID, PacketFactory.ResourcePackStatus status) throws IOException;

    void processLogin(int packetID, DataInputStream data, int dataLength) throws IOException;
    void processConfiguration(int packetID, DataInputStream data, int dataLength) throws IOException;
    void processPlay(int packetID, DataInputStream data, int dataLength) throws IOException;




}
