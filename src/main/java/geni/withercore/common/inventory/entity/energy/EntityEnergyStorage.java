package geni.withercore.common.inventory.entity.energy;

import geni.withercore.common.inventory.blockentity.energy.MachineEnergyStorage;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class EntityEnergyStorage extends MachineEnergyStorage {

    @Nullable
    private final WeakReference<LivingEntity> owner;

    public EntityEnergyStorage(int capacity) {
        this(null, capacity, capacity, capacity);
    }

    public EntityEnergyStorage(@Nullable LivingEntity owner, int capacity) {
        this(owner, capacity, capacity, capacity);
    }

    public EntityEnergyStorage(@Nullable LivingEntity owner, int capacity, int maxTransfer) {
        this(owner, capacity, maxTransfer, maxTransfer);
    }

    public EntityEnergyStorage(@Nullable LivingEntity owner, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
        this.owner = owner != null ? new WeakReference<>(owner) : null;
    }

    @Nullable
    public LivingEntity getOwner() {
        return owner != null ? owner.get() : null;
    }
}
