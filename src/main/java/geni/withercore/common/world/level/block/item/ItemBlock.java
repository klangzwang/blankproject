package geni.withercore.common.world.level.block.item;

import geni.witherapi.providers.IBlockProvider;
import geni.withercore.common.world.level.block.WitherAbstractBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ItemBlock<B extends Block> extends BlockItem implements IBlockProvider {
	
    public ItemBlock(@NotNull B block, Item.Properties builder)
    {
        super(block, builder);
    }
    
    @NotNull
    @Override
    public Component getName(ItemStack stack)
    {
        if (getBlock() instanceof WitherAbstractBlock)
            return getBlock().getName();
        return super.getName(stack);
    }

	@NotNull
    @Override
    @SuppressWarnings("unchecked")
    public B getBlock()
    {
        return (B) super.getBlock();
    }
}
