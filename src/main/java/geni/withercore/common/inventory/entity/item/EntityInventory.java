package geni.withercore.common.inventory.entity.item;

import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntConsumer;

public class EntityInventory extends ItemStackHandler {

    private final EntityInventoryLayout layout;
    @Nullable
    private final LivingEntity owner;
    private IntConsumer changeListener = i -> {};

    public EntityInventory(EntityInventoryLayout layout) {
        this(null, layout);
    }

    public EntityInventory(@Nullable LivingEntity owner, EntityInventoryLayout layout) {
        super(layout.getSlotCount());
        this.owner = owner;
        this.layout = layout;
    }

    public void addSlotChangedCallback(IntConsumer callback) {
        changeListener = changeListener.andThen(callback);
    }

    public final EntityInventoryLayout getLayout() {
        return layout;
    }

    @Nullable
    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return layout.isItemValid(slot, stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return layout.getStackLimit(slot);
    }

    @Nullable
    public IItemHandler getForSide(@Nullable Direction side) {
        return new Wrapped(this, side);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        boolean wasEmpty = !simulate && getStackInSlot(slot).isEmpty();
        ItemStack itemStack = super.insertItem(slot, stack, simulate);
        if (wasEmpty && itemStack.getCount() != stack.getCount()) {
            changeListener.accept(slot);
        }
        return itemStack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack itemStack = super.extractItem(slot, amount, simulate);
        if (!itemStack.isEmpty() && !simulate && getStackInSlot(slot).isEmpty()) {
            changeListener.accept(slot);
        }
        return itemStack;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        boolean changed = stack.getItem() != getStackInSlot(slot).getItem();
        super.setStackInSlot(slot, stack);
        if (changed) {
            this.changeListener.accept(slot);
        }
    }

    public void dropAll(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide) {
            return;
        }

        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(entity.level(), entity.getX(), entity.getY(), entity.getZ(), stack);
                setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    private record Wrapped(EntityInventory master, @Nullable Direction side) implements IItemHandler {

        @Override
        public int getSlots() {
            return master.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return master.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!master.getLayout().canInsert(slot)) {
                return stack;
            }
            return master.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!master.getLayout().canExtract(slot)) {
                return ItemStack.EMPTY;
            }
            return master.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return master.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return master.isItemValid(slot, stack);
        }
    }
}
