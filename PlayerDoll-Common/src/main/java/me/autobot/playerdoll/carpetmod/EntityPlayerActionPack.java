package me.autobot.playerdoll.carpetmod;

import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.Doll;
import me.autobot.playerdoll.util.ReflectionUtil;
import me.autobot.playerdoll.wrapper.WrapperServerLevel;
import me.autobot.playerdoll.wrapper.block.WrapperBlockPos;
import me.autobot.playerdoll.wrapper.block.WrapperBlockState;
import me.autobot.playerdoll.wrapper.block.WrapperDirection;
import me.autobot.playerdoll.wrapper.entity.WrapperEntity;
import me.autobot.playerdoll.wrapper.entity.WrapperGameType;
import me.autobot.playerdoll.wrapper.entity.WrapperInteractionResult;
import me.autobot.playerdoll.wrapper.packet.WrapperServerboundPlayerActionPacket_Action;
import me.autobot.playerdoll.wrapper.phys.WrapperBlockHitResult;
import me.autobot.playerdoll.wrapper.phys.WrapperVec2;
import me.autobot.playerdoll.wrapper.phys.WrapperVec3;
import net.minecraft.core.EnumDirection;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;

import java.util.*;


public class EntityPlayerActionPack {
    private final BaseEntity baseEntity;
    private final Player player;
    private final ActionPackPlayer actionPackPlayer;
    //private WrapperServerPlayer serverPlayer;
    //private final BaseEntity baseEntity;

    private final Map<ActionType, Action> actions = new EnumMap<>(ActionType.class);

    private WrapperBlockPos currentBlock;
    private int blockHitDelay;
    private boolean isHittingBlock;
    private float curBlockDamageMP;

    private UUID lookingAtEntity;

    private boolean sneaking;
    private boolean sprinting;
    private float forward;
    private float strafing;

    private int itemUseCooldown;

    public EntityPlayerActionPack(BaseEntity playerIn, ActionPackPlayer actionPackPlayer)
    {
        baseEntity = playerIn;
        player = playerIn.getBukkitPlayer();
        this.actionPackPlayer = actionPackPlayer;
        stopAll();
    }
//    public void copyFrom(EntityPlayerActionPack other)
//    {
//        actions.putAll(other.actions);
//        currentBlock = other.currentBlock;
//        blockHitDelay = other.blockHitDelay;
//        isHittingBlock = other.isHittingBlock;
//        curBlockDamageMP = other.curBlockDamageMP;
//
//        sneaking = other.sneaking;
//        sprinting = other.sprinting;
//        forward = other.forward;
//        strafing = other.strafing;
//
//        itemUseCooldown = other.itemUseCooldown;
//    }
    public void setLookingAtEntity(UUID uuid) {
        lookingAtEntity = uuid;
    }

    public EntityPlayerActionPack start(ActionType type, Action action)
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

    public EntityPlayerActionPack setSneaking(boolean doSneak)
    {
        sneaking = doSneak;
        player.setSneaking(doSneak);
        //baseEntity.setShiftKeyDown(doSneak);
        if (sprinting && sneaking)
            setSprinting(false);
        return this;
    }
    public EntityPlayerActionPack setSprinting(boolean doSprint)
    {
        sprinting = doSprint;
        player.setSprinting(doSprint);
        //baseEntity.setSprinting(doSprint);
        if (sneaking && sprinting)
            setSneaking(false);
        return this;
    }

    public EntityPlayerActionPack setForward(float value)
    {
        forward = value;
        return this;
    }
    public EntityPlayerActionPack setStrafing(float value)
    {
        strafing = value;
        return this;
    }
    public EntityPlayerActionPack look(EnumDirection direction)
    {
        if (direction == WrapperDirection.NORTH) {
            return look(180, 0);
        }
        if (direction == WrapperDirection.SOUTH) {
            return look(0, 0);
        }
        if (direction == WrapperDirection.EAST) {
            return look(-90, 0);
        }
        if (direction == WrapperDirection.WEST) {
            return look(90, 0);
        }
        if (direction == WrapperDirection.UP) {
            return look(player.getEyeLocation().getYaw(), -90);
        }
        if (direction == WrapperDirection.DOWN) {
            return look(player.getEyeLocation().getYaw(), 90);
        }
        return this;
    }
    public EntityPlayerActionPack look(WrapperVec2 rotation)
    {
        return look(rotation.x, rotation.y);
    }

