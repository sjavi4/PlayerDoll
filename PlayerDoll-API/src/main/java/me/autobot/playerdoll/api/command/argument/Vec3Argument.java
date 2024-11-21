package me.autobot.playerdoll.api.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.api.wrapper.builtin.WVec3;

public abstract class Vec3Argument extends ACommandArgument {
    public abstract ArgumentType<?> getVec3Argument();
    public abstract WVec3<?> getVec3(CommandContext<?> commandcontext, String s);

}
