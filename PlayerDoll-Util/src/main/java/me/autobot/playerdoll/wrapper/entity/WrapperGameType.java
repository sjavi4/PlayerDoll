package me.autobot.playerdoll.wrapper.entity;

import net.minecraft.world.level.EnumGamemode;
import org.bukkit.GameMode;

public interface WrapperGameType {

    EnumGamemode SURVIVAL = EnumGamemode.a;
    EnumGamemode CREATIVE = EnumGamemode.b;
    EnumGamemode ADVENTURE = EnumGamemode.c;
    EnumGamemode SPECTATOR = EnumGamemode.d;

    static EnumGamemode parse(GameMode craftGameMode) {
        return switch (craftGameMode) {
            case CREATIVE -> CREATIVE;
            case SURVIVAL -> SURVIVAL;
            case ADVENTURE -> ADVENTURE;
            case SPECTATOR -> SPECTATOR;
        };
    }
}
