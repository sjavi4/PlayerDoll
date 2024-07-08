package me.autobot.playerdoll.packet.factory;

import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.event.DollRespawnEvent;
import me.autobot.playerdoll.packet.Packets;
import me.autobot.playerdoll.socket.io.SocketReader;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class Packet_v1_20_R4 extends Packet_v1_20_R3 {
    public Packet_v1_20_R4(SocketReader socketReader) {
        super(socketReader);
    }

    @Override
    protected int getProtocol() {
        return 766;
    }

    @Override
    protected int getKeepAlivePacketId(SocketReader.ConnectionState state) {
        return state == SocketReader.ConnectionState.PLAY ? 0x18 : 0x04;
    }
    @Override
    protected int getConfigAckId() {
        return 0x03;
    }
    @Override
    protected int getRespawnPacketId() {
        return 0x09;
    }

//    @Override
//    public void processLogin(int packetID, DataInputStream data, int dataLength) throws IOException {
//        switch (packetID) {
//            // Disconnect Packet
//            case 0x00 -> socketReader.endStream();
//            // Login Success (Profile)
//            case 0x02 -> {
//                output.write(loginAck());
//                socketReader.nextState();
//            }
//            // Setup Compression
//            case 0x03 -> socketReader.setCompressionThreshold(Packets.readVarInt(data));
//            // Encryption Packet, Plugin Message, Cookie Request
//            //case 0x01, 0x04, 0x05 -> {}
//            //default -> System.out.println("Unknown Login Packet ID: " + packetID);
//        }
//    }

    @Override
    public void processConfiguration(int packetID, DataInputStream data, int dataLength) throws IOException {
        switch (packetID) {
            // Disconnect
            case 0x02 -> {
                PlayerDoll.LOGGER.info("Client disconnected (config phase)");
                //System.out.println("Config Disconnect");
                socketReader.endStream();
            }
            // Finish Configuration
            case 0x03 -> {
                //System.out.println("Finish Config");
                output.write(configAck());
                socketReader.nextState();
            }
            // Keep Alive
            case 0x04 -> output.write(keepAlive(socketReader.getCurrentState(), data.readLong()));

            // Cookie Request 0x00
            // Plugin Message 0x01
            // Pong 0x04
            // Reset Chat 0x06
            // Registry 0x07
            // Remove Resource Pack (resourcePack Pop) Config 0x08
            // Add Resource Pack (resource Pack Push) Config 0x09
            // Store Cookie 0x0A
            // Transfer 0x0B
            // Feature Flags 0x0C
            // Tags 0x0D
            // KnownPacks 0x0E
            //case 0x00, 0x01, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E -> System.out.println("Ignored Config packet: " + packetID);
            //default -> System.out.println("Unknown Config Packet ID: " + packetID);
        }
    }

    @Override
    public void processPlay(int packetID, DataInputStream data, int dataLength) throws IOException {
        switch (packetID) {
            // Disconnect
            case 0x1D -> {
                PlayerDoll.LOGGER.info("Client disconnected (play phase)");
                //System.out.println("Play Disconnect Packet");
                socketReader.endStream();
            }
            // Game Event (Folia end Credits)
            case 0x22 -> {
                byte event = data.readByte();
                //float value = data.readFloat();
                // end Credits
                if (event == 0x04) {
                    output.write(requestRespawn());
                }
            }
            // Keep Alive
            case 0x26 -> output.write(keepAlive(socketReader.getCurrentState(), data.readLong()));
            // Death Screen
            case 0x3C -> {
                int entityId = Packets.readVarInt(data);
                Player player = DollManager.ONLINE_DOLLS.get(socketReader.profile.getId()).getBukkitPlayer();
                if (entityId == player.getEntityId()) {
                    output.write(requestRespawn());
                    PlayerDoll.scheduler.globalTaskDelayed(() -> PlayerDoll.callSyncEvent(new DollRespawnEvent(player)), 5);
                }
            }
            // Sync Pos
            case 0x40 -> {
                double x = data.readDouble();
                double y = data.readDouble();
                double z = data.readDouble();
                float yaw = data.readFloat();
                float pitch = data.readFloat();
                byte flag = data.readByte();
                int id = Packets.readVarInt(data);
                output.write(acceptTeleport(id));
            }
            case 0x46 -> {
                // Weird problem when sending resource pack respond
                //System.out.println("ResourcePack Push (Play)");
                UUID uuid = Packets.readUUID(data);
                String url = Packets.readString(data);
                String hash = Packets.readString(data);
                boolean forced = data.readBoolean();
                if (forced) {
                    //System.out.println("It is a forced Resource Pack");
                    //System.out.println("Not implemented, not respond to this");
                } else {
                    //System.out.println("It is an optional Resource Pack, Skip");
                    resourcePackPush(socketReader.getCurrentState(), uuid, ResourcePackStatus.DECLINED);
                }
            }
            //default -> System.out.println("Unknown Play Packet ID: " + packetID);
        }
    }
}
