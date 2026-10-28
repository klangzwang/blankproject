package geni.withercore.registration.impl;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import geni.witherapi.providers.IItemProvider;
import geni.withercore.registration.WUTDeferredHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class ItemRegistryObject<ITEM extends Item> extends WUTDeferredHolder<Item, ITEM> implements IItemProvider {

    @Nullable
    private List<Consumer<RegisterCapabilitiesEvent>> containerCapabilities;

    public ItemRegistryObject(ResourceKey<Item> key)
    {
        super(key);
    }

    @NotNull
    @Override
    public ITEM asItem()
    {
        return value();
    }
}