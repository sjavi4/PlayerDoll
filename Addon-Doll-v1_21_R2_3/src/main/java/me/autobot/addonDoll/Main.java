package me.autobot.addonDoll;

import me.autobot.addonDoll.action.TracerImpl;
import me.autobot.addonDoll.argument.GameProfileArgImpl;
import me.autobot.addonDoll.argument.RotationArgImpl;
import me.autobot.addonDoll.argument.Vec3ArgImpl;
import me.autobot.addonDoll.listener.APIEvents;
import me.autobot.addonDoll.wrapper.*;
import me.autobot.playerdoll.api.Addon;
import me.autobot.playerdoll.api.PlayerDollAPI;
import me.autobot.playerdoll.api.action.pack.Tracer;
import me.autobot.playerdoll.api.wrapper.WrapperRegistry;
import org.bukkit.Bukkit;

public class Main implements Addon {
    @Override
    public void onEnable() {
        new GameProfileArgImpl();
        new RotationArgImpl();
        new Vec3ArgImpl();
        Tracer.setTracer(new TracerImpl());
        Bukkit.getPluginManager().registerEvents(new APIEvents(), PlayerDollAPI.getInstance());
        WrapperRegistry.put(WBlockStateImpl.class);
        WrapperRegistry.put(WEntityImpl.class);
        WrapperRegistry.put(WServerLevelImpl.class);
        WrapperRegistry.put(WDirectionImpl.class);
        WrapperRegistry.put(WServerPlayerActionImpl.class);
    }

    @Override
    public void onDisable() {

    }
}
