package me.autobot.playerdoll.api.config.key;

import me.autobot.playerdoll.api.config.AbstractConfig;
import org.bukkit.Material;

import java.util.Objects;

public class FlagKey<R extends AbstractConfig> extends ConfigKey<R,Material> {
    public FlagKey(R config, String path, Material defaultValue) {
        super(config, path, defaultValue);
    }

    @Override
    protected Material getOrSetDefault(Material defaultValue) {
        if (this.config.getYamlConfiguration().contains(path)) {
            return Material.getMaterial(Objects.requireNonNull(this.config.getYamlConfiguration().getString(path)));
        } else {
            this.config.getYamlConfiguration().set(path,defaultValue.name());
            return defaultValue;
        }
    }

    @Override
    public void setNewValue(Material value) {
        this.value = value;
        this.config.getYamlConfiguration().set(this.path,value.name());
    }
}
