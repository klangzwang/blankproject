package geni.withercore.common.inventory.item.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;

public class FakeSlotChestWrapper implements IItemHandler {
    private final IItemHandler realHandler;

    public FakeSlotChestWrapper(IItemHandler realHandler) {
        this.realHandler = realHandler;
    }

    @Override
    public int getSlots() {
        return realHandler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == 0) {
            // Täuscht dem Kabel vor, Slot 0 sei voll (z.B. mit 64 Stein)
            return new ItemStack(Items.STONE, 64);
        }
        return realHandler.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot == 0) {
            // Verhindert jegliches Einfügen in Slot 0
            return stack; 
        }
        // Ab Slot 1 wandern die Items direkt in die echte Kiste
        return realHandler.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == 0) return ItemStack.EMPTY;
        return realHandler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return realHandler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot == 0) return false;
        return realHandler.isItemValid(slot, stack);
    }
}