package me.autobot.addonWrapper.wrapper;

import me.autobot.playerdoll.api.wrapper.Wrapper;
import me.autobot.playerdoll.api.wrapper.builtin.WInteractionResult;
import net.minecraft.world.InteractionResult;

@Wrapper(wrapping = InteractionResult.class, method = "wrap")
public class WInteractionResultImpl extends WInteractionResult<InteractionResult> {

    private final InteractionResult interactionResult;
    public static WInteractionResultImpl wrap(InteractionResult interactionResult) {
        return new WInteractionResultImpl(interactionResult);
    }

    private WInteractionResultImpl(InteractionResult interactionResult) {
        this.interactionResult = interactionResult;
    }

    @Override
    public boolean consumesAction() {
        return interactionResult instanceof InteractionResult.Success;
    }

    @Override
    public boolean shouldSwing() {
        if (interactionResult instanceof InteractionResult.Success success) {
            return success.swingSource() == InteractionResult.SwingSource.SERVER;
        }
        return false;
    }

    @Override
    public InteractionResult getInstance() {
        return interactionResult;
    }
}
