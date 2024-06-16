package me.autobot.playerdoll.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.autobot.playerdoll.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CommandRegister {
    private static final CommandDispatcher<Object> dispatcher;
    //private static final Object wrapperInstance;
    //private static final Method registerMethod;

    static {
        // Commands.class (mojang) / CommandDispatcher.class

        Class<?> nmsCommandsClass = ReflectionUtil.getClass("net.minecraft.commands.CommandDispatcher");
        Object minecraftServerInstance = ReflectionUtil.getDedicatedServerInstance();
        try {
            Method commandsMethod = Arrays.stream(minecraftServerInstance.getClass().getMethods())
                    .filter(method -> method.getReturnType() == nmsCommandsClass)
                    .findFirst()
                    .orElseThrow();
            commandsMethod.setAccessible(true);
            Object vanillaCommandDispatcherInstance = ReflectionUtil.invokeMethod(commandsMethod, minecraftServerInstance);

            //Object vanillaCommandDispatcherInstance = commandsMethod.invoke(minecraftServerInstance);
            Field commandDispatcherField = Arrays.stream(vanillaCommandDispatcherInstance.getClass().getDeclaredFields())
                    .filter(field -> field.getType() == CommandDispatcher.class)
                    .findFirst()
                    .orElseThrow();
            commandDispatcherField.setAccessible(true);

            dispatcher = (CommandDispatcher<Object>) commandDispatcherField.get(vanillaCommandDispatcherInstance);

            /*

            Class<?> bukkitCommandWrapperClass = ReflectionUtil.getCBClass("command.BukkitCommandWrapper");
            registerMethod = bukkitCommandWrapperClass.getMethod("register", CommandDispatcher.class, String.class);
            Constructor<?> bukkitCommandWrapperConstructor = bukkitCommandWrapperClass.getConstructor(Bukkit.getServer().getClass(), Command.class);
            wrapperInstance = bukkitCommandWrapperConstructor.newInstance(Bukkit.getServer(), null);

             */
        } catch (IllegalAccessException
                //| NoSuchMethodException | InvocationTargetException | InstantiationException
                  e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerCommand(LiteralCommandNode<?> node) {
        dispatcher.getRoot().removeCommand(node.getName());
        dispatcher.getRoot().addChild((CommandNode<Object>) node);
    }
    /*
    public static void registerCommand(String label) {
        try {
            registerMethod.invoke(wrapperInstance, dispatcher, label);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

     */
}
