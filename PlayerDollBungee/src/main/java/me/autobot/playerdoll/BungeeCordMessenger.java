package me.autobot.playerdoll;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;

public class BungeeCordMessenger implements Listener {

    public static final Set<PendingMessage> PENDING_MESSAGE = new HashSet<>();
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        String tag = event.getTag();
        if (tag.equals("playerdoll:doll")) {
            processDoll(event);
        }
        /*
        if (tag.equals("playerdoll:player")) {
            processPlayer(event);
        } else if (tag.equals("playerdoll:doll")) {
            processDoll(event);
        }

         */
    }

    private void processDoll(PluginMessageEvent event) {
        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        /*
        if (input.readInt() == 1) {
            String address = input.readUTF();
            UUID dollUUID = UUID.fromString(input.readUTF());
            String dollName = input.readUTF();

            Map<String, Object> dollConnections = Proxy.dollConnections;
            if (!dollConnections.containsKey(address)) {
                dollConnections.clear();
                return;
            }
            Object initialHandler = dollConnections.get(address);
            setup(initialHandler, dollUUID, dollName);
            dollConnections.remove(address);
        }
         */
        int id = input.readInt();
        if (id == 1) {
            String dollUUID = input.readUTF(); // doll UUID
            String dollName = input.readUTF(); // doll Name
            String callerUUID = input.readUTF(); // caller UUID

            ProxiedPlayer caller = Proxy.getInstance().getProxy().getPlayer(UUID.fromString(callerUUID));
            if (caller == null) {
                return;
            }
            PendingMessage pendingMessage = new PendingMessage(caller, dollUUID, dollName);
            PENDING_MESSAGE.add(pendingMessage);

            // Query All Servers
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeInt(1);
            output.writeUTF(dollUUID); // doll UUID
            Proxy.getInstance().getProxy().getServers().forEach((s,i) -> i.sendData( "playerdoll:doll", output.toByteArray()));
        } else if (id == 2) {
            // process result
            String dollUUID = input.readUTF();
            for (PendingMessage message : PENDING_MESSAGE) {
                if (message.dollUUID.equals(dollUUID)) {
                    PENDING_MESSAGE.remove(message);
                    boolean exist = input.readBoolean();
                    ProxiedPlayer player = message.player;
                    if (player == null) {
                        return;
                    }
                    ByteArrayDataOutput output = ByteStreams.newDataOutput();
                    output.writeInt(2);
                    output.writeBoolean(exist); // exist
                    if (exist) {
                        message.player.getServer().sendData("playerdoll:doll", output.toByteArray());
                        return;
                    }
                    output.writeUTF(message.dollUUID); // doll UUID
                    output.writeUTF(message.dollName); // doll Name
                    output.writeUTF(message.player.getUniqueId().toString()); // caller UUID

                    message.player.getServer().sendData("playerdoll:doll", output.toByteArray());
                    break;
                }
            }
        }
    }
/*
    private void processPlayer(PluginMessageEvent event) {
        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        ProxiedPlayer caller = ProxyServer.getInstance().getPlayer(UUID.fromString(input.readUTF()));
        UUID doll = UUID.fromString(input.readUTF());
        Proxy.callerServer.put(doll, caller.getServer());
    }
    private void setup(Object initialHandler, UUID uuid, String name) {
        Proxy.dollNames.put(uuid,name);
        Class<?> initialHandlerClass = initialHandler.getClass();
        for (Field field : initialHandlerClass.getDeclaredFields()) {
            if (field.getType() == UUID.class) {
                field.setAccessible(true);
                try {
                    field.set(initialHandler,uuid);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else if (field.getType() == String.class && field.getName().equals("name")) {
                field.setAccessible(true);
                try {
                    field.set(initialHandler, name);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else if (field.getType().isEnum()) {
                field.setAccessible(true);
                try {
                    Method method = field.getType().getMethod("values");
                    method.setAccessible(true);
                    Enum<?>[] states = (Enum<?>[]) method.invoke(null);
                    // Set to state.FINISHING
                    field.set(initialHandler,states[states.length-1]);
                } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            Method finish = initialHandlerClass.getDeclaredMethod("finish");
            finish.setAccessible(true);
            finish.invoke(initialHandler);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

 */
}
