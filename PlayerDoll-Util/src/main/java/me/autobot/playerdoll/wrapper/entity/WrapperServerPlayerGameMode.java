package me.autobot.playerdoll.wrapper.entity;


import me.autobot.playerdoll.wrapper.WrapperServerLevel;
import me.autobot.playerdoll.wrapper.block.WrapperBlockPos;
import me.autobot.playerdoll.wrapper.phys.WrapperBlockHitResult;

public interface WrapperServerPlayerGameMode {

    void handleBlockBreakAction(WrapperBlockPos blockPos, Enum<?> type, Enum<?> direction, int maxBuildHeight, int i);

    WrapperInteractionResult useItemOn(Object player, WrapperServerLevel serverLevel, Object itemInHand, Enum<?> hand, WrapperBlockHitResult blockHit);

    WrapperInteractionResult useItem(Object player, WrapperServerLevel level, Object handItem, Enum<?> hand);
}
