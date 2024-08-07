package me.autobot.playerdoll.v1_20_R3.carpetmod;

import me.autobot.playerdoll.carpetmod.ActionPackPlayer;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.v1_20_R3.wrapper.NMSEntity;
import me.autobot.playerdoll.v1_20_R3.wrapper.NMSServerLevel;
import me.autobot.playerdoll.v1_20_R3.wrapper.NMSServerPlayerGamemode;
import me.autobot.playerdoll.wrapper.WrapperServerLevel;
import me.autobot.playerdoll.wrapper.block.WrapperBlockPos;
import me.autobot.playerdoll.wrapper.entity.WrapperEntity;
import me.autobot.playerdoll.wrapper.entity.WrapperInteractionResult;
import me.autobot.playerdoll.wrapper.entity.WrapperServerPlayerGameMode;
import me.autobot.playerdoll.wrapper.phys.WrapperVec3;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class NMSActionPackPlayer extends ActionPackPlayer {

    private final ServerPlayer player;
    public NMSActionPackPlayer(BaseEntity entity) {
        this.player = (ServerPlayer) entity;
    }

    @Override
    public Object toServerPlayer() {
        return player;
    }

    @Override
    public List<?> getEntities(Object box) {
        return player.level().getEntities(player, (AABB) box, e -> !e.isSpectator() && e.isPickable());
    }

    @Override
    public void resetLastActionTime() {
        player.resetLastActionTime();
    }

    @Override
    public void releaseUsingItem() {
        player.releaseUsingItem();
    }

    @Override
    public void resetAttackStrengthTicker() {
        player.resetAttackStrengthTicker();
    }

    @Override
    public WrapperServerPlayerGameMode getGameMode() {
        return new NMSServerPlayerGamemode(player.gameMode);
    }

    @Override
    public void setJumping(boolean b) {
        player.setJumping(b);
    }

    @Override
    public void jumpFromGround() {
        player.jumpFromGround();
    }

    @Override
    public WrapperServerLevel serverLevel() {
        return new NMSServerLevel(player.serverLevel());
    }

    @Override
    public void look(float yaw, float pitch) {
        player.setYRot(yaw % 360); //setYaw
        player.setXRot(Mth.clamp(pitch, -90, 90)); // setPitch
    }

    @Override
    public void lookAt(WrapperVec3 vec3) {
        player.lookAt(EntityAnchorArgument.Anchor.EYES, (Vec3) vec3.toObj());
    }

    @Override
    public void setZZA(float f) {
        player.zza = f;
    }

    @Override
    public void setXXA(float f) {
        player.xxa = f;
    }

    @Override
    public WrapperServerLevel level() {
        return new NMSServerLevel(player.level());
    }

    @Override
    public Object getItemInHand(Enum<?> interactionHand) {
        return player.getItemInHand((InteractionHand) interactionHand);
    }

    @Override
    public WrapperInteractionResult interactOn(WrapperEntity entity, Enum<?> hand) {
        return new WrapperInteractionResult(player.interactOn((Entity) entity.toObj(), (InteractionHand) hand));
    }

    @Override
    public boolean blockActionRestricted(WrapperServerLevel level, WrapperBlockPos blockPos, Enum<?> gameType) {
        return player.blockActionRestricted((Level) level.toObj(), (BlockPos) blockPos.toObj(), (GameType) gameType);
    }

    @Override
    public WrapperEntity wrapEntity(Object nmsEntity) {
        return new NMSEntity(nmsEntity);
    }
}
