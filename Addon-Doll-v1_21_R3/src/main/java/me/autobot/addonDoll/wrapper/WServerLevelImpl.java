package me.autobot.addonDoll.wrapper;

import me.autobot.addonDoll.action.PackPlayerImpl;
import me.autobot.playerdoll.api.action.pack.AbsPackPlayer;
import me.autobot.playerdoll.api.wrapper.Wrapper;
import me.autobot.playerdoll.api.wrapper.WrapperRegistry;
import me.autobot.playerdoll.api.wrapper.builtin.WBlockPos;
import me.autobot.playerdoll.api.wrapper.builtin.WBlockState;
import me.autobot.playerdoll.api.wrapper.builtin.WServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

@Wrapper(wrapping = ServerLevel.class, method = "wrap")
public class WServerLevelImpl extends WServerLevel<ServerLevel> {

    private final ServerLevel serverLevel;

    public static WServerLevelImpl wrap(ServerLevel serverLevel) {
        return new WServerLevelImpl(serverLevel);
    }

    private WServerLevelImpl(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
    }

    @Override
    public boolean mayInteract(AbsPackPlayer player, WBlockPos<?> pos) {
        return serverLevel.mayInteract(((PackPlayerImpl)player).getServerPlayer(), WrapperRegistry.getInstance(pos, BlockPos.class));
    }

    @Override
    public WBlockState<BlockState> getBlockState(WBlockPos<?> pos) {
        BlockState state = serverLevel.getBlockState(WrapperRegistry.getInstance(pos, BlockPos.class));
        Class<? extends WBlockState<BlockState>> wrapper = (Class<? extends WBlockState<BlockState>>) WrapperRegistry.getWrapper(WBlockState.class, state);
        return WrapperRegistry.wrapFrom(wrapper, state);
    }

    @Override
    public ServerLevel getInstance() {
        return serverLevel;
    }
}
