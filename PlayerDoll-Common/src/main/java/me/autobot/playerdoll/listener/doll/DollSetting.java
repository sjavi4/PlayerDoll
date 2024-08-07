package me.autobot.playerdoll.listener.doll;

import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.event.DollSettingEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DollSetting implements Listener {
    @EventHandler
    public void onDollSetting(DollSettingEvent event) {
        Player player = event.getWhoChanged().getBukkitPlayer();
        boolean b = event.getToggleState();
        DollConfig dollConfig = DollConfig.DOLL_CONFIGS.get(player.getUniqueId());
        dollConfig.dollSetting.get(event.getSetting().getType()).setNewValue(b);
        switch (event.getSetting()) {
            case GLOW -> player.setGlowing(b);
            case GRAVITY -> player.setGravity(b);
            case PICKABLE -> player.setCanPickupItems(b);
            case PUSHABLE -> player.setCollidable(b);
            case INVULNERABLE -> player.setInvulnerable(b);
            case LARGE_STEP_SIZE -> event.getWhoChanged().setDollMaxUpStep(b ? 1.0d : 0.6d);
        }
    }
}
