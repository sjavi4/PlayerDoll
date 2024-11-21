package me.autobot.playerdoll.api.action.type.builtin;

import me.autobot.playerdoll.api.action.Action;
import me.autobot.playerdoll.api.action.pack.AbsPackPlayer;
import me.autobot.playerdoll.api.action.pack.ActionPack;
import me.autobot.playerdoll.api.action.type.AbsActionType;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.wrapper.builtin.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

public class Use extends AbsActionType {
    public Use(boolean preventSpectator) {
        super(preventSpectator);
    }

    @Override
    public String registerName() {
        return "use";
    }

    @Override
    public boolean execute(BaseEntity player, Action action) {
        ActionPack ap = player.getActionPack();
        AbsPackPlayer packPlayer = ap.packPlayer;
        Player bukkitPlayer = player.getBukkitPlayer();
        if (ap.itemUseCooldown > 0)
        {
            ap.itemUseCooldown--;
            return true;
        }
        if (bukkitPlayer.getItemInUse() != null)
        {
            return true;
        }
        WHitResult<?> hit = ActionPack.getTarget(player);
        for (Enum<?> hand : packPlayer.getInteractionHandEnums())
        {
            Boolean executed = hit.parseResultForUse((blockHit) -> {
                // Block Hit Result
                packPlayer.resetLastActionTime();
                WServerLevel<?> world = packPlayer.serverLevel();

                WBlockPos<?> pos = blockHit.getBlockPos();
                WDirection.Direction side = blockHit.getDirection();
                if (pos.getY() < bukkitPlayer.getWorld().getMaxHeight() - (side == WDirection.Direction.UP ? 1 : 0) && world.mayInteract(packPlayer, pos))
                {
                    // WInteractionResult.Results result = player.gameMode.useItemOn(player, world, packPlayer.getItemInHand(hand), hand, blockHit);
                    WInteractionResult<?> result = packPlayer.useItemOn(world, packPlayer.getItemInHand(hand), hand, blockHit);
                    // 1.20.2 - 1.21.1 (consumeAction)
                    // 1.21.2+ (another check (result instanceof InteractionResult.Success success))
                    if (result.consumesAction())
                    {
                        // 1.20.2 - 1.21.1 (shouldSwing)
                        // 1.21.2+ (another check (success.swingSource() == InteractionResult.SwingSource.SERVER))
                        if (result.shouldSwing()) {
                            if (hand.ordinal() == 0) {
                                bukkitPlayer.swingMainHand();
                            } else {
                                bukkitPlayer.swingOffHand();
                            }
                        }
                        ap.itemUseCooldown = 3;
                        return true;
                    }
                }
                return null;
            }, (entityHit) -> {
                // Entity Hit Result
                packPlayer.resetLastActionTime();
                WEntity<?> entity = entityHit.getEntity();
                boolean handWasEmpty = ActionPack.isEmpty(packPlayer.getItemInHand(hand));
                boolean itemFrameEmpty = (entity instanceof ItemFrame) && ActionPack.isEmpty(((ItemFrame) entity).getItem());
                WVec3<?> relativeHitPos = entityHit.getLocation().subtract(entity.getX(), entity.getY(), entity.getZ());
                if (entity.interactAt(packPlayer, relativeHitPos, hand).consumesAction())
                {
                    ap.itemUseCooldown = 3;
                    return true;
                }
                // fix for SS itemframe always returns CONSUME even if no action is performed
                if (packPlayer.interactOn(entity, hand).consumesAction() && !(handWasEmpty && itemFrameEmpty))
                {
                    ap.itemUseCooldown = 3;
                    return true;
                }
                return null;
            });
            if (executed == null) {
                if (packPlayer.useItem(hand).consumesAction())
                {
                    ap.itemUseCooldown = 3;
                    return true;
                }
            } else if (executed) {
                return true;
            }

//            Object handItem = packPlayer.getItemInHand(hand);
//            // if (packPlayer.getGameMode().useItem(player, packPlayer.level(), handItem, hand).consumesAction())
//            if (packPlayer.useItem(hand).consumesAction())
//            {
//                ap.itemUseCooldown = 3;
//                return true;
//            }
        }
        return false;
    }

    @Override
    public void inactiveTick(BaseEntity player, Action action)
    {
        ActionPack ap = player.getActionPack();
        ap.itemUseCooldown = 0;
        ap.packPlayer.releaseUsingItem();
    }
}
