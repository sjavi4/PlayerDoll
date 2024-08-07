package me.autobot.playerdoll.wrapper.block;

import net.minecraft.core.EnumDirection;
import org.bukkit.block.BlockFace;

public interface WrapperDirection {
    EnumDirection DOWN = EnumDirection.a;
    EnumDirection UP = EnumDirection.b;
    EnumDirection NORTH = EnumDirection.c;
    EnumDirection SOUTH = EnumDirection.d;
    EnumDirection WEST = EnumDirection.e;
    EnumDirection EAST = EnumDirection.f;

    static EnumDirection convertBlockFace(BlockFace face) {
        if (face.isCartesian()) {
            if (face == BlockFace.NORTH) {
                return NORTH;
            } else if (face == BlockFace.EAST) {
                return EAST;
            } else if (face == BlockFace.SOUTH) {
                return SOUTH;
            } else if (face == BlockFace.WEST) {
                return WEST;
            } else if (face == BlockFace.UP) {
                return UP;
            } else if (face == BlockFace.DOWN) {
                return DOWN;
            } else {
                throw new UnsupportedOperationException("Non cartesian Block Face");
            }
        } else {
            throw new UnsupportedOperationException("Non cartesian Block Face");
        }
    }
}
