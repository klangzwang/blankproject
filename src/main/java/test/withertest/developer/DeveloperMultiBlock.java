package test.withertest.developer;

import geni.withercore.common.helper.NullHelper;
import geni.withercore.common.world.level.block.WitherAbstractBlock;
import geni.withercore.init.WUTBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import javax.annotation.Nonnull;
import java.util.Locale;

public class DeveloperMultiBlock extends WitherAbstractBlock {

    @Nonnull
    public static final EnumProperty<BlockType> BLOCK_TYPE = EnumProperty
            .<BlockType>create("blocktype", BlockType.class);

    public DeveloperMultiBlock() {
        super(WUTBlocks.defaultProps());
        this.registerDefaultState(this.stateDefinition.any().setValue(BLOCK_TYPE, BlockType.SINGLE));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return !blockstate.is(this);
    }

    public @Nonnull BlockState getStateFromType(int types) {
        return defaultBlockState().setValue(BLOCK_TYPE, BlockType.getType(types));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BLOCK_TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level worldIn = ctx.getLevel();
        BlockPos selfPos = ctx.getClickedPos();

        BlockPos masterPos = getMasterPosForNewMB(worldIn, selfPos);
        if (masterPos == null) {
            return super.getStateForPlacement(ctx);
        }

        BlockType myType = null;
        for (BlockType bt : BlockType.values()) {
            if (bt != BlockType.SINGLE) {
                BlockPos testMaster = bt.getLocationOfMaster(selfPos);
                if (testMaster != null && testMaster.equals(masterPos)) {
                    myType = bt;
                    break;
                }
            }
        }

        if (myType == null) {
            return super.getStateForPlacement(ctx);
        }

        if (!worldIn.isClientSide) {
            updateMultiBlock(worldIn, masterPos, selfPos, true);
        }

        return defaultBlockState().setValue(BLOCK_TYPE, myType);
    }

    private void updateMultiBlock(Level world, BlockPos masterPos, BlockPos ignorePos, boolean form) {
        for (BlockType type : BlockType.values()) {
            if (type != BlockType.SINGLE && type != BlockType.MASTER) {
                Vec3i offsetFromMaster = type.getOffsetFromMaster();
                BlockPos targetPos = masterPos.offset(offsetFromMaster.getX(), offsetFromMaster.getY(), offsetFromMaster.getZ());

                if (!targetPos.equals(ignorePos)) {
                    BlockType setToType = form ? type : BlockType.SINGLE;
                    world.setBlock(targetPos, defaultBlockState().setValue(BLOCK_TYPE, setToType), 3);
                }
            }
        }

        if (!masterPos.equals(ignorePos)) {
            BlockType setToType = form ? BlockType.MASTER : BlockType.SINGLE;
            world.setBlock(masterPos, defaultBlockState().setValue(BLOCK_TYPE, setToType), 3);
        }
    }

    private BlockPos getMasterPosForNewMB(@Nonnull Level level, @Nonnull BlockPos selfPos) {
        // 1. Fall: Der platzierte Block ist das Zentrum (MASTER)
        if (isSingleOrAir(level, selfPos) &&
                isSingleOrAir(level, selfPos.north()) &&
                isSingleOrAir(level, selfPos.south()) &&
                isSingleOrAir(level, selfPos.east()) &&
                isSingleOrAir(level, selfPos.west())) {
            return selfPos;
        }

        // 2. Fall: Der platzierte Block ist einer der vier Arme (N, S, E, W)
        BlockPos[] candidateCenters = new BlockPos[] {
                selfPos.south(), // selfPos liegt im Norden vom Master
                selfPos.north(), // selfPos liegt im Süden vom Master
                selfPos.west(),  // selfPos liegt im Osten vom Master
                selfPos.east()   // selfPos liegt im Westen vom Master
        };

        for (BlockPos center : candidateCenters) {
            if (isSingleOrAir(level, center) &&
                    isSingleOrAir(level, center.north()) &&
                    isSingleOrAir(level, center.south()) &&
                    isSingleOrAir(level, center.east()) &&
                    isSingleOrAir(level, center.west())) {
                return center;
            }
        }

        return null;
    }

    private boolean isSingleOrAir(@Nonnull Level worldIn, @Nonnull BlockPos pos) {
        BlockState bs = worldIn.getBlockState(pos);
        if (bs.isAir()) {
            return true;
        }
        if (bs.getBlock() == this) {
            return bs.getValue(BLOCK_TYPE) == BlockType.SINGLE;
        }
        return false;
    }

    @Override
    public void destroy(@Nonnull LevelAccessor worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        if (state.getBlock() == this) {
            BlockType type = state.getValue(BLOCK_TYPE);
            if (type != BlockType.SINGLE) {
                BlockPos masterPos = type.getLocationOfMaster(pos);
                if (masterPos != null) {
                    updateMultiBlock((Level) worldIn, masterPos, pos, false);
                }
            }
        }
    }

    //
    // ENUM BLOCKTYPE (Plus-Form: Master + N, E, S, W)
    //
    public enum BlockType implements StringRepresentable {

        SINGLE(null),
        MASTER(new BlockPos(0, 0, 0)),
        N(reverseOffsetFor(Direction.NORTH)),
        E(reverseOffsetFor(Direction.EAST)),
        S(reverseOffsetFor(Direction.SOUTH)),
        W(reverseOffsetFor(Direction.WEST));

        private final BlockPos offsetToMaster;

        BlockType(BlockPos offsetToMaster) {
            this.offsetToMaster = offsetToMaster;
        }

        @Override
        public @Nonnull String getSerializedName() {
            return NullHelper.notnullJ(name().toLowerCase(Locale.US), "toLowerCase returned null!");
        }

        private static BlockPos reverseOffsetFor(Direction... dirs) {
            BlockPos res = new BlockPos(0, 0, 0);
            for (Direction dir : dirs) {
                res = res.relative(dir.getOpposite());
            }
            return res;
        }

        public BlockPos getOffsetToMaster() {
            return offsetToMaster;
        }

        public @Nonnull Vec3i getOffsetFromMaster() {
            if (offsetToMaster == null) {
                return new Vec3i(0, 0, 0);
            }
            return new BlockPos(-offsetToMaster.getX(), -offsetToMaster.getY(), -offsetToMaster.getZ());
        }

        public BlockPos getLocationOfMaster(@Nonnull BlockPos loc) {
            if (offsetToMaster == null) {
                return null;
            }
            return loc.offset(offsetToMaster.getX(), offsetToMaster.getY(), offsetToMaster.getZ());
        }

        public static @Nonnull BlockType getType(int meta) {
            if (meta < 0 || meta >= values().length) {
                return BlockType.SINGLE;
            }
            return NullHelper.notnullJ(values()[meta], "BlockType value is null!");
        }
    }
}
