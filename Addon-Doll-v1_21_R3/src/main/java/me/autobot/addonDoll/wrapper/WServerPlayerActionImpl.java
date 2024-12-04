package me.autobot.addonDoll.wrapper;

import me.autobot.playerdoll.api.wrapper.Wrapper;
import me.autobot.playerdoll.api.wrapper.builtin.WServerPlayerAction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;

@Wrapper(wrapping = ServerboundPlayerActionPacket.class, method = "wrap")
public class WServerPlayerActionImpl extends WServerPlayerAction<ServerboundPlayerActionPacket> {

    public static ServerboundPlayerActionPacket.Action parse(Action action) {
        return switch (action) {
            case ABORT_DESTROY_BLOCK -> ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK;
            case START_DESTROY_BLOCK -> ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK;
            case STOP_DESTROY_BLOCK -> ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK;
        };
    }

    @Override
    public ServerboundPlayerActionPacket getInstance() {
        return null;
    }
}
