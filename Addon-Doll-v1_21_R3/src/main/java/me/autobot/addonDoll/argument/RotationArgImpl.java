package me.autobot.addonDoll.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.command.argument.RotationArgument;
import me.autobot.playerdoll.api.wrapper.WrapperRegistry;
import me.autobot.playerdoll.api.wrapper.builtin.WVec2;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.Vec2;

public class RotationArgImpl extends RotationArgument {

    public RotationArgImpl() {
        super();
    }
    @Override
    public ArgumentType<?> getRotationArgument() {
        return net.minecraft.commands.arguments.coordinates.RotationArgument.rotation();
    }

    @Override
    public WVec2<Vec2> getRotation(CommandContext<?> commandcontext, String s) {
        Vec2 v = net.minecraft.commands.arguments.coordinates.RotationArgument.getRotation((CommandContext<CommandSourceStack>) commandcontext, s).getRotation((CommandSourceStack) (commandcontext).getSource());
        Class<? extends WVec2<Vec2>> wrapper = (Class<? extends WVec2<Vec2>>) WrapperRegistry.getWrapper(WVec2.class, v);
        return WrapperRegistry.wrapFrom(wrapper, v);
    }
}
