package me.autobot.playerdoll.Command;

import java.util.LinkedList;
import java.util.List;

public class SuggestionBuilder {
    public final String data;
    public final boolean exactValue;
    private SuggestionBuilder parent;
    public final List<SuggestionBuilder> children;
    private SuggestionBuilder(CommandType root) {
        this.data = root.name();
        this.exactValue = true;
        this.children = new LinkedList<>();
        SuggestionHelper.suggestions.put(root, this);
    }
    private SuggestionBuilder(String child, boolean exactValue) {
        this.data = child;
        this.exactValue = exactValue;
        this.children = new LinkedList<>();
    }
    public static SuggestionBuilder create(CommandType root) {
        return new SuggestionBuilder(root);
    }
    private boolean isRoot() {
        return parent == null;
    }
    public boolean isChild() {
        return children.size() == 0;
    }

    public int getLevel() {
        if (this.isRoot()) {
            return 0;
        } else {
            return parent.getLevel() + 1;
        }
    }
    public SuggestionBuilder addChild(String child, boolean exactValue) {
        SuggestionBuilder children = new SuggestionBuilder(child, exactValue);
        this.children.add(children);
        children.parent = this;
        return children;
    }
    public SuggestionBuilder addChild(ArgumentType child) {
        String type = "*" + child.name();
        SuggestionBuilder children = new SuggestionBuilder(type, false);
        this.children.add(children);
        children.parent = this;
        return children;
    }
}