    public EntityPlayerActionPack look(float yaw, float pitch)
    {
        actionPackPlayer.look(yaw, pitch);
        return this;
    }

    public EntityPlayerActionPack lookAt(WrapperVec3 position)
    {
        actionPackPlayer.lookAt(position);
        return this;
    }

    public EntityPlayerActionPack turn(float yaw, float pitch)
    {
        return look(player.getEyeLocation().getYaw() + yaw,player.getEyeLocation().getPitch() + pitch);
    }

    public EntityPlayerActionPack turn(WrapperVec2 rotation)
    {
        return turn(rotation.x, rotation.y);
    }

    public EntityPlayerActionPack stopMovement()
    {
        setSneaking(false);
        setSprinting(false);
        forward = 0.0F;
        strafing = 0.0F;
        return this;
    }


    public EntityPlayerActionPack stopAll()
    {
        for (ActionType type : actions.keySet()) type.stop(baseEntity, actions.get(type));
        actions.clear();
        return stopMovement();
    }

    public EntityPlayerActionPack mount(boolean onlyRideables)
    {
        //test what happens
        List<Entity> entities;
        if (onlyRideables)
        {
            entities = player.getNearbyEntities(3,1,3).stream()
                    .filter(e -> e instanceof AbstractHorse || e instanceof Boat || e instanceof RideableMinecart)
                    .toList();
        }
        else
        {
            entities = player.getNearbyEntities(3,1,3);
        }
        if (entities.isEmpty()) return this;
        Entity closest = null;
        double distance = Double.POSITIVE_INFINITY;
        Entity currentVehicle = player.getVehicle();
        for (Entity e: entities)
        {
            if (e == player || (currentVehicle == e))
                continue;
            double dd = player.getLocation().distanceSquared(e.getLocation());
            if (dd<distance)
            {
                distance = dd;
                closest = e;
            }
        }
        if (closest == null) return this;
        closest.addPassenger(player);
        return this;
    }
    public EntityPlayerActionPack dismount()
    {
        Entity v = player.getVehicle();
        if (v != null) {
            v.removePassenger(player);
        }
        return this;
    }

