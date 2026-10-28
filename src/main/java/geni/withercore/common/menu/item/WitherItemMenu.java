package geni.withercore.common.menu.item;

import geni.withercore.common.network.menu.ClientboundSyncSlotDataPacket;
import geni.withercore.common.network.menu.ClientboundSyncSlotDataPacket.PayloadPair;
import geni.withercore.common.network.menu.ContainerSyncData;
import geni.withercore.common.network.menu.ServerboundSetSyncSlotDataPacket;
import geni.withercore.common.network.menu.SyncSlot;
import geni.withercore.common.network.menu.SyncSlot.ChangeType;
import geni.withercore.common.network.menu.payload.SlotPayload;
import geni.withercore.common.world.item.WitherItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class WitherItemMenu<T extends WitherItem> extends AbstractContainerMenu {
    
    @Nullable
    private final T itemInstance;
    public final ItemStack itemStack;
    public final Inventory playerInventory;
    private final List<Slot> playerInventorySlots = new ArrayList<>();
    private boolean playerInvVisible = true;

    protected static final int PLAYER_INVENTORY_SIZE = 36;

    protected WitherItemMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, @Nullable T itemInstance, ItemStack itemStack) {
        super(menuType, containerId);
        this.playerInventory = playerInventory;
        this.itemInstance = itemInstance;
        this.itemStack = itemStack;
    }

    public Inventory getPlayerInventory() {
        return playerInventory;
    }
    
    @Nullable
    public T getItemInstance() {
        return itemInstance;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean getPlayerInvVisible() {
        return playerInvVisible;
    }
    
    public boolean setPlayerInvVisible(boolean visible) {
        if (playerInvVisible != visible) {
            playerInvVisible = visible;
            int offset = playerInvVisible ? 1000 : -1000;
            for (int i = 0; i < 9; i++) {
                playerInventorySlots.get(i).y += offset;
            }
        }
        return visible;
    }
    
    private final List<SyncSlot> syncSlots = new ArrayList<>();
    private final List<SyncSlot> clientUpdateSyncSlots = new ArrayList<>();

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

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (playerInventory.player instanceof ServerPlayer player) {
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