package geni.withercore.block;

import javax.annotation.Nullable;

import geni.withercore.blockentity.WitherBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public interface WitherEntityBlock extends EntityBlock {

    @Nullable
    @Override
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType)
    {
        return (level, blockPos, blockState, t) -> {
            if (t instanceof WitherBlockEntity tickable)
            {
                if (level.isClientSide())
                {
                    tickable.clientTick();
                }
                else
                {
                    tickable.serverTick();
                }
            }
        };
    }
}
