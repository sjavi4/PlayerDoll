package me.autobot.playerdoll.api.registry;

import me.autobot.playerdoll.api.Addon;

import java.util.HashMap;
import java.util.Map;

public class AddonRegistry {
    public final Map<String, Addon> addons = new HashMap<>();

    public <T extends Addon> void register(String name, T addon) {
        addons.put(name, addon);
    }
}
