package me.autobot.playerdoll.api.inv.button;

public class ActionButton extends InvButton {

    private final String name;

    public ActionButton(String name) {
        this.name = name;
        put(this);
    }

    @Override
    public boolean isToggleable() {
        return false;
    }

    @Override
    public String registerName() {
        return name;
    }


    // Common Buttons
    public static final ActionButton NONE = new ActionButton("NONE");
    public static final ActionButton RETURN = new ActionButton("RETURN");
    public static final ActionButton PREVIOUS_PAGE = new ActionButton("PREVIOUS_PAGE");
    public static final ActionButton NEXT_PAGE = new ActionButton("NEXT_PAGE");
    public static final ActionButton PAGE_DISPLAY = new ActionButton("PAGE_DISPLAY");

    // INFO menu
    public static final ActionButton OPEN_DOLL_SETTING = new ActionButton("OPEN_DOLL_SETTING");
    public static final ActionButton OPEN_GSETTING = new ActionButton("OPEN_GSETTING");
    public static final ActionButton OPEN_DATA = new ActionButton("OPEN_DATA");
    public static final ActionButton DOLL_OFFLINE = new ActionButton("DOLL_OFFLINE");
    public static final ActionButton DOLL_REMOVE = new ActionButton("DOLL_REMOVE");

    // DATA menu
    public static final ActionButton OPEN_BACKPACK = new ActionButton("OPEN_BACKPACK");
    public static final ActionButton OPEN_INVENTORY = new ActionButton("OPEN_INVENTORY");
    public static final ActionButton OPEN_ENDER_CHEST = new ActionButton("OPEN_ENDER_CHEST");
    public static final ActionButton GET_EXP = new ActionButton("GET_EXP");

}
