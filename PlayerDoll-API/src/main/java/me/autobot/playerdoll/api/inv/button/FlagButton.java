package me.autobot.playerdoll.api.inv.button;

import org.bukkit.Material;

public abstract class FlagButton extends InvButton {

    private final String name;
    private final String permission;
    private final String flagPath;
    private final Material material;

    public FlagButton(String name, String permission, String flagPath, Material material) {
        this.name = name;
        this.permission = permission;
        this.flagPath = flagPath;
        this.material = material;
        put(this);
    }

    public String getPermission() {
        return permission;
    }

    public String getFlagPath() {
        return flagPath;
    }
    public Material getMaterial() {
        return material;
    }

    @Override
    public boolean isToggleable() {
        return true;
    }

    @Override
    public String registerName() {
        return name;
    }
}
