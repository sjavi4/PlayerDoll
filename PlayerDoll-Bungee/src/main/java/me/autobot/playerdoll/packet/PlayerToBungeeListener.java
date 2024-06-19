package me.autobot.playerdoll.packet;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.autobot.playerdoll.DollProxy;
import me.autobot.playerdoll.doll.DollData;
import me.autobot.playerdoll.listener.ServerConnectListener;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.packet.LoginAcknowledged;
import net.md_5.bungee.protocol.packet.LoginSuccess;

public class PlayerToBungeeListener extends ChannelDuplexHandler {
    private final DollData dollData;
    public final Channel channel;

    public boolean pauseSend = false;
    public PlayerToBungeeListener(DollData dollData, Channel channel) {
        this.dollData = dollData;
        this.channel = channel;
        if (channel.pipeline().get("doll_listener") == null) {
            channel.pipeline().addBefore("inbound-boss", "doll_listener",this);
        }
    }

    public void close() {
        if (channel.pipeline().get("doll_listener") != null) {
            pauseSend = false;
            channel.pipeline().remove(this);
        }
    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        if (msg instanceof PacketWrapper wrapper) {
//            System.out.println("Read: " + wrapper.packet);
//            if (!readPacket(wrapper)) {
//                // login Ack, Encrypt
//                return;
//            }
//        }
//        super.channelRead(ctx, msg);
//    }
//
//    private boolean readPacket(PacketWrapper wrapper) {
//        boolean send = false;
//        if (wrapper.packet instanceof LoginAcknowledged loginAcknowledged) {
//
//        }
//
//        return true;
//    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //SetCompression, Login Success
        // Write packet to client
        if (pauseSend) {
            return;
        }
        if (msg instanceof LoginSuccess success) {
            pauseSend = true;
            success.setUsername(dollData.getFullName());
            processDollLogin(dollData);
            return;
        }
        super.write(ctx, msg, promise);
    }

    private void processDollLogin(DollData dollData) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        // Start capture Login Listener
        output.writeInt(0);
        output.writeUTF(dollData.getUuid().toString()); // doll UUID
        output.writeUTF(dollData.getListener().channel.remoteAddress().toString());
        dollData.getTargetServer().sendData("playerdoll:doll", output.toByteArray());

    }
}