    public void onUpdate()
    {
        Map<ActionType, Boolean> actionAttempts = new HashMap<>();
        actions.values().removeIf(e -> e.done);
        for (Map.Entry<ActionType, Action> e : actions.entrySet())
        {
            ActionType type = e.getKey();
            Action action = e.getValue();
            // skipping attack if use was successful
            if (!(actionAttempts.getOrDefault(ActionType.USE, false) && type == ActionType.ATTACK))
            {
                Boolean actionStatus = action.tick(this, type);
                if (actionStatus != null)
                    actionAttempts.put(type, actionStatus);
            }
            // optionally retrying use after successful attack and unsuccessful use
            if (type == ActionType.ATTACK
                    && actionAttempts.getOrDefault(ActionType.ATTACK, false)
                    && !actionAttempts.getOrDefault(ActionType.USE, true) )
            {
                // according to MinecraftClient.handleInputEvents
                Action using = actions.get(ActionType.USE);
                if (using != null) // this is always true - we know use worked, but just in case
                {
                    using.retry(this, ActionType.USE);
                }
            }
            if (type == ActionType.LOOK_AT) {
                if (lookingAtEntity == null || Bukkit.getEntity(lookingAtEntity) == null) {
                    lookingAtEntity = null;
                    action.done = true;
                }
            }
        }
        float vel = sneaking?0.3F:1.0F;
        // The != 0.0F checks are needed given else real players can't control minecarts, however it works with fakes and else they don't stop immediately
        if (forward != 0.0F || baseEntity instanceof Doll) {
            actionPackPlayer.setZZA(forward * vel);
        }
        if (strafing != 0.0F || baseEntity instanceof Doll) {
            actionPackPlayer.setXXA(strafing * vel);
        }
    }
//    static WrapperHitResult getTarget(EntityPlayerActionPack actionPack)
//    {
//        double reach = actionPack.player.getGameMode() == GameMode.CREATIVE ? 5 : 4.5f;
//
//        return Tracer.rayTrace(actionPack.actionPackPlayer, 1, reach, false);
//    }
    static RayTraceResult getTarget(EntityPlayerActionPack actionPack)
    {
        double reach = actionPack.player.getGameMode() == GameMode.CREATIVE ? 5 : 4.5f;

        return Tracer.rayTrace(actionPack.player, reach, false);
    }
    static WrapperBlockHitResult getTargetBlock(EntityPlayerActionPack actionPack)
    {
        double reach = actionPack.player.getGameMode() == GameMode.CREATIVE ? 5 : 4.5f;

        return Tracer.rayTraceBlocks(actionPack.actionPackPlayer, actionPack.baseEntity, reach, false, actionPack.player);
    }

    private void dropItemFromSlot(int slot, boolean dropAll)
    {
        PlayerInventory inv = player.getInventory(); // getInventory;
        ItemStack itemStack = inv.getItem(slot);
        if (!isEmpty(itemStack)) {
            final int hand = inv.getHeldItemSlot();
            inv.setHeldItemSlot(slot);
            player.dropItem(dropAll);
            inv.setHeldItemSlot(hand);
        }
    }

    public void drop(int selectedSlot, boolean dropAll)
    {
        PlayerInventory inv = player.getInventory(); // getInventory;
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
        player.getInventory().setHeldItemSlot(slot-1);
    }

