package geni.withercore.common.inventory.blockentity.fluid;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.lang.ref.WeakReference;

public class SmartSyncTank extends FluidTank {

    private FluidStack syncedFluidStackDesc = FluidStack.EMPTY;
    private FluidStack syncedFluidStackGui = FluidStack.EMPTY;

    private boolean pending = false;

    private int syncTimer = 0;
    private final WeakReference<BlockEntity> owner;
    private final int threshold;

    public SmartSyncTank(BlockEntity owner, int capacity) {
        super(capacity);

        this.owner = new WeakReference<>(owner);
        this.threshold = Math.min(1000, capacity / 100);
    }

    /**
     * Call from the holding BE's tick() method on both client and server
     */
    public void tick() {
        BlockEntity te = owner.get();
        if (te != null) {
            if (te.getLevel().isClientSide) {
//                if (ClientUtils.isGuiOpen(te)) {
//                    super.setFluid(syncedFluidStackGui);
//                } else {
//                    int currAmount = getFluidAmount();
//                    FluidStack tgt = syncedFluidStackDesc;
//                    int delta = tgt.getAmount() - currAmount;
//                    if (delta != 0) {
//                        int newAmount = Math.abs(delta) < capacity / 200 ? tgt.getAmount() : currAmount + delta / 20;
//                        Fluid newFluid = fluidStack.isEmpty() ? tgt.getFluid() : fluidStack.getFluid();
//                        super.setFluid(new FluidStack(newFluid, newAmount));
//                    }
//                }
            } else {
                if (syncTimer > 0) {
                    syncTimer--;
                } else if (syncTimer == 0) {
                    if (pending) {
                        syncedFluidStackDesc = getFluid().copy();
                        pending = false;
//                        syncTimer = ConfigHelper.common().advanced.fluidTankUpdateRate.get();
                    } else {
                        syncTimer = -1;
                    }
                }
            }
        }
    }

    private void deferredSync(int ticks) {
        if (syncTimer == -1) {
            syncedFluidStackDesc = getFluid().copy();
            syncTimer = ticks;
        } else {
            pending = true;
        }
    }

    private void onFluidChange(FluidStack newFluid) {
        // we can update the gui-synced value every tick
        syncedFluidStackGui = newFluid.copy();

        // more careful checking for the desc-synced value
        int delta = Math.abs(syncedFluidStackDesc.getAmount() - newFluid.getAmount());
        if (delta >= threshold || syncedFluidStackDesc.getFluid() != newFluid.getFluid()) {
//            deferredSync(ConfigHelper.common().advanced.fluidTankUpdateRate.get());
        }
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filled = super.fill(resource, action);
        if (filled != 0 && action.execute()) {
            onFluidChange(getFluid());
        }
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack drained = super.drain(resource, action);
        if (!drained.isEmpty() && action.execute()) {
            onFluidChange(getFluid());
        }
        return drained;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack drained = super.drain(maxDrain, action);
        if (!drained.isEmpty() && action.execute()) {
            onFluidChange(getFluid());
        }
        return drained;
    }

    @Override
    protected void onContentsChanged(FluidStack prevStack) {
        BlockEntity be = owner.get();
        if (be != null && be.getLevel() != null && !be.getLevel().isClientSide()) {
            be.setChanged();
        }
    }

    @Override
    public void setFluid(FluidStack stack) {
        onFluidChange(stack);

        super.setFluid(stack);
    }

    @Override
    public void loadFromContent(SimpleFluidContent contents) {
        super.loadFromContent(contents);

        pending = true;
        syncedFluidStackGui = fluidStack.copy();

    }
}
