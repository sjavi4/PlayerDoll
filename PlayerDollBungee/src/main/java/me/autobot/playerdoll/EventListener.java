package me.autobot.playerdoll;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class EventListener implements Listener {

    // Avoid multi doll connections at the same time, assign 63 temporary single letter name
    /*
    private final char[] names = new char[] {
        48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78,
        79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 95, 97, 98, 99, 100, 101, 102, 103, 104, 105,
        106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122
    };
    @EventHandler
    public void onHandshake(PlayerHandshakeEvent event) {
        PendingConnection pendingConnection = event.getConnection();
        String address = pendingConnection.getSocketAddress().toString();
        try {
            Class<?> initialHandlerClass = Class.forName("net.md_5.bungee.connection.InitialHandler");
            if (initialHandlerClass.isInstance(pendingConnection)) {
                //System.out.println("pendingConnection is initialHandler");
                Object initialHandler = initialHandlerClass.cast(pendingConnection);

                Proxy.dollConnections.put(address,initialHandler);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

     */
/*
    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (player.getName().startsWith("-")) {
            try {
                Class<?> userConnectionClass = Class.forName("net.md_5.bungee.UserConnection");
                Object userConnection = userConnectionClass.cast(player);
                Field chField = userConnectionClass.getDeclaredField("ch");
                chField.setAccessible(true);
                Object ch = chField.get(userConnection);
                Class<?> channelWrapperClass = Class.forName("net.md_5.bungee.netty.ChannelWrapper");
                if (channelWrapperClass.isInstance(ch)) {
                    Channel channel = (Channel) channelWrapperClass.getMethod("getHandle").invoke(ch);
                    channel.pipeline().addLast("doll_control", new DollChannelInitializer());
                }
            } catch (ClassNotFoundException | NoSuchFieldException | InvocationTargetException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

 */
    /*

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        Map<UUID,String> dollNames = Proxy.dollNames;
        ProxiedPlayer player = event.getPlayer();
        if (!dollNames.containsKey(player.getUniqueId())) {
            return;
        }
        Server callerServer = Proxy.callerServer.get(player.getUniqueId());
        if (event.getTarget() != callerServer.getInfo()) {
            event.setTarget(callerServer.getInfo());
        }
        Proxy.callerServer.remove(player.getUniqueId());
        Proxy plugin = Proxy.getInstance();

        modifyDollName(player, Character.toString(names[(dollNames.size()-1)%63]));
        TaskScheduler scheduler = ProxyServer.getInstance().getScheduler();
        scheduler.schedule(plugin,() ->{
            modifyDollName(player, dollNames.get(player.getUniqueId()));
            dollNames.remove(player.getUniqueId());
        },5, TimeUnit.MILLISECONDS);

     */
        /*
        Runnable serverConnectorTask = () -> {
            try {
                Class<?> handlerBossClass = Class.forName("net.md_5.bungee.netty.HandlerBoss");
                Class<?> userConnectionClass = Class.forName("net.md_5.bungee.UserConnection");

                    //System.out.println("pendingConnection is initialHandler");
                Object userConnection = userConnectionClass.cast(player);

                Field chField = userConnectionClass.getDeclaredField("ch");
                chField.setAccessible(true);
                Object ch = chField.get(userConnection);
                Class<?> channelWrapperClass = Class.forName("net.md_5.bungee.netty.ChannelWrapper");
                if (channelWrapperClass.isInstance(ch)) {
                    Channel channel = (Channel) channelWrapperClass.getMethod("getHandle").invoke(ch);
                    ChannelHandler handler = channel.pipeline().last();
                    if (handlerBossClass.isInstance(handler)) {
                        Object handlerBoss = handlerBossClass.cast(handler);
                        Field channelField = handlerBossClass.getDeclaredField("channel");
                        channelField.setAccessible(true);
                        boolean quit = true;
                        while (quit) {
                            Field handlerField = handlerBossClass.getDeclaredField("handler");
                            handlerField.setAccessible(true);
                            //System.out.println(handlerField.get(handlerBoss));
                            Class<?> serverConnectorClass = Class.forName("net.md_5.bungee.ServerConnector");
                            if (serverConnectorClass.isInstance(handlerField)) {
                                quit = false;
                            }
                        }
                        System.out.println("True");
                        Channel realConnectionChannel = (Channel) channelWrapperClass.getMethod("getHandle").invoke(channelField.get(handlerBoss));
                        realConnectionChannel.pipeline().addLast(new DollChannelInitializer());
                        System.out.println("LocalAddr: "+ realConnectionChannel.localAddress());
                        System.out.println("RemoteAddr: "+ realConnectionChannel.remoteAddress());

                    }
                }
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        };

        Thread thread = new Thread(serverConnectorTask);
        thread.start();

        //scheduler.schedule(plugin,serverConnectorTask, 10,TimeUnit.NANOSECONDS);

         */
    /*
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (player.getName().length() == 1 || player.getName().startsWith("-")) {
            event.getPlayer().getPendingConnection().disconnect();
            event.getPlayer().disconnect();
        }
    }

    public static void modifyDollName(ProxiedPlayer player, String name) {
        try {
            Class<?> userConnectionClass = Class.forName("net.md_5.bungee.UserConnection");
            Object userConnection = userConnectionClass.cast(player);
            Field userName = userConnectionClass.getDeclaredField("name");
            userName.setAccessible(true);
            userName.set(userConnection, name);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

     */
}