    public enum ActionType
    {
        USE(true)
                {
                    @Override
                    boolean execute(BaseEntity player, Action action)
                    {
                        EntityPlayerActionPack ap = player.getActionPack();
                        ActionPackPlayer packPlayer = ap.actionPackPlayer;
                        Player bukkitPlayer = player.getBukkitPlayer();
                        if (ap.itemUseCooldown > 0)
                        {
                            ap.itemUseCooldown--;
                            return true;
                        }
                        if (bukkitPlayer.getItemInUse() != null)
                        {
                            return true;
                        }
                        RayTraceResult hit = getTarget(ap);
                        for (Enum<?> hand : ActionPackPlayer.getInteractionHandEnums())
                        {
                            if (hit != null) {
                                Block blockHit = hit.getHitBlock();
                                Entity entityHit = hit.getHitEntity();
                                if (blockHit != null) {
                                    packPlayer.resetLastActionTime();
                                    WrapperServerLevel world = packPlayer.serverLevel();
                                    //WrapperBlockHitResult blockHit = (WrapperBlockHitResult) hit;
                                    WrapperBlockPos pos = WrapperBlockPos.construct(blockHit.getX(), blockHit.getY(), blockHit.getZ());
                                    BlockFace side = hit.getHitBlockFace();
                                    //EnumDirection side = blockHit.getDirection();
                                    if (pos.getY() < bukkitPlayer.getWorld().getMaxHeight() - (side == BlockFace.UP ? 1 : 0) && world.mayInteract(player, pos)) {
                                        WrapperBlockHitResult blockHitResult = getTargetBlock(ap);

                                        WrapperInteractionResult result = packPlayer.getGameMode().useItemOn(player, world, packPlayer.getItemInHand(hand), hand, blockHitResult);
                                        if (result.consumesAction()) {
                                            if (result.shouldSwing()) EntityPlayerActionPack.ActionType.swingCorrectHand(bukkitPlayer, hand);
                                            ap.itemUseCooldown = 3;
                                            return true;
                                        }
                                    }
                                } else if (entityHit != null) {
                                    packPlayer.resetLastActionTime();
                                    //WrapperEntityHitResult entityHit = (WrapperEntityHitResult) hit;
                                    WrapperEntity entity = packPlayer.wrapEntity(ReflectionUtil.getNMSEntity(entityHit));

                                    boolean handWasEmpty = ap.isEmpty(EntityPlayerActionPack.ActionType.getItemInHand(bukkitPlayer, hand));

                                    boolean itemFrameEmpty = (entityHit instanceof ItemFrame itemFrame) && ap.isEmpty(itemFrame.getItem());
                                    Location entityLocation = entityHit.getLocation();
                                    Location subtractedLocation = hit.getHitPosition().toLocation(entityHit.getWorld()).subtract(entityLocation);
                                    WrapperVec3 relativeHitPos = WrapperVec3.construct(subtractedLocation.getX(), subtractedLocation.getY(), subtractedLocation.getZ());
                                    if (entity.interactAt(player, relativeHitPos, hand).consumesAction()) {
                                        ap.itemUseCooldown = 3;
                                        return true;
                                    }
                                    // fix for SS itemframe always returns CONSUME even if no action is performed
                                    if (packPlayer.interactOn(entity, hand).consumesAction() && !(handWasEmpty && itemFrameEmpty)) {
                                        ap.itemUseCooldown = 3;
                                        return true;
                                    }
                                }
                            }
                            Object handItem = packPlayer.getItemInHand(hand);
                            if (packPlayer.getGameMode().useItem(player, packPlayer.level(), handItem, hand).consumesAction())
                            {
                                ap.itemUseCooldown = 3;
                                return true;
                            }
                        }
                        return false;
//                        WrapperHitResult hit = getTarget(ap);
//                        for (Enum<?> hand : ActionPackPlayer.getInteractionHandEnums())
//                        {
//                            if (hit.getType() == WrapperHitResult.BLOCK) {
//                                packPlayer.resetLastActionTime();
//                                WrapperServerLevel world = packPlayer.serverLevel();
//                                WrapperBlockHitResult blockHit = (WrapperBlockHitResult) hit;
//                                WrapperBlockPos pos = blockHit.getBlockPos();
//                                EnumDirection side = blockHit.getDirection();
//                                if (pos.getY() < bukkitPlayer.getWorld().getMaxHeight() - (side == WrapperDirection.UP ? 1 : 0) && world.mayInteract(player, pos))
//                                {
//                                    WrapperInteractionResult result = packPlayer.getGameMode().useItemOn(player, world, packPlayer.getItemInHand(hand), hand, blockHit);
//                                    if (result.consumesAction()) {
//                                        if (result.shouldSwing()) ActionType.swingCorrectHand(bukkitPlayer, hand);
//                                        ap.itemUseCooldown = 3;
//                                        return true;
//                                    }
//                                }
//                            } else if (hit.getType() == WrapperHitResult.ENTITY) {
//                                packPlayer.resetLastActionTime();
//                                WrapperEntityHitResult entityHit = (WrapperEntityHitResult) hit;
//                                WrapperEntity entity = packPlayer.wrapEntity(entityHit.getEntity());
//
//                                Entity craftEntity = entity.getCraftEntity();
//
//                                boolean handWasEmpty = ap.isEmpty(ActionType.getItemInHand(bukkitPlayer, hand));
//
//                                boolean itemFrameEmpty = (craftEntity instanceof ItemFrame itemFrame) && ap.isEmpty(itemFrame.getItem());
//                                Location entityLocation = craftEntity.getLocation();
//                                WrapperVec3 relativeHitPos = WrapperVec3.wrap(entityHit.getLocation().subtract(entityLocation.getX(), entityLocation.getY(), entityLocation.getZ()));
//                                if (entity.interactAt(player, relativeHitPos, hand).consumesAction())
//                                {
//                                    ap.itemUseCooldown = 3;
//                                    return true;
//                                }
//                                // fix for SS itemframe always returns CONSUME even if no action is performed
//                                if (packPlayer.interactOn(entity, hand).consumesAction() && !(handWasEmpty && itemFrameEmpty))
//                                {
//                                    ap.itemUseCooldown = 3;
//                                    return true;
//                                }
//                            }
//                            Object handItem = packPlayer.getItemInHand(hand);
//                            if (packPlayer.getGameMode().useItem(player, packPlayer.level(), handItem, hand).consumesAction())
//                            {
//                                ap.itemUseCooldown = 3;
//                                return true;
//                            }
//                        }
//                        return false;
                    }

                    @Override
                    void inactiveTick(BaseEntity player, Action action)
                    {
                        EntityPlayerActionPack ap = player.getActionPack();
                        ap.itemUseCooldown = 0;
                        ap.actionPackPlayer.releaseUsingItem();
                    }
                },
        ATTACK(true) {
            @Override
            boolean execute(BaseEntity player, Action action) {
                EntityPlayerActionPack actionPack = player.getActionPack();
                Player bukkitPlayer = actionPack.player;
                //WrapperHitResult hit = getTarget(actionPack);
                RayTraceResult hit = getTarget(actionPack);
                ActionPackPlayer packPlayer = actionPack.actionPackPlayer;
                if (hit == null) {
                    return false;
                }
                Block blockHit = hit.getHitBlock();
                Entity entityHit = hit.getHitEntity();
                if (entityHit != null) {
                    if (!action.isContinuous)
                    {
                        bukkitPlayer.attack(entityHit);
                        bukkitPlayer.swingMainHand();
                    }
                    packPlayer.resetAttackStrengthTicker();
                    packPlayer.resetLastActionTime();
                    return true;
                } else if (blockHit != null) {
                    EntityPlayerActionPack ap = player.getActionPack();
                    if (ap.blockHitDelay > 0)
                    {
                        ap.blockHitDelay--;
                        return false;
                    }
                    WrapperBlockPos pos = WrapperBlockPos.construct(blockHit.getX(), blockHit.getY(), blockHit.getZ());
                    EnumDirection side = WrapperDirection.convertBlockFace(hit.getHitBlockFace());
                    //EnumDirection side = blockHit.getDirection();
                    if (packPlayer.blockActionRestricted(packPlayer.level(), pos, WrapperGameType.parse(bukkitPlayer.getGameMode()))) return false;
                    if (ap.currentBlock != null && packPlayer.level().getBlockState(ap.currentBlock).isAir())
                    {
                        ap.currentBlock = null;
                        return false;
                    }
                    WrapperBlockState state = packPlayer.level().getBlockState(pos);
                    boolean blockBroken = false;
                    if (bukkitPlayer.getGameMode() == GameMode.CREATIVE)
                    {
                        packPlayer.getGameMode().handleBlockBreakAction(pos, WrapperServerboundPlayerActionPacket_Action.START_DESTROY_BLOCK, side, bukkitPlayer.getWorld().getMaxHeight(), -1);
                        ap.blockHitDelay = 5;
                        blockBroken = true;
                    }
                    else if (ap.currentBlock == null || ap.currentBlock.getSource() == null || !ap.currentBlock.getSource().equals(pos.getSource()))
                    {
                        if (ap.currentBlock != null && ap.currentBlock.getSource() != null)
                        {
                            packPlayer.getGameMode().handleBlockBreakAction(pos, WrapperServerboundPlayerActionPacket_Action.ABORT_DESTROY_BLOCK, side, bukkitPlayer.getWorld().getMaxHeight(), -1);
                        }
                        packPlayer.getGameMode().handleBlockBreakAction(pos, WrapperServerboundPlayerActionPacket_Action.START_DESTROY_BLOCK, side, bukkitPlayer.getWorld().getMaxHeight(), -1);
                        boolean notAir = !state.isAir();
                        if (notAir && ap.curBlockDamageMP == 0)
                        {
                            state.attack(packPlayer.level(), pos, player);
                        }
                        if (notAir && state.getDestroyProgress(player, packPlayer.level(), pos) >= 1)
                        {
                            ap.currentBlock = null;
                            //instamine??
                            blockBroken = true;
                        }
                        else
                        {
                            ap.currentBlock = pos;
                            ap.curBlockDamageMP = 0;
                        }
                    }
                    else
                    {
                        ap.curBlockDamageMP += state.getDestroyProgress(player, packPlayer.level(), pos);
                        if (ap.curBlockDamageMP >= 1)
                        {
                            packPlayer.getGameMode().handleBlockBreakAction(pos, WrapperServerboundPlayerActionPacket_Action.STOP_DESTROY_BLOCK, side, bukkitPlayer.getWorld().getMaxHeight(), -1);
                            ap.currentBlock = null;
                            ap.blockHitDelay = 5;
                            blockBroken = true;
                        }
                        packPlayer.level().destroyBlockProgress(-1, pos, (int) (ap.curBlockDamageMP * 10));

                    }
                    packPlayer.resetLastActionTime();
                    ap.player.swingMainHand();
                    return blockBroken;
                }
                return false;
//                if (hit.getType() == WrapperHitResult.ENTITY) {
//                    WrapperEntityHitResult entityHit = (WrapperEntityHitResult) hit;
//                    if (!action.isContinuous)
//                    {
//                        bukkitPlayer.attack(packPlayer.wrapEntity(entityHit.getEntity()).getCraftEntity());
//                        bukkitPlayer.swingMainHand();
//                    }
//                    packPlayer.resetAttackStrengthTicker();
//                    packPlayer.resetLastActionTime();
//                    return true;
//                } else if (hit.getType() == WrapperHitResult.BLOCK) {
//                    EntityPlayerActionPack ap = player.getActionPack();
//                    if (ap.blockHitDelay > 0)
//                    {
//                        ap.blockHitDelay--;
//                        return false;
//                    }
//                    WrapperBlockHitResult blockHit = (WrapperBlockHitResult) hit;
//                    WrapperBlockPos pos = blockHit.getBlockPos();
//                    EnumDirection side = blockHit.getDirection();
//                    if (packPlayer.blockActionRestricted(packPlayer.level(), pos, WrapperGameType.parse(bukkitPlayer.getGameMode()))) return false;
//                    if (ap.currentBlock != null && packPlayer.level().getBlockState(ap.currentBlock).isAir())
//                    {
//                        ap.currentBlock = null;
//                        return false;
//                    }
//                    WrapperBlockState state = packPlayer.level().getBlockState(pos);
//                    boolean blockBroken = false;
//                    if (bukkitPlayer.getGameMode() == GameMode.CREATIVE)
//                    {
//                        packPlayer.getGameMode().handleBlockBreakAction(pos, WrapperServerboundPlayerActionPacket_Action.START_DESTROY_BLOCK, side, bukkitPlayer.getWorld().getMaxHeight(), -1);
//                        ap.blockHitDelay = 5;
//                        blockBroken = true;
//                    }
//                    else if (ap.currentBlock == null || ap.currentBlock.getSource() == null || !ap.currentBlock.getSource().equals(pos.getSource()))
//                    {
//                        if (ap.currentBlock != null && ap.currentBlock.getSource() != null)
//                        {
//                            packPlayer.getGameMode().handleBlockBreakAction(pos, WrapperServerboundPlayerActionPacket_Action.ABORT_DESTROY_BLOCK, side, bukkitPlayer.getWorld().getMaxHeight(), -1);
//                        }
//                        packPlayer.getGameMode().handleBlockBreakAction(pos, WrapperServerboundPlayerActionPacket_Action.START_DESTROY_BLOCK, side, bukkitPlayer.getWorld().getMaxHeight(), -1);
//                        boolean notAir = !state.isAir();
//                        if (notAir && ap.curBlockDamageMP == 0)
//                        {
//                            state.attack(packPlayer.level(), pos, player);
//                        }
//                        if (notAir && state.getDestroyProgress(player, packPlayer.level(), pos) >= 1)
//                        {
//                            ap.currentBlock = null;
//                            //instamine??
//                            blockBroken = true;
//                        }
//                        else
//                        {
//                            ap.currentBlock = pos;
//                            ap.curBlockDamageMP = 0;
//                        }
//                    }
//                    else
//                    {
//                        ap.curBlockDamageMP += state.getDestroyProgress(player, packPlayer.level(), pos);
//                        if (ap.curBlockDamageMP >= 1)
//                        {
//                            packPlayer.getGameMode().handleBlockBreakAction(pos, WrapperServerboundPlayerActionPacket_Action.STOP_DESTROY_BLOCK, side, bukkitPlayer.getWorld().getMaxHeight(), -1);
//                            ap.currentBlock = null;
//                            ap.blockHitDelay = 5;
//                            blockBroken = true;
//                        }
//                        packPlayer.level().destroyBlockProgress(-1, pos, (int) (ap.curBlockDamageMP * 10));
//
//                    }
//                    packPlayer.resetLastActionTime();
//                    ap.player.swingMainHand();
//                    return blockBroken;
//                }
//                return false;
            }

            @Override
            void inactiveTick(BaseEntity player, Action action)
            {
                EntityPlayerActionPack ap = player.getActionPack();
                ActionPackPlayer packPlayer = ap.actionPackPlayer;
                if (ap.currentBlock == null) return;
                packPlayer.level().destroyBlockProgress(-1, ap.currentBlock, -1);
                packPlayer.getGameMode().handleBlockBreakAction(ap.currentBlock, WrapperServerboundPlayerActionPacket_Action.ABORT_DESTROY_BLOCK, WrapperDirection.DOWN, ap.player.getWorld().getMaxHeight(), -1);
                ap.currentBlock = null;
            }
        },
        JUMP(true)
                {
                    @Override
                    boolean execute(BaseEntity player, Action action)
                    {
                        EntityPlayerActionPack ap = player.getActionPack();
                        if (action.limit == 1)
                        {
                            if (ap.player.isOnGround()) {
                                ap.actionPackPlayer.jumpFromGround();
                            }
                        }
                        else
                        {
                            ap.actionPackPlayer.setJumping(true);
                        }
                        return false;
                    }

                    @Override
                    void inactiveTick(BaseEntity player, Action action)
                    {
                        player.getActionPack().actionPackPlayer.setJumping(false);
                    }
                },
        DROP_ITEM(true)
                {
                    @Override
                    boolean execute(BaseEntity player, Action action)
                    {
                        EntityPlayerActionPack ap = player.getActionPack();
                        ap.actionPackPlayer.resetLastActionTime();
                        ap.player.dropItem(false);
                        return false;
                    }
                },
        DROP_STACK(true)
                {
                    @Override
                    boolean execute(BaseEntity player, Action action)
                    {
                        EntityPlayerActionPack ap = player.getActionPack();
                        ap.actionPackPlayer.resetLastActionTime();
                        ap.player.dropItem(true);
                        return false;
                    }
                },
        SWAP_HANDS(true)
                {
                    @Override
                    boolean execute(BaseEntity player, Action action)
                    {
                        EntityPlayerActionPack ap = player.getActionPack();
                        ap.actionPackPlayer.resetLastActionTime();
                        PlayerInventory inv = ap.player.getInventory();
                        ItemStack itemStack_1 = inv.getItemInOffHand();
                        inv.setItemInOffHand(inv.getItemInMainHand());
                        inv.setItemInMainHand(itemStack_1);
                        return false;
                    }
                },

