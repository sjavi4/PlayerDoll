package me.autobot.playerdoll.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.constant.AbsServerBranch;
import me.autobot.playerdoll.api.constant.AbsServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class CommandRegisterHelper {
    private static final CommandDispatcher<Object> brigadierDispatcher;
    private static final Object vanillaCommandDispatcherInstance;
    private static final Constructor<?> vanillaCommandWrapperConstructor;
    private static final SimpleCommandMap simpleCommandMap;

    static {
        // Commands.class (mojang) / CommandDispatcher.class

        Class<?> nmsCommandsClass = ReflectionUtil.getNMClass("commands.CommandDispatcher");
        Object minecraftServerInstance = ReflectionUtil.getDedicatedServerInstance();

        Method commandsMethod = Arrays.stream(minecraftServerInstance.getClass().getMethods())
                .filter(method -> method.getReturnType() == nmsCommandsClass)
                .findFirst()
                .orElseThrow();
        commandsMethod.setAccessible(true);
        vanillaCommandDispatcherInstance = ReflectionUtil.invokeMethod(commandsMethod, minecraftServerInstance);


        Field commandDispatcherField = Arrays.stream(vanillaCommandDispatcherInstance.getClass().getDeclaredFields())
                .filter(field -> field.getType() == CommandDispatcher.class)
                .findFirst()
                .orElseThrow();
        commandDispatcherField.setAccessible(true);

        brigadierDispatcher = (CommandDispatcher<Object>) ReflectionUtil.getField(commandDispatcherField, vanillaCommandDispatcherInstance);


        vanillaCommandWrapperConstructor = ReflectionUtil.getCBClass("command.VanillaCommandWrapper").getDeclaredConstructors()[0];
        try {
            Server server = Bukkit.getServer();
            Field craftCommandMapField = server.getClass().getDeclaredField("commandMap");
            craftCommandMapField.setAccessible(true);
            simpleCommandMap = (SimpleCommandMap) craftCommandMapField.get(server);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerCommand(LiteralCommandNode<?> node) {
        brigadierDispatcher.getRoot().removeCommand(node.getName());
        brigadierDispatcher.getRoot().addChild((CommandNode<Object>) node);

        if (PlayerDollAPI.getServerBranch() == AbsServerBranch.SPIGOT ||
                (PlayerDollAPI.getServerVersion() == AbsServerVersion.v1_20_R2 || PlayerDollAPI.getServerVersion() == AbsServerVersion.v1_20_R3)) {
            Command wrapperCommand = (Command) ReflectionUtil.newInstance(vanillaCommandWrapperConstructor, vanillaCommandDispatcherInstance, node);
            simpleCommandMap.register("minecraft", wrapperCommand);
        }
    }

}