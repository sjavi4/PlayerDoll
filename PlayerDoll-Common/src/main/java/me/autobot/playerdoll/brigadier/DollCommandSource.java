package me.autobot.playerdoll.brigadier;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.util.ReflectionUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ProxiedCommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class DollCommandSource {
    //public static final Field commandSourceField;
    public static final Method getBukkitSenderMethod;

    static {
        // CommandSourceStack (mojang) / CommandListenerWrapper
        Class<?> commandSourceStackClass = ReflectionUtil.getClass("net.minecraft.commands.CommandListenerWrapper");
        // CommandSource (mojang) / ICommandListener
        Class<?> commandSourceClass = ReflectionUtil.getClass("net.minecraft.commands.ICommandListener");
        Objects.requireNonNull(commandSourceStackClass, "CommandSourceStack.class");
        Objects.requireNonNull(commandSourceClass, "CommandSource.class");
/*
        commandSourceField = Arrays.stream(commandSourceStackClass.getFields())
                .filter(field -> field.getType() == commandSourceClass)
                .findFirst()
                .orElseThrow();

 */

        getBukkitSenderMethod = Arrays.stream(commandSourceStackClass.getMethods())
                .filter(method -> method.getReturnType() == CommandSender.class || method.getName().equals("getBukkitSender"))
                .findFirst()
                .orElseThrow();

    }

    public static CommandSender toCommandSender(Object commandSource) {
        return ReflectionUtil.invokeMethod(CommandSender.class, getBukkitSenderMethod, commandSource);
        /*
        try {
            return (CommandSender) getBukkitSenderMethod.invoke(commandSource);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

         */
    }

    private final CommandContext<Object> context;
    private final CommandSender sender;

    private DollCommandSource(CommandContext<Object> commandSourceStack, DollCommandExecutor executor) {
        // When execute Command, have to getSource()
        sender = toCommandSender(commandSourceStack.getSource());
        this.context = commandSourceStack;
    }

    public static int execute(CommandContext<Object> commandSourceStack, DollCommandExecutor executor) {
        DollCommandSource source = new DollCommandSource(commandSourceStack, executor);
        // Should be useless
        if (source.sender instanceof ProxiedCommandSender proxiedCommandSender) {
            return executor.onCommand(proxiedCommandSender.getCallee(), source.context);
        }
        return executor.onCommand(source.sender, source.context);
    }
}
