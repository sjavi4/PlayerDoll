package me.autobot.playerdoll.doll;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.netty.ConnectionFetcher;
import me.autobot.playerdoll.util.ReflectionUtil;

import java.util.function.Consumer;

public class PlayerConvertInjector extends ChannelDuplexHandler {
    private static final Class<?> loginAckPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket");
    private static final Class<?> dollLoginListenerClass = ReflectionUtil.getPluginNMSClass("connection.login.DollLoginListener");

    // Just trigger the static block
    //private static final Class<?> playerLoginListenerClass = ReflectionUtil.getPluginNMSClass("connection.login.PlayerLoginListener");
    private final Object connection;
    private final Channel channel;
    public static Consumer<Object> swapListenerFunc;
    private boolean suspend = false;
    public PlayerConvertInjector(Object connection, Channel channel) {
        this.connection = connection;
        this.channel = channel;
        channel.pipeline().addBefore("packet_handler", "player_convert_injector", this);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (suspend) {
            return;
        }
        if (loginAckPacketClass.isInstance(msg)) {
            suspend = true;
            PlayerDoll.LOGGER.info("Found Acknowledged Packet for Player");
            Object packetListener = ConnectionFetcher.getPacketListener(connection);
            if (dollLoginListenerClass.equals(packetListener.getClass())) {
                // Doll Connection
                channel.pipeline().remove("player_convert_injector");
                suspend = false;
                super.channelRead(ctx, msg);
                return;
            }
            // Main Thread
            Runnable t = () -> {
                swapListenerFunc.accept(packetListener);
                channel.pipeline().remove("player_convert_injector");
                suspend = false;
                try {
                    super.channelRead(ctx, msg);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            if (PlayerDoll.serverBranch == PlayerDoll.ServerBranch.SPIGOT) {
                PlayerDoll.scheduler.globalTask(t);
            } else {
                t.run();
            }
            return;
            //swapListenerFunc.accept(packetListener);
            //channel.pipeline().remove("player_convert_injector");
            //CursedConnection.setPacketListener(connection, swapListenerFunc.apply(packetListener));
            //finish = true;
            //return;
        }
        super.channelRead(ctx, msg);
    }
}
