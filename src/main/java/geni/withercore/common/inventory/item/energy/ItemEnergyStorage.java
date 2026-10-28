package geni.withercore.common.inventory.item.energy;

import geni.withercore.init.WUTComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class ItemEnergyStorage implements IEnergyStorage {

    protected final ItemStack container;
    protected final int capacity;
    protected final int maxReceive;
    protected final int maxExtract;

    public ItemEnergyStorage(ItemStack container, int capacity) {
        this(container, capacity, capacity, capacity);
    }

    public ItemEnergyStorage(ItemStack container, int capacity, int maxTransfer) {
        this(container, capacity, maxTransfer, maxTransfer);
    }

    public ItemEnergyStorage(ItemStack container, int capacity, int maxReceive, int maxExtract) {
        this.container = container;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    public ItemStack getContainer() {
        return container;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive() || maxReceive <= 0 || container.getCount() != 1) {
            return 0;
        }

        int energy = getEnergyStored();
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate && energyReceived > 0) {
            setEnergyStored(energy + energyReceived);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract() || maxExtract <= 0 || container.getCount() != 1) {
            return 0;
        }

        int energy = getEnergyStored();
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate && energyExtracted > 0) {
            setEnergyStored(energy - energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return container.getOrDefault(WUTComponents.ENERGY.get(), 0);
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    public void setEnergyStored(int energy) {
        int clamped = Math.max(0, Math.min(energy, capacity));
        container.set(WUTComponents.ENERGY.get(), clamped);
        container.set(WUTComponents.HAS_ENERGY.get(), clamped > 0);
    }
}
