package me.autobot.playerdoll.Events;

import me.autobot.playerdoll.FoliaSupport;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Random;

public class DollDamageEvent implements Listener {
    @EventHandler
    public void onDollTakeDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!PlayerDoll.dollManagerMap.containsKey(player.getName())) return;
            if (player.isBlocking() && event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) != 0) {
                // Fix weird knock-back while holding shield
                AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
                attribute.setBaseValue(Double.MAX_VALUE);
                Runnable r = () -> attribute.setBaseValue(attribute.getDefaultValue());
                if (PlayerDoll.isFolia) FoliaSupport.entityTask(player,r,1);
                else Bukkit.getScheduler().runTask(PlayerDoll.getPlugin(),r);
                player.getWorld().playSound(player, Sound.ITEM_SHIELD_BLOCK,0.8F, 0.8F + new Random().nextFloat()*0.4F);
            }
        }
    }
}
