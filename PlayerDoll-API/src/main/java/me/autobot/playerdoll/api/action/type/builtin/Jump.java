package me.autobot.playerdoll.api.action.type.builtin;

import me.autobot.playerdoll.api.action.Action;
import me.autobot.playerdoll.api.action.pack.ActionPack;
import me.autobot.playerdoll.api.action.type.AbsActionType;
import me.autobot.playerdoll.api.doll.BaseEntity;

public class Jump extends AbsActionType {
    public Jump(boolean preventSpectator) {
        super(preventSpectator);
    }

    @Override
    public String registerName() {
        return "jump";
    }

    @Override
    public boolean execute(BaseEntity player, Action action) {
        ActionPack ap = player.getActionPack();
        if (action.limit == 1)
        {
            if (player.getBukkitPlayer().isOnGround()) {
                ap.packPlayer.jumpFromGround();
            }
        }
        else
        {
            ap.packPlayer.setJumping(true);
        }
        return false;
    }

    @Override
    public void inactiveTick(BaseEntity player, Action action) {
        player.getActionPack().packPlayer.setJumping(false);
    }
}
