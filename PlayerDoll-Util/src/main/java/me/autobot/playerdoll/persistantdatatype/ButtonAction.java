package me.autobot.playerdoll.persistantdatatype;

public enum ButtonAction implements Button {

    // Common Buttons
    NONE, RETURN,

    // INFO menu
    OPEN_DOLL_SETTING, OPEN_GSETTING, OPEN_DATA, DOLL_OFFLINE, DOLL_REMOVE,

    // DATA menu
    OPEN_BACKPACK, OPEN_INVENTORY, OPEN_ENDER_CHEST, GET_EXP;

    // Doll Setting menu
//    DOLL_ECHEST(true), GLOW(true), GRAVITY(true), HOSTILITY(true), DOLL_INV(true),
//    INVULNERABLE(true), JOIN_AT_START(true), LARGE_STEP_SIZE(true),
//    PHANTOM(true), PICKABLE(true), PUSHABLE(true),
//    REAL_PLAYER_TICK_UPDATE(true), REAL_PLAYER_TICK_ACTION(true),

    // GSET PSET menu
//    ADMIN(true),
//    ATTACK(true),
//    DESPAWN(true),
//    DROP(true),
//    ECHEST(true),
//    EXP(true),
//    GSET(true),
//    INFO(true),
//    INV(true),
//    JUMP(true),
//    LOOK(true),
//    LOOKAT(true),
//    MENU(true),
//    MOUNT(true),
//    MOVE(true),
//    PSET(true),
//    SET(true),
//    SLOT(true),
//    SNEAK(true),
//    SPAWN(true),
//    SPRINT(true),
//    STOP(true),
//    SWAP(true),
//    TP(true),
//    TURN(true),
//    USE(true),
//
//
//    CUSTOM;

    @Override
    public boolean isToggleable() {
        return false;
    }

    @Override
    public String getCommand() {
        return name();
    }
}
