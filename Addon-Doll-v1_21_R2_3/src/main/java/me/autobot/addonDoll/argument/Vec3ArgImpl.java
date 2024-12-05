package me.autobot.addonDoll.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.command.argument.Vec3Argument;
import me.autobot.playerdoll.api.wrapper.WrapperRegistry;
import me.autobot.playerdoll.api.wrapper.builtin.WVec3;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec3;

public class Vec3ArgImpl extends Vec3Argument {

    public Vec3ArgImpl() {
        super();
    }
    @Override
    public ArgumentType<?> getVec3Argument() {
        return net.minecraft.commands.arguments.coordinates.Vec3Argument.vec3();
    }

    @Override
    public WVec3<Vec3> getVec3(CommandContext<?> commandcontext, String s) {
        Vec3 v = net.minecraft.commands.arguments.coordinates.Vec3Argument.getVec3((CommandContext<CommandSourceStack>) commandcontext, s);
        Class<? extends WVec3<Vec3>> wrapper = (Class<? extends WVec3<Vec3>>) WrapperRegistry.getWrapper(WVec3.class, v);
        return WrapperRegistry.wrapFrom(wrapper, v);
    }
}
