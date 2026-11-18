package geni.withercore.common.menu;

import geni.withercore.common.menu.blockentity.slot.WitherMachineSlot;
import geni.withercore.common.menu.blockentity.slot.WitherMachineSlotGhost;
import geni.withercore.common.network.menu.ClientboundSyncSlotDataPacket;
import geni.withercore.common.network.menu.ClientboundSyncSlotDataPacket.PayloadPair;
import geni.withercore.common.network.menu.ContainerSyncData;
import geni.withercore.common.network.menu.ServerboundSetSyncSlotDataPacket;
import geni.withercore.common.network.menu.SyncSlot;
import geni.withercore.common.network.menu.SyncSlot.ChangeType;
import geni.withercore.common.network.menu.payload.SlotPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Gemeinsame Basisklasse für alle Wither-Menüs.
 * Konsolidiert die Sync-Slot-Logik, Transfer-Architektur und Ghost-Slot-Schutz.
 */
public abstract class WitherAbstractMenu extends AbstractContainerMenu {

    public final Inventory playerInventory;
    protected final List<Slot> playerInventorySlots = new ArrayList<>();
    protected boolean playerInvVisible = true;

    protected static final int PLAYER_INVENTORY_SIZE = 36;

    private final List<SyncSlot> syncSlots = new ArrayList<>();
    private final List<SyncSlot> clientUpdateSyncSlots = new ArrayList<>();

    @Nullable
    private geni.withercore.common.inventory.transfer.DelayedTransferProcess activeDelayedTransfer;

