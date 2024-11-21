package me.autobot.playerdoll.api.doll;

import me.autobot.playerdoll.api.inv.button.GlobalFlagButton;

import java.util.ArrayList;
import java.util.List;

public class DollSetting {

    public static final List<DollSetting> SETTINGS = new ArrayList<>();

    private final GlobalFlagButton type;
    private final String path;
    private final boolean b;
    public DollSetting(GlobalFlagButton type, String path, boolean b) {
        this.type = type;
        this.path = "Doll-Setting.".concat(path);
        this.b = b;
        SETTINGS.add(this);
    }
    public GlobalFlagButton getType() {
        return type;
    }
    public String getPath() {
        return path;
    }
    public boolean defaultSetting() {
        return b;
    }


    public static final DollSetting GLOW = new DollSetting(GlobalFlagButton.GLOW, "glow", false);
    public static final DollSetting GRAVITY = new DollSetting(GlobalFlagButton.GRAVITY, "gravity", true);
    public static final DollSetting HOSTILITY = new DollSetting(GlobalFlagButton.HOSTILITY, "hostility", true);
    public static final DollSetting HIDE_FROM_LIST = new DollSetting(GlobalFlagButton.HIDE_FROM_LIST, "hide_from_list", false);
    public static final DollSetting INVULNERABLE = new DollSetting(GlobalFlagButton.INVULNERABLE, "invulnerable", false);
    public static final DollSetting JOIN_AT_START = new DollSetting(GlobalFlagButton.JOIN_AT_START, "join_at_start", false);
    public static final DollSetting LARGE_STEP_SIZE = new DollSetting(GlobalFlagButton.LARGE_STEP_SIZE, "large_step_size", false);
    public static final DollSetting PHANTOM = new DollSetting(GlobalFlagButton.PHANTOM, "phantom", false);
    public static final DollSetting PICKABLE = new DollSetting(GlobalFlagButton.PICKABLE, "pickable", true);
    public static final DollSetting PUSHABLE = new DollSetting(GlobalFlagButton.PUSHABLE, "pushable", false);
    public static final DollSetting REAL_PLAYER_TICK_UPDATE = new DollSetting(GlobalFlagButton.REAL_PLAYER_TICK_UPDATE, "real_player_tick_update", false);
    public static final DollSetting REAL_PLAYER_TICK_ACTION = new DollSetting(GlobalFlagButton.REAL_PLAYER_TICK_ACTION, "real_player_tick_action", false);
}
