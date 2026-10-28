package geni.withercore.block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class WitherAbstractRailingBlock extends WitherAbstractBlock implements SimpleWaterloggedBlock {
	
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	protected final Map<BlockState, VoxelShape> shapes;
	protected final Map<BlockState, VoxelShape> collision_shapes;

	public WitherAbstractRailingBlock(BlockBehaviour.Properties properties, AABB base_aabb, final AABB[] side_aabb, int railing_height_extension)
	{
		super(properties);
		Map<BlockState, VoxelShape> build_shapes = new HashMap<>();
		Map<BlockState, VoxelShape> build_collision_shapes = new HashMap<>();
		for (BlockState state : getStateDefinition().getPossibleStates()) {
			{
				VoxelShape shape = ((base_aabb.getXsize() == 0) || (base_aabb.getYsize() == 0)
						|| (base_aabb.getZsize() == 0)) ? Shapes.empty() : Shapes.create(base_aabb);
				if (state.getValue(NORTH))
					shape = Shapes.joinUnoptimized(shape,
							getUnionShape(getRotatedAABB(side_aabb, Direction.NORTH, true)),
							BooleanOp.OR);
				if (state.getValue(EAST))
					shape = Shapes.joinUnoptimized(shape,
							getUnionShape(getRotatedAABB(side_aabb, Direction.EAST, true)),
							BooleanOp.OR);
				if (state.getValue(SOUTH))
					shape = Shapes.joinUnoptimized(shape,
							getUnionShape(getRotatedAABB(side_aabb, Direction.SOUTH, true)),
							BooleanOp.OR);
				if (state.getValue(WEST))
					shape = Shapes.joinUnoptimized(shape,
							getUnionShape(getRotatedAABB(side_aabb, Direction.WEST, true)),
							BooleanOp.OR);
				if (shape.isEmpty())
					shape = Shapes.block();
				build_shapes.put(state.setValue(WATERLOGGED, false), shape);
				build_shapes.put(state.setValue(WATERLOGGED, true), shape);
			}
			{
				VoxelShape shape = ((base_aabb.getXsize() == 0) || (base_aabb.getYsize() == 0)
						|| (base_aabb.getZsize() == 0)) ? Shapes.empty() : Shapes.create(base_aabb);
				if (state.getValue(NORTH))
					shape = Shapes.joinUnoptimized(shape,
							getUnionShape(getMappedAABB(
									getRotatedAABB(side_aabb, Direction.NORTH, true),
									bb -> bb.expandTowards(0, railing_height_extension, 0))),
							BooleanOp.OR);
				if (state.getValue(EAST))
					shape = Shapes.joinUnoptimized(shape,
							getUnionShape(getMappedAABB(
									getRotatedAABB(side_aabb, Direction.EAST, true),
									bb -> bb.expandTowards(0, railing_height_extension, 0))),
							BooleanOp.OR);
				if (state.getValue(SOUTH))
					shape = Shapes.joinUnoptimized(shape,
							getUnionShape(getMappedAABB(
									getRotatedAABB(side_aabb, Direction.SOUTH, true),
									bb -> bb.expandTowards(0, railing_height_extension, 0))),
							BooleanOp.OR);
				if (state.getValue(WEST))
					shape = Shapes.joinUnoptimized(shape,
							getUnionShape(getMappedAABB(
									getRotatedAABB(side_aabb, Direction.WEST, true),
									bb -> bb.expandTowards(0, railing_height_extension, 0))),
							BooleanOp.OR);
				if (shape.isEmpty())
					shape = Shapes.block();
				build_collision_shapes.put(state.setValue(WATERLOGGED, false), shape);
				build_collision_shapes.put(state.setValue(WATERLOGGED, true), shape);
			}
		}
		shapes = build_shapes;
		collision_shapes = build_collision_shapes;
		registerDefaultState(super.defaultBlockState().setValue(NORTH, false).setValue(EAST, false)
				.setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
	}

	public WitherAbstractRailingBlock(BlockBehaviour.Properties properties, AABB base_aabb, final AABB side_aabb, int railing_height_extension)
	{
		this(properties, base_aabb, new AABB[] { side_aabb }, railing_height_extension);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		super.createBlockStateDefinition(builder);
		builder.add(NORTH, EAST, SOUTH, WEST, WATERLOGGED);
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		return super.getStateForPlacement(context).setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
	{
		return shapes.getOrDefault(state, Shapes.block());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
	{
		return collision_shapes.getOrDefault(state, Shapes.block());
	}

	public static BooleanProperty getDirectionProperty(Direction face)
	{
		return switch (face)
		{
			case EAST -> WitherAbstractRailingBlock.EAST;
			case SOUTH -> WitherAbstractRailingBlock.SOUTH;
			case WEST -> WitherAbstractRailingBlock.WEST;
			default -> WitherAbstractRailingBlock.NORTH;
		};
	}
	
	  public static boolean isWaterLogged(BlockState state)
	  { return state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED); }

	  public static AABB getPixeledAABB(double x0, double y0, double z0, double x1, double y1, double z1)
	  { return new AABB(x0/16.0, y0/16.0, z0/16.0, x1/16.0, y1/16.0, z1/16.0); }

	  public static AABB getRotatedAABB(AABB bb, Direction new_facing)
	  { return getRotatedAABB(bb, new_facing, false); }

	  public static AABB[] getRotatedAABB(AABB[] bb, Direction new_facing)
	  { return getRotatedAABB(bb, new_facing, false); }

	  public static AABB getRotatedAABB(AABB bb, Direction new_facing, boolean horizontal_rotation)
	  {
	    if(!horizontal_rotation) {
	      switch(new_facing.get3DDataValue()) {
	        case 0: return new AABB(1-bb.maxX,   bb.minZ,   bb.minY, 1-bb.minX,   bb.maxZ,   bb.maxY); // D
	        case 1: return new AABB(1-bb.maxX, 1-bb.maxZ, 1-bb.maxY, 1-bb.minX, 1-bb.minZ, 1-bb.minY); // U
	        case 2: return new AABB(  bb.minX,   bb.minY,   bb.minZ,   bb.maxX,   bb.maxY,   bb.maxZ); // N --> bb
	        case 3: return new AABB(1-bb.maxX,   bb.minY, 1-bb.maxZ, 1-bb.minX,   bb.maxY, 1-bb.minZ); // S
	        case 4: return new AABB(  bb.minZ,   bb.minY, 1-bb.maxX,   bb.maxZ,   bb.maxY, 1-bb.minX); // W
	        case 5: return new AABB(1-bb.maxZ,   bb.minY,   bb.minX, 1-bb.minZ,   bb.maxY,   bb.maxX); // E
	      }
	    } else {
	      switch(new_facing.get3DDataValue()) {
	        case 0: return new AABB(  bb.minX, bb.minY,   bb.minZ,   bb.maxX, bb.maxY,   bb.maxZ); // D --> bb
	        case 1: return new AABB(  bb.minX, bb.minY,   bb.minZ,   bb.maxX, bb.maxY,   bb.maxZ); // U --> bb
	        case 2: return new AABB(  bb.minX, bb.minY,   bb.minZ,   bb.maxX, bb.maxY,   bb.maxZ); // N --> bb
	        case 3: return new AABB(1-bb.maxX, bb.minY, 1-bb.maxZ, 1-bb.minX, bb.maxY, 1-bb.minZ); // S
	        case 4: return new AABB(  bb.minZ, bb.minY, 1-bb.maxX,   bb.maxZ, bb.maxY, 1-bb.minX); // W
	        case 5: return new AABB(1-bb.maxZ, bb.minY,   bb.minX, 1-bb.minZ, bb.maxY,   bb.maxX); // E
	      }
	    }
	    return bb;
	  }

	  public static AABB[] getRotatedAABB(AABB[] bbs, Direction new_facing, boolean horizontal_rotation)
	  {
	    final AABB[] transformed = new AABB[bbs.length];
	    for(int i=0; i<bbs.length; ++i) transformed[i] = getRotatedAABB(bbs[i], new_facing, horizontal_rotation);
	    return transformed;
	  }

	  public static AABB getYRotatedAABB(AABB bb, int clockwise_90deg_steps)
	  {
	    final Direction[] direction_map = new Direction[]{Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	    return getRotatedAABB(bb, direction_map[(clockwise_90deg_steps+4096) & 0x03], true);
	  }

	  public static AABB[] getYRotatedAABB(AABB[] bbs, int clockwise_90deg_steps)
	  {
	    final AABB[] transformed = new AABB[bbs.length];
	    for(int i=0; i<bbs.length; ++i) transformed[i] = getYRotatedAABB(bbs[i], clockwise_90deg_steps);
	    return transformed;
	  }

	  public static AABB getMirroredAABB(AABB bb, Direction.Axis axis)
	  {
	    return switch (axis) {
	      case X -> new AABB(1 - bb.maxX, bb.minY, bb.minZ, 1 - bb.minX, bb.maxY, bb.maxZ);
	      case Y -> new AABB(bb.minX, 1 - bb.maxY, bb.minZ, bb.maxX, 1 - bb.minY, bb.maxZ);
	      case Z -> new AABB(bb.minX, bb.minY, 1 - bb.maxZ, bb.maxX, bb.maxY, 1 - bb.minZ);
	    };
	  }

	  public static AABB[] getMirroredAABB(AABB[] bbs, Direction.Axis axis)
	  {
	    final AABB[] transformed = new AABB[bbs.length];
	    for(int i=0; i<bbs.length; ++i) transformed[i] = getMirroredAABB(bbs[i], axis);
	    return transformed;
	  }

	  public static VoxelShape getUnionShape(AABB ... aabbs)
	  {
	    VoxelShape shape = Shapes.empty();
	    for(AABB aabb: aabbs) shape = Shapes.joinUnoptimized(shape, Shapes.create(aabb), BooleanOp.OR);
	    return shape;
	  }

	  public static VoxelShape getUnionShape(AABB[] ... aabb_list)
	  {
	    VoxelShape shape = Shapes.empty();
	    for(AABB[] aabbs:aabb_list) {
	      for(AABB aabb: aabbs) shape = Shapes.joinUnoptimized(shape, Shapes.create(aabb), BooleanOp.OR);
	    }
	    return shape;
	  }

	  public static AABB[] getMappedAABB(AABB[] bbs, Function<AABB,AABB> mapper) {
	    final AABB[] transformed = new AABB[bbs.length];
	    for(int i=0; i<bbs.length; ++i) transformed[i] = mapper.apply(bbs[i]);
	    return transformed;
	  }
	  
		/*
		 * 
		 * WATERLOGGABLE
		 * 
		 */
	    @Override
	    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos)
	    {
	    	return !state.getValue(WATERLOGGED) && super.propagatesSkylightDown(state, reader, pos);
	    }
	    @Override
	    @SuppressWarnings("deprecation")
	    public FluidState getFluidState(BlockState state)
	    {
	    	return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	    }
	    @Override
	    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos)
	    {
	    	if(state.getValue(WATERLOGGED)) world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
	    	return state;
	    }

		@Override
		public boolean canPlaceLiquid(@org.jetbrains.annotations.Nullable Player p_294196_, BlockGetter p_56301_, BlockPos p_56302_, BlockState p_56303_, Fluid p_56304_)
		{ return SimpleWaterloggedBlock.super.canPlaceLiquid(p_294196_, p_56301_, p_56302_, p_56303_, p_56304_); }
	    @Override
	    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState)
	    { return SimpleWaterloggedBlock.super.placeLiquid(world, pos, state, fluidState); }
		@Override
		public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state)
		{ return SimpleWaterloggedBlock.super.pickupBlock(player, level, pos, state); }
	    @Override
	    public Optional<SoundEvent> getPickupSound()
	    { return SimpleWaterloggedBlock.super.getPickupSound(); }
}
