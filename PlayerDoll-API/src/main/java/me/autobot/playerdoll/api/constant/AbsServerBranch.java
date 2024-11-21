package me.autobot.playerdoll.api.constant;

import me.autobot.playerdoll.api.Addon;
import me.autobot.playerdoll.api.PlayerDollAPI;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Level;

public abstract class AbsServerBranch {
    private static final Map<Class<? extends AbsServerBranch>, AbsServerBranch> BRANCHES = new LinkedHashMap<>();

    public static AbsServerBranch put(AbsServerBranch branch) {
        if (BRANCHES.values().stream().anyMatch(branches -> branches.registerName().equalsIgnoreCase(branch.registerName()))) {
            PlayerDollAPI.getLogger().log(Level.WARNING, "Ignored Duplicated ServerBranch [{0}]", branch.registerName());
            return null;
        }
        return BRANCHES.put(branch.getClass(), branch);
    }

    public static Map<Class<? extends AbsServerBranch>, AbsServerBranch> getBranches() {
        return BRANCHES;
    }

    public static <A extends AbsServerBranch> A getServerBranchImpl(Class<A> argClass) {
        AbsServerBranch arg = BRANCHES.get(argClass);
        Objects.requireNonNull(arg, "Server Branch Class does not exist.");
        return arg.getClass() == argClass ? argClass.cast(arg) : null;
    }

    /**
     * @apiNote Please pay attention to the order of Insertion.<br>
     * If you are using a custom implementation,<br>
     * you should set the Basic config key [server-mod] to your implementation {@link #registerName()}.
     */
    public static AbsServerBranch parse() {
        var list = BRANCHES.values().stream().filter(AbsServerBranch::match).toList();
        return list.get(list.size() - 1);
    }
    public static AbsServerBranch parse(String config) {
        var list = BRANCHES.values().stream().filter(b -> b.registerName().equalsIgnoreCase(config)).toList();
        if (list.isEmpty()) {
            throw new NoSuchElementException(String.format("Cannot Find Server Branch (%s) from Config Setting.", config));
        }
        return list.get(list.size() - 1);
    }

    public static AbsServerBranch SPIGOT;
    public static AbsServerBranch PAPER;
    public static AbsServerBranch FOLIA;

    private final String path;
    private Addon addon = null;
    public AbsServerBranch(String path) {
        this.path = path;
        put(this);
    }
    public String getPath() {
        return path;
    }
    public void setAddon(Addon addon) {
        this.addon = addon;
    }
    public Addon getAddon() {
        return addon;
    }
    public abstract String registerName();
    public abstract boolean match();
}
