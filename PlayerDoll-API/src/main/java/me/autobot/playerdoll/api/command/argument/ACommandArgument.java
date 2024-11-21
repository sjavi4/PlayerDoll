package me.autobot.playerdoll.api.command.argument;

import me.autobot.playerdoll.api.command.CommandArgUtil;

public abstract class ACommandArgument {

    public ACommandArgument() {
        CommandArgUtil.put(this);
    }
}