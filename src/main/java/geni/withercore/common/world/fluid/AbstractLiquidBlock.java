package geni.withercore.common.world.fluid;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * AbstractLiquidBlock providing:
 * - isFlowAway: after 2x spreads, the block gradually dissipates downward in level and disappears.
 * - isFlowLevelAway: suppresses spread, block gradually loses level height and disappears.
 * - setLevel(level): updates BlockStateProperties.LEVEL directly.
 * - setPossibleFlowDirections(ImmutableList): restricts spreading to specific directions.
 * - Tank im FluidBlock: when harvested with a bucket, level drops to 7 (thinnest visible) and
 *   regenerates one level higher every 10 seconds (200 ticks) until back to level 0 (full source).
 */
public abstract class AbstractLiquidBlock extends LiquidBlock {

    protected final Supplier<? extends FlowingFluid> fluidSupplier;
    protected boolean isFlowAway = false;
    protected boolean isFlowLevelAway = false;
    protected ImmutableList<Direction> possibleFlowDirections = ImmutableList.copyOf(Direction.values());

    public AbstractLiquidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier.get(), properties);
        this.fluidSupplier = supplier;
    }

    /*public AbstractLiquidBlock(FlowingFluid fluid, BlockBehaviour.Properties properties, Supplier<? extends FlowingFluid> fluidSupplier) {
        super(fluid, properties);
        this.fluidSupplier = fluidSupplier;
    }*/

    public FlowingFluid getFluid() {
        return this.fluidSupplier.get();
    }

    public boolean isFlowAway() {
        return isFlowAway;
    }

    public void setFlowAway(boolean flowAway) {
        this.isFlowAway = flowAway;
    }

    public boolean isFlowLevelAway() {
        return isFlowLevelAway;
    }

    public void setFlowLevelAway(boolean flowLevelAway) {
        this.isFlowLevelAway = flowLevelAway;
    }

    public ImmutableList<Direction> getPossibleFlowDirections() {
        return possibleFlowDirections;
    }

    public void setPossibleFlowDirections(ImmutableList<Direction> directions) {
        this.possibleFlowDirections = directions;
    }

    /**
     * Updates the BlockStateProperties.LEVEL on the target block.
     * In Vanilla convention, 0 is full source and 7 is lowest visible flowing level.
     */
    public void setLevel(Level level, BlockPos pos, int levelValue) {
        int clamped = Math.max(0, Math.min(15, levelValue));
        BlockState currentState = level.getBlockState(pos);
        if (currentState.hasProperty(BlockStateProperties.LEVEL)) {
            level.setBlock(pos, currentState.setValue(BlockStateProperties.LEVEL, clamped), 3);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level.isClientSide) return;

        if (this.isFlowLevelAway) {
            LiquidBlockTickManager.registerLevelAway(level, pos, 60);
        } else if (this.isFlowAway) {
            LiquidBlockTickManager.registerFlowAway(level, pos);
        }
    }

    /**
     * Intercepts bucket interaction on the fluid block:
     * Drains the source without destroying the block, lowering height to LEVEL 7 (lowest visible level)
     * and scheduling a 10-second per level regeneration cycle (7 -> 6 -> ... -> 0).
     */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.BUCKET) && state.hasProperty(BlockStateProperties.LEVEL)) {
            int currentLevel = state.getValue(BlockStateProperties.LEVEL);

            // Kann nur eingesammelt werden, wenn es eine volle Quelle ist (LEVEL 0)
            if (currentLevel == 0) {
                if (!level.isClientSide) {
                    // Dem Spieler einen gefüllten Eimer geben
                    ItemStack filledBucket = new ItemStack(getFluid().getBucket());
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, filledBucket));

                    // Level auf die niedrigste sichtbare Stufe (LEVEL 7) setzen
                    setLevel(level, pos, 7);

                    // 10-Sekunden-Regeneration pro Level zurück zur Quelle (LEVEL 0) registrieren
                    LiquidBlockTickManager.registerRegeneration(level, pos, 7, 0, 200);

                    level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                // WICHTIG: Gib direkt SUCCESS zurück und rufe NIEMALS super.useItemOn auf,
                // da super den Block sonst komplett entfernt!
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        if (state.hasProperty(BlockStateProperties.LEVEL)) {
            int currentLevel = state.getValue(BlockStateProperties.LEVEL);

            // Kann nur eingesammelt werden, wenn es eine volle Quelle ist (LEVEL 0)
            if (currentLevel == 0) {
                if (level instanceof Level serverLevel && !serverLevel.isClientSide()) {
                    // Level auf die niedrigste sichtbare Stufe (LEVEL 7) setzen
                    setLevel(serverLevel, pos, 7);

                    // 10-Sekunden-Regeneration pro Level zurück zur Quelle (LEVEL 0) registrieren
                    LiquidBlockTickManager.registerRegeneration(serverLevel, pos, 7, 0, 200);

                    serverLevel.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                // Gibt den gefüllten Eimer zurück, aber löscht den Block NICHT (kein Luft-Austausch)
                return new ItemStack(getFluid().getBucket());
            }
        }
        return ItemStack.EMPTY;
    }
}