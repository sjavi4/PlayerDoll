package me.autobot.playerdoll.CarpetMod;

import net.minecraft.core.Direction;

public interface IEntityPlayerActionPack {
    void copyFrom(IEntityPlayerActionPack other);
    void start(Object actionType, Object action);
    void setSneaking(boolean doSneak);
    void setSprinting(boolean doSprint);
    void setForward(float value);
    void setStrafing(float value);
    void look(String value);
    //void look(Vec2 rotation);
    void look(float yaw, float pitch);
    void lookAt(float x, float y, float z);
    void lookAt(String player);
    void turn(float yaw, float pitch);
    //void turn(Vec2 rotation);
    void stopMovement();
    void stopAll();
    void mount(boolean onlyRideables);
    void dismount();
    void onUpdate();
    void drop(int selectedSlot, boolean dropAll);
    void setSlot(int slot);

    Object ActionType_use();
    Object ActionType_attack();
    Object ActionType_jump();
    Object ActionType_drop_item();
    Object ActionType_drop_stack();
    Object ActionType_swap_hands();
    Object Action_once();
    Object Action_continuous();
    Object Action_interval(int interval);
    Object Action_interval(int interval, int offset);

}

