package me.autobot.playerdoll.api.config.key;


import me.autobot.playerdoll.api.config.AbstractConfig;

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
        this.config.getYamlConfiguration().set(this.path,value);
    }

    @SuppressWarnings("unchecked")
    protected T getOrSetDefault(T defaultValue) {
        if (this.config.getYamlConfiguration().contains(path)) {
            return (T) this.config.getYamlConfiguration().get(path);
        } else {
            this.config.getYamlConfiguration().set(path,defaultValue);
            return defaultValue;
        }
    }
}