        LOOK_AT(true)
        {
            @Override
            boolean execute(BaseEntity player, Action action)
            {
                EntityPlayerActionPack ap = player.getActionPack();
                Entity entity = Bukkit.getEntity(ap.lookingAtEntity);
                if (entity != null) {
                    BoundingBox box = entity.getBoundingBox();
                    ap.lookAt(WrapperVec3.construct(box.getCenterX(),box.getCenterY(),box.getCenterZ()));
                }
                return false;
            }
        };

        public final boolean preventSpectator;

        ActionType(boolean preventSpectator)
        {
            this.preventSpectator = preventSpectator;
        }

        void start(BaseEntity player, Action action) {}
        abstract boolean execute(BaseEntity player, Action action);
        void inactiveTick(BaseEntity player, Action action) {}
        void stop(BaseEntity player, Action action)
        {
            inactiveTick(player, action);
        }

        private static ItemStack getItemInHand(Player bukkitPlayer, Enum<?> hand) {
            int index = Arrays.asList(ActionPackPlayer.getInteractionHandEnums()).indexOf(hand);
            return index == 0 ? bukkitPlayer.getInventory().getItemInMainHand() : bukkitPlayer.getInventory().getItemInOffHand();
        }
        private static void swingCorrectHand(Player bukkitPlayer, Enum<?> hand) {
            int index = Arrays.asList(ActionPackPlayer.getInteractionHandEnums()).indexOf(hand);
            if (index == 0) {
                bukkitPlayer.swingMainHand();
            } else {
                bukkitPlayer.swingOffHand();
            }
        }
    }

