package me.autobot.playerdoll.CarpetMod;

import me.autobot.playerdoll.Dolls.IDoll;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.*;

public class EntityPlayerActionPack {
    private final IDoll doll;
    private final Player player;

    private final Map<ActionType, Action> actions = new EnumMap<>(ActionType.class);

    private Object currentBlock;
    private int blockHitDelay;
    private boolean isHittingBlock;
    private float curBlockDamageMP;
    //private Entity currentLooking;
    private UUID lookingAtEntity;
    protected boolean sneaking;
    protected boolean sprinting;
    protected float forward;
    protected float strafing;

    private int itemUseCooldown;
    protected static Tracer tracer;

    public EntityPlayerActionPack(Player playerIn, IDoll dollIn)
    {
        player = playerIn;
        doll = dollIn;
        stopAll();
    }
    public void copyFrom(IDoll doll)
    {
        EntityPlayerActionPack other = doll.getActionPack();
        actions.putAll(other.actions);
        currentBlock = other.currentBlock;
        blockHitDelay = other.blockHitDelay;
        isHittingBlock = other.isHittingBlock;
        curBlockDamageMP = other.curBlockDamageMP;
        lookingAtEntity = other.lookingAtEntity;

        sneaking = other.sneaking;
        sprinting = other.sprinting;
        forward = other.forward;
        strafing = other.strafing;

        itemUseCooldown = other.itemUseCooldown;
    }
    public void start(ActionType type, Action action)
    {
        Action previous = actions.remove(type);
        if (previous != null) type.stop(player, doll, previous);
        if (action != null)
        {
            actions.put(type, action);
            type.start(player, doll, action); // noop
        }
    }
    public void setSneaking(boolean doSneak)
    {
        sneaking = doSneak;
        player.setSneaking(doSneak);
        if (sprinting && sneaking) setSprinting(false);
    }
    public void setSprinting(boolean doSprint) {
        sprinting = doSprint;
        player.setSprinting(doSprint);
        if (sneaking && sprinting) setSneaking(false);
    }
    public void setForward(float value)
    {
        forward = value;

    }
    public void setStrafing(float value)
    {
        strafing = value;

    }

    public void look(String direction)
    {
        switch (direction)
            {
                case "NORTH" -> look(180, 0);
                case "SOUTH" -> look(0, 0);
                case "EAST"  -> look(-90, 0);
                case "WEST"  -> look(90, 0);
                case "UP"    -> look(player.getEyeLocation().getYaw(), -90);
                case "DOWN"  -> look(player.getEyeLocation().getYaw(), 90);
            };
    }

    public void look(Player player) {
        if (player != null) look(player.getEyeLocation().getYaw(),player.getEyeLocation().getPitch());
    }

    public void look(float yaw, float pitch)
    {
    }

    public void lookAt(float x, float y, float z) {
        lookAt(new Vector(x,y,z));
    }
    public void lookAt(double x, double y, double z) {
        lookAt(new Vector(x,y,z));
    }

    public void lookAt(Vector position)
    {
    }

    //public void lookAt(String player) {
    //    lookAt(Bukkit.getPlayer(player));
    //}
    public void lookAt(Player player) {
        if (player != null) {
            Location loc = player.getEyeLocation();
            lookAt(new Vector(loc.getX(),loc.getY(),loc.getZ()));
        }
    }
    public void lookAt(Entity entity) {
        if (entity != null) {
            this.lookingAtEntity = entity.getUniqueId();
            BoundingBox box = entity.getBoundingBox();
            lookAt(box.getCenterX(),box.getCenterY(),box.getCenterZ());
        }
    }
    private Entity getEntity(UUID target) {
        return Bukkit.getEntity(target);
    }
    public void turn(float yaw, float pitch)
    {
        look(player.getEyeLocation().getYaw()+yaw,player.getEyeLocation().getPitch()+pitch);
        //look(player.getYRot() + yaw, player.getXRot() + pitch);
    }
    public void stopMovement()
    {
        setSneaking(false);
        setSprinting(false);
        forward = 0.0F;
        strafing = 0.0F;
    }
    public void stopAll()
    {
        for (ActionType type : actions.keySet()) type.stop(player, doll, actions.get(type));
        actions.clear();
        stopMovement();
    }

