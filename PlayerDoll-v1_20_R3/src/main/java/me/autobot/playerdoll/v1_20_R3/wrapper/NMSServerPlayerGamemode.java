package me.autobot.playerdoll.v1_20_R3.wrapper;

import me.autobot.playerdoll.wrapper.*;
import me.autobot.playerdoll.wrapper.entity.WrapperServerPlayerGameMode;
import me.autobot.playerdoll.wrapper.phys.WrapperBlockHitResult;
import me.autobot.playerdoll.wrapper.block.WrapperBlockPos;
import me.autobot.playerdoll.wrapper.entity.WrapperInteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class NMSServerPlayerGamemode extends Wrapper<ServerPlayerGameMode> implements WrapperServerPlayerGameMode {
    public NMSServerPlayerGamemode(ServerPlayerGameMode o) {
        super(o);
    }

    @Override
    public void handleBlockBreakAction(WrapperBlockPos blockPos, Enum<?> type, Enum<?> direction, int maxBuildHeight, int i) {
        source.handleBlockBreakAction((BlockPos) blockPos.toObj(), (ServerboundPlayerActionPacket.Action) type, (Direction) direction, maxBuildHeight, i);
    }

    @Override
    public WrapperInteractionResult useItemOn(Object player, WrapperServerLevel serverLevel, Object itemInHand, Enum<?> hand, WrapperBlockHitResult blockHit) {
        return new WrapperInteractionResult(source.useItemOn((ServerPlayer) player, (ServerLevel) serverLevel.toObj(), (ItemStack) itemInHand, (InteractionHand) hand, (BlockHitResult) blockHit.toObj()));
    }

    @Override
    public WrapperInteractionResult useItem(Object player, WrapperServerLevel level, Object handItem, Enum<?> hand) {
        return new WrapperInteractionResult(source.useItem((ServerPlayer) player, (Level) level.toObj(), (ItemStack) handItem, (InteractionHand) hand));
    }
}
