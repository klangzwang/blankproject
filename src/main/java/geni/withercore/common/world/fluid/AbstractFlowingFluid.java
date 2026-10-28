package geni.withercore.common.world.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

/**
 * Custom FlowingFluid implementation enforcing:
 * - Directional gating via AbstractLiquidBlock.getPossibleFlowDirections()
 * - Suppression of spreading when AbstractLiquidBlock.isFlowLevelAway() is true
 * - Spread tracking for AbstractLiquidBlock.isFlowAway()
 */
public abstract class AbstractFlowingFluid extends BaseFlowingFluid {

    protected AbstractFlowingFluid(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canSpreadTo(BlockGetter level, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluid) {
        if (fromBlockState.getBlock() instanceof AbstractLiquidBlock liquidBlock) {
            // If isFlowLevelAway is true, no spread is allowed anywhere
            if (liquidBlock.isFlowLevelAway()) {
                return false;
            }

            // Directional check: must be in possible flow directions
            if (!liquidBlock.getPossibleFlowDirections().contains(direction)) {
                return false;
            }

            // Check if isFlowAway has exceeded max allowed spreads (2x)
            /*if (liquidBlock.isFlowAway()) {
                if (LiquidBlockTickManager.isFlowAwaySpreadLimitReached(fromPos)) {
                    return false;
                }
                LiquidBlockTickManager.recordSpread(fromPos);
            }*/
        }

        return super.canSpreadTo(level, fromPos, fromBlockState, direction, toPos, toBlockState, toFluidState, fluid);
    }

    @Override
    public int getSlopeFindDistance(LevelReader level) {
        return 4;
    }

    @Override
    public int getDropOff(LevelReader level) {
        return 1;
    }
}
