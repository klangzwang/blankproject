package geni.withercore.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface IBlock {
	
    default ItemBlock<?> getBlockItem(Item.Properties properties)
    {
        return new ItemBlock<>((Block) this, properties);
    }
}
