package geni.withercore.common.inventory.blockentity.energy;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class MachineEnergyStorage implements IEnergyStorage {

    protected int energy;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    protected boolean canReceive = true;
    protected boolean canExtract = true;

    protected Runnable changeListener = () -> {};

    public MachineEnergyStorage(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public MachineEnergyStorage(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public MachineEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public MachineEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        this.capacity = Math.max(0, capacity);
        this.maxReceive = Math.max(0, maxReceive);
        this.maxExtract = Math.max(0, maxExtract);
        this.energy = Math.max(0, Math.min(capacity, energy));
    }

    public void addEnergyChangedCallback(Runnable callback) {
        Runnable prev = this.changeListener;
        this.changeListener = () -> {
            prev.run();
            callback.run();
        };
    }

    protected void onContentsChanged() {
        this.changeListener.run();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive() || maxReceive <= 0) {
            return 0;
        }

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate && energyReceived > 0) {
            energy += energyReceived;
            onContentsChanged();
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract() || maxExtract <= 0) {
            return 0;
        }

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate && energyExtracted > 0) {
            energy -= energyExtracted;
            onContentsChanged();
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return canExtract && this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return canReceive && this.maxReceive > 0;
    }

    public void setCanReceive(boolean canReceive) {
        this.canReceive = canReceive;
    }

    public void setCanExtract(boolean canExtract) {
        this.canExtract = canExtract;
    }

    public void setCapacity(int capacity) {
        this.capacity = Math.max(0, capacity);
        if (this.energy > this.capacity) {
            this.energy = this.capacity;
            onContentsChanged();
        }
    }

    public void setMaxReceive(int maxReceive) {
        this.maxReceive = Math.max(0, maxReceive);
    }

    public void setMaxExtract(int maxExtract) {
        this.maxExtract = Math.max(0, maxExtract);
    }

    public void setEnergy(int energy) {
        int clamped = Math.max(0, Math.min(energy, capacity));
        if (this.energy != clamped) {
            this.energy = clamped;
            onContentsChanged();
        }
    }

    public int consumeEnergy(int amount, boolean simulate) {
        if (amount <= 0) return 0;
        int consumed = Math.min(this.energy, amount);
        if (!simulate && consumed > 0) {
            this.energy -= consumed;
            onContentsChanged();
        }
        return consumed;
    }

    @Nullable
    public IEnergyStorage getForSide(@Nullable Direction side) {
        return new Sided(this, side);
    }

    public MachineEnergyStorage readFromNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        if (nbt.contains("Energy", Tag.TAG_INT)) {
            setEnergy(nbt.getInt("Energy"));
        } else if (nbt.contains("energy", Tag.TAG_INT)) {
            setEnergy(nbt.getInt("energy"));
        }
        if (nbt.contains("Capacity", Tag.TAG_INT)) {
            this.capacity = nbt.getInt("Capacity");
        }
        return this;
    }

    public CompoundTag writeToNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Energy", this.energy);
        tag.putInt("Capacity", this.capacity);
        return tag;
    }

    private record Sided(MachineEnergyStorage master, @Nullable Direction side) implements IEnergyStorage {

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!master.canReceive()) {
                return 0;
            }
            return master.receiveEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!master.canExtract()) {
                return 0;
            }
            return master.extractEnergy(maxExtract, simulate);
        }

        @Override
        public int getEnergyStored() {
            return master.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return master.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return master.canExtract();
        }

        @Override
        public boolean canReceive() {
            return master.canReceive();
        }
    }
}
