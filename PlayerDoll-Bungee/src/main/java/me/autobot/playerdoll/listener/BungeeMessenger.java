package me.autobot.playerdoll.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.autobot.playerdoll.doll.DollData;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class BungeeMessenger implements Listener {
    @EventHandler
    public void onMessage(PluginMessageEvent event) {
        // This Event must require 1 player on server
        String tag = event.getTag();
        if (!tag.equals("playerdoll:doll")) {
            return;
        }
        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        int id = input.readInt();
        switch (id) {
            // Add Doll Address
            case 0 -> {
                System.out.println("get Packet 0, add DollData");
                String address = input.readUTF();
                UUID uuid = UUID.fromString(input.readUTF());
                String name = input.readUTF();
                boolean autoJoin = input.readBoolean();
                DollData dollData;
                if (autoJoin) {
                    dollData = new DollData(address, uuid, name, input.readUTF());
                } else {
                    dollData = new DollData(address, uuid, name, UUID.fromString(input.readUTF()));
                }
                DollData.DOLL_DATA_LIST.add(dollData);

                ByteArrayDataOutput output = ByteStreams.newDataOutput();
                output.writeInt(2);
                output.writeUTF(uuid.toString());
                dollData.getTargetServer().sendData("playerdoll:doll", output.toByteArray());

            }
            case 1 -> {
                System.out.println("get Close Listener Packet");
                UUID dollUUID = UUID.fromString(input.readUTF());
                // Deject Listener
                DollData.DOLL_DATA_LIST.stream().filter(dollData -> dollData.getUuid().equals(dollUUID))
                        .findFirst()
                        .ifPresent(dollData -> {
                            //dollData.getListener().sendSuccess.run();
                            //dollData.getListener().close();
                        });
            }
            case 2 -> {
                System.out.println("get clear DollData packet");
                UUID dollUUID = UUID.fromString(input.readUTF());
                DollData.DOLL_DATA_LIST.removeIf(dollData -> dollData.getUuid().equals(dollUUID));
            }
        }
    }
}