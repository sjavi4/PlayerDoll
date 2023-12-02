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
        put("setting.Invulnerable", (b) -> doll.setNoDamageTicks(b ? Integer.MAX_VALUE : 0));
        put("setting.Glow", (b) -> doll.setGlowing(b));
        put("setting.Large Step Size", (b) -> iDoll._setMaxUpStep(b ? 1.0f : 0.6f));
        put("setting.Pushable", (b) -> doll.setCollidable(b));
        put("setting.Gravity", (b) -> doll.setGravity(b));
        put("setting.Phantom", (b) -> iDoll.setNoPhantom(!b));
    }};
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (settings.containsKey(evt.getPropertyName())) {
            settings.get(evt.getPropertyName()).accept((Boolean) evt.getNewValue());
        }
    }
}
