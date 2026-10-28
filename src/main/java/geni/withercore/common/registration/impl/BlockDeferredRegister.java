package geni.withercore.common.registration.impl;

import geni.withercore.common.registration.DoubleDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockDeferredRegister extends DoubleDeferredRegister<Block, Item> {

    public BlockDeferredRegister(String modid) {
        super(modid, Registries.BLOCK, new ItemDeferredRegister(modid));
    }

    public <BLOCK extends Block> BlockRegistryObject<BLOCK, BlockItem> registerNoItem(String name, Supplier<? extends BLOCK> blockSupplier) {
        DeferredHolder<Block, BLOCK> blockHolder = registerPrimaryOnly(name, blockSupplier);
        return new BlockRegistryObject<>(blockHolder, null);
    }

    public <BLOCK extends Block> BlockRegistryObject<BLOCK, BlockItem> register(String name, Supplier<? extends BLOCK> blockSupplier)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, new Item.Properties()));
    }

    public <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> register(String name, Supplier<? extends BLOCK> blockSupplier,
                                                                                                   Function<BLOCK, ITEM> itemCreator)
    {
        return register(name, blockSupplier, block -> {
            return itemCreator.apply(block);
        }, BlockRegistryObject::new);
    }

    /*public BlockRegistryObject<Block, BlockItem> register(String name, BlockBehaviour.Properties properties) {
        return register(name, () -> new Block(properties), BlockItem::new);
    }

    public <BLOCK extends Block> BlockRegistryObject<BLOCK, BlockItem> register(String name, Supplier<? extends BLOCK> blockSupplier) {
        return register(name, blockSupplier, BlockItem::new);
    }

    public <BLOCK extends Block> BlockRegistryObject<BLOCK, ItemBlockTooltip<BLOCK>> registerDetails(String name, Supplier<? extends BLOCK> blockSupplier) {
        return register(name, blockSupplier, (block, properties) -> new ItemBlockTooltip<>(block, true, properties));
    }

    public <BLOCK extends Block, ITEM extends BlockItem> BlockRegistryObject<BLOCK, ITEM> register(String name, Supplier<? extends BLOCK> blockSupplier,
                                                                                                   BiFunction<BLOCK, Item.Properties, ITEM> itemCreator) {
        return register(name, blockSupplier, block -> {
            Item.Properties properties = new Item.Properties();
            *//*properties.component(WUTComponents.ENERGY, 0);*//*
            return itemCreator.apply(block, properties);
        }, BlockRegistryObject::new);
	}*/
}