package me.autobot.playerdoll;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.md_5.bungee.protocol.MinecraftEncoder;
import net.md_5.bungee.protocol.Protocol;

public class DollChannelInitializer extends ChannelDuplexHandler {
    //boolean once = false;
/*

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx.channel().pipeline().get(MinecraftEncoder.class).getProtocol() != Protocol.GAME) {
            //System.out.println("Read Packet: "+ msg);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        */
        /*

        if (ctx.channel().pipeline().get(MinecraftEncoder.class).getProtocol() == Protocol.HANDSHAKE) {
            System.out.println("HandShake: " + msg);
        }
        if (ctx.channel().pipeline().get(MinecraftEncoder.class).getProtocol() == Protocol.LOGIN) {
            if (!once) {
                System.out.println("Sleep");
                once = true;
                Thread.sleep(1000L);
            }
        }

        */
    /*
        if (ctx.channel().pipeline().get(MinecraftEncoder.class).getProtocol() != Protocol.GAME) {
            //System.out.println("Write Packet: "+ msg);
        }
        super.write(ctx, msg, promise);
    }

     */

}
