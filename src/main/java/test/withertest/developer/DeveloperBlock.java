package test.withertest.developer;

import geni.witherapi.block.ICustomBlockProperties;
import geni.witherapi.block.WitherEntityBlock;
import geni.withercore.common.world.level.block.WitherAbstractBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DeveloperBlock extends WitherAbstractBlock implements WitherEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public DeveloperBlock() {
        super(((ICustomBlockProperties) BlockBehaviour.Properties.of()).checkArea(true));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void onEntityAroundArea(Level level, BlockPos posBlock, List<AreaEntityContext> entities) {
        for (AreaEntityContext context : entities) {
            level.addParticle(ParticleTypes.FLAME, context.position().x, context.position().y, context.position().z, 0, 0, 0);
            System.out.println(context.entity());
            System.out.println(context.position());
        }
        super.onEntityAroundArea(level, posBlock, entities);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        Direction facing = ctx.getClickedFace();
        return super.getStateForPlacement(ctx).setValue(FACING, facing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DeveloperBlockEntity(pos, state);
    }
}
