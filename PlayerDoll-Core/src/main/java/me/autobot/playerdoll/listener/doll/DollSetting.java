package me.autobot.playerdoll.listener.doll;

import me.autobot.playerdoll.api.doll.DollConfig;
import me.autobot.playerdoll.api.event.doll.DollSettingEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static me.autobot.playerdoll.api.doll.DollSetting.*;

public class DollSetting implements Listener {
    @EventHandler
    public void onDollSetting(DollSettingEvent event) {
        Player player = event.getWhoChanged().getBukkitPlayer();
        boolean b = event.getToggleState();
        DollConfig dollConfig = DollConfig.getOnlineConfig(player.getUniqueId());
        dollConfig.dollSetting.get(event.getSetting().getType()).setNewValue(b);
        if (event.getSetting() == GLOW) {
            player.setGlowing(b);
        } else if (event.getSetting() == GRAVITY) {
            player.setGravity(b);
        } else if (event.getSetting() == PICKABLE) {
            player.setCanPickupItems(b);
        } else if (event.getSetting() == PUSHABLE) {
            player.setCollidable(b);
        } else if (event.getSetting() == INVULNERABLE) {
            player.setInvulnerable(b);
        } else if (event.getSetting() == LARGE_STEP_SIZE) {
            event.getWhoChanged().setDollMaxUpStep(b ? 1.0d : 0.6d);
        }
    }
}
