package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.PlayerDoll;
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
        property = DollConfigManager.dollConfigManagerMap.get(player);
        doll = player;
        this.iDoll = iDoll;
        property.addListener(this);
    }

    final Map<String, Consumer<Boolean>> settings = new HashMap<>() {{
        put("Invulnerable", (b) -> doll.setNoDamageTicks(b ? Integer.MAX_VALUE : 0));
        put("Glow", (b) -> doll.setGlowing(b));
        put("Large Step Size", (b) -> iDoll._setMaxUpStep(b ? 1.0f : 0.6f));
        put("Pushable", (b) -> doll.setCollidable(b));
        put("Gravity", (b) -> doll.setGravity(b));
        put("Phantom", (b) -> iDoll.setNoPhantom(!b));
        put("Pickup-able",(b) -> doll.setCanPickupItems(b));
    }};
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (settings.containsKey(evt.getPropertyName())) {
            settings.get(evt.getPropertyName()).accept((Boolean) evt.getNewValue());
        }
    }
}
