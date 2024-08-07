package me.autobot.playerdoll.v1_21_R1.wrapper;

import me.autobot.playerdoll.wrapper.Wrapper;
import me.autobot.playerdoll.wrapper.entity.WrapperEntity;
import me.autobot.playerdoll.wrapper.entity.WrapperInteractionResult;
import me.autobot.playerdoll.wrapper.phys.WrapperVec3;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class NMSEntity extends Wrapper<Entity> implements WrapperEntity {
    public NMSEntity(Object o) {
        super(o);
    }

    @Override
    public org.bukkit.entity.Entity getCraftEntity() {
        return source.getBukkitEntity();
    }

    @Override
    public WrapperInteractionResult interactAt(Object serverPlayer, WrapperVec3 vec3, Enum<?> hand) {
        return new WrapperInteractionResult(source.interactAt((ServerPlayer) serverPlayer, (Vec3) vec3.toObj(), (InteractionHand) hand));
    }

    @Override
    public float getPickRadius() {
        return source.getPickRadius();
    }

    @Override
    public Object getRootVehicle() {
        return source.getRootVehicle();
    }

    @Override
    public Object getViewVector(float f) {
        return source.getViewVector(f);
    }

    @Override
    public Object getEyePosition(float f) {
        return source.getEyePosition(f);
    }
}
