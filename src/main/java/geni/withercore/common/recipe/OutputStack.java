package geni.withercore.common.recipe;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public record OutputStack(Either<ItemStack, FluidStack> stack) {

    public static final OutputStack EMPTY = OutputStack.of(ItemStack.EMPTY);
    
    public static final String ITEM = "Item";
    public static final String FLUID = "Fluid";

    public static OutputStack of(ItemStack itemStack)
    {
        return new OutputStack(Either.left(itemStack));
    }

    public static OutputStack of(FluidStack fluidStack)
    {
        return new OutputStack(Either.right(fluidStack));
    }

    public ItemStack getItem()
    {
        return stack.left().orElse(ItemStack.EMPTY);
    }

    public FluidStack getFluid()
    {
        return stack.right().orElse(FluidStack.EMPTY);
    }

    public boolean isItem()
    {
        return stack.left().isPresent();
    }

    public boolean isFluid()
    {
        return stack.right().isPresent();
    }

    public boolean isEmpty()
    {
        if (isItem())
        {
            return stack.left().get().isEmpty();
        }

        if (isFluid())
        {
            return stack.right().get().isEmpty();
        }

        return true;
    }

    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider)
    {
        CompoundTag tag = new CompoundTag();
        if (isItem())
        {
            tag.put(ITEM, stack.left().get().saveOptional(lookupProvider));
        }
        else if (isFluid())
        {
            tag.put(FLUID, stack.right().get().saveOptional(lookupProvider));
        }
        return tag;
    }

    public static OutputStack fromNBT(HolderLookup.Provider lookupProvider, CompoundTag tag)
    {
        if (tag.contains(ITEM))
        {
            return OutputStack.of(ItemStack.parseOptional(lookupProvider, tag.getCompound(ITEM)));
        }
        else if (tag.contains(FLUID))
        {
            return OutputStack.of(FluidStack.parseOptional(lookupProvider, tag.getCompound(FLUID)));
        }
        return OutputStack.EMPTY;
    }
}
