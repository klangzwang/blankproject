package geni.withercore.registration.impl;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import geni.withercore.registration.WUTDeferredRegister;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;

public class ItemDeferredRegister extends WUTDeferredRegister<Item> {

    public ItemDeferredRegister(String modid)
    {
        super(Registries.ITEM, modid, ItemRegistryObject::new);
    }

    @Override
    public void register(@NotNull IEventBus bus)
    {
        super.register(bus);
    }

    @SuppressWarnings("unused")
    private void forEntries(Consumer<ItemRegistryObject<?>> consumer)
    {
        for (Holder<Item> entry : getEntries())
        {
            if (entry instanceof ItemRegistryObject<?> registryObject)
            {
                consumer.accept(registryObject);
            }
            else if (!FMLEnvironment.production)
            {
                throw new IllegalStateException("Expected entry to be an ItemRegistryObject");
            }
        }
    }

    public ItemRegistryObject<Item> registerStandingWallBlockItem(String name)
    {
        return registerItem(name, Item::new);
    }

    public <BLOCKITEM extends BlockItem> ItemRegistryObject<StandingAndWallBlockItem> registerStandWallBlockItem(
            String name, Block standBlockSupplier, Block wallBlockSupplier, Function<BlockItem.Properties, BLOCKITEM> sup)
    {
        return register(name, () -> new StandingAndWallBlockItem(standBlockSupplier, wallBlockSupplier, new BlockItem.Properties(), Direction.DOWN));
    }

    public <BLOCKITEM extends BlockItem> ItemRegistryObject<BLOCKITEM> registerBlockItem(String name, Function<BlockItem.Properties, BLOCKITEM> sup)
    {
        return register(name, () -> sup.apply(new BlockItem.Properties()));
    }

    public ItemRegistryObject<Item> registerUnburnable(String name)
    {
        return registerUnburnable(name, Item::new);
    }

    public ItemRegistryObject<Item> register(String name)
    {
        return registerItem(name, Item::new);
    }

    public <ITEM extends Item> ItemRegistryObject<ITEM> registerItem(String name, Function<Item.Properties, ITEM> sup)
    {
        return register(name, () -> sup.apply(new Item.Properties()));
    }

    public <ITEM extends Item> ItemRegistryObject<ITEM> registerUnburnable(String name, Function<Item.Properties, ITEM> sup)
    {
        return register(name, () -> sup.apply(new Item.Properties().fireResistant()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ITEM extends Item> ItemRegistryObject<ITEM> register(String name, Supplier<? extends ITEM> sup)
    {
        return (ItemRegistryObject<ITEM>) super.register(name, sup);
    }
}