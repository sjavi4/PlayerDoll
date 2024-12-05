package me.autobot.addonDoll.argument;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.autobot.playerdoll.api.command.argument.GameProfileArgument;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collection;

public class GameProfileArgImpl extends GameProfileArgument {

    public GameProfileArgImpl() {
        super();
    }
    @Override
    public ArgumentType<?> getGameProfileArgument() {
        return net.minecraft.commands.arguments.GameProfileArgument.gameProfile();
    }

    @Override
    public Collection<GameProfile> getGameProfiles(CommandContext<?> commandcontext, String s) throws CommandSyntaxException {
        return net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>) commandcontext, s);
    }
}
