package geni.witherutils.common.world.item;

import java.util.List;

import geni.witherapi.soulbank.SoulBankData;
import geni.withercore.render.tooltip.Tooltips;
import geni.withercore.init.WUTComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class SoulBankItem extends Item {
	
	public static class Basic extends SoulBankItem
    {
        public Basic(Item.Properties props)
        {
            super(props.component(WUTComponents.SOULBANK_DATA, SoulBankData.simple(1)));
        }
    }
	
	public static class Advanced extends SoulBankItem
    {
        public Advanced(Item.Properties props)
        {
            super(props.component(WUTComponents.SOULBANK_DATA, SoulBankData.simple(2)));
        }
    }
	
	public static class Ultra extends SoulBankItem
    {
        public Ultra(Item.Properties props)
        {
            super(props.component(WUTComponents.SOULBANK_DATA, SoulBankData.simple(3)));
        }
    }
	
	public SoulBankItem(Item.Properties props)
	{
		super(props.stacksTo(1));
	}
	
	@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
		Tooltips.Tooltip.add(stack, context, tooltip, flag);
		super.appendHoverText(stack, context, tooltip, flag);
    }
}
