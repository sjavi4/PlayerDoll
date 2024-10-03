package me.autobot.playerdoll.netty;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.netty.handler.HandshakeHandler;
import me.autobot.playerdoll.netty.handler.PacketHandler;
import me.autobot.playerdoll.netty.handler.StateChangePacketHandler;
import me.autobot.playerdoll.netty.packet.ClientPackets;
import me.autobot.playerdoll.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class DollConnection {

    public static final boolean BUNGEECORD = ConnectionFetcher.isBungeeCord;
    public static String IP = Bukkit.getIp();
    public static int PORT = Bukkit.getPort();
    public static InetSocketAddress HOST = new InetSocketAddress(IP, PORT);
    public static final boolean PRE_1_20_4;
    public static final int protocolNumber;
    private static final Method configureSerializationMethod;
    public static final Object packetFlow_Clientbound;
    public static final Object packetFlow_Serverbound;
    private static final EventLoopGroup EVENT_LOOP_GROUP = new NioEventLoopGroup(0, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Netty Doll #%d").build());

    public static final Map<UUID, Channel> DOLL_CONNECTIONS = new ConcurrentHashMap<>();

    static {
        BasicConfig basicConfig = BasicConfig.get();
        if (BUNGEECORD || basicConfig.forceProxyIP.getValue()) {
            IP = basicConfig.proxyIP.getValue();
            PORT = basicConfig.proxyPort.getValue();
            HOST = new InetSocketAddress(IP, PORT);
        }

        PRE_1_20_4 = ReflectionUtil.gameVersion.matches("v1_20_R2|v1_20_R3");
        protocolNumber = switch (ReflectionUtil.gameVersion) {
            case "v1_20_R2" -> 764;
            case "v1_20_R3" -> 765;
            case "v1_20_R4" -> 766;
            case "v1_21_R1" -> 767;
            default -> throw new IllegalStateException("Unsupported game version: " + ReflectionUtil.gameVersion);
        };
        // Connection.class / NetworkManager.class
        Class<?> connectionClass = ReflectionUtil.getClass("net.minecraft.network.NetworkManager");
        // PacketFlow.class / EnumProtocolDirection.class
        Class<?> packetFlowClass = ReflectionUtil.getClass("net.minecraft.network.protocol.EnumProtocolDirection");
        // BandwidthDebugMonitor.class
        Class<?> bandwidthDebugMonitorClass = ReflectionUtil.getClass("net.minecraft.network.BandwidthDebugMonitor");

        Objects.requireNonNull(connectionClass, "Connection.class");
        Objects.requireNonNull(packetFlowClass, "PacketFlow.class");

        Object[] enums = packetFlowClass.getEnumConstants();
        Objects.requireNonNull(enums, "PacketFlow$enums");

        packetFlow_Serverbound = enums[0];
        packetFlow_Clientbound = enums[1];



        configureSerializationMethod = Arrays.stream(connectionClass.getDeclaredMethods())
                .filter(method -> Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers()) && method.getReturnType() == void.class)
                .filter(method -> {
                    Class<?>[] param = method.getParameterTypes();
                    if (method.getParameterCount() == 3) {
                        return param[0] == ChannelPipeline.class && param[1] == packetFlowClass && param[2] == bandwidthDebugMonitorClass;
                    } else if (method.getParameterCount() == 4) {
                        return param[0] == ChannelPipeline.class && param[1] == packetFlowClass && param[2] == boolean.class && param[3] == bandwidthDebugMonitorClass;
                    }
                    return false;
                })
                .findFirst().orElseThrow();
        configureSerializationMethod.setAccessible(true);
    }

    public static void connect(String name, UUID uuid, Player caller) {
        connect(new GameProfile(uuid, name), caller);
    }
    public static void connect(GameProfile profile, Player caller) {
        new Bootstrap().group(EVENT_LOOP_GROUP)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {
//                        if (!channel.eventLoop().inEventLoop()) {
//                            Bukkit.getLogger().warning("[PlayerDoll] New connection is not inside EventLoop, canceling");
//                        }
//                        if (channel.eventLoop().isShutdown()) {
//                            Bukkit.getLogger().warning("[PlayerDoll] EventLoop has already shutdown");
//                        }
//                        if (!channel.eventLoop().inEventLoop() || channel.eventLoop().isShutdown()) {
//                            channel.close();
//                            return;
//                        }

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

    public static void shutDown() {
        EVENT_LOOP_GROUP.shutdownGracefully();
    }

    private static void configureSerialization(ChannelPipeline pipeline) {
        if (configureSerializationMethod.getParameterCount() == 3) {
            ReflectionUtil.invokeMethod(configureSerializationMethod, null, pipeline, packetFlow_Clientbound, null);
        } else if (configureSerializationMethod.getParameterCount() == 4) {
            ReflectionUtil.invokeMethod(configureSerializationMethod, null, pipeline, packetFlow_Clientbound, false, null);
        }
    }
}
