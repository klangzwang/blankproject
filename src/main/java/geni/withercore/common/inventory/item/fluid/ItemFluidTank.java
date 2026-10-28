package geni.withercore.common.inventory.item.fluid;

import geni.withercore.init.WUTComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ItemFluidTank implements IFluidHandlerItem, IFluidTank {

    protected final ItemStack container;
    protected final int capacity;
    protected Predicate<FluidStack> validator;

    public ItemFluidTank(ItemStack container, int capacity) {
        this(container, capacity, stack -> true);
    }

    public ItemFluidTank(ItemStack container, int capacity, Predicate<FluidStack> validator) {
        this.container = container;
        this.capacity = capacity;
        this.validator = validator != null ? validator : stack -> true;
    }

    @Override
    @NotNull
    public ItemStack getContainer() {
        return container;
    }

    @Override
    @NotNull
    public FluidStack getFluid() {
        SimpleFluidContent content = container.getOrDefault(WUTComponents.FLUID.get(), SimpleFluidContent.EMPTY);
        return content.copy();
    }

    protected void setFluid(FluidStack stack) {
        if (stack.isEmpty()) {
            container.remove(WUTComponents.FLUID.get());
        } else {
            container.set(WUTComponents.FLUID.get(), SimpleFluidContent.copyOf(stack));
        }
    }

    @Override
    public int getFluidAmount() {
        return getFluid().getAmount();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    @NotNull
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }

        FluidStack current = getFluid();
        if (current.isEmpty()) {
            int toFill = Math.min(capacity, resource.getAmount());
            if (action.execute()) {
                setFluid(resource.copyWithAmount(toFill));
            }
            return toFill;
        }

        if (!FluidStack.isSameFluidSameComponents(current, resource)) {
            return 0;
        }

        int toFill = Math.min(capacity - current.getAmount(), resource.getAmount());
        if (toFill > 0 && action.execute()) {
            setFluid(current.copyWithAmount(current.getAmount() + toFill));
        }
        return toFill;
    }

    @Override
    @NotNull
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack current = getFluid();
        if (current.isEmpty() || !FluidStack.isSameFluidSameComponents(current, resource)) {
            return FluidStack.EMPTY;
        }

        return drain(resource.getAmount(), action);
    }

    @Override
    @NotNull
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (container.getCount() != 1 || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack current = getFluid();
        if (current.isEmpty()) {
            return FluidStack.EMPTY;
        }

        int toDrain = Math.min(current.getAmount(), maxDrain);
        FluidStack drained = current.copyWithAmount(toDrain);

        if (action.execute()) {
            if (current.getAmount() <= toDrain) {
                setFluid(FluidStack.EMPTY);
            } else {
                setFluid(current.copyWithAmount(current.getAmount() - toDrain));
            }
        }
        return drained;
    }
}
