package me.autobot.playerdoll.addon;

import me.autobot.playerdoll.api.Addon;
import me.autobot.playerdoll.api.constant.AbsServerBranch;

public class Main implements Addon {
    @Override
    public void onEnable() {
        AbsServerBranch.FOLIA.setAddon(this);
    }

    @Override
    public void onDisable() {

    }
}
