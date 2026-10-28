package geni.withercore.common.menu.entity;

import geni.withercore.common.network.menu.ClientboundSyncSlotDataPacket;
import geni.withercore.common.network.menu.ClientboundSyncSlotDataPacket.PayloadPair;
import geni.withercore.common.network.menu.ContainerSyncData;
import geni.withercore.common.network.menu.ServerboundSetSyncSlotDataPacket;
import geni.withercore.common.network.menu.SyncSlot;
import geni.withercore.common.network.menu.SyncSlot.ChangeType;
import geni.withercore.common.network.menu.payload.SlotPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class WitherMenu<T extends BlockEntity> extends AbstractContainerMenu {
	
    @Nullable
    private final T blockEntity;
    public final Inventory playerInventory;
    private final List<Slot> playerInventorySlots = new ArrayList<>();
    private boolean playerInvVisible = true;

    protected static final int PLAYER_INVENTORY_SIZE = 36;
    @SuppressWarnings("unused")
	private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

    protected WitherMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, @Nullable T blockEntity)
    {
        super(menuType, containerId);
        this.playerInventory = playerInventory;
        this.blockEntity = blockEntity;
    }

    protected Inventory getPlayerInventory()
    {
        return playerInventory;
    }
    
    @Nullable
    public T getBlockEntity()
    {
        return blockEntity;
    }

    public boolean getPlayerInvVisible()
    {
        return playerInvVisible;
    }
    
    public boolean setPlayerInvVisible(boolean visible)
    {
        if (playerInvVisible != visible)
        {
            playerInvVisible = visible;
            int offset = playerInvVisible ? 1000 : -1000;
            for (int i = 0; i < 9; i++)
            {
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
        } else {
        }
    }

    public void serverHandleIncomingPayload(short slotIndex, SlotPayload payload) {
        if (slotIndex >= 0 && slotIndex < clientUpdateSyncSlots.size()) {
            var slot = clientUpdateSyncSlots.get(slotIndex);
            slot.unpackPayload(playerInventory.player.level(), payload);
        } else {
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

                // Initialize the change detectors, we're sending all data no matter what.
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
