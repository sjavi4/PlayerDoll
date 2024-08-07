package me.autobot.playerdoll.configkey;

import me.autobot.playerdoll.config.AbstractConfig;
import org.bukkit.Material;

import java.util.Objects;

public class FlagKey<R extends AbstractConfig> extends ConfigKey<R,Material> {
    public FlagKey(R config, String path, Material defaultValue) {
        super(config, path, defaultValue);
    }

    @Override
    protected Material getOrSetDefault(Material defaultValue) {
        if (this.config.yamlConfiguration.contains(path)) {
            return Material.getMaterial(Objects.requireNonNull(this.config.yamlConfiguration.getString(path)));
        } else {
            this.config.yamlConfiguration.set(path,defaultValue.name());
            return defaultValue;
        }
    }

    @Override
    public void setNewValue(Material value) {
        this.value = value;
        this.config.yamlConfiguration.set(this.path,value.name());
    }
}
