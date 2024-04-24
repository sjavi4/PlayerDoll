package me.autobot.playerdoll.v1_20_R4.Event;

import me.autobot.playerdoll.CustomEvent.DollSettingChangeEvent;
import me.autobot.playerdoll.Dolls.IServerDoll;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SettingChangeEvent implements Listener {

    static {
        Bukkit.getPluginManager().registerEvents(new SettingChangeEvent(), PlayerDoll.getPlugin());
    }

    @EventHandler
    public void onDollSettingChange(DollSettingChangeEvent event) {
        IServerDoll doll = event.getWhoChanged();
        boolean b = event.getToggleState();
        switch (event.getType()) {
             case GLOW -> doll.getBukkitPlayer().setGlowing(b);
             case GRAVITY -> doll.getBukkitPlayer().setGravity(b);
             //case PHANTOM -> doll.setNoPhantom(!b);
             case PICKABLE -> doll.getBukkitPlayer().setCanPickupItems(b);
             case PUSHABLE -> doll.getBukkitPlayer().setCollidable(b);
             case INVULNERABLE -> doll.getBukkitPlayer().setInvulnerable(b);
             case LARGE_STEP_SIZE -> doll.setDollMaxUpStep(b? 1.0f : 0.6f);
        }
    }
}
