package me.autobot.playerdoll.api.inv.button;

import org.bukkit.Material;

public class PersonalFlagButton extends FlagButton {

    public PersonalFlagButton(String name, String permission, String flagPath, Material material) {
        super(name, "playerdoll.personalflag.".concat(permission), "Personal-Flag.".concat(flagPath), material);
    }

    public PersonalFlagButton(String name, Material material) {
        super(name.toUpperCase(), "playerdoll.personalflag.".concat(name), "Personal-Flag.".concat(name), material);
    }

    public static final PersonalFlagButton ADMIN = new PersonalFlagButton("admin", Material.WOODEN_PICKAXE);
    public static final PersonalFlagButton ATTACK = new PersonalFlagButton("attack", Material.WOODEN_SWORD);
    public static final PersonalFlagButton DESPAWN = new PersonalFlagButton("despawn", Material.CRYING_OBSIDIAN);
    public static final PersonalFlagButton DISMOUNT = new PersonalFlagButton( "dismount", Material.MINECART);
    public static final PersonalFlagButton DROP = new PersonalFlagButton("drop", Material.DROPPER);
    public static final PersonalFlagButton ECHEST = new PersonalFlagButton("echest", Material.ENDER_EYE);
    public static final PersonalFlagButton EXP = new PersonalFlagButton("exp", Material.EXPERIENCE_BOTTLE);
    public static final PersonalFlagButton GSET = new PersonalFlagButton("gset", Material.CRAFTING_TABLE);
    public static final PersonalFlagButton HIDDEN = new PersonalFlagButton("hidden", Material.SPECTRAL_ARROW);
    public static final PersonalFlagButton INV = new PersonalFlagButton("inv", Material.CHEST_MINECART);
    public static final PersonalFlagButton JUMP = new PersonalFlagButton("jump", Material.RABBIT_FOOT);
    public static final PersonalFlagButton LOOK = new PersonalFlagButton("look", Material.SPYGLASS);
    public static final PersonalFlagButton LOOKAT = new PersonalFlagButton("lookat", Material.OBSERVER);
    public static final PersonalFlagButton MENU = new PersonalFlagButton("menu", Material.BARREL);
    public static final PersonalFlagButton MOUNT = new PersonalFlagButton("mount", Material.MINECART);
    public static final PersonalFlagButton MOVE = new PersonalFlagButton("move", Material.POWERED_RAIL);
    public static final PersonalFlagButton PSET = new PersonalFlagButton("pset", Material.FURNACE);
    public static final PersonalFlagButton SET = new PersonalFlagButton("set", Material.LECTERN);
    public static final PersonalFlagButton SLOT = new PersonalFlagButton("slot", Material.HOPPER);
    public static final PersonalFlagButton SNEAK = new PersonalFlagButton( "sneak", Material.MAGMA_BLOCK);
    public static final PersonalFlagButton SPAWN = new PersonalFlagButton("spawn", Material.RED_BED);
    public static final PersonalFlagButton SPRINT = new PersonalFlagButton("sprint", Material.ICE);
    public static final PersonalFlagButton STOP = new PersonalFlagButton("stop", Material.BLAZE_POWDER);
    public static final PersonalFlagButton SWAP = new PersonalFlagButton("swap", Material.DAYLIGHT_DETECTOR);
    public static final PersonalFlagButton TP = new PersonalFlagButton("tp", Material.ENDER_PEARL);
    public static final PersonalFlagButton TURN = new PersonalFlagButton("turn", Material.IRON_TRAPDOOR);
    public static final PersonalFlagButton UNSNEAK = new PersonalFlagButton("unsneak", Material.MAGMA_BLOCK);
    public static final PersonalFlagButton UNSPRINT = new PersonalFlagButton("unsprint", Material.ICE);
    public static final PersonalFlagButton USE = new PersonalFlagButton("use", Material.OAK_BUTTON);
}
