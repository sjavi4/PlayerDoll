package me.autobot.playerdoll.newCommand.Helper;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ArgumentHelper <T> {
    T type;
    ArrayList<T> arguments = new ArrayList<>();
    @SuppressWarnings("unchecked")
    public ArrayList<T> castTo(String[] args) {
        for (String s: args) {
            try {
                arguments.add((T)type.getClass().getDeclaredMethod("valueOf", String.class).invoke(null, s));
            } catch (ClassCastException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
            }
        }
        return arguments;
    }
}