    public void mount(boolean onlyRideables)
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
        if (entities.size()==0) return;
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
        if (closest == null) return;
        closest.addPassenger(player);
    }
    public void dismount()
    {
        Entity v = player.getVehicle();
        if (v != null) v.removePassenger(player);
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
        }
        /*
        float vel = sneaking?0.3F:1.0F;
        // The != 0.0F checks are needed given else real players can't control minecarts, however it works with fakes and else they don't stop immediately

        if (forward != 0.0F) {
            doll.setzza(forward * vel);
            //player.zza = forward * vel;
        }
        if (strafing != 0.0F) {
            doll.setxxa(strafing * vel);
            //player.xxa = strafing * vel;
        }

         */
    }
    static Object getTarget(IDoll doll, Player player)
    {
        float reach = player.getGameMode() == GameMode.CREATIVE ? 5 : 4.5f;
        return tracer.rayTrace(doll, 1,reach, false);
    }
    private boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().isAir() || itemStack.getAmount() <= 0;
    }
    private void dropItemFromSlot(int slot, boolean dropAll)
    {
        PlayerInventory inv = player.getInventory(); // getInventory;
        if (!isEmpty(inv.getItem(slot))) {
            dropItem(slot,dropAll,inv.getItem(slot).getAmount());
            /*
            final int hand = inv.getHeldItemSlot();
            inv.setHeldItemSlot(slot);
            player.dropItem(dropAll);
            inv.setHeldItemSlot(hand);

             */
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

    protected void dropItem(int slot,boolean dropAll,int count) {
    }
    protected Object[] getInteractionHand() {
        return null;
    }
    protected String getHitType(Object hit) {
        return null;
    }
    protected Object getBlockHitResult(Object hit) {
        return null;
    }
    protected Object getHitBlockPos(Object blockHit) {
        return null;
    }
    protected String getBlockHitDirection(Object blockHit) {
        return null;
    }
    protected boolean mayInteract(Object pos) {
        return true;
    }
    protected int getBlockPosY(Object pos) {
        return 0;
    }
    protected void swingHand(Object hand) {
    }
    protected String useItemOn(Object hand, Object blockHit) {
        return null;
    }
    protected Object getEntityHitResult(Object hit) {
        return null;
    }
    protected Object getHitEntity(Object entityHit) {
        return null;
    }
    protected boolean getHandEmpty(Object hand) {
        return true;
    }
    protected Object getRelativeHitPos(Object entityHit, Object entity) {
        return null;
    }
    protected String entityInteractAt(Object entity, IDoll doll, Object relativeHitPos, Object hand) {
        return null;
    }
    protected String playerInteractOn(Object entity, Object hand) {
        return null;
    }
    protected String player_gameMode_useItem(Object hand) {
        return null;
    }
    protected void player_releaseUsingItem() {
    }
    protected Entity getCraftEntity(Object entity) {
        return null;
    }
    protected boolean player_blockActionRestricted(Object blockpos) {
        return true;
    }
    protected Object player_level_getBlockState(Object currentBlock) {
        return null;
    }
    protected boolean blockState_isAir(Object blockState) {
        return true;
    }
    protected void player_gameMode_handleBlockBreakAction(Object blockpos, String packet, String side, int value) {
    }
    protected void blockState_attack(Object blockState, Object blockpos) {
    }
    protected float blockState_getDestrotProgress(Object blockState, Object blockpos) {
        return 0;
    }
    protected void player_level_destroyBlockProgress(int value, Object blockpos, int damage) {
    }
    public enum ActionType
    {
        USE(true)
            {
                @Override
                boolean execute(Player player, IDoll doll, Action action)
                {
                    EntityPlayerActionPack ap = doll.getActionPack();
                    if (ap.itemUseCooldown > 0)
                    {
                        ap.itemUseCooldown--;
                        return true;
                    }
                    if (player.getItemInUse() != null)
                    {
                        return true;
                    }
                    Object hit = getTarget(doll,player);
                    for (Object hand : ap.getInteractionHand())
                    {
                        switch (ap.getHitType(hit))
                        {
                            case "BLOCK":
                            {
                                doll._resetLastActionTime();
                                World world = player.getWorld();
                                Object blockHit = ap.getBlockHitResult(hit);
                                Object pos = ap.getHitBlockPos(blockHit);
                                String side = ap.getBlockHitDirection(blockHit);
                                if (ap.getBlockPosY(pos) < world.getMaxHeight() - (side.equalsIgnoreCase("UP") ? 1 : 0) && ap.mayInteract(pos))
                                //if (ap.getBlockPosY(pos) < world.getMaxHeight() - (side.equalsIgnoreCase("UP") ? 1 : 0) && ap.mayInteract(pos))
                                {
                                    String result = ap.useItemOn(hand,blockHit);
                                    //InteractionResult result = player.gameMode.useItemOn(player, world, player.getItemInHand(hand), hand, blockHit);
                                    if (result.matches("(?i)SUCCESS|CONSUME|CONSUME_PARTIAL"))
                                    //if (result.consumesAction())
                                    {
                                        if (result.equalsIgnoreCase("SUCCESS")) ap.swingHand(hand);
                                        //if (result.shouldSwing()) player.swing(hand);
                                        ap.itemUseCooldown = 3;
                                        return true;
                                    }
                                }
                                break;
                            }
                            case "ENTITY":
                            {
                                doll._resetLastActionTime();
                                Object entityHit = ap.getEntityHitResult(hit);
                                Object entity = ap.getHitEntity(entityHit);
                                boolean handWasEmpty = ap.getHandEmpty(hand);
                                boolean itemFrameEmpty = (entity instanceof ItemFrame) && ((ItemFrame) entity).isEmpty();

                                Object relativeHitPos = ap.getRelativeHitPos(entityHit,entity);// entityHit.getLocation().subtract(entity.getX(), entity.getY(), entity.getZ());
                                if (ap.entityInteractAt(entity,doll,relativeHitPos,hand).matches("(?i)SUCCESS|CONSUME|CONSUME_PARTIAL")) {
                                    ap.itemUseCooldown = 3;
                                    return true;
                                }
                                /*
                                if (entity.interactAt(player, relativeHitPos, hand).consumesAction())
                                {
                                    ap.itemUseCooldown = 3;
                                    return true;
                                }

                                 */
                                // fix for SS itemframe always returns CONSUME even if no action is performed
                                if (ap.playerInteractOn(entity,hand).matches("(?i)SUCCESS|CONSUME|CONSUME_PARTIAL") && !(handWasEmpty && itemFrameEmpty)) {
                                    ap.itemUseCooldown = 3;
                                    return true;
                                }
                                /*
                                if (player.interactOn(entity, hand).consumesAction() && !(handWasEmpty && itemFrameEmpty))
                                {
                                    ap.itemUseCooldown = 3;
                                    return true;
                                }

                                 */
                                break;
                            }
                        }
                        //ItemStack handItem = player.getItemInHand(hand);
                        if (ap.player_gameMode_useItem(hand).matches("(?i)SUCCESS|CONSUME|CONSUME_PARTIAL")) {
                            ap.itemUseCooldown = 3;
                            return true;
                        }
                        /*
                        if (player.gameMode.useItem(player, player.level(), handItem, hand).consumesAction())
                        {
                            ap.itemUseCooldown = 3;
                            return true;
                        }

                         */
                    }
                    return false;
                }

                @Override
                void inactiveTick(Player player, IDoll doll, Action action)
                {
                    EntityPlayerActionPack ap = doll.getActionPack();
                    ap.itemUseCooldown = 0;
                    ap.player_releaseUsingItem();
                    //player.releaseUsingItem();
                }
            },
        ATTACK(true) {
            @Override
            boolean execute(Player player, IDoll doll, Action action) {
                EntityPlayerActionPack ap = doll.getActionPack();
                Object hit = getTarget(doll,player);
                switch (ap.getHitType(hit)) {
                    case "ENTITY": {
                        Object entityHit = ap.getEntityHitResult(hit);
                        if (!action.isContinuous)
                        {
                            player.attack(ap.getCraftEntity(ap.getHitEntity(entityHit)));
                            player.swingMainHand();
                            //player.swing(InteractionHand.MAIN_HAND);
                        }
                        doll._resetAttackStrengthTicker();
                        doll._resetLastActionTime();
                        //player.resetAttackStrengthTicker();
                        //player.resetLastActionTime();
                        return true;
                    }
                    case "BLOCK": {
                        //EntityPlayerActionPack ap = doll.getActionPack();
                        if (ap.blockHitDelay > 0)
                        {
                            ap.blockHitDelay--;
                            return false;
                        }
                        Object blockHit = ap.getBlockHitResult(hit);
                        Object pos = ap.getHitBlockPos(blockHit);
                        String side = ap.getBlockHitDirection(blockHit);
                        if (ap.player_blockActionRestricted(pos)) return false;
                        //if (player.blockActionRestricted(player.level(), pos, player.gameMode.getGameModeForPlayer())) return false;
                        if (ap.currentBlock != null && ap.blockState_isAir(ap.player_level_getBlockState(ap.currentBlock)))// player.level().getBlockState(ap.currentBlock).isAir())
                        {
                            ap.currentBlock = null;
                            return false;
                        }
                        Object state = ap.player_level_getBlockState(pos);// player.level().getBlockState(pos);
                        boolean blockBroken = false;
                        if (player.getGameMode() == GameMode.CREATIVE) {
                            ap.player_gameMode_handleBlockBreakAction(pos,"START_DESTROY_BLOCK",side,-1);
                            ap.blockHitDelay = 5;
                            blockBroken = true;
                        }
                        /*
                        if (player.gameMode.getGameModeForPlayer().isCreative())
                        {
                            player.gameMode.handleBlockBreakAction(pos, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, side, player.level().getMaxBuildHeight(), -1);
                            ap.blockHitDelay = 5;
                            blockBroken = true;
                        }

                         */
                        else  if (ap.currentBlock == null || !ap.currentBlock.equals(pos))
                        {
                            if (ap.currentBlock != null)
                            {
                                ap.player_gameMode_handleBlockBreakAction(ap.currentBlock,"ABORT_DESTROY_BLOCK",side,-1);
                                //player.gameMode.handleBlockBreakAction(ap.currentBlock, ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, side, player.level().getMaxBuildHeight(), -1);
                            }
                            ap.player_gameMode_handleBlockBreakAction(pos,"START_DESTROY_BLOCK",side,-1);
                            //player.gameMode.handleBlockBreakAction(pos, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, side, player.level().getMaxBuildHeight(), -1);
                            boolean notAir = !ap.blockState_isAir(state);// state.isAir();
                            if (notAir && ap.curBlockDamageMP == 0)
                            {
                                ap.blockState_attack(state, pos);
                                //state.attack(player.level(), pos, player);
                            }
                            if (notAir && ap.blockState_getDestrotProgress(state,pos) >= 1) // state.getDestroyProgress(player, player.level(), pos) >= 1)
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
                            ap.curBlockDamageMP += ap.blockState_getDestrotProgress(state,pos); //state.getDestroyProgress(player, player.level(), pos);
                            if (ap.curBlockDamageMP >= 1)
                            {
                                ap.player_gameMode_handleBlockBreakAction(pos,"STOP_DESTROY_BLOCK",side,-1);
                                //player.gameMode.handleBlockBreakAction(pos, ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, side, player.level().getMaxBuildHeight(), -1);
                                ap.currentBlock = null;
                                ap.blockHitDelay = 5;
                                blockBroken = true;
                            }
                            ap.player_level_destroyBlockProgress(-1,pos, (int) (ap.curBlockDamageMP * 10));
                            //player.level().destroyBlockProgress(-1, pos, (int) (ap.curBlockDamageMP * 10));

                        }
                        doll._resetLastActionTime();
                        //player.resetLastActionTime();
                        player.swingMainHand();
                        return blockBroken;
                    }
                }
                return false;
            }
            @Override
            void inactiveTick(Player player, IDoll doll, Action action)
            {
                EntityPlayerActionPack ap = doll.getActionPack();
                if (ap.currentBlock == null) return;
                ap.player_level_destroyBlockProgress(-1,ap.currentBlock,-1);
                ap.player_gameMode_handleBlockBreakAction(ap.currentBlock, "ABORT_DESTROY_BLOCK", "DOWN", -1);
                //player.level().destroyBlockProgress(-1, ap.currentBlock, -1);
                //player.gameMode.handleBlockBreakAction(ap.currentBlock, ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, Direction.DOWN, player.level().getMaxBuildHeight(), -1);
                ap.currentBlock = null;
            }
        },
        JUMP(true)
            {
                @Override
                boolean execute(Player player, IDoll doll, Action action)
                {
                    if (action.limit == 1)
                    {
                        if (player.isOnGround()) doll._jumpFromGround(); // onGround
                    }
                    else
                    {
                        doll._setJumping(true);
                    }
                    return false;
                }
                @Override
                void inactiveTick(Player player, IDoll doll, Action action)
                {
                    doll._setJumping(false);
                }
            },
        DROP_ITEM(true)
            {
                @Override
                boolean execute(Player player, IDoll doll, Action action)
                {
                    doll._resetLastActionTime();
                    player.dropItem(false); // dropSelectedItem
                    return false;
                }
            },
        DROP_STACK(true)
            {
                @Override
                boolean execute(Player player, IDoll doll, Action action)
                {
                    doll._resetLastActionTime();
                    player.dropItem(true); // dropSelectedItem
                    return false;
                }
            },
        SWAP_HANDS(true)
            {
                @Override
                boolean execute(Player player, IDoll doll, Action action)
                {
                    doll._resetLastActionTime();
                    PlayerInventory inv = player.getInventory();
                    ItemStack itemStack_1 = inv.getItemInOffHand();
                    inv.setItemInOffHand(inv.getItemInMainHand());
                    inv.setItemInMainHand(itemStack_1);
                    return false;
                }
            },
        LOOK_AT(true)
                {
                    @Override
                    boolean execute(Player player, IDoll doll, Action action)
                    {
                        EntityPlayerActionPack ap = doll.getActionPack();
                        Entity entity = ap.getEntity(ap.lookingAtEntity);
                        if (entity != null) ap.lookAt(entity);
                        return false;
                    }
                };

        public final boolean preventSpectator;

        ActionType(boolean preventSpectator)
        {
            this.preventSpectator = preventSpectator;
        }

        void start(Player player, IDoll doll, Action action) {}
        abstract boolean execute(Player player, IDoll doll, Action action);
        void inactiveTick(Player player, IDoll doll, Action action) {}
        void stop(Player player, IDoll doll, Action action)
        {
            inactiveTick(player, doll, action);
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
                        type.inactiveTick(actionPack.player, actionPack.doll, this);
                    }
                }

                if (!type.preventSpectator || actionPack.player.getGameMode() != GameMode.SPECTATOR)
                {
                    cancel = type.execute(actionPack.player, actionPack.doll, this);
                }
                count++;
                if (count == limit)
                {
                    type.stop(actionPack.player, actionPack.doll, null);
                    done = true;
                    return cancel;
                }
                next = interval;
            }
            else
            {
                if (!type.preventSpectator || actionPack.player.getGameMode() != GameMode.SPECTATOR)
                {
                    type.inactiveTick(actionPack.player, actionPack.doll, this);
                }
            }
            return cancel;
        }

        void retry(EntityPlayerActionPack actionPack, ActionType type)
        {
            //assuming action run but was unsuccesful that tick, but opportunity emerged to retry it, lets retry it.
            if (!type.preventSpectator || actionPack.player.getGameMode() != GameMode.SPECTATOR)
            {
                type.execute(actionPack.player, actionPack.doll, this);
            }
            count++;
            if (count == limit)
            {
                type.stop(actionPack.player, actionPack.doll, null);
                done = true;
            }
        }
    }
}
