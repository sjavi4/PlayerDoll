package me.autobot.playerdoll.Command;

import java.util.*;


public class SuggestionHelper {
    public static final Map<CommandType, SuggestionBuilder> suggestions = new EnumMap<>(CommandType.class);
    static {
        for (CommandType c : CommandType.values()) {
            c.buildSuggestion();
        }
    }
    public static List<String> walkThrough(List<String> arguments) {
        if (arguments.stream().filter(String::isBlank).count() > 1) {
            return ArgumentType.NONE.get();
        }

        CommandType commandType;
        try {
            commandType = CommandType.valueOf(arguments.get(0).toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ignored) {
            if (arguments.size() > 1) {
                return ArgumentType.NONE.get();
            }
            return suggestions.keySet().stream().map(t -> t.toString().toLowerCase()).toList();
        }

        SuggestionBuilder builder = suggestions.get(commandType);
        String[] inputArguments = arguments.toArray(new String[0]);
        SuggestionBuilder currentBuilder = builder;
        List<String> suggestion = ArgumentType.NONE.get();
        boolean invalidString = false;
        for (String input : inputArguments) {
            if (invalidString) {
                String previous = arguments.get(arguments.lastIndexOf(input) - 1);
                Optional<SuggestionBuilder> matchType = currentBuilder.children.stream()
                        .filter(sb -> sb.data.startsWith("*"))
                        .filter(sb -> ArgumentType.valueOf(sb.data.substring(1).toUpperCase()).argumentValid(previous))
                        .findFirst();
                if (matchType.isPresent()) {
                    currentBuilder = matchType.get();
                    invalidString = false;
                }
            }

            if (invalidString || currentBuilder.isChild()) {
                return ArgumentType.NONE.get();
            }

            if (input.isBlank() || currentBuilder.getLevel() == arguments.lastIndexOf(input)) {
                suggestion = currentBuilder.children.stream().map(sb -> sb.data.toLowerCase()).toList();
                continue;
            }

            Optional<SuggestionBuilder> matchChild = currentBuilder.children.stream()
                    .filter(sb -> sb.data.equalsIgnoreCase(input))
                    .findFirst();
            if (matchChild.isPresent()) {
                currentBuilder = matchChild.get();
                invalidString = false;
            } else {
                invalidString = true;
            }

            suggestion = currentBuilder.children.stream().map(sb -> sb.data.toLowerCase()).toList();
        }
        return checkSpecial(suggestion);
    }
    private static List<String> checkSpecial(List<String> list) {
        List<String> l = new ArrayList<>();
        for (String s : list) {
            if (s.startsWith("*")) {
                ArgumentType types = ArgumentType.valueOf(s.substring(1).toUpperCase());
                l.addAll(types.get());
            } else {
                l.add(s);
            }
        }
        return l;
    }

}
