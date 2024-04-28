package me.autobot.playerdoll.v1_20_R4.Network.ClientPacketHandler;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R4.Network.CursedConnections;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;

public class ClientPacketListener extends ClientCommonListenerAbs implements TickablePacketListener, ClientGamePacketListener {
    private final GameProfile profile;
    public ClientPacketListener(Connection connection, GameProfile profile) {
        super(connection);
        this.profile = profile;
    }
    @Override
    public void handleCustomPayload(ClientboundCustomPayloadPacket clientboundCustomPayloadPacket) {
        // Not Handle This
    }
    @Override
    public void handleAddEntity(ClientboundAddEntityPacket clientboundAddEntityPacket) {

    }

    @Override
    public void handleAddExperienceOrb(ClientboundAddExperienceOrbPacket clientboundAddExperienceOrbPacket) {

    }

    @Override
    public void handleAddObjective(ClientboundSetObjectivePacket clientboundSetObjectivePacket) {

    }

    @Override
    public void handleAnimate(ClientboundAnimatePacket clientboundAnimatePacket) {

    }

    @Override
    public void handleHurtAnimation(ClientboundHurtAnimationPacket clientboundHurtAnimationPacket) {

    }

    @Override
    public void handleAwardStats(ClientboundAwardStatsPacket clientboundAwardStatsPacket) {

    }

    @Override
    public void handleAddOrRemoveRecipes(ClientboundRecipePacket clientboundRecipePacket) {

    }

    @Override
    public void handleBlockDestruction(ClientboundBlockDestructionPacket clientboundBlockDestructionPacket) {

    }

    @Override
    public void handleOpenSignEditor(ClientboundOpenSignEditorPacket clientboundOpenSignEditorPacket) {

    }

    @Override
    public void handleBlockEntityData(ClientboundBlockEntityDataPacket clientboundBlockEntityDataPacket) {

    }

    @Override
    public void handleBlockEvent(ClientboundBlockEventPacket clientboundBlockEventPacket) {

    }

    @Override
    public void handleBlockUpdate(ClientboundBlockUpdatePacket clientboundBlockUpdatePacket) {

    }

    @Override
    public void handleSystemChat(ClientboundSystemChatPacket clientboundSystemChatPacket) {

    }

    @Override
    public void handlePlayerChat(ClientboundPlayerChatPacket clientboundPlayerChatPacket) {

    }

    @Override
    public void handleDisguisedChat(ClientboundDisguisedChatPacket clientboundDisguisedChatPacket) {

    }

    @Override
    public void handleDeleteChat(ClientboundDeleteChatPacket clientboundDeleteChatPacket) {

    }

    @Override
    public void handleChunkBlocksUpdate(ClientboundSectionBlocksUpdatePacket clientboundSectionBlocksUpdatePacket) {

    }

    @Override
    public void handleMapItemData(ClientboundMapItemDataPacket clientboundMapItemDataPacket) {

    }

    @Override
    public void handleContainerClose(ClientboundContainerClosePacket clientboundContainerClosePacket) {

    }

    @Override
    public void handleContainerContent(ClientboundContainerSetContentPacket clientboundContainerSetContentPacket) {

    }

    @Override
    public void handleHorseScreenOpen(ClientboundHorseScreenOpenPacket clientboundHorseScreenOpenPacket) {

    }

    @Override
    public void handleContainerSetData(ClientboundContainerSetDataPacket clientboundContainerSetDataPacket) {

    }

    @Override
    public void handleContainerSetSlot(ClientboundContainerSetSlotPacket clientboundContainerSetSlotPacket) {

    }

    @Override
    public void handleEntityEvent(ClientboundEntityEventPacket clientboundEntityEventPacket) {

    }

    @Override
    public void handleEntityLinkPacket(ClientboundSetEntityLinkPacket clientboundSetEntityLinkPacket) {

    }

