package me.autobot.playerdoll.carpetmod;

import me.autobot.playerdoll.util.ReflectionUtil;
import me.autobot.playerdoll.wrapper.WrapperServerLevel;
import me.autobot.playerdoll.wrapper.block.WrapperBlockPos;
import me.autobot.playerdoll.wrapper.entity.WrapperEntity;
import me.autobot.playerdoll.wrapper.entity.WrapperInteractionResult;
import me.autobot.playerdoll.wrapper.entity.WrapperServerPlayerGameMode;
import me.autobot.playerdoll.wrapper.phys.WrapperVec3;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

public abstract class ActionPackPlayer {

    private static final Enum<?>[] interactionHandEnums;

    static {
        // InteractionHand (mojang) / EnumHand
        Class<?> interactionHandClass =  ReflectionUtil.getClass("net.minecraft.world.EnumHand");
        Objects.requireNonNull(interactionHandClass, "interactionHandClass");
        try {
            interactionHandEnums = (Enum<?>[]) interactionHandClass.getMethod("values").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public static Enum<?>[] getInteractionHandEnums() {
        // MainHand, OffHand
        return interactionHandEnums;
    }
    public abstract Object toServerPlayer();
    public abstract List<?> getEntities(Object box);
    public abstract void resetLastActionTime();
    public abstract void releaseUsingItem();
    public abstract void resetAttackStrengthTicker();
    public abstract WrapperServerPlayerGameMode getGameMode();
    public abstract void setJumping(boolean b);
    public abstract void jumpFromGround();
    public abstract WrapperServerLevel serverLevel();

    public abstract void look(float yaw, float pitch);
    public abstract void lookAt(WrapperVec3 vec3);
    public abstract void setZZA(float f);
    public abstract void setXXA(float f);
    public abstract WrapperServerLevel level();

    public abstract Object getItemInHand(Enum<?> interactionHand);
    public abstract WrapperInteractionResult interactOn(WrapperEntity entity, Enum<?> hand);
    public abstract boolean blockActionRestricted(WrapperServerLevel level, WrapperBlockPos blockPos, Enum<?> gameType);

    public abstract WrapperEntity wrapEntity(Object nmsEntity);
}
