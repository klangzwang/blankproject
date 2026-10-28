package geni.withercore.blockentity;

import geni.withercore.registration.impl.TileEntityTypeRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WitherRenderBlockEntity extends WitherBlockEntity {

    /*public static class AngelBlock extends WitherRenderBlockEntity
    {
        public AngelBlock(BlockPos worldPosition, BlockState blockState)
        {
            super(WUTBlockEntities.ANGEL_BE, worldPosition, blockState);
        }

        @Override
        public void clientTick() {
            super.clientTick();
        }
    }*/

    public WitherRenderBlockEntity(TileEntityTypeRegistryObject<?> type, BlockPos worldPosition, BlockState blockState)
    {
        super(type, worldPosition, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WitherRenderBlockEntity blockEntity)
    {
        if (level.isClientSide)
        {
            blockEntity.clientTick();
        }
        else
        {
            blockEntity.serverTick();
        }
    }
    
    public void serverTick()
    {
    }
    public void clientTick()
    {
    }
}
