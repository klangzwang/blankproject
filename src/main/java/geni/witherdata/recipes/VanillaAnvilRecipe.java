package geni.witherdata.recipes;

import geni.withercore.init.WUTComponents;
import geni.withercore.init.WUTItems;
import geni.withercore.item.IModularItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

public class VanillaAnvilRecipe {

	@SubscribeEvent
	public void handle(AnvilUpdateEvent event)
	{
		ItemStack leftItem = event.getLeft();
		ItemStack rightItem = event.getRight();

		handleEnergyLevel(leftItem, rightItem, event);
	}
	
	/*
	 * 
	 * SOULBANKS
	 * 
	 */
	public void handleEnergyLevel(ItemStack itemSlot, ItemStack upgradeSlot, AnvilUpdateEvent event)
	{
		if(itemSlot.getItem() instanceof IModularItem)
		{
			ItemStack stack = itemSlot.copy();
			if(!stack.get(WUTComponents.HAS_ENERGY))
			{
				if(upgradeSlot.getItem() == WUTItems.SOULBANK_BASIC.get())
				{
					stack.set(WUTComponents.HAS_ENERGY, true);
					event.setCost(3);
					event.setOutput(stack);
					event.setMaterialCost(1);
				}
				if(upgradeSlot.getItem() == WUTItems.SOULBANK_ADVANCED.get())
				{
					stack.set(WUTComponents.HAS_ENERGY, true);
					event.setCost(3);
					event.setOutput(stack);
					event.setMaterialCost(1);
				}
				if(upgradeSlot.getItem() == WUTItems.SOULBANK_ULTRA.get())
				{
					stack.set(WUTComponents.HAS_ENERGY, true);
					event.setCost(3);
					event.setOutput(stack);
					event.setMaterialCost(1);
				}
			}
		}
	}
}