    protected WitherAbstractMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory) {
        super(menuType, containerId);
        this.playerInventory = playerInventory;
    }

    protected Inventory getPlayerInventory() {
        return playerInventory;
    }

    public boolean getPlayerInvVisible() {
        return playerInvVisible;
    }

    public boolean setPlayerInvVisible(boolean visible) {
        if (playerInvVisible != visible) {
            playerInvVisible = visible;
            int offset = playerInvVisible ? -1000 : 1000;
            for (Slot slot : playerInventorySlots) {
                slot.y += offset;
            }
        }
        return visible;
    }

    public void addPlayerInventorySlots(int left, int top) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                Slot slot = this.addSlot(new Slot(playerInventory, col + row * 9 + 9, left + col * 18, top + row * 18));
                playerInventorySlots.add(slot);
            }
        }
        int hotbarTop = top + 3 * 18 + 4;
        for (int col = 0; col < 9; ++col) {
            Slot slot = this.addSlot(new Slot(playerInventory, col, left + col * 18, hotbarTop));
            playerInventorySlots.add(slot);
        }
    }

    // Ghost-Slots vor versehentlicher PickAll / Drag Interaktion schützen
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

    /*
     *
     * TRANSFER-ARCHITEKTUR
     *
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        if (index < 0 || index >= this.slots.size()) {
            return itemstack;
        }
        Slot slot = this.slots.get(index);

        if (slot instanceof WitherMachineSlotGhost) {
            slot.set(ItemStack.EMPTY);
            return itemstack;
        }

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            int nonPlayerSlots = this.slots.size() - PLAYER_INVENTORY_SIZE;
            if (nonPlayerSlots > 0) {
                if (index < nonPlayerSlots) {
                    if (!this.moveItemStackTo(slotStack, nonPlayerSlots, this.slots.size(), true)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(slotStack, 0, nonPlayerSlots, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            slot.onTake(player, slotStack);
        }

        return itemstack;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) break;
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.slots.get(i);

                if (slot.isActive() && !(slot instanceof WitherMachineSlotGhost)) {
                    if (!(slot instanceof WitherMachineSlot machineSlot) || machineSlot.canQuickInsertStack()) {
                        ItemStack itemstack = slot.getItem();
                        if (!itemstack.isEmpty() && ItemStack.isSameItemSameComponents(stack, itemstack)) {
                            int j = itemstack.getCount() + stack.getCount();
                            int maxSize = Math.min(slot.getMaxStackSize(stack), stack.getMaxStackSize());
                            if (j <= maxSize) {
                                stack.setCount(0);
                                itemstack.setCount(j);
                                slot.set(itemstack);
                                flag = true;
                            } else if (itemstack.getCount() < maxSize) {
                                stack.shrink(maxSize - itemstack.getCount());
                                itemstack.setCount(maxSize);
                                slot.set(itemstack);
                                flag = true;
                            }
                        }
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) break;
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.slots.get(i);
                if (slot.isActive() && !(slot instanceof WitherMachineSlotGhost)) {
                    if (!(slot instanceof WitherMachineSlot machineSlot) || machineSlot.canQuickInsertStack()) {
                        ItemStack itemstack = slot.getItem();
                        if (itemstack.isEmpty() && slot.mayPlace(stack)) {
                            int limit = slot.getMaxStackSize(stack);
                            slot.setByPlayer(stack.split(Math.min(stack.getCount(), limit)));
                            slot.setChanged();
                            flag = true;
                            break;
                        }
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    public boolean dropSlotToWorld(Player player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= this.slots.size()) return false;
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasItem() || !slot.mayPickup(player)) return false;

        ItemStack stack = slot.getItem();
        if (stack.isEmpty()) return false;

        if (player instanceof ServerPlayer sp) {
            ItemStack removed = slot.remove(stack.getCount());
            if (!removed.isEmpty()) {
                sp.drop(removed, true);
                slot.setChanged();
                return true;
            }
        }
        return false;
    }

    public boolean trashSlotItem(Player player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= this.slots.size()) return false;
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasItem() || !slot.mayPickup(player)) return false;

        if (player instanceof ServerPlayer) {
            slot.set(ItemStack.EMPTY);
            slot.setChanged();
            return true;
        }
        return false;
    }

    /*
     *
     * SYNC SLOTS & NETWORK
     *
     */
    protected <S extends SyncSlot> S addSyncSlot(S syncSlot) {
        syncSlots.add(syncSlot);
        return syncSlot;
    }

    protected <S extends SyncSlot> S addUpdatableSyncSlot(S syncSlot) {
        syncSlots.add(syncSlot);
        clientUpdateSyncSlots.add(syncSlot);
        return syncSlot;
    }

    protected <S extends ContainerSyncData> S addContainerSyncData(S syncData) {
        syncSlots.addAll(syncData.syncSlots());
        clientUpdateSyncSlots.addAll(syncData.updatableSyncSlots());
        return syncData;
    }

    public <T extends net.minecraft.world.item.crafting.Recipe<?>> geni.withercore.common.network.menu.RecipeSyncSlot<T> addRecipeSyncSlot(
            net.minecraft.world.item.crafting.RecipeType<T> recipeType,
            java.util.function.Supplier<net.minecraft.world.item.crafting.RecipeHolder<T>> getter,
            java.util.function.Consumer<net.minecraft.world.item.crafting.RecipeHolder<T>> setter) {
        return addSyncSlot(geni.withercore.common.network.menu.RecipeSyncSlot.simple(recipeType, getter, setter));
    }

    protected void updateSlot(SyncSlot syncSlot) {
        if (!clientUpdateSyncSlots.contains(syncSlot)) {
            throw new IllegalArgumentException("This slot is not client updatable!");
        }

        if (playerInventory.player instanceof LocalPlayer) {
            short slotIndex = (short) clientUpdateSyncSlots.indexOf(syncSlot);
            ChangeType changeType = syncSlot.detectChanges();

            if (changeType != ChangeType.NONE) {
                var payload = syncSlot.createPayload(playerInventory.player.level(), ChangeType.FULL);
                PacketDistributor.sendToServer(new ServerboundSetSyncSlotDataPacket(containerId, slotIndex, payload));
            }
        }
    }

    public void clientHandleIncomingPayload(short slotIndex, SlotPayload payload) {
        if (slotIndex >= 0 && slotIndex < syncSlots.size()) {
            var slot = syncSlots.get(slotIndex);
            slot.unpackPayload(playerInventory.player.level(), payload);
        }
    }

    public void serverHandleIncomingPayload(short slotIndex, SlotPayload payload) {
        if (slotIndex >= 0 && slotIndex < clientUpdateSyncSlots.size()) {
            var slot = clientUpdateSyncSlots.get(slotIndex);
            slot.unpackPayload(playerInventory.player.level(), payload);
        }
    }

    public void startDelayedTransfer(int fromSlot, int toSlot, int amount, int ticks) {
        this.activeDelayedTransfer = new geni.withercore.common.inventory.transfer.DelayedTransferProcess(fromSlot, toSlot, amount, ticks);
    }

    public void cancelDelayedTransfer() {
        if (this.activeDelayedTransfer != null) {
            this.activeDelayedTransfer.abort();
            this.activeDelayedTransfer = null;
        }
    }

    @Nullable
    public geni.withercore.common.inventory.transfer.DelayedTransferProcess getActiveDelayedTransfer() {
        return activeDelayedTransfer;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (playerInventory.player instanceof ServerPlayer player) {
            if (activeDelayedTransfer != null) {
                if (activeDelayedTransfer.tick(this, player)) {
                    activeDelayedTransfer = null;
                }
            }

            List<PayloadPair> payloads = new ArrayList<>();
            Level level = player.level();

            for (short i = 0; i < syncSlots.size(); i++) {
                var slot = syncSlots.get(i);
                ChangeType changeType = slot.detectChanges();
                if (changeType != ChangeType.NONE) {
                    var payload = slot.createPayload(level, ChangeType.FULL);
                    payloads.add(new PayloadPair(i, payload));
                }
            }

            if (!payloads.isEmpty()) {
                PacketDistributor.sendToPlayer(player, new ClientboundSyncSlotDataPacket(containerId, payloads));
            }
        }
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();

        if (playerInventory.player instanceof ServerPlayer player) {
            List<PayloadPair> payloads = new ArrayList<>();
            Level level = player.level();

            for (short i = 0; i < syncSlots.size(); i++) {
                var slot = syncSlots.get(i);

                slot.detectChanges();

                var payload = slot.createPayload(level, ChangeType.FULL);
                payloads.add(new PayloadPair(i, payload));
            }

            if (!payloads.isEmpty()) {
                PacketDistributor.sendToPlayer(player, new ClientboundSyncSlotDataPacket(containerId, payloads));
            }
        }
    }
}
