package me.autobot.playerdoll.api.config.key;

import me.autobot.playerdoll.api.config.impl.PermConfig;
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
        if (this.config.getYamlConfiguration().contains(path)) {
            ConfigurationSection section = Objects.requireNonNull(this.config.getYamlConfiguration().getConfigurationSection(path));
            return (Map<K, V>) section.getValues(false);
        } else {
            this.config.getYamlConfiguration().set(path,defaultValue);
            return defaultValue;
        }
    }
}
