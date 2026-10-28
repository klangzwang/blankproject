package geni.withercore.common.world.level.block;

import geni.withercore.client.tooltip.Tooltips;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class WitherBlock extends Block {

	public WitherBlock(Properties props)
    {
        super(props);
        BlockState state = getStateDefinition().any();
        registerDefaultState(state);
	}

	@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
		Tooltips.Tooltip.add(stack, context, tooltip, flag);
		super.appendHoverText(stack, context, tooltip, flag);
    }

    public boolean hasSignalConnector(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side)
    { return state.isSignalSource(); }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        final boolean rsup = (state.hasBlockEntity() && (state.getBlock() != newState.getBlock()));
        super.onRemove(state, world, pos, newState, isMoving);
        if(rsup) world.updateNeighbourForOutputSignal(pos, this);
    }
}
