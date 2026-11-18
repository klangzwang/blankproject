package geni.withercore.common.inventory.transfer;

import geni.withercore.common.menu.WitherAbstractMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Verwaltet einen serverseitig tickbasierten verzögerten Transferprozess (Abschnitt 3.3).
 * Verhindert Item-Verlust bei Abbruch und validiert Mengen und Slots beim Abschluss erneut.
 */
public class DelayedTransferProcess {

    private final int totalTicks;
    private int currentTicks = 0;
    private final int sourceSlotIndex;
    private final int targetSlotIndex;
    private final int amount;
    private boolean completed = false;
    private boolean aborted = false;

    public DelayedTransferProcess(int sourceSlotIndex, int targetSlotIndex, int amount, int totalTicks) {
        this.sourceSlotIndex = sourceSlotIndex;
        this.targetSlotIndex = targetSlotIndex;
        this.amount = amount;
        this.totalTicks = Math.max(1, totalTicks);
    }

    public float getProgress() {
        return Math.min(1.0f, (float) currentTicks / (float) totalTicks);
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isAborted() {
        return aborted;
    }

    public void abort() {
        this.aborted = true;
    }

    /**
     * Ticked den Transferprozess auf dem Server.
     * @return true, wenn der Prozess beendet (erfolgreich oder abgebrochen) ist.
     */
    public boolean tick(WitherAbstractMenu menu, ServerPlayer player) {
        if (aborted || completed) {
            return true;
        }

        if (sourceSlotIndex < 0 || sourceSlotIndex >= menu.slots.size() ||
            targetSlotIndex < 0 || targetSlotIndex >= menu.slots.size()) {
            abort();
            return true;
        }

        Slot sourceSlot = menu.slots.get(sourceSlotIndex);
        Slot targetSlot = menu.slots.get(targetSlotIndex);

        if (!sourceSlot.hasItem() || !sourceSlot.mayPickup(player)) {
            abort();
            return true;
        }

        currentTicks++;

        if (currentTicks >= totalTicks) {
            executeTransfer(sourceSlot, targetSlot);
            completed = true;
            return true;
        }

        return false;
    }

    private void executeTransfer(Slot sourceSlot, Slot targetSlot) {
        ItemStack sourceStack = sourceSlot.getItem();
        if (sourceStack.isEmpty()) {
            abort();
            return;
        }

        int toTransfer = Math.min(amount, sourceStack.getCount());
        ItemStack targetStack = targetSlot.getItem();

        if (targetStack.isEmpty()) {
            if (targetSlot.mayPlace(sourceStack)) {
                ItemStack moved = sourceSlot.remove(toTransfer);
                targetSlot.set(moved);
                targetSlot.setChanged();
                sourceSlot.setChanged();
            }
        } else if (ItemStack.isSameItemSameComponents(sourceStack, targetStack)) {
            int space = Math.min(targetSlot.getMaxStackSize(targetStack), targetStack.getMaxStackSize()) - targetStack.getCount();
            int actualMove = Math.min(toTransfer, space);
            if (actualMove > 0) {
                sourceSlot.remove(actualMove);
                targetStack.grow(actualMove);
                targetSlot.setChanged();
                sourceSlot.setChanged();
            }
        }
    }
}

