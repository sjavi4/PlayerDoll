package me.autobot.playerdoll.v1_20_R4.CarpetMod;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Dolls.IServerPlayerExt;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.util.Vector;

public class NMSPlayerEntityActionPack extends EntityPlayerActionPack {

    private final ServerPlayer player;
    public NMSPlayerEntityActionPack(ServerPlayer playerIn) {
        super(playerIn.getBukkitEntity(), (IServerPlayerExt) playerIn);
        player = playerIn;
        tracer = new NMSTracer();
    }
    private void broadCast(Packet<?> packet) {
        player.server.getPlayerList().broadcastAll(packet);
    }
    private void sendPacket(Packet<?> packet) {
        player.connection.send(packet);
    }
    @Override
    public void look(float yaw, float pitch)
    {
        player.setYRot(yaw % 360); //setYaw
        player.setXRot(Mth.clamp(pitch, -90, 90)); // setPitch
        broadCast(new ClientboundRotateHeadPacket(player, (byte) (player.getYRot()%360*256/360)));
        broadCast(new ClientboundMoveEntityPacket.Rot(player.getId(), (byte) (player.getYRot()%360*256/360), (byte) (player.getXRot()%360*256/360), player.onGround()));
    }
    @Override
    public void lookAt(Vector position)
    {
        player.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(position.getX(), position.getY(), position.getZ()));
        sendPacket(new ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor.EYES, position.getX(), position.getY(), position.getZ()));
    }
    @Override
    public void onUpdate() {
        super.onUpdate();
        float vel = sneaking?0.3F:1.0F;

        if (forward != 0.0F) {
            player.zza = forward * vel;
        }
        if (strafing != 0.0F) {
            player.xxa = strafing * vel;
        }
    }
    @Override
    protected void dropItem(int slot,boolean dropAll,int count) {
        player.drop(player.getInventory().removeItem(slot, dropAll? count : 1),false,true);
    }
    @Override
    protected Object[] getInteractionHand() {
        return new Object[]{InteractionHand.MAIN_HAND,InteractionHand.OFF_HAND};
    }
    @Override
    protected String getHitType(Object hit) {
        return ((HitResult)hit).getType().toString();
    }
    @Override
    protected Object getBlockHitResult(Object hit) {
        return (BlockHitResult)hit;
    }
    protected Object getHitBlockPos(Object blockHit) {
        return ((BlockHitResult)blockHit).getBlockPos();
    }
    protected String getBlockHitDirection(Object blockHit) {
        return ((BlockHitResult)blockHit).getDirection().getName();
    }
    @Override
    protected boolean mayInteract(Object pos) {
        return player.mayInteract(player.level(), (BlockPos) pos);
    }
    @Override
    protected int getBlockPosY(Object pos) {
        return ((BlockPos)pos).getY();
    }
    @Override
    protected void swingHand(Object hand) {
        player.swing((InteractionHand) hand);
    }
    @Override
    protected String useItemOn(Object _hand, Object block) {
        InteractionHand hand = (InteractionHand) _hand;
        return player.gameMode.useItemOn(player, player.level(), player.getItemInHand(hand), hand, (BlockHitResult)block).toString();
    }
    @Override
    protected Object getEntityHitResult(Object hit) {
        return ((EntityHitResult)hit);
    }
    @Override
    protected Object getHitEntity(Object entityHit) {
        return ((EntityHitResult)entityHit).getEntity();
    }
    @Override
    protected boolean getHandEmpty(Object hand) {
        return player.getItemInHand((InteractionHand) hand).isEmpty();
    }
    @Override
    protected Object getRelativeHitPos(Object entityHit, Object entity) {
        Entity e = (Entity) entity;
        return ((EntityHitResult)entityHit).getLocation().subtract(e.getX(), e.getY(), e.getZ());
    }
    @Override
    protected String entityInteractAt(Object entity, IServerPlayerExt serverPlayerExt, Object relativeHitPos, Object hand) {
        return ((Entity)entity).interactAt(player ,(Vec3) relativeHitPos, (InteractionHand) hand).toString();
    }
    @Override
    protected String playerInteractOn(Object entity, Object hand) {
        return player.interactOn((Entity) entity, (InteractionHand) hand).toString();
    }
    @Override
    protected String player_gameMode_useItem(Object hand) {
        InteractionHand h = (InteractionHand) hand;
        return player.gameMode.useItem(player,player.level(),player.getItemInHand(h),h).toString();
    }
    @Override
    protected void player_releaseUsingItem() {
        player.releaseUsingItem();
    }
    @Override
    protected org.bukkit.entity.Entity getCraftEntity(Object entity) {
        return ((Entity)entity).getBukkitEntity();
    }
    @Override
    protected boolean player_blockActionRestricted(Object blockpos) {
        return player.blockActionRestricted(player.level(), (BlockPos) blockpos,player.gameMode.getGameModeForPlayer());
    }
    @Override
    protected Object player_level_getBlockState(Object currentBlock) {
        return player.level().getBlockState((BlockPos) currentBlock);
    }
    @Override
    protected boolean blockState_isAir(Object blockState) {
        return ((BlockState)blockState).isAir();
    }
    @Override
    protected void player_gameMode_handleBlockBreakAction(Object blockpos, String packet, String side, int value) {
        player.gameMode.handleBlockBreakAction((BlockPos) blockpos, ServerboundPlayerActionPacket.Action.valueOf(packet), Direction.byName(side), player.level().getMaxBuildHeight(), value);
    }
    @Override
    protected void blockState_attack(Object blockState, Object blockpos) {
        ((BlockState)blockState).attack(player.level(), (BlockPos) blockpos,player);
    }
    @Override
    protected float blockState_getDestrotProgress(Object blockState, Object blockpos) {
        return ((BlockState)blockState).getDestroyProgress(player,player.level(),(BlockPos) blockpos);
    }
    @Override
    protected void player_level_destroyBlockProgress(int value, Object blockpos, int damage) {
        player.level().destroyBlockProgress(value, (BlockPos) blockpos,damage);
    }
}
