package me.autobot.playerdoll.Command.arguments;

import java.util.List;

public class OfflineDollType extends AbstractType {
    @Override
    boolean validate(String s) {
        return false;
    }

    @Override
    List<String> suggestions() {
        return null;
    }
}
