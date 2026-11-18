package geni.withercore.common.world.level.block;

import geni.witherapi.block.IBlock;
import geni.withercore.client.tooltip.Tooltips;
import geni.withercore.common.world.level.block.item.ItemBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;
import java.util.List;

public class WitherBlock extends Block implements IBlock {

	public WitherBlock(Properties props)
    {
        super(props);
        BlockState state = getStateDefinition().any();
        registerDefaultState(state);
	}

    @Override
    public ItemBlock<?> getBlockItem(Item.Properties properties)
    {
        return new ItemBlock<>(this, properties);
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

    //
    // PARTICLE STEPON FALLON
    //
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {

        if (entity instanceof Player player) {

            double dx = player.getX() - player.xo;
            double dz = player.getZ() - player.zo;
            double movementSq = dx * dx + dz * dz;

            if (movementSq > 0.0001D && level.isClientSide()) {

                ParticleOptions particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
                level.addParticle(
                        particle,
                        player.getX() + (level.random.nextDouble() - 0.5) * 0.3,
                        pos.getY() + 1.01D,
                        player.getZ() + (level.random.nextDouble() - 0.5) * 0.3,
                        0.0D, 0.02D, 0.0D
                );
            }
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide() && entity instanceof Player) {
            SoundType soundtype = state.getSoundType(level, pos, entity);
            level.playSound(
                    null,
                    pos,
                    soundtype.getStepSound(),
                    SoundSource.BLOCKS,
                    (soundtype.getVolume() + 1.0F) / 2.0F,
                    soundtype.getPitch() * 0.8F
            );
        }
        super.fallOn(level, state, pos, entity, fallDistance);
    }

    private ParticleOptions getParticleForMapColor(MapColor mapColor) {
        if (mapColor == MapColor.COLOR_GRAY) {
            return ParticleTypes.DUST_PLUME;
        } else if (mapColor == MapColor.DIRT) {
            return ParticleTypes.WHITE_SMOKE;
        } else if (mapColor == MapColor.ICE) {
            return ParticleTypes.DUST_PLUME;
        } else if (mapColor == MapColor.METAL) {
            return ParticleTypes.ELECTRIC_SPARK;
        } else if (mapColor == MapColor.STONE) {
            return ParticleTypes.DUST_PLUME;
        } else if (mapColor == MapColor.WOOD) {
            return ParticleTypes.INFESTED;
        }
        return null;
    }
}
