package geni.withercore.common.menu.item;

import geni.withercore.common.menu.entity.slot.WitherMachineSlotGhost;
import geni.withercore.common.world.item.WitherItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class WitherContainerItemMenu<T extends WitherItem> extends WitherItemMenu<T> {

    protected WitherContainerItemMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, @Nullable T itemInstance, ItemStack itemStack) {
        super(menuType, containerId, inventory, itemInstance, itemStack);
    }

    public IItemHandler getItemInventory() {
        IItemHandler handler = itemStack.getCapability(Capabilities.ItemHandler.ITEM);
        if (handler == null) {
            throw new IllegalStateException("Item does not have an item handler capability.");
        }
        return handler;
    }

    @Override
    public boolean stillValid(Player player) {
        // Prüft, ob das Item noch im Inventar/Hand des Spielers existiert und nicht leer ist
        return !itemStack.isEmpty() && (player.getMainHandItem() == itemStack || player.getOffhandItem() == itemStack || player.getInventory().contains(itemStack));
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        if (slot instanceof WitherMachineSlotGhost) {
            return false;
        }
        return super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public boolean canDragTo(Slot slot) {
        if (slot instanceof WitherMachineSlotGhost) {
            return false;
        }
        return super.canDragTo(slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot instanceof WitherMachineSlotGhost) {
            slot.set(ItemStack.EMPTY);
            return itemstack;
        }

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.slots.size() - PLAYER_INVENTORY_SIZE) {
                if (!this.moveItemStackTo(itemstack1, this.slots.size() - PLAYER_INVENTORY_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.slots.size() - PLAYER_INVENTORY_SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }
}