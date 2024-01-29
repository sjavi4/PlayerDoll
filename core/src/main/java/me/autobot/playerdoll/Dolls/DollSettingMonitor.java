package me.autobot.playerdoll.Dolls;

import org.bukkit.entity.Player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DollSettingMonitor implements PropertyChangeListener {
    public DollConfigManager property;
    private Player doll;
    private IDoll iDoll;
    public DollSettingMonitor(Player player, IDoll iDoll) {
        property = DollConfigManager.dollConfigManagerMap.get(player.getUniqueId());
        doll = player;
        this.iDoll = iDoll;
        property.addListener(this);
    }

    final Map<String, Consumer<Boolean>> settings = new HashMap<>() {{
        put("invulnerable", (b) -> doll.setNoDamageTicks(b ? Integer.MAX_VALUE : 0));
        put("glow", (b) -> doll.setGlowing(b));
        put("large_step_size", (b) -> iDoll._setMaxUpStep(b ? 1.0f : 0.6f));
        put("pushable", (b) -> doll.setCollidable(b));
        put("gravity", (b) -> doll.setGravity(b));
        put("phantom", (b) -> iDoll.setNoPhantom(!b));
        put("pickupable",(b) -> doll.setCanPickupItems(b));
    }};
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (settings.containsKey(evt.getPropertyName())) {
            settings.get(evt.getPropertyName()).accept((Boolean) evt.getNewValue());
        }
    }
}
