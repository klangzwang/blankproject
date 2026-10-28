package geni.withercore.common.world.item;

import geni.withercore.common.inventory.item.energy.ItemEnergyStorage;
import geni.withercore.common.inventory.item.fluid.ItemFluidTank;
import geni.withercore.common.inventory.item.item.ItemInventory;
import geni.withercore.common.inventory.item.item.ItemInventoryLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WitherInventoryItem extends WitherItem implements MenuProvider {

    public WitherInventoryItem(Item.Properties props) {
        super(props);
    }

    /*
     * 
     * CAPABILITIES
     * 
     */
    public boolean hasItemCapability(ItemStack stack) {
        return true;
    }

    public boolean hasFluidCapability(ItemStack stack) {
        return false;
    }

    public boolean hasEnergyCapability(ItemStack stack) {
        return false;
    }

    public IItemHandler getItemHandler(ItemStack stack) {
        return new ItemInventory(stack, getInventoryLayout(stack));
    }

    public IFluidHandlerItem getFluidHandler(ItemStack stack) {
        return new ItemFluidTank(stack, getFluidCapacity(stack));
    }

    public IEnergyStorage getEnergyHandler(ItemStack stack) {
        return new ItemEnergyStorage(stack, getEnergyCapacity(stack), getEnergyTransfer(stack));
    }

    /*
     * 
     * CONFIG / LAYOUT
     * 
     */
    public ItemInventoryLayout getInventoryLayout(ItemStack stack) {
        return ItemInventoryLayout.builder().storageSlot(9).build();
    }

    public int getFluidCapacity(ItemStack stack) {
        return 4000;
    }

    public int getEnergyCapacity(ItemStack stack) {
        return 100000;
    }

    public int getEnergyTransfer(ItemStack stack) {
        return 1000;
    }

    /*
     * 
     * MENU
     * 
     */
    public boolean canOpenMenu(ItemStack stack, Player player) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (canOpenMenu(stack, player)) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(this, buf -> ItemStack.STREAM_CODEC.encode(buf, stack));
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return super.use(level, player, hand);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        if (hasEnergyCapability(stack)) {
            IEnergyStorage energy = getEnergyHandler(stack);
            tooltip.add(Component.translatable("tooltip.witherutils.energy",
                    String.format("%,d", energy.getEnergyStored()),
                    String.format("%,d", energy.getMaxEnergyStored())));
        }

        if (hasFluidCapability(stack)) {
            IFluidHandlerItem fluid = getFluidHandler(stack);
            if (!fluid.getFluidInTank(0).isEmpty()) {
                tooltip.add(Component.translatable("tooltip.witherutils.fluid",
                        fluid.getFluidInTank(0).getHoverName().getString(),
                        fluid.getFluidInTank(0).getAmount(),
                        fluid.getTankCapacity(0)));
            }
        }
    }
}
