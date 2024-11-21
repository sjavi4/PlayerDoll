package me.autobot.playerdoll.api.command;

import me.autobot.playerdoll.api.command.argument.ACommandArgument;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CommandArgUtil {
    private static final Map<Class<? extends ACommandArgument>, ACommandArgument> ARGUMENTS = new HashMap<>();

    public static ACommandArgument put(ACommandArgument arg) {
        return ARGUMENTS.put(arg.getClass(), arg);
    }

    public static Map<Class<? extends ACommandArgument>, ACommandArgument> getArguments() {
        return ARGUMENTS;
    }

    public static <A extends ACommandArgument> A getArgumentImpl(Class<A> argClass) {
        ACommandArgument arg = null;
        for (Map.Entry<Class<? extends ACommandArgument>, ACommandArgument> entry : ARGUMENTS.entrySet()) {
            if (argClass.isInstance(entry.getValue())) {
                arg = entry.getValue();
                break;
            }
        }
        Objects.requireNonNull(arg, "Argument Class does not exist.");
        return argClass.cast(arg);
    }
}
