package me.autobot.playerdoll.listener;

import me.autobot.playerdoll.doll.DollData;
import me.autobot.playerdoll.wrapper.InitialHandler;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class HandshakeListener implements Listener {
    @EventHandler
    public void onHandshake(PlayerHandshakeEvent event) {
        // Add Packet Listener
        int intent = event.getHandshake().getRequestedProtocol();
        if (intent != 2) {
            return;
        }
        System.out.println("Found Login Intent");
        PendingConnection connection = event.getConnection();
        String address = connection.getSocketAddress().toString();
        DollData.DOLL_DATA_LIST.stream().filter(dollData -> dollData.getAddress().equals(address))
                .findFirst()
                .ifPresent(dollData -> {
                    System.out.println("Found Doll Address in Handshake");
                    System.out.println("Setup Packet Listener");
                    InitialHandler handler = new InitialHandler(connection);
                    //dollData.setPacketListener(new PlayerToBungeeListener(dollData, handler.channelWrapper().channel()));
                    modifyLoginStatus(handler, dollData.getUuid(), dollData.getFullName());
                });
    }

    private static void modifyLoginStatus(InitialHandler handler, UUID dollUUID, String dollName) {
        System.out.println("Modify Doll Login");
        handler.setUUID(dollUUID);
        handler.setName(dollName);
        handler.setState(InitialHandler.stateEnums[InitialHandler.stateEnums.length -1]);
        System.out.println("Doll modify call Finish (Start Login)");
        handler.finish();
    }
}
