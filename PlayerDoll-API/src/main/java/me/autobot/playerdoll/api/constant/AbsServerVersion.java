package me.autobot.playerdoll.api.constant;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.autobot.playerdoll.api.PlayerDollAPI;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public abstract class AbsServerVersion {

    private static final Map<Integer, AbsServerVersion> VERSIONS = new Int2ObjectOpenHashMap<>();

    public static AbsServerVersion put(AbsServerVersion version) {
        if (VERSIONS.containsKey(version.getProtocol())) {
            PlayerDollAPI.getLogger().log(Level.WARNING, "Ignored Duplicated ServerVersion [{0}]", version.registerVersion());
            return null;
        }
        return VERSIONS.put(version.getProtocol(), version);
    }

    public static Map<Integer, AbsServerVersion> getVersions() {
        return VERSIONS;
    }

    /**
     * @param bukkitVersion accept string in the following format<br> Example: "1.20.1"
     */
    public static Optional<AbsServerVersion> parse(String bukkitVersion) {
        return VERSIONS.values().stream().filter(v -> v.match(bukkitVersion)).findAny();
    }

    public static AbsServerVersion v1_20_R2;

    public static AbsServerVersion v1_20_R3;

    public static AbsServerVersion v1_20_R4;

    public static AbsServerVersion v1_21_R1;

    public static AbsServerVersion v1_21_R2;

    private final int protocol;
    public AbsServerVersion(int protocol) {
        this.protocol = protocol;
        put(this);
    }
    public abstract String registerVersion();
    public abstract boolean match(String ver);
    public int getProtocol() {
        return protocol;
    }

//    public abstract <C> C getWrapperImpl(String registerName, Class<C> clazz);
//    public abstract <C> C getArgumentImpl(String registerName, Class<C> clazz);
    //public abstract <C> C getImpl(String registerName, Class<C> clazz);
}
