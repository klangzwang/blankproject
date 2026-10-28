package geni.witherutils.common.world.item;

import com.mojang.datafixers.util.Either;
import geni.withercore.init.WUTItems;
import geni.withercore.item.WitherItem;
import geni.withercore.network.CoreNetwork;
import geni.withercore.network.packet.RotateBlockPacket;
import geni.withercore.util.BlockEntityUtil;
import geni.withercore.util.ItemStackUtil;
import geni.withercore.util.SoundUtil;
import geni.witherutils.common.world.level.block.FloorSensorBlock;
import geni.witherutils.common.world.level.block.entity.FloorSensorBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Optional;

public class WrenchItem extends WitherItem {

    public WrenchItem(Item.Properties props) {
        super(props.stacksTo(1).durability(250));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    public static boolean isHoldingWrench(Player player) {
        for (ItemStack stack : player.getHandSlots()) {
            if (stack.getItem() == WUTItems.WRENCH.get()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        return super.useOn(context);
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack == null || newStack == null || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext pContext) {

        Player player = pContext.getPlayer();
        Level level = pContext.getLevel();
        BlockPos clickedPos = pContext.getClickedPos();

        if (player == null)
            return InteractionResult.PASS;

        if (BlockEntityUtil.getBlockEntity(FloorSensorBlockEntity.class, level, clickedPos) instanceof FloorSensorBlockEntity sensor)
        {
            if (!level.isClientSide) {
                if (sensor.cover != null) {
                    if (player.isCrouching())
                    {
                        sensor.removeCover();
                        level.setBlockAndUpdate(clickedPos, level.getBlockState(clickedPos).setValue(FloorSensorBlock.COVERED, false));

                        if (!player.isCreative())
                            ItemStackUtil.drop(level, clickedPos, new ItemStack(sensor.cover.getBlock().asItem()), 20, 0);

                        FloorSensorBlockEntity.sendBlockEntityToClients(sensor);

                        level.playSound(null, clickedPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 1, 1);
                        /*ParticleUtil.playParticleStarEffect(pLevel, pPlayer, ParticleTypes.SMOKE, 0.5D, ParticleUtil.EParticlePosition.HITRESULT);*/
                        SoundUtil.playSoundDistrib(level, clickedPos, SoundEvents.PLAYER_ATTACK_STRONG, 0.75f, 1.0F, false, true);
                    }
                    else
                    {
                        /*onItemUseRotate(level, sensor.cover, stack, pContext);*/
                    }
                }
            }
        }

        onItemUseRotate(level, level.getBlockState(pContext.getClickedPos()), stack, pContext);

        return super.onItemUseFirst(stack, pContext);
    }

    public InteractionResult onItemUseRotate(Level level, BlockState state, ItemStack stack, UseOnContext pContext) {

        CoreNetwork.sendToServer(new RotateBlockPacket(pContext.getClickedPos(), pContext.getClickedFace(), pContext.getHand()));

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        /*BlockState state = level.getBlockState(pContext.getClickedPos());*/
        Optional<Either<DirectionProperty, EnumProperty<Direction.Axis>>> property = getRotationProperty(state);
        if (property.isPresent()) {
            BlockState newState = getNextState(pContext, state, property.get());
            pContext.getLevel().setBlock(
                    pContext.getClickedPos(),
                    newState,
                    Block.UPDATE_NEIGHBORS + Block.UPDATE_CLIENTS);
            return InteractionResult.SUCCESS;
        }

        return super.onItemUseFirst(stack, pContext);
    }

    private static Optional<Either<DirectionProperty, EnumProperty<Direction.Axis>>> getRotationProperty(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (property instanceof DirectionProperty directionProperty && directionProperty.getName().equals("facing")) {
                return Optional.of(Either.left(directionProperty));
            }
            if (property instanceof EnumProperty enumProperty && enumProperty.getName().equals("axis") && enumProperty.getValueClass().equals(Direction.Axis.class)) {
                return Optional.of(Either.right(enumProperty));
            }
        }
        return Optional.empty();
    }

    private static BlockState getNextState(UseOnContext pContext, BlockState state, Either<DirectionProperty, EnumProperty<Direction.Axis>> property) {
        return handleProperties(pContext, state, property.left(), property.right());
    }

    private static BlockState handleProperties(UseOnContext pContext, BlockState state, Optional<DirectionProperty> directionProperty, Optional<EnumProperty<Direction.Axis>> axisProperty) {
        if (directionProperty.isPresent())
            return handleProperty(pContext, state, directionProperty.get());
        if (axisProperty.isPresent())
            return handleProperty(pContext, state, axisProperty.get());
        throw new IllegalArgumentException("At least one Optional should be set");
    }

    private static <T extends Comparable<T>> BlockState handleProperty(UseOnContext pContext, BlockState state, Property<T> property) {
        int noValidStateIndex = 0;
        do {
            state = getNextBlockState(state, property);
            noValidStateIndex++;
        } while (noValidStateIndex != property.getPossibleValues().size() && !state.canSurvive(pContext.getLevel(), pContext.getClickedPos()));
        return state;
    }

    private static <T extends Comparable<T>> BlockState getNextBlockState(BlockState currentState, Property<T> property) {
        return currentState.setValue(property, getNextValue(currentState.getValue(property), property));
    }

    private static <T extends Comparable<T>> T getNextValue(T value, Property<T> property) {
        boolean foundValid = false;
        for (T possibleValue : property.getPossibleValues()) {
            if (foundValid)
                return possibleValue;
            foundValid = possibleValue == value;
        }
        return property.getPossibleValues().iterator().next();
    }
}