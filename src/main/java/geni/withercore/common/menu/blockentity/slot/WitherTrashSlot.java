package geni.withercore.common.menu.blockentity.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * Ein spezialisierter Slot zum Löschen von Items (TrashSlot).
 * Unterstützt sofortiges Löschen oder Undo-Modus zur temporären Aufbewahrung des letzten Items.
 */
public class WitherTrashSlot extends WitherMachineSlot {

    public enum TrashMode {
        IMMEDIATE,
        UNDO
    }

    private TrashMode mode = TrashMode.IMMEDIATE;
    private ItemStack lastTrashed = ItemStack.EMPTY;

    public WitherTrashSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public WitherTrashSlot(int xPosition, int yPosition) {
        super(new ItemStackHandler(1), 0, xPosition, yPosition);
    }

    public WitherTrashSlot setTrashMode(TrashMode mode) {
        this.mode = mode;
        return this;
    }

    public TrashMode getTrashMode() {
        return mode;
    }

    public ItemStack getLastTrashed() {
        return lastTrashed;
    }

    public void clearUndo() {
        this.lastTrashed = ItemStack.EMPTY;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !isLocked() && isActive();
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return mode == TrashMode.UNDO && !lastTrashed.isEmpty() && !isLocked() && isActive();
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int amount) {
        if (!stack.isEmpty() && mayPlace(stack)) {
            int toTrash = Math.min(stack.getCount(), amount);
            lastTrashed = stack.copyWithCount(toTrash);
            stack.shrink(toTrash);
            setChanged();
        }
        return stack;
    }

    @Override
    public ItemStack remove(int amount) {
        if (mode == TrashMode.UNDO && !lastTrashed.isEmpty()) {
            ItemStack restored = lastTrashed.copy();
            lastTrashed = ItemStack.EMPTY;
            setChanged();
            return restored;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getItem() {
        if (mode == TrashMode.UNDO) {
            return lastTrashed;
        }
        return ItemStack.EMPTY;
    }
}

