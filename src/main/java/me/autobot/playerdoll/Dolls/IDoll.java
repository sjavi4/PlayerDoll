package me.autobot.playerdoll.Dolls;


import net.minecraft.world.damagesource.DamageSource;
import org.bukkit.entity.Player;

public interface IDoll {
    void setConfigInformation();
    void initDoll();
    void setDollSkin();
    void teleportTo();
    void setDollLookAt();
    boolean canBeSeenAsEnemy();
    void die(DamageSource damageSource);
    void disconnect();
    void setNoPhantom(boolean b);
    String getDollName();
    Player getOwner();
    DollConfigManager getConfigManager();
}
