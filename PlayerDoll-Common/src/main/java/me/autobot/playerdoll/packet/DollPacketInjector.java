package me.autobot.playerdoll.packet;

import io.netty.channel.*;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.util.ReflectionUtil;

public class DollPacketInjector extends ChannelDuplexHandler {
    public boolean allowPacketSend = true;

    private static final Class<?> loginPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.game.PacketPlayOutLogin");
    private static final Class<?> keepAlivePacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.common.ClientboundKeepAlivePacket");
    private static final Class<?> disconnectPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.common.ClientboundDisconnectPacket");
    //private static final Class<?> playStartConfigPacketClass = ReflectionUtil.getClass("net.minecraft.network.protocol.common.ClientboundStartConfigurationPacket");
    //private static final Class<?> playerPositionClass = ReflectionUtil.getClass("net.minecraft.network.protocol.game.PacketPlayOutPosition");
    public DollPacketInjector(Channel connectionChannel) {
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
            if (keepAlivePacketClass.isInstance(msg) || disconnectPacketClass.isInstance(msg) ){ //|| playerPositionClass.isInstance(msg)) {
//                if (keepAlivePacketClass.isInstance(msg)) {
//                    System.out.println("Server send KeepAlive: " + keepAlivePacketClass.getMethod("a").invoke(msg));
//                }
                //System.out.println(playStartConfigPacketClass.isInstance(msg));
                //System.out.println(acceptTeleportPacketClass.isInstance(msg));
                super.write(ctx, msg, promise);
            }
            return;
        }
        super.write(ctx, msg, promise);
    }
}