    public static class Action
    {
        public boolean done = false;
        public final int limit;
        public final int interval;
        public final int offset;
        private int count;
        private int next;
        private final boolean isContinuous;

        private Action(int limit, int interval, int offset, boolean continuous)
        {
            this.limit = limit;
            this.interval = interval;
            this.offset = offset;
            next = interval + offset;
            isContinuous = continuous;
        }

        public static Action once()
        {
            return new Action(1, 1, 0, false);
        }

        public static Action continuous()
        {
            return new Action(-1, 1, 0, true);
        }

        public static Action interval(int interval)
        {
            return new Action(-1, interval, 0, false);
        }

        public static Action interval(int interval, int offset)
        {
            return new Action(-1, interval, offset, false);
        }

        Boolean tick(EntityPlayerActionPack actionPack, ActionType type)
        {
            next--;
            Boolean cancel = null;
            if (next <= 0)
            {
                if (interval == 1 && !isContinuous)
                {
                    // need to allow entity to tick, otherwise won't have effect (bow)
                    // actions are 20 tps, so need to clear status mid tick, allowing entities process it till next time
                    if (!type.preventSpectator || actionPack.player.getGameMode() != GameMode.SPECTATOR)
                    {
                        type.inactiveTick(actionPack.baseEntity, this);
                    }
                }

                if (!type.preventSpectator || actionPack.player.getGameMode() != GameMode.SPECTATOR)
                {
                    cancel = type.execute(actionPack.baseEntity, this);
                }
                count++;
                if (count == limit)
                {
                    type.stop(actionPack.baseEntity, null);
                    done = true;
                    return cancel;
                }
                next = interval;
            }
            else
            {
                if (!type.preventSpectator || actionPack.player.getGameMode() != GameMode.SPECTATOR)
                {
                    type.inactiveTick(actionPack.baseEntity, this);
                }
            }
            return cancel;
        }

        void retry(EntityPlayerActionPack actionPack, ActionType type)
        {
            //assuming action run but was unsuccesful that tick, but opportunity emerged to retry it, lets retry it.
            if (!type.preventSpectator || actionPack.player.getGameMode() != GameMode.SPECTATOR)
            {
                type.execute(actionPack.baseEntity, this);
            }
            count++;
            if (count == limit)
            {
                type.stop(actionPack.baseEntity, null);
                done = true;
            }
        }
    }

    private boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().isAir();
    }

}
