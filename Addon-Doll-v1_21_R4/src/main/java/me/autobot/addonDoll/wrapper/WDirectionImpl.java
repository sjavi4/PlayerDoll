package me.autobot.addonDoll.wrapper;

import me.autobot.playerdoll.api.wrapper.Wrapper;
import me.autobot.playerdoll.api.wrapper.builtin.WDirection;
import net.minecraft.core.Direction;

@Wrapper(wrapping = Direction.class, method = "wrap")
public class WDirectionImpl extends WDirection<Direction> {

    private final net.minecraft.core.Direction direction;

    public static WDirectionImpl wrap(net.minecraft.core.Direction direction) {
        return new WDirectionImpl(direction);
    }

    private WDirectionImpl(net.minecraft.core.Direction direction) {
        this.direction = direction;
    }

    @Override
    public Direction parse() {
        return switch (direction) {
            case DOWN -> Direction.DOWN;
            case UP -> Direction.UP;
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case WEST -> Direction.WEST;
            case EAST -> Direction.EAST;
        };
    }

    public static net.minecraft.core.Direction parse(Direction direction) {
        return switch (direction) {
            case NORTH -> net.minecraft.core.Direction.NORTH;
            case EAST -> net.minecraft.core.Direction.EAST;
            case SOUTH -> net.minecraft.core.Direction.SOUTH;
            case WEST -> net.minecraft.core.Direction.WEST;
            case UP -> net.minecraft.core.Direction.UP;
            case DOWN -> net.minecraft.core.Direction.DOWN;
        };
    }

    @Override
    public net.minecraft.core.Direction getInstance() {
        return direction;
    }
}
