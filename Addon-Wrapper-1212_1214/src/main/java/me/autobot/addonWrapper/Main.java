package me.autobot.addonWrapper;

import me.autobot.addonWrapper.wrapper.*;
import me.autobot.playerdoll.api.Addon;
import me.autobot.playerdoll.api.wrapper.WrapperRegistry;

public class Main implements Addon {
    @Override
    public void onEnable() {
        WrapperRegistry.put(WBlockHitResultImpl.class);
        WrapperRegistry.put(WBlockPosImpl.class);
        WrapperRegistry.put(WEntityHitResultImpl.class);
        WrapperRegistry.put(WHitResultImpl.class);
        WrapperRegistry.put(WInteractionResultImpl.class);
        WrapperRegistry.put(WVec2Impl.class);
        WrapperRegistry.put(WVec3Impl.class);
    }

    @Override
    public void onDisable() {

    }
}
