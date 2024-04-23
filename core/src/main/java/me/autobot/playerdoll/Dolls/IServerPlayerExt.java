package me.autobot.playerdoll.Dolls;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import org.bukkit.entity.Player;

public interface IServerPlayerExt {

    boolean isPlayer();
    boolean isDoll();
    Player getBukkitPlayer();
    EntityPlayerActionPack getActionPack();

    // Action Pack
    void _resetLastActionTime();
    void _resetAttackStrengthTicker();
    void _setJumping(boolean b);
    void _jumpFromGround();

    // Action Pack
}
