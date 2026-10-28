package geni.withercore.common.inventory.blockentity.energy;

import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.ref.WeakReference;

public class SmartSyncEnergy extends MachineEnergyStorage {

    private int syncedEnergyDesc = 0;
    private int syncedEnergyGui = 0;

    private boolean pending = false;
    private int syncTimer = 0;
    private final WeakReference<BlockEntity> owner;
    private final int threshold;

    public SmartSyncEnergy(BlockEntity owner, int capacity) {
        this(owner, capacity, capacity, capacity);
    }

    public SmartSyncEnergy(BlockEntity owner, int capacity, int maxTransfer) {
        this(owner, capacity, maxTransfer, maxTransfer);
    }

    public SmartSyncEnergy(BlockEntity owner, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.owner = new WeakReference<>(owner);
        this.threshold = Math.max(1, Math.min(1000, capacity / 100));
    }

    /**
     * Call from holding BlockEntity serverTick/clientTick
     */
    public void tick() {
        BlockEntity te = owner.get();
        if (te != null && te.getLevel() != null) {
            if (!te.getLevel().isClientSide) {
                if (syncTimer > 0) {
                    syncTimer--;
                } else if (syncTimer == 0) {
                    if (pending) {
                        syncedEnergyDesc = getEnergyStored();
                        pending = false;
                        te.setChanged();
                    } else {
                        syncTimer = -1;
                    }
                }
            }
        }
    }

    private void deferredSync(int ticks) {
        if (syncTimer == -1) {
            syncedEnergyDesc = getEnergyStored();
            syncTimer = ticks;
        } else {
            pending = true;
        }
    }

    private void onEnergyChange(int newEnergy) {
        syncedEnergyGui = newEnergy;

        int delta = Math.abs(syncedEnergyDesc - newEnergy);
        if (delta >= threshold) {
            deferredSync(5);
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (received != 0 && !simulate) {
            onEnergyChange(getEnergyStored());
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (extracted != 0 && !simulate) {
            onEnergyChange(getEnergyStored());
        }
        return extracted;
    }

    @Override
    public void setEnergy(int energy) {
        super.setEnergy(energy);
        onEnergyChange(getEnergyStored());
    }

    public int getSyncedEnergyDesc() {
        return syncedEnergyDesc;
    }

    public int getSyncedEnergyGui() {
        return syncedEnergyGui;
    }
}
