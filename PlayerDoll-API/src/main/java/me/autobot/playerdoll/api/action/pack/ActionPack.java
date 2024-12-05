package me.autobot.playerdoll.api.action.pack;

import me.autobot.playerdoll.api.action.Action;
import me.autobot.playerdoll.api.action.ActionTypeHelper;
import me.autobot.playerdoll.api.action.type.AbsActionType;
import me.autobot.playerdoll.api.doll.BaseEntity;
import me.autobot.playerdoll.api.doll.Doll;
import me.autobot.playerdoll.api.wrapper.builtin.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ActionPack {

    public final BaseEntity baseEntity;
    public final AbsPackPlayer packPlayer;
    private final Map<AbsActionType, Action> actions = new HashMap<>();
    public UUID lookingAtEntity;
    public WBlockPos<?> currentBlock;
    public int blockHitDelay;
    private boolean isHittingBlock;
    public float curBlockDamageMP;

    private boolean sneaking;
    private boolean sprinting;
    private float forward;
    private float strafing;

    public int itemUseCooldown;

    public Player bukkitPlayer() {
        return baseEntity.getBukkitPlayer();
    }
    public ActionPack(BaseEntity baseEntity, AbsPackPlayer packPlayer) {
        this.baseEntity = baseEntity;
        this.packPlayer = packPlayer;
        stopAll();
    }

    public ActionPack start(AbsActionType type, Action action)
    {
        Action previous = actions.remove(type);
        if (previous != null) type.stop(baseEntity, previous);
        if (action != null)
        {
            actions.put(type, action);
            type.start(baseEntity, action); // noop
        }
        return this;
    }

    public ActionPack setSneaking(boolean doSneak)
    {
        sneaking = doSneak;
        bukkitPlayer().setSneaking(doSneak);
        if (sprinting && sneaking)
            setSprinting(false);
        return this;
    }
    public ActionPack setSprinting(boolean doSprint)
    {
        sprinting = doSprint;
        bukkitPlayer().setSprinting(doSprint);
        if (sneaking && sprinting)
            setSneaking(false);
        return this;
    }

    public ActionPack setForward(float value)
    {
        forward = value;
        return this;
    }
    public ActionPack setStrafing(float value)
    {
        strafing = value;
        return this;
    }
    public ActionPack look(WDirection<?> direction)
    {
        return switch (direction.parse())
        {
            case NORTH -> look(180, 0);
            case SOUTH -> look(0, 0);
            case EAST  -> look(-90, 0);
            case WEST  -> look(90, 0);
            case UP    -> look(bukkitPlayer().getEyeLocation().getYaw(), -90);
            case DOWN  -> look(bukkitPlayer().getEyeLocation().getYaw(), 90);
        };
    }

    public ActionPack look(WDirection.Direction direction) {

        return switch (direction) {
            case NORTH -> look(180, 0);
            case SOUTH -> look(0, 0);
            case EAST -> look(-90, 0);
            case WEST -> look(90, 0);
            case UP -> look(bukkitPlayer().getEyeLocation().getYaw(), -90);
            case DOWN -> look(bukkitPlayer().getEyeLocation().getYaw(), 90);
        };
    }
    public ActionPack look(WVec2<?> rotation)
    {
        return look(rotation.x(), rotation.y());
    }

    public ActionPack look(float yaw, float pitch)
    {
        packPlayer.look(yaw, pitch);
        return this;
    }

    public ActionPack lookAt(WVec3<?> position)
    {
        packPlayer.lookAt(position);
        return this;
    }
    public ActionPack lookAt(double x, double y, double z)
    {
        packPlayer.lookAt(x, y, z);
        return this;
    }

    public ActionPack turn(float yaw, float pitch)
    {
        return look(bukkitPlayer().getEyeLocation().getYaw() + yaw, bukkitPlayer().getEyeLocation().getPitch() + pitch);
    }

    public ActionPack turn(WVec2<?> rotation)
    {
        return turn(rotation.x(), rotation.y());
    }

    public ActionPack stopMovement()
    {
        setSneaking(false);
        setSprinting(false);
        forward = 0.0F;
        strafing = 0.0F;
        return this;
    }

    public ActionPack stopAll()
    {
        for (AbsActionType type : actions.keySet()) type.stop(baseEntity, actions.get(type));
        actions.clear();
        return stopMovement();
    }

    public ActionPack mount(boolean onlyRideables)
    {
        //test what happens
        List<Entity> entities;
        if (onlyRideables)
        {
            entities = bukkitPlayer().getNearbyEntities(3,1,3).stream()
                    .filter(e -> e instanceof AbstractHorse || e instanceof Boat || e instanceof RideableMinecart)
                    .toList();
        }
        else
        {
            entities = bukkitPlayer().getNearbyEntities(3,1,3);
        }
        if (entities.isEmpty()) return this;
        Entity closest = null;
        double distance = Double.POSITIVE_INFINITY;
        Entity currentVehicle = bukkitPlayer().getVehicle();
        for (Entity e: entities)
        {
            if (e == bukkitPlayer() || (currentVehicle == e))
                continue;
            double dd = bukkitPlayer().getLocation().distanceSquared(e.getLocation());
            if (dd<distance)
            {
                distance = dd;
                closest = e;
            }
        }
        if (closest == null) return this;
        closest.addPassenger(bukkitPlayer());
        return this;
    }
    public ActionPack dismount()
    {
        Entity v = bukkitPlayer().getVehicle();
        if (v != null) {
            v.removePassenger(bukkitPlayer());
        }
        return this;
    }

    public void onUpdate()
    {
        Map<AbsActionType, Boolean> actionAttempts = new HashMap<>();
        actions.values().removeIf(e -> e.done);
        for (Map.Entry<AbsActionType, Action> e : actions.entrySet())
        {
            AbsActionType type = e.getKey();
            Action action = e.getValue();
            // skipping attack if use was successful
            if (!(actionAttempts.getOrDefault(ActionTypeHelper.Defaults.USE.get(), false) && type == ActionTypeHelper.Defaults.ATTACK.get()))
            {
                Boolean actionStatus = action.tick(this, type);
                if (actionStatus != null)
                    actionAttempts.put(type, actionStatus);
            }
            // optionally retrying use after successful attack and unsuccessful use
            if (type == ActionTypeHelper.Defaults.ATTACK.get()
                    && actionAttempts.getOrDefault(ActionTypeHelper.Defaults.ATTACK.get(), false)
                    && !actionAttempts.getOrDefault(ActionTypeHelper.Defaults.USE.get(), true) )
            {
                // according to MinecraftClient.handleInputEvents
                Action using = actions.get(ActionTypeHelper.Defaults.USE.get());
                if (using != null) // this is always true - we know use worked, but just in case
                {
                    using.retry(this, ActionTypeHelper.Defaults.USE.get());
                }
            }
            if (type == ActionTypeHelper.Defaults.LOOK_AT.get()) {
                if (lookingAtEntity == null || Bukkit.getEntity(lookingAtEntity) == null) {
                    lookingAtEntity = null;
                    action.done = true;
                }
            }
        }
        float vel = sneaking?0.3F:1.0F;
        // The != 0.0F checks are needed given else real players can't control minecarts, however it works with fakes and else they don't stop immediately
        if (forward != 0.0F || baseEntity instanceof Doll) {
            packPlayer.setZZA(forward * vel);
        }
        if (strafing != 0.0F || baseEntity instanceof Doll) {
            packPlayer.setXXA(strafing * vel);
        }
    }

    public static WHitResult<?> getTarget(BaseEntity player)
    {
        double reach = player.getBukkitPlayer().getGameMode() == GameMode.CREATIVE ? 5 : 4.5f;

        return Tracer.getTracer().rayTrace(player, 1, reach, false);
    }

    private void dropItemFromSlot(int slot, boolean dropAll)
    {
        PlayerInventory inv = bukkitPlayer().getInventory(); // getInventory;
        ItemStack itemStack = inv.getItem(slot);
        if (!isEmpty(itemStack)) {
            packPlayer.dropItem(slot, dropAll, itemStack.getAmount());
        }
    }

    public void drop(int selectedSlot, boolean dropAll)
    {
        PlayerInventory inv = bukkitPlayer().getInventory(); // getInventory;
        if (selectedSlot == -2) // all
        {
            for (int i = inv.getSize(); i >= 0; i--)
                dropItemFromSlot(i, dropAll);
        }
        else // one slot
        {
            if (selectedSlot == -1)
                selectedSlot = inv.getHeldItemSlot();
            dropItemFromSlot(selectedSlot, dropAll);
        }
    }

    public void setSlot(int slot)
    {
        bukkitPlayer().getInventory().setHeldItemSlot(slot-1);
    }

    public static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().isAir();
    }

    public void setLookingAtEntity(UUID uuid) {
        lookingAtEntity = uuid;
    }
}
