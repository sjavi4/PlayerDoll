package me.autobot.playerdoll.network;

import io.netty.channel.*;
import net.minecraft.network.Connection;

import java.util.function.Consumer;

public class PlayerConvertInjector extends ChannelDuplexHandler {
    private final Consumer<Object> readPacketTask;
    public PlayerConvertInjector(Channel connectionChannel, Consumer<Object> readPacketTask) {
        this.readPacketTask = readPacketTask;
        connectionChannel.pipeline().addBefore("packet_handler", "player_convert_injector", this);
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        readPacketTask.accept(msg);
        super.channelRead(ctx, msg);
    }
}
