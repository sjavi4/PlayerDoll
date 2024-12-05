package me.autobot.addonWrapper.wrapper;

import me.autobot.playerdoll.api.wrapper.Wrapper;
import me.autobot.playerdoll.api.wrapper.WrapperRegistry;
import me.autobot.playerdoll.api.wrapper.builtin.WEntity;
import me.autobot.playerdoll.api.wrapper.builtin.WEntityHitResult;
import me.autobot.playerdoll.api.wrapper.builtin.WVec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

@Wrapper(wrapping = EntityHitResult.class, method = "wrap")
public class WEntityHitResultImpl extends WEntityHitResult<EntityHitResult> {

    private final EntityHitResult entityHitResult;
    public static WEntityHitResultImpl wrap(EntityHitResult hitResult) {
        return new WEntityHitResultImpl(hitResult);
    }

    private WEntityHitResultImpl(EntityHitResult entityHitResult) {
        this.entityHitResult = entityHitResult;
    }
    @Override
    public WEntity<Entity> getEntity() {
        Class<? extends WEntity<Entity>> wrapper = (Class<? extends WEntity<Entity>>) WrapperRegistry.getWrapper(WEntity.class, entityHitResult.getEntity());
        return WrapperRegistry.wrapFrom(wrapper, entityHitResult.getEntity());
    }

    @Override
    public WVec3<Vec3> getLocation() {
        Class<? extends WVec3<Vec3>> wrapper = (Class<? extends WVec3<Vec3>>) WrapperRegistry.getWrapper(WVec3.class, entityHitResult.getLocation());
        return WrapperRegistry.wrapFrom(wrapper, entityHitResult.getLocation());

    }

    @Override
    public EntityHitResult getInstance() {
        return entityHitResult;
    }
}
