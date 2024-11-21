package me.autobot.playerdoll.api.doll;

import me.autobot.playerdoll.api.PlayerDollAPI;

public final class DollNameUtil {
    private static final String DOLL_IDENTIFIER = PlayerDollAPI.getConfigLoader().getBasicConfig().dollIdentifier.getValue();
    private static final String NAME_PATTERN_COMMAND = ".*[.+\\-].*";

    public static boolean validateDollName(String dollName) {
        return dollShortName(dollName).matches(NAME_PATTERN_COMMAND);
    }

    public static String dollShortName(String name) {
        if (DOLL_IDENTIFIER.isEmpty()) {
            return name;
        } else {
            return name.startsWith(DOLL_IDENTIFIER) ? name.substring(1) : name;
        }
    }

    public static String dollFullName(String name) {
        return name.startsWith(DOLL_IDENTIFIER) ? name : DOLL_IDENTIFIER.concat(name);
    }

    public static String getNamePatternCommand() {
        return NAME_PATTERN_COMMAND;
    }
}
