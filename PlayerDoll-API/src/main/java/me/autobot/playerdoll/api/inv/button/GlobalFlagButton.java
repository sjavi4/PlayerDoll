package me.autobot.playerdoll.api.inv.button;

import org.bukkit.Material;

public class GlobalFlagButton extends FlagButton {
    public GlobalFlagButton(String name, String permission, String flagPath, Material material) {
        super(name, "playerdoll.globalflag.".concat(permission), "Global-Flag.".concat(flagPath), material);
    }
    public GlobalFlagButton(String name, Material material) {
        super(name.toUpperCase(), "playerdoll.globalflag.".concat(name), "Global-Flag.".concat(name), material);
    }

    public static final GlobalFlagButton GLOW = new GlobalFlagButton("glow", Material.GLOW_INK_SAC);
    public static final GlobalFlagButton GRAVITY = new GlobalFlagButton("gravity", Material.GRAVEL);
    public static final GlobalFlagButton HOSTILITY = new GlobalFlagButton("hostility", Material.TARGET);
    public static final GlobalFlagButton HIDE_FROM_LIST = new GlobalFlagButton("hide_from_list", Material.TINTED_GLASS);
    public static final GlobalFlagButton INVULNERABLE = new GlobalFlagButton("invulnerable", Material.TOTEM_OF_UNDYING);
    public static final GlobalFlagButton JOIN_AT_START = new GlobalFlagButton("join_at_start", Material.RECOVERY_COMPASS);
    public static final GlobalFlagButton LARGE_STEP_SIZE = new GlobalFlagButton("large_step_size", Material.STONE_STAIRS);
    public static final GlobalFlagButton PHANTOM = new GlobalFlagButton("phantom", Material.PHANTOM_MEMBRANE);
    public static final GlobalFlagButton PICKABLE = new GlobalFlagButton("pickable", Material.HOPPER);
    public static final GlobalFlagButton PUSHABLE = new GlobalFlagButton("pushable", Material.ARMOR_STAND);
    public static final GlobalFlagButton REAL_PLAYER_TICK_UPDATE = new GlobalFlagButton("real_player_tick_update", Material.FISHING_ROD);
    public static final GlobalFlagButton REAL_PLAYER_TICK_ACTION = new GlobalFlagButton("real_player_tick_action", Material.CARROT_ON_A_STICK);
}
