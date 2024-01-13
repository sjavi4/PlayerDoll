package me.autobot.playerdoll.Command;

import org.bukkit.entity.Player;

import java.util.*;


public class SuggestionHelper {
    public static final Map<CommandType, SuggestionBuilder> suggestions = new EnumMap<>(CommandType.class);
    static {
        for (CommandType c : CommandType.values()) {
            c.buildSuggestion();
        }
    }
    public static List<String> walkThrough(List<String> arguments, Player sender) {
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
        return checkSpecial(suggestion, sender);
    }
    private static List<String> checkSpecial(List<String> list, Player sender) {
        List<String> l = new ArrayList<>();
        for (String s : list) {
            if (s.startsWith("*")) {
                ArgumentType types = ArgumentType.valueOf(s.substring(1).toUpperCase());
                List<String> suggestionList = types.get();
                if (sender != null) {
                    if (types == ArgumentType.ALL_PERMISSIONED_DOLL
                            || types == ArgumentType.ONLINE_PERMISSIONED_DOLL
                            || types == ArgumentType.OFFLINE_PERMISSIONED_DOLL) {
                        l.addAll(getSenderPermission(suggestionList,sender));
                        return l;
                    }
                    l.addAll(suggestionList);
                } else {
                    l.addAll(suggestionList);
                }
            } else {
                l.add(s);
            }
        }
        return l;
    }

    private static List<String> getSenderPermission(List<String> list, Player sender) {
        if (sender.isOp()) {
            return list;
        }
        ArrayList<String> arrayList = new ArrayList<>(list);
        arrayList.removeIf(s -> !CommandType.checkHasPermission(null, sender, s, false));
        return arrayList;
    }

}
