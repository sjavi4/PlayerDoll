package me.autobot.playerdoll.wrapper.packet;

import net.minecraft.network.protocol.game.PacketPlayInBlockDig;

public interface WrapperServerboundPlayerActionPacket_Action {
    PacketPlayInBlockDig.EnumPlayerDigType START_DESTROY_BLOCK = PacketPlayInBlockDig.EnumPlayerDigType.a;
    PacketPlayInBlockDig.EnumPlayerDigType ABORT_DESTROY_BLOCK = PacketPlayInBlockDig.EnumPlayerDigType.b;
    PacketPlayInBlockDig.EnumPlayerDigType STOP_DESTROY_BLOCK = PacketPlayInBlockDig.EnumPlayerDigType.c;


}
