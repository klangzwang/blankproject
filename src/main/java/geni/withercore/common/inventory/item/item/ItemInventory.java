package geni.withercore.common.inventory.item.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntConsumer;

public class ItemInventory implements IItemHandlerModifiable {

    protected final ItemStack container;
    protected final ItemInventoryLayout layout;
    protected final NonNullList<ItemStack> stacks;
    private IntConsumer changeListener = i -> {};

    public ItemInventory(ItemStack container, ItemInventoryLayout layout) {
        this.container = container;
        this.layout = layout;
        this.stacks = NonNullList.withSize(layout.getSlotCount(), ItemStack.EMPTY);
        loadFromItem();
    }

    public ItemInventory(ItemStack container, int slotCount) {
        this(container, ItemInventoryLayout.builder().storageSlot(slotCount).build());
    }

    public void addSlotChangedCallback(IntConsumer callback) {
        changeListener = changeListener.andThen(callback);
    }

    public final ItemInventoryLayout getLayout() {
        return layout;
    }

    public final ItemStack getContainer() {
        return container;
    }

    protected void loadFromItem() {
        ItemContainerContents contents = container.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(this.stacks);
    }

    protected void saveToItem() {
        container.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.stacks));
    }

    protected void onContentsChanged(int slot) {
        saveToItem();
        this.changeListener.accept(slot);
    }

    @Override
    public int getSlots() {
        return stacks.size();
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return stacks.get(slot);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        validateSlotIndex(slot);
        stacks.set(slot, stack);
        onContentsChanged(slot);
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        validateSlotIndex(slot);

        if (!layout.canInsert(slot)) {
            return stack;
        }

        if (!isItemValid(slot, stack)) {
            return stack;
        }

        ItemStack existing = this.stacks.get(slot);
        int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(stack, existing)) {
                return stack;
            }
            limit -= existing.getCount();
        }

        if (limit <= 0) {
            return stack;
        }

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.stacks.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        validateSlotIndex(slot);

        if (!layout.canExtract(slot)) {
            return ItemStack.EMPTY;
        }

        ItemStack existing = this.stacks.get(slot);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.stacks.set(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.stacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                onContentsChanged(slot);
            }
            return existing.copyWithCount(toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return layout.getStackLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return layout.isItemValid(slot, stack);
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.size()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
        }
    }
}
