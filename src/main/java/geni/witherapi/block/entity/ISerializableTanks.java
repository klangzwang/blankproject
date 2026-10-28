package geni.witherapi.block.entity;

import geni.withercore.common.inventory.blockentity.fluid.FluidTank;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import javax.annotation.Nonnull;
import java.util.Map;

@FunctionalInterface
public interface ISerializableTanks {
	
    @Nonnull
    Map<DataComponentType<SimpleFluidContent>, FluidTank> getSerializableTanks();

    default void deserializeTanks(HolderLookup.Provider provider, CompoundTag tag)
    {
        getSerializableTanks().forEach((comp, tank) ->
                tank.readFromNBT(provider, tag.getCompound(comp.toString()))
        );
    }

    default CompoundTag serializeTanks(HolderLookup.Provider provider)
    {
        return Util.make(new CompoundTag(), tag -> getSerializableTanks().forEach((comp, tank) -> {
            if (!tank.getFluid().isEmpty()) {
                tag.put(comp.toString(), tank.writeToNBT(provider));
            }
        }));
    }
}
