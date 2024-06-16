package me.autobot.playerdoll.wrapper.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.server.level.EntityPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public interface WrapperEntityArgument {
/*
    Method getPlayerMethod = Arrays.stream(ArgumentEntity.class.getMethods())
            .filter(method -> Modifier.isStatic(method.getModifiers()))
            .filter(method -> method.getReturnType() == EntityPlayer.class)
            .findFirst()
            .orElseThrow();

    ArgumentEntity player = ArgumentEntity.c();
    static WrapperServerPlayer getPlayer(CommandContext<Object> commandcontext, String s) throws CommandSyntaxException {
        try {
            return new WrapperServerPlayer(getPlayerMethod.invoke(null, commandcontext, s));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw ArgumentEntity.e.create();
        }
    }

 */
}
