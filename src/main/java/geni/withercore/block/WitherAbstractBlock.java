package geni.withercore.block;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import geni.withercore.init.WUTBlocks;
import geni.withercore.init.WUTSounds;
import geni.witherutils.common.world.item.WrenchItem;
import org.jetbrains.annotations.NotNull;

import geni.withercore.blockentity.WitherBlockEntity;
import geni.withercore.item.IBlock;
import geni.withercore.item.ItemBlock;
import geni.withercore.util.BlockEntityUtil;
import geni.withercore.util.SoundUtil;
import geni.withercore.render.tooltip.Tooltips;
import geni.withercore.blockentity.WitherMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;

public abstract class WitherAbstractBlock extends WitherBlock implements IBlock {
	
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    
    protected WitherAbstractBlock(Properties props)
    {
        super(props);
    }
    
	@Override
	public ItemBlock<?> getBlockItem(Item.Properties properties)
	{
		return new ItemBlock<>(this, properties);
	}
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        if(context.getLevel().getBlockState(context.getClickedPos()).hasProperty(BlockStateProperties.HORIZONTAL_FACING))
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        else if(context.getLevel().getBlockState(context.getClickedPos()).hasProperty(HorizontalDirectionalBlock.FACING))
        {
            if (context.getClickedFace().getAxis() == Direction.Axis.Y)
                return this.defaultBlockState().setValue(FACING, Direction.NORTH);
            return this.defaultBlockState().setValue(FACING, context.getClickedFace());
        }
        else
            return super.defaultBlockState();
    }

    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }
    @SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

	public BlockState getStateFromItemStack(ItemStack itemStack)
	{
		return stateById(Item.getId(itemStack.getItem()));
	}
	
	@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
		super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public void attack(BlockState state, Level world, BlockPos pos, Player player)
    {
        if (this instanceof WitherEntityBlock)
        {
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof WitherBlockEntity be)
            {
            	be.attack(state, world, pos, player);
            }
        }
        super.attack(state, world, pos, player);
    }
    
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(state.getBlock() != newState.getBlock())
		{
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if(tileentity instanceof WitherMachineBlockEntity wbe)
			{
				if(wbe != null && wbe.getInventory() != null)
				{
					for(int i = 0; i < wbe.getInventory().getSlots(); i++)
					{
						Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), wbe.getInventory().getStackInSlot(i));
					}
				}
				worldIn.updateNeighbourForOutputSignal(pos, this);
				
				wbe.onRemove(state, worldIn, pos, newState, isMoving);
			}
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
    {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof WitherMachineBlockEntity machineBlockEntity && machineBlockEntity.canOpenMenu())
        {
            if (player instanceof ServerPlayer serverPlayer)
            {
                serverPlayer.openMenu(machineBlockEntity, machineBlockEntity.getBlockPos());
                SoundUtil.playSoundFromServer(serverPlayer, WUTSounds.BUCKET.get(), 0.4f, 1.0f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult hit)
    {
        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof WitherMachineBlockEntity machineBlockEntity))
        {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

		if(machineBlockEntity instanceof MenuProvider menuprovider && machineBlockEntity.canOpenMenu() && !player.isShiftKeyDown() && !(player.getItemInHand(interactionHand).getItem() instanceof WrenchItem))
		{
	        if (player instanceof ServerPlayer serverPlayer)
	        {
	        	serverPlayer.openMenu((MenuProvider) machineBlockEntity, machineBlockEntity.getBlockPos());
                SoundUtil.playSoundFromServer(serverPlayer, WUTSounds.BUCKET.get(), 0.4f, 1.0f);
	        }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
		}
        
        var result = machineBlockEntity.onBlockEntityUsed(state, level, pos, player, interactionHand, hit);
        if (result != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)
        {
            return result;
        }

        return ItemInteractionResult.sidedSuccess(true);
    }
    
	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
	{
		if(!world.isClientSide)
		{
			BlockEntity tile = world.getBlockEntity(pos);
			if(tile instanceof WitherBlockEntity)
				((WitherBlockEntity)tile).onNeighborBlockChange(fromPos);
		}
		super.neighborChanged(state, world, fromPos, block, pos, isMoving);
		checkRedstone(world, pos);
	}
	
    @Override
    public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack)
    {
        super.setPlacedBy(world, pos, state, placer, stack);
        checkRedstone(world, pos);
    }
    
    @Override
    protected void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston)
    {
		if(!pLevel.isClientSide)
		{
			BlockEntity tile = pLevel.getBlockEntity(pPos);
			if(tile instanceof WitherBlockEntity)
			{
				((WitherBlockEntity)tile).onAdded(pLevel, pState, pOldState, pMovedByPiston);
			}
		}
    	super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }
    
    @Override
    public boolean addLandingEffects(@Nonnull BlockState state, @Nonnull ServerLevel world, @Nonnull BlockPos bp, @Nonnull BlockState iblockstate, @Nonnull LivingEntity entity, int numberOfParticles)
    {
//    	world.sendParticles(new DustParticleOptions(DustParticleOptions.REDSTONE_PARTICLE_COLOR, 1), bp.getX() + 0.5D, bp.getY() + 1.0D, bp.getZ() + 0.5D, numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
    	return true;
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type)
    {
        return state.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty();
    }

	public static Direction getFacingFromEntity(BlockPos clickedBlock, LivingEntity entity)
	{
		return Direction.getNearest(
				(float) (entity.xOld - clickedBlock.getX()),
				(float) (entity.yOld - clickedBlock.getY()),
				(float) (entity.zOld - clickedBlock.getZ()));
	}
	
    public static Direction getDirectionBySidesInOrder(int side)
    {
        switch (side) {
            case 0:
                return Direction.UP;
            case 1:
                return Direction.DOWN;
            case 2:
                return Direction.NORTH;
            case 3:
                return Direction.EAST;
            case 4:
                return Direction.SOUTH;
            default:
                return Direction.WEST;
        }
    }
    
    @SuppressWarnings("unchecked")
	public static <T extends BlockEntity> Optional<T> getBlockEntityAt(BlockGetter w, BlockPos pos, Class<T> cls)
    {
        if (w != null && pos != null)
        {
            BlockEntity te = w.getBlockEntity(pos);
            if (te != null && cls.isAssignableFrom(te.getClass()))
            {
                return Optional.of((T) te);
            }
        }
        return Optional.empty();
    }
    
    /*
     * 
     * latest Members here
     * 
     */
    @Override
    protected float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos)
    {
        return getDestroyProgress(state, player, blockGetter, pos, state.hasBlockEntity() ? BlockEntityUtil.getTileEntity(blockGetter, pos) : null);
    }

	protected float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos, @Nullable BlockEntity tile)
    {
        Level level = tile == null ? null : tile.getLevel();

        if (level == null && blockGetter instanceof Level)
        {
            level = (Level) blockGetter;
        }
        float speed = super.getDestroyProgress(state, player, blockGetter, pos);
        return speed;
    }
    
    @Nullable
    private BlockState facing(BlockPlaceContext context, boolean b)
    {
        BlockState blockstate = this.defaultBlockState();
        for (Direction direction : context.getNearestLookingDirections())
        {
            if (b || direction.getAxis().isHorizontal())
            {
                blockstate = blockstate.setValue(FACING, b ? direction : direction.getOpposite());
                if (blockstate.canSurvive(context.getLevel(), context.getClickedPos()))
                {
                    return blockstate;
                }
            }
        }
        return null;
    }
    
