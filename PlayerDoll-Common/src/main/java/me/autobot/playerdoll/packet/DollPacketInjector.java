package me.autobot.playerdoll.packet;

import io.netty.channel.*;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.util.ReflectionUtil;

import java.util.HashSet;
import java.util.Set;

public class DollPacketInjector extends ChannelDuplexHandler {
    public boolean allowPacketSend = true;

    private static final Set<Class<?>> packetSet = new HashSet<>();

    private static final Class<?> loginPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.game.PacketPlayOutLogin");

    static {
        String s = "net.minecraft.network.protocol.";
        Class<?> keepAlivePacketClass = ReflectionUtil.getClass(s + "common.ClientboundKeepAlivePacket");
        Class<?> disconnectPacketClass = ReflectionUtil.getClass(s + "common.ClientboundDisconnectPacket");
        Class<?> deathScreenPacketClass = ReflectionUtil.getClass(s + "game.ClientboundPlayerCombatKillPacket");

        packetSet.add(keepAlivePacketClass);
        packetSet.add(disconnectPacketClass);
        packetSet.add(deathScreenPacketClass);
        if (PlayerDoll.serverBranch == PlayerDoll.ServerBranch.FOLIA) {
            Class<?> gameEventPacketClass = ReflectionUtil.getClass(s + "game.PacketPlayOutGameStateChange");
            Class<?> playerPositionPacketClass = ReflectionUtil.getClass(s + "game.PacketPlayOutPosition");
            packetSet.add(gameEventPacketClass);
            packetSet.add(playerPositionPacketClass);
        }
    }

    public DollPacketInjector(Channel connectionChannel) {
        ChannelPipeline pipeline = connectionChannel.pipeline();
        if (pipeline.get("doll_packet_injector") != null) {
            return;
        }
        connectionChannel.pipeline().addBefore("packet_handler", "doll_packet_injector", this);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //System.out.println(msg.getClass());
        if (allowPacketSend && loginPacketClass.isInstance(msg)) {
            allowPacketSend = false;
            PlayerDoll.LOGGER.info("Doll Joined Successfully, Suspend Server-side Packet Sending.");
            return;
        } else if (!allowPacketSend) {
            if (packetSet.contains(msg.getClass())) {
                super.write(ctx, msg, promise);
            }
            return;
        }
        super.write(ctx, msg, promise);
    }
}
