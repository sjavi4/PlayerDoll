package me.autobot.playerdoll.Util.Keys;

import me.autobot.playerdoll.Util.Configs.AbstractConfig;

public class ConfigKey<R extends AbstractConfig, T> {
    protected final String path;
    protected final R config;
    protected T value;
    public ConfigKey(R config, String path, T defaultValue) {
        this.config = config;
        this.path = path;
        this.value = getOrSetDefault(defaultValue);
    }

    public String getPath() {
        return path;
    }
    public T getValue() {
        return value;
    }
    public void setNewValue(T value) {
        this.value = value;
        this.config.yamlConfiguration.set(this.path,value);
    }

    @SuppressWarnings("unchecked")
    protected T getOrSetDefault(T defaultValue) {
        if (this.config.yamlConfiguration.contains(path)) {
            return (T) this.config.yamlConfiguration.get(path);
        } else {
            this.config.yamlConfiguration.set(path,defaultValue);
            return defaultValue;
        }
    }
}
