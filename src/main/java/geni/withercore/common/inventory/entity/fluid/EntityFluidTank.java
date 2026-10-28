package geni.withercore.common.inventory.entity.fluid;

import geni.withercore.common.inventory.blockentity.fluid.FluidTank;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.function.Predicate;

public class EntityFluidTank extends FluidTank {

    @Nullable
    private final WeakReference<LivingEntity> owner;

    public EntityFluidTank(int capacity) {
        this(null, capacity, stack -> true);
    }

    public EntityFluidTank(@Nullable LivingEntity owner, int capacity) {
        this(owner, capacity, stack -> true);
    }

    public EntityFluidTank(@Nullable LivingEntity owner, int capacity, Predicate<FluidStack> validator) {
        super(capacity, validator);
        this.owner = owner != null ? new WeakReference<>(owner) : null;
    }

    @Nullable
    public LivingEntity getOwner() {
        return owner != null ? owner.get() : null;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filled = super.fill(resource, action);
        if (filled > 0 && action.execute()) {
            onContentsChanged();
        }
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack drained = super.drain(resource, action);
        if (!drained.isEmpty() && action.execute()) {
            onContentsChanged();
        }
        return drained;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack drained = super.drain(maxDrain, action);
        if (!drained.isEmpty() && action.execute()) {
            onContentsChanged();
        }
        return drained;
    }

    protected void onContentsChanged() {
        // Can be overridden or extended for entity synchronization
    }
}
