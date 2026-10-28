package geni.withercore.item.block;

import geni.withercore.block.WitherBlock;
import geni.withercore.init.WUTComponents;
import geni.withercore.init.WUTCreativeTab.ICustomCreativeTabContents;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class ItemBlockEnergyCube extends ItemBlockTooltip<WitherBlock> implements ICustomCreativeTabContents {

    public ItemBlockEnergyCube(WitherBlock block, Item.Properties properties) {
        super(block, true, properties
              .component(WUTComponents.ENERGY, 0)
        );
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return stack.getCount() == 1;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return 0;
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0;
    }

    @Override
    public void addItems(Holder<Item> item, Consumer<ItemStack> tabOutput) {
        /*tabOutput.accept();*/
    }
}