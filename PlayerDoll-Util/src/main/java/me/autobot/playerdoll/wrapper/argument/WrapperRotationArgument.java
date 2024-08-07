package me.autobot.playerdoll.wrapper.argument;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.wrapper.phys.WrapperVec2;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.coordinates.ArgumentRotation;
import net.minecraft.commands.arguments.coordinates.IVectorPosition;
import net.minecraft.world.phys.Vec2F;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public interface WrapperRotationArgument {

    Method getRotationMethod = Arrays.stream(ArgumentRotation.class.getMethods())
            .filter(method -> Modifier.isStatic(method.getModifiers()))
            .filter(method -> method.getParameterCount() == 2 && method.getParameterTypes()[0] == CommandContext.class && method.getParameterTypes()[1] == String.class)
            .findFirst()
            .orElseThrow();
    ArgumentRotation rotation = ArgumentRotation.a();

    static WrapperVec2 getRotation(CommandContext<Object> commandcontext, String s) {
        try {
            return WrapperVec2.wrap(((IVectorPosition) getRotationMethod.invoke(null, commandcontext, s)).b((CommandListenerWrapper) commandcontext.getSource()));
        } catch (InvocationTargetException | IllegalAccessException ignored) {
            return WrapperVec2.wrap(new Vec2F(0,0));
        }
    }
}
