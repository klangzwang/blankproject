package geni.withercore.registration.impl;

import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

import geni.withercore.registration.WUTDeferredHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class TileEntityTypeRegistryObject<BE extends BlockEntity> extends WUTDeferredHolder<BlockEntityType<?>, BlockEntityType<BE>> {

    @Nullable
    private List<CapabilityData<BE, ?, ?>> capabilityProviders;

    public TileEntityTypeRegistryObject(ResourceKey<BlockEntityType<?>> key)
    {
        super(key);
    }

    @Internal
    void capabilities(@Nullable List<CapabilityData<BE, ?, ?>> capabilityProviders)
    {
        this.capabilityProviders = capabilityProviders;
    }

    @Internal
    void registerCapabilityProviders(RegisterCapabilitiesEvent event)
    {
        if (capabilityProviders != null)
        {
            for (CapabilityData<BE, ?, ?> capabilityProvider : capabilityProviders)
            {
                capabilityProvider.registerProvider(event, value());
            }
        }
    }

    record CapabilityData<BE extends BlockEntity, CAP, CONTEXT>(BlockCapability<CAP, CONTEXT> capability, ICapabilityProvider<? super BE, CONTEXT, CAP> provider)
    {
        private void registerProvider(RegisterCapabilitiesEvent event, BlockEntityType<BE> type)
        {
            event.registerBlockEntity(capability, type, provider);
        }
    }
}
