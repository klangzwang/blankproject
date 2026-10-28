package geni.withercore.item.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ItemBlockWither<BLOCK extends Block> extends BlockItem {

    public ItemBlockWither(@NotNull BLOCK block, Properties properties) {
        super(block, properties);
    }

    @NotNull
    @Override
    public BLOCK getBlock() {
        return (BLOCK) super.getBlock();
    }

    @NotNull
    @Override
    public Component getName(@NotNull ItemStack stack) {
        return super.getName(stack);
    }
}