    @Override
    public void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket clientboundSetPassengersPacket) {

    }

    @Override
    public void handleExplosion(ClientboundExplodePacket clientboundExplodePacket) {

    }

    @Override
    public void handleGameEvent(ClientboundGameEventPacket clientboundGameEventPacket) {

    }

    @Override
    public void handleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket clientboundLevelChunkWithLightPacket) {

    }

    @Override
    public void handleChunksBiomes(ClientboundChunksBiomesPacket clientboundChunksBiomesPacket) {

    }

    @Override
    public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket clientboundForgetLevelChunkPacket) {

    }

    @Override
    public void handleLevelEvent(ClientboundLevelEventPacket clientboundLevelEventPacket) {

    }

    @Override
    public void handleLogin(ClientboundLoginPacket clientboundLoginPacket) {

    }

    @Override
    public void handleMoveEntity(ClientboundMoveEntityPacket clientboundMoveEntityPacket) {

    }

    @Override
    public void handleMovePlayer(ClientboundPlayerPositionPacket clientboundPlayerPositionPacket) {

    }

    @Override
    public void handleParticleEvent(ClientboundLevelParticlesPacket clientboundLevelParticlesPacket) {

    }

    @Override
    public void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket clientboundPlayerAbilitiesPacket) {

    }

    @Override
    public void handlePlayerInfoRemove(ClientboundPlayerInfoRemovePacket clientboundPlayerInfoRemovePacket) {

    }

    @Override
    public void handlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket clientboundPlayerInfoUpdatePacket) {

    }

    @Override
    public void handleRemoveEntities(ClientboundRemoveEntitiesPacket clientboundRemoveEntitiesPacket) {

    }

    @Override
    public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket clientboundRemoveMobEffectPacket) {

    }

    @Override
    public void handleRespawn(ClientboundRespawnPacket clientboundRespawnPacket) {

    }

    @Override
    public void handleRotateMob(ClientboundRotateHeadPacket clientboundRotateHeadPacket) {

    }

    @Override
    public void handleSetCarriedItem(ClientboundSetCarriedItemPacket clientboundSetCarriedItemPacket) {

    }

    @Override
    public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket clientboundSetDisplayObjectivePacket) {

    }

    @Override
    public void handleSetEntityData(ClientboundSetEntityDataPacket clientboundSetEntityDataPacket) {

    }

    @Override
    public void handleSetEntityMotion(ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket) {

    }

    @Override
    public void handleSetEquipment(ClientboundSetEquipmentPacket clientboundSetEquipmentPacket) {

    }

    @Override
    public void handleSetExperience(ClientboundSetExperiencePacket clientboundSetExperiencePacket) {

    }

    @Override
    public void handleSetHealth(ClientboundSetHealthPacket clientboundSetHealthPacket) {

    }

    @Override
    public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket clientboundSetPlayerTeamPacket) {

    }

    @Override
    public void handleSetScore(ClientboundSetScorePacket clientboundSetScorePacket) {

    }

    @Override
    public void handleResetScore(ClientboundResetScorePacket clientboundResetScorePacket) {

    }

    @Override
    public void handleSetSpawn(ClientboundSetDefaultSpawnPositionPacket clientboundSetDefaultSpawnPositionPacket) {

    }

    @Override
    public void handleSetTime(ClientboundSetTimePacket clientboundSetTimePacket) {

    }

    @Override
    public void handleSoundEvent(ClientboundSoundPacket clientboundSoundPacket) {

    }

    @Override
    public void handleSoundEntityEvent(ClientboundSoundEntityPacket clientboundSoundEntityPacket) {

    }

    @Override
    public void handleTakeItemEntity(ClientboundTakeItemEntityPacket clientboundTakeItemEntityPacket) {

    }

    @Override
    public void handleTeleportEntity(ClientboundTeleportEntityPacket clientboundTeleportEntityPacket) {

    }

    @Override
    public void handleTickingState(ClientboundTickingStatePacket clientboundTickingStatePacket) {

    }

    @Override
    public void handleTickingStep(ClientboundTickingStepPacket clientboundTickingStepPacket) {

    }

    @Override
    public void handleUpdateAttributes(ClientboundUpdateAttributesPacket clientboundUpdateAttributesPacket) {

    }

    @Override
    public void handleUpdateMobEffect(ClientboundUpdateMobEffectPacket clientboundUpdateMobEffectPacket) {

    }

    @Override
    public void handlePlayerCombatEnd(ClientboundPlayerCombatEndPacket clientboundPlayerCombatEndPacket) {

    }

    @Override
    public void handlePlayerCombatEnter(ClientboundPlayerCombatEnterPacket clientboundPlayerCombatEnterPacket) {

    }

    @Override
    public void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket clientboundPlayerCombatKillPacket) {

    }

    @Override
    public void handleChangeDifficulty(ClientboundChangeDifficultyPacket clientboundChangeDifficultyPacket) {

    }

    @Override
    public void handleSetCamera(ClientboundSetCameraPacket clientboundSetCameraPacket) {

    }

    @Override
    public void handleInitializeBorder(ClientboundInitializeBorderPacket clientboundInitializeBorderPacket) {

    }

    @Override
    public void handleSetBorderLerpSize(ClientboundSetBorderLerpSizePacket clientboundSetBorderLerpSizePacket) {

    }

    @Override
    public void handleSetBorderSize(ClientboundSetBorderSizePacket clientboundSetBorderSizePacket) {

    }

    @Override
    public void handleSetBorderWarningDelay(ClientboundSetBorderWarningDelayPacket clientboundSetBorderWarningDelayPacket) {

    }

    @Override
    public void handleSetBorderWarningDistance(ClientboundSetBorderWarningDistancePacket clientboundSetBorderWarningDistancePacket) {

    }

    @Override
    public void handleSetBorderCenter(ClientboundSetBorderCenterPacket clientboundSetBorderCenterPacket) {

    }

    @Override
    public void handleTabListCustomisation(ClientboundTabListPacket clientboundTabListPacket) {

    }

    @Override
    public void handleBossUpdate(ClientboundBossEventPacket clientboundBossEventPacket) {

    }

    @Override
    public void handleItemCooldown(ClientboundCooldownPacket clientboundCooldownPacket) {

    }

    @Override
    public void handleMoveVehicle(ClientboundMoveVehiclePacket clientboundMoveVehiclePacket) {

    }

    @Override
    public void handleUpdateAdvancementsPacket(ClientboundUpdateAdvancementsPacket clientboundUpdateAdvancementsPacket) {

    }

    @Override
    public void handleSelectAdvancementsTab(ClientboundSelectAdvancementsTabPacket clientboundSelectAdvancementsTabPacket) {

    }

    @Override
    public void handlePlaceRecipe(ClientboundPlaceGhostRecipePacket clientboundPlaceGhostRecipePacket) {

    }

    @Override
    public void handleCommands(ClientboundCommandsPacket clientboundCommandsPacket) {

    }

    @Override
    public void handleStopSoundEvent(ClientboundStopSoundPacket clientboundStopSoundPacket) {

    }

    @Override
    public void handleCommandSuggestions(ClientboundCommandSuggestionsPacket clientboundCommandSuggestionsPacket) {

    }

    @Override
    public void handleUpdateRecipes(ClientboundUpdateRecipesPacket clientboundUpdateRecipesPacket) {

    }

    @Override
    public void handleLookAt(ClientboundPlayerLookAtPacket clientboundPlayerLookAtPacket) {

    }

    @Override
    public void handleTagQueryPacket(ClientboundTagQueryPacket clientboundTagQueryPacket) {

    }

    @Override
    public void handleLightUpdatePacket(ClientboundLightUpdatePacket clientboundLightUpdatePacket) {

    }

    @Override
    public void handleOpenBook(ClientboundOpenBookPacket clientboundOpenBookPacket) {

    }

    @Override
    public void handleOpenScreen(ClientboundOpenScreenPacket clientboundOpenScreenPacket) {

    }

    @Override
    public void handleMerchantOffers(ClientboundMerchantOffersPacket clientboundMerchantOffersPacket) {

    }

    @Override
    public void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket clientboundSetChunkCacheRadiusPacket) {

    }

    @Override
    public void handleSetSimulationDistance(ClientboundSetSimulationDistancePacket clientboundSetSimulationDistancePacket) {

    }

    @Override
    public void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket clientboundSetChunkCacheCenterPacket) {

    }

    @Override
    public void handleBlockChangedAck(ClientboundBlockChangedAckPacket clientboundBlockChangedAckPacket) {

    }

    @Override
    public void setActionBarText(ClientboundSetActionBarTextPacket clientboundSetActionBarTextPacket) {

    }

    @Override
    public void setSubtitleText(ClientboundSetSubtitleTextPacket clientboundSetSubtitleTextPacket) {

    }

    @Override
    public void setTitleText(ClientboundSetTitleTextPacket clientboundSetTitleTextPacket) {

    }

    @Override
    public void setTitlesAnimation(ClientboundSetTitlesAnimationPacket clientboundSetTitlesAnimationPacket) {

    }

    @Override
    public void handleTitlesClear(ClientboundClearTitlesPacket clientboundClearTitlesPacket) {

    }

    @Override
    public void handleServerData(ClientboundServerDataPacket clientboundServerDataPacket) {

    }

    @Override
    public void handleCustomChatCompletions(ClientboundCustomChatCompletionsPacket clientboundCustomChatCompletionsPacket) {

    }

    @Override
    public void handleBundlePacket(ClientboundBundlePacket clientboundBundlePacket) {

    }

    @Override
    public void handleDamageEvent(ClientboundDamageEventPacket clientboundDamageEventPacket) {

    }

    @Override
    public void handleConfigurationStart(ClientboundStartConfigurationPacket clientboundStartConfigurationPacket) {
        if (!PlayerDoll.isFolia) {
            PacketUtils.ensureRunningOnSameThread(clientboundStartConfigurationPacket, this, CursedConnections.server);
        }
        this.connection.setupInboundProtocol(ConfigurationProtocols.CLIENTBOUND, new ClientConfigurationListener(this.connection, profile));
        this.send(ServerboundConfigurationAcknowledgedPacket.INSTANCE);
        this.connection.setupOutboundProtocol(ConfigurationProtocols.SERVERBOUND);

    }

    @Override
    public void handleChunkBatchStart(ClientboundChunkBatchStartPacket clientboundChunkBatchStartPacket) {

    }

    @Override
    public void handleChunkBatchFinished(ClientboundChunkBatchFinishedPacket clientboundChunkBatchFinishedPacket) {

    }

    @Override
    public void handleDebugSample(ClientboundDebugSamplePacket clientboundDebugSamplePacket) {

    }

    @Override
    public void handleProjectilePowerPacket(ClientboundProjectilePowerPacket clientboundProjectilePowerPacket) {

    }

    @Override
    public void handlePongResponse(ClientboundPongResponsePacket clientboundPongResponsePacket) {

    }

    @Override
    public void tick() {

    }

    @Override
    public boolean isAcceptingMessages() {
        return connection.isConnected();
    }

    @Override
    public void handleDisconnect(ClientboundDisconnectPacket clientboundDisconnectPacket) {
        connection.disconnect(clientboundDisconnectPacket.reason());
    }

    @Override
    public void handleStoreCookie(ClientboundStoreCookiePacket clientboundStoreCookiePacket) {

    }

    @Override
    public void handleTransfer(ClientboundTransferPacket clientboundTransferPacket) {

    }

    @Override
    public void handleRequestCookie(ClientboundCookieRequestPacket clientboundCookieRequestPacket) {

    }

}
