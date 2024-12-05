package me.autobot.addonWrapper.wrapper;

import me.autobot.playerdoll.api.wrapper.Wrapper;
import me.autobot.playerdoll.api.wrapper.WrapperRegistry;
import me.autobot.playerdoll.api.wrapper.builtin.WBlockHitResult;
import me.autobot.playerdoll.api.wrapper.builtin.WEntityHitResult;
import me.autobot.playerdoll.api.wrapper.builtin.WHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.Function;

@Wrapper(wrapping = HitResult.class, method = "wrap")
public class WHitResultImpl extends WHitResult<HitResult> {

    private final HitResult hitResult;
    public static WHitResultImpl wrap(HitResult hitResult) {
        return new WHitResultImpl(hitResult);
    }

    private WHitResultImpl(HitResult hitResult) {
        this.hitResult = hitResult;
    }

    @Override
    public Boolean parseResultForUse(Function<WBlockHitResult<?>, Boolean> blockHit, Function<WEntityHitResult<?>, Boolean> entityHit) {
        Boolean executed = null;
        switch (hitResult.getType()) {
            case BLOCK -> {
                Class<? extends WBlockHitResult<BlockHitResult>> wrapper = (Class<? extends WBlockHitResult<BlockHitResult>>) WrapperRegistry.getWrapper(WBlockHitResult.class, (BlockHitResult) hitResult);
                executed = blockHit.apply(WrapperRegistry.wrapFrom(wrapper, (BlockHitResult) hitResult));
            }
            case ENTITY -> {
                Class<? extends WEntityHitResult<EntityHitResult>> wrapper = (Class<? extends WEntityHitResult<EntityHitResult>>) WrapperRegistry.getWrapper(WEntityHitResult.class, (EntityHitResult) hitResult);
                executed = entityHit.apply(WrapperRegistry.wrapFrom(wrapper, (EntityHitResult) hitResult));
            }
        }
        return executed;
    }

    @Override
    public boolean parseResultForAttack(Function<WBlockHitResult<?>, Boolean> blockHit, Function<WEntityHitResult<?>, Boolean> entityHit) {
        boolean result = false;
        switch (hitResult.getType()) {
            case ENTITY -> {
                Class<? extends WEntityHitResult<EntityHitResult>> wrapper = (Class<? extends WEntityHitResult<EntityHitResult>>) WrapperRegistry.getWrapper(WEntityHitResult.class, (EntityHitResult) hitResult);
                result = entityHit.apply(WrapperRegistry.wrapFrom(wrapper, (EntityHitResult) hitResult));
            }
            case BLOCK -> {
                Class<? extends WBlockHitResult<BlockHitResult>> wrapper = (Class<? extends WBlockHitResult<BlockHitResult>>) WrapperRegistry.getWrapper(WBlockHitResult.class, (BlockHitResult) hitResult);
                result = blockHit.apply(WrapperRegistry.wrapFrom(wrapper, (BlockHitResult) hitResult));
            }
        }
        return result;
    }

    @Override
    public HitResult getInstance() {
        return hitResult;
    }
}
