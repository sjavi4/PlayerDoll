package me.autobot.playerdoll.Command.arguments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractType {

    abstract boolean validate(String s);
    abstract List<String> suggestions();

    protected static List<String> INTERSECT(AbstractType A, AbstractType B) {
        Set<String> set = new HashSet<>(A.suggestions());
        B.suggestions().forEach(set::remove);
        return new ArrayList<>(set);
    }
}
