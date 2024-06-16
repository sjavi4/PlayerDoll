package me.autobot.playerdoll.packet.factory;

import me.autobot.playerdoll.packet.PacketFactory;
import me.autobot.playerdoll.packet.Packets;
import me.autobot.playerdoll.socket.ClientSocket;
import me.autobot.playerdoll.socket.io.SocketReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class Packet_v1_21_R1 extends Packet_v1_20_R4 {
    public Packet_v1_21_R1(SocketReader socketReader) {
        super(socketReader);
    }

    @Override
    protected int getProtocol() {
        return 767;
    }

//    @Override
//    public void processConfiguration(int packetID, DataInputStream data, int dataLength) throws IOException {
//        switch (packetID) {
//            // Disconnect
//            case 0x02 -> {
//                System.out.println("Config Disconnect");
//                socketReader.endStream();
//            }
//            // Finish Configuration
//            case 0x03 -> {
//                System.out.println("Finish Config");
//                output.write(configAck());
//                socketReader.nextState();
//            }
//            // Keep Alive
//            case 0x04 -> output.write(keepAlive(socketReader.getCurrentState(), data.readLong()));
//            // Cookie Request 0x00
//            // Plugin Message 0x01
//            // Pong 0x04
//            // Reset Chat 0x06
//            // Registry 0x07
//            // Remove Resource Pack (resourcePack Pop) Config 0x08
//            // Add Resource Pack (resource Pack Push) Config 0x09
//            // Store Cookie 0x0A
//            // Transfer 0x0B
//            // Feature Flags 0x0C
//            // Tags 0x0D
//            // KnownPacks 0x0E
//            // Custom Report Details 0x0F
//            // Server Links 0x10
//
//            //case 0x00, 0x01, 0x05, 0x06, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0F, 0x10 -> System.out.println("Ignored Config packet: " + packetID);
//            //default -> System.out.println("Unknown Config Packet ID: " + packetID);
//        }
//    }

}
