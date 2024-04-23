package me.autobot.playerdoll.Command.arguments;

import me.autobot.playerdoll.Command.CommandType;
import me.autobot.playerdoll.PlayerDoll;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllDollType extends AbstractType {
    @Override
    boolean validate(String s) {
        return suggestions().contains(CommandType.getDollName(s,true));
    }

    @Override
    List<String> suggestions() {
        ArrayList<String> list = new ArrayList<>();
        File[] dollFiles = new File(PlayerDoll.getDollDirectory()).listFiles();
        if (dollFiles != null) {
            list.addAll(Arrays.stream(dollFiles).map(File::getName).map(s -> s.substring(1, s.lastIndexOf("."))).toList());
        }
        return list;
    }
}
