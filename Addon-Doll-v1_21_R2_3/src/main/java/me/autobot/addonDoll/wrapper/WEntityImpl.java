package me.autobot.addonDoll.wrapper;

import me.autobot.addonDoll.action.PackPlayerImpl;
import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.action.pack.AbsPackPlayer;
import me.autobot.playerdoll.api.wrapper.Wrapper;
import me.autobot.playerdoll.api.wrapper.WrapperRegistry;
import me.autobot.playerdoll.api.wrapper.builtin.WEntity;
import me.autobot.playerdoll.api.wrapper.builtin.WInteractionResult;
import me.autobot.playerdoll.api.wrapper.builtin.WVec3;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@Wrapper(wrapping = Entity.class, method = "wrap")
public class WEntityImpl extends WEntity<Entity> {

    private final Entity entity;
    public static WEntityImpl wrap(Entity entity) {
        return new WEntityImpl(entity);
    }

    private WEntityImpl(Entity entity) {
        this.entity = entity;
    }

    @Override
    public double getX() {
        return entity.getX();
    }

    @Override
    public double getY() {
        return entity.getY();
    }

    @Override
    public double getZ() {
        return entity.getZ();
    }

    @Override
    public WInteractionResult<InteractionResult> interactAt(AbsPackPlayer player, WVec3<?> relativeHitPos, Enum<?> hand) {
        InteractionResult result = entity.interactAt(((PackPlayerImpl)player).getServerPlayer() , new Vec3(relativeHitPos.x(), relativeHitPos.y(), relativeHitPos.z()), (InteractionHand) hand);
        Class<? extends WInteractionResult<InteractionResult>> wrapper = (Class<? extends WInteractionResult<InteractionResult>>) WrapperRegistry.getWrapper(WInteractionResult.class, result);
        return WrapperRegistry.wrapFrom(wrapper, result);
    }

    @Override
    public org.bukkit.entity.Entity getBukkitEntity() {
        return ReflectionUtil.getCraftEntity(entity);
    }

    public Entity getInstance() {
        return entity;
    }
}
