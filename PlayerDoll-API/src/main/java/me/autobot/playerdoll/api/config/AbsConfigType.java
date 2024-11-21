package me.autobot.playerdoll.api.config;

import me.autobot.playerdoll.api.PlayerDollAPI;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public abstract class AbsConfigType {
    private static final Map<Class<? extends AbsConfigType>, AbsConfigType> CONFIGS = new HashMap<>();

    public static AbsConfigType put(AbsConfigType config) {
        if (CONFIGS.values().stream().anyMatch(c -> c.registerName().equalsIgnoreCase(config.registerName()))) {
            PlayerDollAPI.getLogger().log(Level.WARNING, "Ignore Duplicated Config [{0}]", config.registerName());
            return null;
        }
        return CONFIGS.put(config.getClass(), config);
    }

    public static Map<Class<? extends AbsConfigType>, AbsConfigType> getConfigs() {
        return CONFIGS;
    }

    public static <A extends AbsConfigType> A getConfigImpl(Class<A> argClass) {
        AbsConfigType arg = CONFIGS.get(argClass);
        Objects.requireNonNull(arg, "ConfigType Class does not exist.");
        return argClass.cast(arg);
    }

    private File location;
    private final String resourceLocation;
    public AbsConfigType(File location, String resourceLocation) {
        this.location = location;
        this.resourceLocation = resourceLocation;
        put(this);
    }

    public abstract String registerName();

    public File getLocation() {
        return location;
    }

    public void setLocation(File location) {
        this.location = location;
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    public static final AbsConfigType BASIC = new AbsConfigType(PlayerDollAPI.getFileUtil().getFile(PlayerDollAPI.getFileUtil().getPluginPath(), "config.yml"), "config.yml") {
        @Override
        public String registerName() {
            return "BASIC";
        }
    };

    public static final AbsConfigType LANGUAGE = new AbsConfigType(PlayerDollAPI.getFileUtil().getFile(PlayerDollAPI.getFileUtil().getLanguageDir(), "default.yml"), "language/default.yml") {
        @Override
        public String registerName() {
            return "LANGUAGE";
        }
    };

    public static final AbsConfigType FLAG = new AbsConfigType(PlayerDollAPI.getFileUtil().getFile(PlayerDollAPI.getFileUtil().getPluginPath(), "flag.yml"), "flag.yml") {
        @Override
        public String registerName() {
            return "FLAG";
        }
    };

    public static final AbsConfigType PERMISSION = new AbsConfigType(PlayerDollAPI.getFileUtil().getFile(PlayerDollAPI.getFileUtil().getPluginPath(), "perm.yml"), "perm.yml") {
        @Override
        public String registerName() {
            return "PERMISSION";
        }
    };
}
