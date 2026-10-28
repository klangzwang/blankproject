package geni.withercore.item;

import java.util.List;

import geni.withercore.render.tooltip.Tooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class WitherItem extends Item {

	public WitherItem(Item.Properties props)
	{
		super(props);
	}
	
	@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
		Tooltips.Tooltip.add(stack, context, tooltip, flag);
		super.appendHoverText(stack, context, tooltip, flag);
    }
}
