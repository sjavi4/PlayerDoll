package me.autobot.playerdoll.api.doll;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.connection.ConnectionFetcher;
import me.autobot.playerdoll.api.constant.AbsServerBranch;
import me.autobot.playerdoll.api.resolver.Connections;

import java.util.function.Consumer;

public class PlayerConvertInjector extends ChannelDuplexHandler {
    private static final Class<?> loginAckPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket");

    // Just trigger the static block
    //private static final Class<?> playerLoginListenerClass = ReflectionUtil.getPluginNMSClass("connection.login.PlayerLoginListener");

    private final Channel channel;
    private final Object packetListener;
    public static Consumer<Object> swapListenerFunc;
    private boolean suspend = false;

    public PlayerConvertInjector(Object connection, Channel channel) {
        this.channel = channel;
        channel.pipeline().addBefore("packet_handler", "player_convert_injector", this);
        packetListener = ConnectionFetcher.getPacketListener(connection);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (suspend) {
            return;
        }
        if (loginAckPacketClass.isInstance(msg)) {
            suspend = true;
            PlayerDollAPI.getLogger().info("Found Acknowledged Packet for Player");
            if (Connections.dollCustomLoginListenerClass.equals(packetListener.getClass())) {
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
            if (PlayerDollAPI.getServerBranch() == AbsServerBranch.SPIGOT) {
                PlayerDollAPI.getScheduler().globalTask(t);
            } else {
                t.run();
            }
            return;
        }
        super.channelRead(ctx, msg);
    }
}
