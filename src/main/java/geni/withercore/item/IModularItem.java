package geni.withercore.item;

import javax.annotation.Nullable;

import geni.withercore.init.WUTCapabilities;
import geni.withercore.init.WUTComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;

public interface IModularItem extends IItemExtension {
	
	/*
	 * 
	 * ENERGYSTORAGE
	 * 
	 */
	default ICapabilityProvider<ItemStack, Void, IEnergyStorage> initEnergyProvider()
	{
		return null;
	};
	ICapabilityProvider<ItemStack, Void, IEnergyStorage> ENERGY_STORAGE_PROVIDER = (stack, v) -> createEnergyComponent(stack);
	
	@Nullable
	public static IEnergyStorage createEnergyComponent(ItemStack stack)
	{
		boolean hasEnergy = Boolean.TRUE.equals(stack.get(WUTComponents.HAS_ENERGY));
		int capacity = hasEnergy ? 100000 : 0;
		int maxReceive = hasEnergy ? 200 : 0;

		return new ComponentEnergyStorage(stack, WUTComponents.ENERGY.get(), capacity, maxReceive)
		{
			@Override
			public boolean canReceive()
			{
				return Boolean.TRUE.equals(stack.get(WUTComponents.HAS_ENERGY));
			}
		};
	}
	
	/*
	 * 
	 * ITEMHANDLER
	 * 
	 */
	default ICapabilityProvider<ItemStack, Void, IItemHandler> initItemHandler()
	{
		return null;
	};
	ICapabilityProvider<ItemStack, Void, IItemHandler> ITEM_HANDLER_PROVIDER = (stack, v) -> createItemComponent(stack);
	
	@Nullable
	public static ComponentItemHandler createItemComponent(ItemStack stack)
	{
		return new ComponentItemHandler(stack, WUTComponents.ITEMS.get(), 9);
	}

	// --
	
    @Nullable
    public default IItemHandler getItemHandler(ItemStack stack)
    {
    	if(!WUTCapabilities.getItemHandler(stack).isPresent())
    		return null;
		return WUTCapabilities.getItemHandler(stack).get();
    }
	
    default boolean damageBarVisible(ItemStack stack)
    {
    	if(hasSoulBank(stack))
    	{
            long max = getEnergyHandler(stack).getMaxEnergyStored();
            return max > 0 && getEnergyHandler(stack).getEnergyStored() < max;
    	}
    	return false;
    }

    default int damageBarWidth(ItemStack stack)
    {
    	if(hasSoulBank(stack))
    	{
            float charge = (float) getEnergyHandler(stack).getEnergyStored() / getEnergyHandler(stack).getMaxEnergyStored();
            return Math.round(13.0F * charge);
    	}
    	return 0;
    }

    default int damageBarColour(ItemStack stack)
    {
    	if(hasSoulBank(stack))
    	{
            float maxEnergy = getEnergyHandler(stack).getMaxEnergyStored();
            float energy = getEnergyHandler(stack).getEnergyStored();
            float f = Math.max(0.0F, (energy) / maxEnergy);
            return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    	}
    	return 0;
    }
    
    public default boolean hasSoulBank(ItemStack stack)
    {
    	return stack.get(WUTComponents.HAS_ENERGY);
    }
    
    @Nullable
    public default IEnergyStorage getEnergyHandler(ItemStack stack)
    {
    	if(!WUTCapabilities.getItemEnergyHandler(stack).isPresent())
    		return null;
		return WUTCapabilities.getItemEnergyHandler(stack).get();
    }
    
    public default void extractEnergy(ItemStack stack, int amount)
    {
		IEnergyStorage storage = WUTCapabilities.getItemEnergyHandler(stack).orElse(null);
		storage.extractEnergy(amount, false);
    }
    
    @Override
    default boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }
}
