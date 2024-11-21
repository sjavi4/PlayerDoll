package me.autobot.playerdoll.api.action.type;

import me.autobot.playerdoll.api.action.Action;
import me.autobot.playerdoll.api.action.ActionTypeHelper;
import me.autobot.playerdoll.api.doll.BaseEntity;

public abstract class AbsActionType {
    public final boolean preventSpectator;
    public AbsActionType(boolean preventSpectator) {
        this.preventSpectator = preventSpectator;
        ActionTypeHelper.put(this);
    }

    public abstract String registerName();

    public void start(BaseEntity player, Action action) {}
    public abstract boolean execute(BaseEntity player, Action action);
    public void inactiveTick(BaseEntity player, Action action) {}
    public void stop(BaseEntity player, Action action)
    {
        inactiveTick(player, action);
    }
}
