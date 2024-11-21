package me.autobot.playerdoll.api.action;


import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.action.type.AbsActionType;
import me.autobot.playerdoll.api.action.type.builtin.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public final class ActionTypeHelper {
    private static final Map<Class<? extends AbsActionType>, AbsActionType> ACTION_TYPES = new HashMap<>();

    public static AbsActionType put(AbsActionType actionType) {
        if (ACTION_TYPES.values().stream().anyMatch(type -> type.registerName().equalsIgnoreCase(actionType.registerName()))) {
            PlayerDollAPI.getLogger().log(Level.WARNING,"Ignoring Duplicated ActionType [{0}]", actionType.registerName());
            return null;
        }
        return ACTION_TYPES.put(actionType.getClass(), actionType);
    }

    public static Map<Class<? extends AbsActionType>, AbsActionType> getActionTypes() {
        return ACTION_TYPES;
    }

    public static <A extends AbsActionType> A getActionTypeImpl(Class<A> argClass) {
        AbsActionType arg = ACTION_TYPES.get(argClass);
        Objects.requireNonNull(arg, "ActionType Class does not exist.");
        return argClass.cast(arg);
    }


    public enum Defaults {
        ATTACK(new Attack(true)),
        USE(new Use(true)),
        JUMP(new Jump(true)),
        DROP_ITEM(new DropItem(true)),
        DROP_STACK(new DropStack(true)),
        SWAP_HANDS(new SwapHands(true));

        private final AbsActionType type;
        Defaults(AbsActionType type) {
            this.type = type;
        }

        public AbsActionType get() {
            return type;
        }
    }
}
