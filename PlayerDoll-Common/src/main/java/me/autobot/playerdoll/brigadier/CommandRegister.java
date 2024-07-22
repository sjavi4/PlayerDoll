package me.autobot.playerdoll.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CommandRegister {
    private static final CommandDispatcher<Object> brigadierDispatcher;
    private static final Object vanillaCommandDispatcherInstance;
    private static final Constructor<?> vanillaCommandWrapperConstructor;
    private static final SimpleCommandMap simpleCommandMap;
    //public static final Map<String, Command> craftCommandMap;
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
            vanillaCommandDispatcherInstance = ReflectionUtil.invokeMethod(commandsMethod, minecraftServerInstance);

            //Object vanillaCommandDispatcherInstance = commandsMethod.invoke(minecraftServerInstance);
            Field commandDispatcherField = Arrays.stream(vanillaCommandDispatcherInstance.getClass().getDeclaredFields())
                    .filter(field -> field.getType() == CommandDispatcher.class)
                    .findFirst()
                    .orElseThrow();
            commandDispatcherField.setAccessible(true);

            brigadierDispatcher = (CommandDispatcher<Object>) commandDispatcherField.get(vanillaCommandDispatcherInstance);


            vanillaCommandWrapperConstructor = ReflectionUtil.getCBClass("command.VanillaCommandWrapper").getDeclaredConstructors()[0];

            Server server = Bukkit.getServer();
            Field craftCommandMapField = server.getClass().getDeclaredField("commandMap");
            craftCommandMapField.setAccessible(true);
            simpleCommandMap = (SimpleCommandMap) craftCommandMapField.get(server);

            //craftCommandMap = (Map<String, Command>) simpleCommandMap.getClass().getMethod("getKnownCommands").invoke(simpleCommandMap);
            /*

            Class<?> bukkitCommandWrapperClass = ReflectionUtil.getCBClass("command.BukkitCommandWrapper");
            registerMethod = bukkitCommandWrapperClass.getMethod("register", CommandDispatcher.class, String.class);
            Constructor<?> bukkitCommandWrapperConstructor = bukkitCommandWrapperClass.getConstructor(Bukkit.getServer().getClass(), Command.class);
            wrapperInstance = bukkitCommandWrapperConstructor.newInstance(Bukkit.getServer(), null);

             */
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerCommand(LiteralCommandNode<?> node) {
        brigadierDispatcher.getRoot().removeCommand(node.getName());
        brigadierDispatcher.getRoot().addChild((CommandNode<Object>) node);

        if (PlayerDoll.serverBranch == PlayerDoll.ServerBranch.SPIGOT || PlayerDoll.INTERNAL_VERSION.matches("v1_20_R2|v1_20_R3")) {
            Command wrapperCommand = (Command) ReflectionUtil.newInstance(vanillaCommandWrapperConstructor, vanillaCommandDispatcherInstance, node);
            simpleCommandMap.register("minecraft", wrapperCommand);
        }

    }
//    public static void unregisterCommand(LiteralCommandNode<?> node) {
//        dispatcher.getRoot().removeCommand(node.getName());
//    }
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
