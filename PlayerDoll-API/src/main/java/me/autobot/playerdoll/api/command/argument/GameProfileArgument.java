package me.autobot.playerdoll.api.command.argument;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Collection;

public abstract class GameProfileArgument extends ACommandArgument {
    public abstract ArgumentType<?> getGameProfileArgument();
    public abstract Collection<GameProfile> getGameProfiles(CommandContext<?> commandcontext, String s) throws CommandSyntaxException;

}
