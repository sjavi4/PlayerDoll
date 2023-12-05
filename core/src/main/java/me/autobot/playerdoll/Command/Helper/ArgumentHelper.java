package me.autobot.playerdoll.Command.Helper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ArgumentHelper <T> {
    ArrayList<T> arguments = new ArrayList<>();
    @SuppressWarnings("unchecked")
    public ArrayList<T> castTo(String[] args, Class<? extends T> type) {
        for (String s: args) {
            try {
                arguments.add((T)type.getDeclaredMethod("valueOf", String.class).invoke(null, s));
            } catch (ClassCastException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            }
        }
        return arguments;
    }
}