//    public static int getRedstonePower(LevelReader world, BlockPos pos, Direction facing)
//    {
//        BlockState blockstate = world.getBlockState(pos);
//        return blockstate.shouldCheckWeakPower(world, pos, facing) ? getStrongPower(world, pos) : blockstate.getSignal(world, pos, facing);
//    }
//
//    public static int getStrongPower(LevelReader world, BlockPos pos)
//    {
//        int i = 0;
//        i = Math.max(i, world.getDirectSignal(pos.below(), Direction.DOWN));
//        if (i >= 15) {
//            return i;
//        } else {
//            i = Math.max(i, world.getDirectSignal(pos.above(), Direction.UP));
//            if (i >= 15) {
//                return i;
//            } else {
//                i = Math.max(i, world.getDirectSignal(pos.north(), Direction.NORTH));
//                if (i >= 15) {
//                    return i;
//                } else {
//                    i = Math.max(i, world.getDirectSignal(pos.south(), Direction.SOUTH));
//                    if (i >= 15) {
//                        return i;
//                    } else {
//                        i = Math.max(i, world.getDirectSignal(pos.west(), Direction.WEST));
//                        if (i >= 15) {
//                            return i;
//                        } else {
//                            return Math.max(i, world.getDirectSignal(pos.east(), Direction.EAST));
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    public static boolean isBlockPowered(LevelReader world, BlockPos pos)
//    {
//        if (getRedstonePower(world, pos.below(), Direction.DOWN) > 0) {
//            return true;
//        } else if (getRedstonePower(world, pos.above(), Direction.UP) > 0) {
//            return true;
//        } else if (getRedstonePower(world, pos.north(), Direction.NORTH) > 0) {
//            return true;
//        } else if (getRedstonePower(world, pos.south(), Direction.SOUTH) > 0) {
//            return true;
//        } else if (getRedstonePower(world, pos.west(), Direction.WEST) > 0) {
//            return true;
//        } else {
//            return getRedstonePower(world, pos.east(), Direction.EAST) > 0;
//        }
//    }
//    
//    @Override
//    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side)
//    {
//        if (this instanceof WitherEntityBlock)
//        {
//            BlockEntity tile = world.getBlockEntity(pos);
//            return tile instanceof IRedstoneEmitter;
//        }
//        return super.canConnectRedstone(state, world, pos, side);
//    }
//
//    @SuppressWarnings("deprecation")
//	@Override
//    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
//    {
//        if (this instanceof WitherEntityBlock)
//        {
//            BlockEntity tile = blockAccess.getBlockEntity(pos);
//            if (tile instanceof IRedstoneEmitter)
//            {
//                return ((IRedstoneEmitter) tile).getWeakPower(blockState, side);
//            }
//        }
//        return super.getSignal(blockState, blockAccess, pos, side);
//    }
//
//    @SuppressWarnings("deprecation")
//	@Override
//    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
//    {
//        if (this instanceof WitherEntityBlock)
//        {
//            BlockEntity tile = blockAccess.getBlockEntity(pos);
//            if (tile instanceof IRedstoneEmitter)
//            {
//                return ((IRedstoneEmitter) tile).getStrongPower(blockState, side);
//            }
//        }
//        return super.getDirectSignal(blockState, blockAccess, pos, side);
//    }
//    
    protected void checkRedstone(Level world, BlockPos pos)
    {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof WitherMachineBlockEntity genericTileEntity)
        {
            genericTileEntity.checkRedstone(world, pos);
        }
    }
//
//    protected int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side)
//    {
//        BlockEntity te = world.getBlockEntity(pos);
//        if (state.getBlock() instanceof WitherAbstractBlock && te instanceof WitherMachineBlockEntity generic)
//        {
//            return generic.getRedstoneOutput(state, world, pos, side);
//        }
//        return 0;
//    }
//    
//    @Override
//    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
//    {
//        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
//    }
//
//    @Override
//    public boolean hasAnalogOutputSignal(BlockState state)
//    {
//        return state.getBlock() instanceof EntityBlock;
//    }
}
