package me.autobot.playerdoll.api.action.type.builtin;

import me.autobot.playerdoll.api.action.Action;
import me.autobot.playerdoll.api.action.pack.AbsPackPlayer;
import me.autobot.playerdoll.api.action.pack.ActionPack;
import me.autobot.playerdoll.api.action.type.AbsActionType;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.wrapper.builtin.*;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Attack extends AbsActionType {
    public Attack(boolean preventSpectator) {
        super(preventSpectator);
    }

    @Override
    public String registerName() {
        return "attack";
    }

    @Override
    public boolean execute(BaseEntity player, Action action) {
        ActionPack ap = player.getActionPack();
        AbsPackPlayer packPlayer = ap.packPlayer;
        Player bukkitPlayer = player.getBukkitPlayer();

        //Boolean[] executeResult = new Boolean[]{null};
        WHitResult<?> hit = ActionPack.getTarget(player);

        return hit.parseResultForAttack((blockHit) -> {
            if (ap.blockHitDelay > 0)
            {
                ap.blockHitDelay--;
                return false;
            }
            WBlockPos<?> pos = blockHit.getBlockPos();
            WDirection.Direction side = blockHit.getDirection();
            if (packPlayer.blockActionRestricted(pos)) {
                return false;
            }
            if (ap.currentBlock != null && packPlayer.serverLevel().getBlockState(ap.currentBlock).isAir())
            {
                ap.currentBlock = null;
                return false;
            }
            WBlockState<?> state = packPlayer.serverLevel().getBlockState(pos);
            boolean blockBroken = false;
            if (bukkitPlayer.getGameMode() == GameMode.CREATIVE)
            {
                packPlayer.handleBlockBreakAction(pos, WServerPlayerAction.Action.START_DESTROY_BLOCK, side, bukkitPlayer.getWorld().getMaxHeight(), -1);
                ap.blockHitDelay = 5;
                blockBroken = true;
            }
            else if (ap.currentBlock == null || !ap.currentBlock.equals(pos))
            {
                if (ap.currentBlock != null)
                {
                    packPlayer.handleBlockBreakAction(ap.currentBlock, WServerPlayerAction.Action.ABORT_DESTROY_BLOCK, side, bukkitPlayer.getWorld().getMaxHeight(), -1);
                }
                packPlayer.handleBlockBreakAction(pos, WServerPlayerAction.Action.START_DESTROY_BLOCK, side, bukkitPlayer.getWorld().getMaxHeight(), -1);
                boolean notAir = !state.isAir();
                if (notAir && ap.curBlockDamageMP == 0)
                {
                    state.attack(pos, packPlayer);
                }
                if (notAir && state.getDestroyProgress(packPlayer, pos) >= 1)
                {
                    ap.currentBlock = null;
                    //instamine??
                    blockBroken = true;
                }
                else
                {
                    ap.currentBlock = pos;
                    ap.curBlockDamageMP = 0;
                }
            }
            else
            {
                ap.curBlockDamageMP += state.getDestroyProgress(packPlayer, pos);
                if (ap.curBlockDamageMP >= 1)
                {
                    packPlayer.handleBlockBreakAction(pos, WServerPlayerAction.Action.STOP_DESTROY_BLOCK, side, bukkitPlayer.getWorld().getMaxHeight(), -1);
                    ap.currentBlock = null;
                    ap.blockHitDelay = 5;
                    blockBroken = true;
                }
                packPlayer.destroyBlockProgress(-1, pos, (int) (ap.curBlockDamageMP * 10));

            }
            packPlayer.resetLastActionTime();
            bukkitPlayer.swingMainHand();
            return blockBroken;
        }, (entityHit) -> {
            if (!action.isContinuous)
            {
                bukkitPlayer.attack(entityHit.getEntity().getBukkitEntity());
                bukkitPlayer.swingMainHand();
            }
            packPlayer.resetAttackStrengthTicker();
            packPlayer.resetLastActionTime();
            return true;
        });
    }

    @Override
    public void inactiveTick(BaseEntity player, Action action)
    {
        ActionPack ap = player.getActionPack();
        AbsPackPlayer packPlayer = ap.packPlayer;
        if (ap.currentBlock == null) return;
        packPlayer.destroyBlockProgress(-1, ap.currentBlock, -1);
        packPlayer.handleBlockBreakAction(ap.currentBlock, WServerPlayerAction.Action.ABORT_DESTROY_BLOCK, WDirection.Direction.DOWN, ap.bukkitPlayer().getWorld().getMaxHeight(), -1);
        ap.currentBlock = null;
    }
}
