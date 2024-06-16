package me.autobot.playerdoll.wrapper.argument;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.util.ReflectionUtil;
import me.autobot.playerdoll.wrapper.phys.WrapperVec3;
import net.minecraft.commands.arguments.coordinates.ArgumentVec3;
import net.minecraft.world.phys.Vec3D;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public interface WrapperVec3Argument {
    Method getVec3Method = Arrays.stream(ArgumentVec3.class.getMethods())
            .filter(method -> Modifier.isStatic(method.getModifiers()))
            .filter(method -> method.getParameterCount() == 2 && method.getParameterTypes()[0] == CommandContext.class && method.getParameterTypes()[1] == String.class)
            .filter(method -> method.getReturnType() == Vec3D.class)
            .findFirst()
            .orElseThrow();
    ArgumentVec3 vec3 = ArgumentVec3.a();

    static WrapperVec3 getVec3(CommandContext<Object> commandcontext, String s) {
        //System.out.println(getVec3Method);
        return new WrapperVec3(ReflectionUtil.invokeMethod(getVec3Method, null, commandcontext, s));
        /*
        try {
            return new WrapperVec3(getVec3Method.invoke(null, commandcontext, s));
        } catch (InvocationTargetException | IllegalAccessException ignored) {
            return new WrapperVec3(new Vec3D(0,0,0));
        }

         */
    }
}
