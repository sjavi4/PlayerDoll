package me.autobot.playerdoll.connection;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.config.impl.BasicConfig;
import me.autobot.playerdoll.api.connection.Connection;
import me.autobot.playerdoll.api.resolver.Connections;
import me.autobot.playerdoll.netty.handler.HandshakeHandler;
import me.autobot.playerdoll.netty.handler.PacketHandler;
import me.autobot.playerdoll.netty.handler.StateChangePacketHandler;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;

public class DollConnection extends Connection {

    private static final EventLoopGroup EVENT_LOOP_GROUP = new NioEventLoopGroup(0, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Netty Doll #%d").build());


    public DollConnection() {
        BasicConfig basicConfig = PlayerDollAPI.getConfigLoader().getBasicConfig();
        if (basicConfig.forceProxyIP.getValue()) {
            IP = basicConfig.proxyIP.getValue();
            PORT = basicConfig.proxyPort.getValue();
            HOST = new InetSocketAddress(IP, PORT);
        }
    }

    @Override
    public void connect(GameProfile profile, Player caller) {
        new Bootstrap().group(EVENT_LOOP_GROUP)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        try {
                            channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                        } catch (ChannelException ignored) {
                        }

                        ChannelPipeline channelpipeline = channel.pipeline();

                        // NMS
                        configureSerialization(channelpipeline);

                        // Custom
                        channelpipeline.addLast("doll_packet_handler", new PacketHandler(profile.getId()));
                        // Handshake
                        channelpipeline.addLast("doll_handshake_handler", new HandshakeHandler(HOST, profile, caller));
                        channelpipeline.addLast("state_change_handler", new StateChangePacketHandler());


                    }
                })
                .connect(HOST.getAddress(), HOST.getPort());
    }

    @Override
    public void shutDown() {
        EVENT_LOOP_GROUP.shutdownGracefully().syncUninterruptibly();
    }

    private static void configureSerialization(ChannelPipeline pipeline) {
        if (Connections.configureSerializationMethod.getParameterCount() == 3) {
            ReflectionUtil.invokeMethod(Connections.configureSerializationMethod, null, pipeline, Connections.packetFlow_Clientbound, null);
        } else if (Connections.configureSerializationMethod.getParameterCount() == 4) {
            ReflectionUtil.invokeMethod(Connections.configureSerializationMethod, null, pipeline, Connections.packetFlow_Clientbound, false, null);
        }
    }

}
