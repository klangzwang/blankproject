package geni.withercore.item.block;

import geni.withercore.attachments.IAttachmentAware;
import geni.withercore.capabilities.ICapabilityAware;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.LongSupplier;

public class ItemBlockTooltip<BLOCK extends Block> extends ItemBlockWither<BLOCK> implements ICapabilityAware, IAttachmentAware {

    private final boolean hasDetails;

    public ItemBlockTooltip(BLOCK block, Item.Properties properties) {
        this(block, false, properties);
    }

    public ItemBlockTooltip(BLOCK block, boolean hasDetails, Properties properties) {
        super(block, properties);
        this.hasDetails = hasDetails;
    }

    @Override
    public void onDestroyed(@NotNull ItemEntity item, @NotNull DamageSource damageSource) {
        /*InventoryUtils.dropItemContents(item, damageSource);*/
    }

    @Override
    public boolean placeBlock(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
        return super.placeBlock(context, state);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
    }

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        /*if (exposesEnergyCapOrTooltips()) {
            return slotChanged || oldStack.getItem() != newStack.getItem();
        }*/
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public boolean shouldCauseBlockBreakReset(@NotNull ItemStack oldStack, @NotNull ItemStack newStack) {
        /*if (exposesEnergyCapOrTooltips()) {
            return oldStack.getItem() != newStack.getItem();
        }*/
        return super.shouldCauseBlockBreakReset(oldStack, newStack);
    }

    @Override
    public void attachCapabilities(RegisterCapabilitiesEvent event) {
    }

    @Override
    public void attachAttachments(IEventBus eventBus) {
    }
}
