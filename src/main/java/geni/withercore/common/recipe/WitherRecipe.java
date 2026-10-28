package geni.withercore.common.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public interface WitherRecipe<T extends RecipeInput> extends Recipe<T> {

    List<OutputStack> craft(T container, RegistryAccess registryAccess);
    List<OutputStack> getResultStacks(RegistryAccess registryAccess);

    @Override
    default ItemStack assemble(T container, HolderLookup.Provider lookupProvider)
    {
        return ItemStack.EMPTY;
    }
    
    @Override
    default ItemStack getResultItem(HolderLookup.Provider lookupProvider)
    {
        return ItemStack.EMPTY;
    }
    
    @Override
    default boolean canCraftInDimensions(int pWidth, int pHeight)
    {
        return true;
    }
    @Override
    default boolean isSpecial()
    {
        return true;
    }
}
