package me.autobot.playerdoll.wrapper.argument;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentProfile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

public interface WrapperGameProfileArgument {


    Method getGameProfileMethod = Arrays.stream(ArgumentProfile.class.getMethods())
            .filter(method -> Modifier.isStatic(method.getModifiers()))
            .filter(method -> method.getParameterCount() == 2 && method.getParameterTypes()[0] == CommandContext.class && method.getParameterTypes()[1] == String.class)
            .filter(method -> method.getExceptionTypes().length == 1 && method.getExceptionTypes()[0] == CommandSyntaxException.class)
            //.filter(method -> method.getGenericReturnType() == Collection.class)
            .findFirst()
            .orElseThrow();
    ArgumentProfile gameProfile = ArgumentProfile.a();

    static Collection<GameProfile> getGameProfiles(CommandContext<Object> commandcontext, String s) throws CommandSyntaxException {
        try {
            return (Collection<GameProfile>) getGameProfileMethod.invoke(null, commandcontext, s);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw ArgumentProfile.a.create();
        }
    }
}
