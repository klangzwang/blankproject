package geni.withercore.common.registration.impl;

import geni.witherapi.providers.IBlockProvider;
import geni.withercore.common.registration.DoubleWrappedRegistryObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BlockRegistryObject<BLOCK extends Block, ITEM extends Item> extends DoubleWrappedRegistryObject<Block, BLOCK, Item, ITEM> implements IBlockProvider {

    public BlockRegistryObject(DeferredHolder<Block, BLOCK> blockRegistryObject, DeferredHolder<Item, ITEM> itemRegistryObject) {
        super(blockRegistryObject, itemRegistryObject);
    }

    @NotNull
    @Override
    public BLOCK getBlock() {
        return getPrimary();
    }

    @NotNull
    public BLOCK get() {
        return getPrimary();
    }

    @NotNull
    @Override
    public ITEM asItem() {
        if (secondaryRO == null) {
            return (ITEM) Items.AIR;
        }
        return getSecondary();
    }

    public BlockRegistryObject<BLOCK, ITEM> forItemHolder(Consumer<ItemRegistryObject<ITEM>> consumer) {
        if (secondaryRO instanceof ItemRegistryObject<ITEM> itemHolder) {
            consumer.accept(itemHolder);
            return this;
        }
        if (secondaryRO == null) {
            return this;
        }
        throw new IllegalStateException("Called method requires an ItemRegistryObject");
    }

    @NotNull
    public DeferredHolder<Item, ITEM> getItemHolder() {
        return secondaryRO;
    }

    public boolean hasItem() {
        return secondaryRO != null;
    }
}