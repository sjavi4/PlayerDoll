package me.autobot.playerdoll.configkey;

import me.autobot.playerdoll.config.PermConfig;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.Objects;

public class PermKey<K, V> extends ConfigKey<PermConfig, Map<K, V>> {
    public PermKey(PermConfig config, String path, Map<K, V> defaultValue) {
        super(config, path, defaultValue);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<K, V> getOrSetDefault(Map<K, V> defaultValue) {
        if (this.config.yamlConfiguration.contains(path)) {
            ConfigurationSection section = Objects.requireNonNull(this.config.yamlConfiguration.getConfigurationSection(path));
            return (Map<K, V>) section.getValues(false);
        } else {
            this.config.yamlConfiguration.set(path,defaultValue);
            return defaultValue;
        }
    }
}
