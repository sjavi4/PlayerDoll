package me.autobot.playerdoll.Util.Keys;

import me.autobot.playerdoll.Util.Configs.AbstractConfig;
import org.bukkit.Material;

public class FlagKey<R extends AbstractConfig> extends ConfigKey<R,Material> {
    public FlagKey(R config, String path, Material defaultValue) {
        super(config, path, defaultValue);
    }

    @Override
    protected Material getOrSetDefault(Material defaultValue) {
        if (this.config.yamlConfiguration.contains(path)) {
            return Material.getMaterial(this.config.yamlConfiguration.getString(path));
        } else {
            this.config.yamlConfiguration.set(path,defaultValue);
            return defaultValue;
        }
    }
}
