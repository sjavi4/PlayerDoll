package me.autobot.playerdoll.api.action.type.builtin;

import me.autobot.playerdoll.api.action.Action;
import me.autobot.playerdoll.api.action.pack.ActionPack;
import me.autobot.playerdoll.api.action.type.AbsActionType;
import me.autobot.playerdoll.api.doll.BaseEntity;

public class DropStack extends AbsActionType {
    public DropStack(boolean preventSpectator) {
        super(preventSpectator);
    }

    @Override
    public String registerName() {
        return "drop_stack";
    }

    @Override
    public boolean execute(BaseEntity player, Action action) {
        ActionPack ap = player.getActionPack();
        ap.packPlayer.resetLastActionTime();
        player.getBukkitPlayer().dropItem(true);
        return false;
    }
}
