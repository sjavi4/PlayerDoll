package me.autobot.playerdoll.Util;

import me.autobot.playerdoll.Util.Configs.PermConfig;

@FunctionalInterface
public interface PermChecker {
    boolean check(PermConfig config);
}
