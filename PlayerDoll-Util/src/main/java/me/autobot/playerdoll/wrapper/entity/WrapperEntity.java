package me.autobot.playerdoll.wrapper.entity;

import me.autobot.playerdoll.util.ReflectionUtil;
import me.autobot.playerdoll.wrapper.Wrapper;
import me.autobot.playerdoll.wrapper.phys.WrapperVec3;
import net.minecraft.world.entity.Entity;

public interface WrapperEntity {

    static WrapperEntity invokeNMS(Object nmsEntity) {
        Class<?> nmsWrapperEntity = ReflectionUtil.getPluginNMSClass("wrapper.NMSEntity");
        return (WrapperEntity) ReflectionUtil.newInstance(nmsWrapperEntity.getConstructors()[0], nmsEntity);
    }
    Object toObj();

    org.bukkit.entity.Entity getCraftEntity();

    WrapperInteractionResult interactAt(Object serverPlayer, WrapperVec3 vec3, Enum<?> hand);

    float getPickRadius();

    Object getRootVehicle();

    Object getViewVector(float f);
    Object getEyePosition(float f);
}
