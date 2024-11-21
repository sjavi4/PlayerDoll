package me.autobot.playerdoll.api.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.wrapper.builtin.WVec2;

public abstract class RotationArgument extends ACommandArgument {
    public abstract ArgumentType<?> getRotationArgument();
    public abstract WVec2<?> getRotation(CommandContext<?> commandcontext, String s);

}
