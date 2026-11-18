package geni.withercore.common.helper;

import geni.witherutils.common.world.level.block.PowerPoleBlock;
import geni.witherutils.common.world.level.block.StreetLampBlock;
import geni.witherutils.common.world.level.block.state.PowerPolePart;
import geni.witherutils.common.world.level.block.state.StreetLampPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared helpers for resolving multi-block IPowerNode base BlockEntity positions.
 */
public final class PowerNodeHelper {

    private PowerNodeHelper() {}

    /**
     * Resolves any clicked segment of a multi-block power node (e.g. PowerPole MIDDLE/UPPER,
     * StreetLamp MIDDLE/UPPER) to the base BlockPos holding the BlockEntity.
     */
    public static BlockPos resolveBasePos(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof PowerPoleBlock) {
            PowerPolePart part = state.getValue(PowerPoleBlock.PART);
            if (part == PowerPolePart.MIDDLE) {
                return pos.below();
            }
            if (part == PowerPolePart.UPPER) {
                return pos.below(2);
            }
        }
        if (state.getBlock() instanceof StreetLampBlock) {
            StreetLampPart part = state.getValue(StreetLampBlock.PART);
            if (part == StreetLampPart.MIDDLE) {
                return pos.below();
            }
            if (part == StreetLampPart.UPPER) {
                return pos.below(2);
            }
        }
        return pos;
    }
}
