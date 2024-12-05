package me.autobot.addonDoll.action;

import me.autobot.addonDoll.wrapper.WDirectionImpl;
import me.autobot.addonDoll.wrapper.WEntityImpl;
import me.autobot.addonDoll.wrapper.WServerPlayerActionImpl;
import me.autobot.playerdoll.api.ReflectionUtil;
import me.autobot.playerdoll.api.action.pack.AbsPackPlayer;
import me.autobot.playerdoll.api.wrapper.WrapperRegistry;
import me.autobot.playerdoll.api.wrapper.builtin.*;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.inventory.ItemStack;

public class PackPlayerImpl extends AbsPackPlayer {

    private final ServerPlayer serverPlayer;
    public PackPlayerImpl(ServerPlayer serverPlayer) {
        this.serverPlayer = serverPlayer;
    }

    public ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    @Override
    public void setZZA(float v) {
        serverPlayer.zza = v;
    }

    @Override
    public void setXXA(float v) {
        serverPlayer.xxa = v;
    }

    @Override
    public void setJumping(boolean b) {
        serverPlayer.setJumping(b);
    }

    @Override
    public void jumpFromGround() {
        serverPlayer.jumpFromGround();
    }

    @Override
    public void resetLastActionTime() {
        serverPlayer.resetLastActionTime();
    }

    @Override
    public void releaseUsingItem() {
        serverPlayer.releaseUsingItem();
    }

    @Override
    public void look(float yaw, float pitch) {
        serverPlayer.setYRot(yaw % 360); //setYaw
        serverPlayer.setXRot(Mth.clamp(pitch, -90, 90)); // setPitch
    }

    @Override
    public void lookAt(WVec3<?> vec3) {
        serverPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, (Vec3) vec3.getInstance());
    }

    @Override
    public void lookAt(double x, double y, double z) {
        serverPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(x, y, z));
    }

    @Override
    public Enum<?>[] getInteractionHandEnums() {
        return InteractionHand.values();
    }

    @Override
    public ItemStack getItemInHand(Enum<?> hand) {
        return ReflectionUtil.NMSToBukkitItemStack(serverPlayer.getItemInHand((InteractionHand) hand));
    }

    @Override
    public void dropItem(int slot, boolean dropAll, int count) {
        serverPlayer.drop(serverPlayer.getInventory().removeItem(slot, dropAll ? count : 1),false,true);
    }

    @Override
    public WServerLevel<ServerLevel> serverLevel() {
        Class<? extends WServerLevel<ServerLevel>> wrapper = (Class<? extends WServerLevel<ServerLevel>>) WrapperRegistry.getWrapper(WServerLevel.class, serverPlayer.serverLevel());
        return WrapperRegistry.wrapFrom(wrapper, serverPlayer.serverLevel());
    }

    @Override
    public void resetAttackStrengthTicker() {
        serverPlayer.resetAttackStrengthTicker();
    }

    @Override
    public WInteractionResult<InteractionResult> interactOn(WEntity<?> entity, Enum<?> hand) {
        InteractionResult result = serverPlayer.interactOn(((WEntityImpl)entity).getInstance(), (InteractionHand) hand);
        Class<? extends WInteractionResult<InteractionResult>> wrapper = (Class<? extends WInteractionResult<InteractionResult>>) WrapperRegistry.getWrapper(WInteractionResult.class, result);
        return WrapperRegistry.wrapFrom(wrapper, result);
    }

    @Override
    public WInteractionResult<?> useItem(Enum<?> hand) {
        InteractionResult result = serverPlayer.gameMode.useItem(serverPlayer, serverPlayer.serverLevel(), serverPlayer.getItemInHand((InteractionHand) hand), (InteractionHand) hand);
        Class<? extends WInteractionResult<InteractionResult>> wrapper = (Class<? extends WInteractionResult<InteractionResult>>) WrapperRegistry.getWrapper(WInteractionResult.class, result);
        return WrapperRegistry.wrapFrom(wrapper, result);
    }

    public WInteractionResult<?> useItemOn(WServerLevel<?> world, ItemStack itemInHand, Enum<?> hand, WBlockHitResult<?> blockHit) {
        InteractionResult result = serverPlayer.gameMode.useItemOn(serverPlayer, serverPlayer.level(), serverPlayer.getItemInHand((InteractionHand) hand), (InteractionHand) hand, (BlockHitResult) blockHit.getInstance());
        Class<? extends WInteractionResult<InteractionResult>> wrapper = (Class<? extends WInteractionResult<InteractionResult>>) WrapperRegistry.getWrapper(WInteractionResult.class, result);
        return WrapperRegistry.wrapFrom(wrapper, result);
    }

    @Override
    public boolean blockActionRestricted(WBlockPos<?> pos) {
        return serverPlayer.blockActionRestricted(serverPlayer.level(), (BlockPos) pos.getInstance(), serverPlayer.gameMode.getGameModeForPlayer());
    }

    @Override
    public void destroyBlockProgress(int i, WBlockPos<?> pos, int i1) {
        serverPlayer.level().destroyBlockProgress(i, (BlockPos) pos.getInstance(), i1);
    }

    @Override
    public void handleBlockBreakAction(WBlockPos<?> pos, WServerPlayerAction.Action action, WDirection.Direction side, int maxHeight, int i) {
        serverPlayer.gameMode.handleBlockBreakAction((BlockPos) pos.getInstance(), WServerPlayerActionImpl.parse(action), WDirectionImpl.parse(side), maxHeight, i);
    }
}
