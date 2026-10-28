package geni.withercore.common.registration.impl;

import geni.withercore.common.registration.WUTDeferredRegister;
import geni.withercore.common.registration.impl.TileEntityTypeRegistryObject.CapabilityData;
import geni.withercore.common.world.level.block.entity.WitherBlockEntity;
import geni.withercore.init.WUTCapabilities;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class TileEntityTypeDeferredRegister extends WUTDeferredRegister<BlockEntityType<?>> {

    public TileEntityTypeDeferredRegister(String modid)
    {
        super(Registries.BLOCK_ENTITY_TYPE, modid, TileEntityTypeRegistryObject::new);
    }

    public <BE extends WitherBlockEntity> BlockEntityTypeBuilder<BE> mekBuilder(BlockRegistryObject<?, ?> block, BlockEntitySupplier<? extends BE> factory)
    {
        BlockEntityTypeBuilder<BE> builder = this.<BE>builder(block, factory);
        return builder;
    }

    public <BE extends BlockEntity> BlockEntityTypeBuilder<BE> builder(BlockRegistryObject<?, ?> block, BlockEntitySupplier<? extends BE> factory)
    {
        return new BlockEntityTypeBuilder<>(block, factory);
    }
    
    @SuppressWarnings("unchecked")
    private <BE extends BlockEntity> TileEntityTypeRegistryObject<BE> registerMek(String name, Supplier<? extends BlockEntityType<BE>> sup)
    {
        return (TileEntityTypeRegistryObject<BE>) super.register(name, sup);
    }
    
    @Override
    public void register(@NotNull IEventBus bus)
    {
        super.register(bus);
        bus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        for (DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> entry : getEntries())
        {
            if (entry instanceof TileEntityTypeRegistryObject<?> tileRO)
            {
                tileRO.registerCapabilityProviders(event);
            }
            else if (!FMLEnvironment.production)
            {
                throw new IllegalStateException("Expected entry to be a TileEntityTypeRegistryObject");
            }
        }
    }

    public class BlockEntityTypeBuilder<BE extends BlockEntity>
    {
        private final BlockRegistryObject<?, ?> block;
        private final BlockEntityType.BlockEntitySupplier<? extends BE> factory;
        private final List<CapabilityData<BE, ?, ?>> capabilityProviders = new ArrayList<>();

        BlockEntityTypeBuilder(BlockRegistryObject<?, ?> block, BlockEntityType.BlockEntitySupplier<? extends BE> factory)
        {
            this.block = block;
            this.factory = factory;
        }
        
        public TileEntityTypeRegistryObject<BE> build()
        {
        	TileEntityTypeRegistryObject<BE> holder = registerMek(block.getName(), () -> BlockEntityType.Builder.<BE>of(factory, block.getBlock()).build(null));
        	holder.capabilities(capabilityProviders.isEmpty() ? null : capabilityProviders);
        	return holder;
        }

        @SuppressWarnings("unchecked")
        public <CAP, CONTEXT> BlockEntityTypeBuilder<BE> withSimple(BlockCapability<CAP, CONTEXT> capability)
        {
            return with(capability, (ICapabilityProvider<? super BE, CONTEXT, CAP>) WUTCapabilities.SIMPLE_PROVIDER);
        }

        public <CAP, CONTEXT> BlockEntityTypeBuilder<BE> with(BlockCapability<CAP, CONTEXT> capability, Function<BlockCapability<CAP, CONTEXT>, ICapabilityProvider<? super BE, CONTEXT, CAP>> provider)
        {
            return with(capability, provider.apply(capability));
        }

        public <CAP, CONTEXT> BlockEntityTypeBuilder<BE> with(BlockCapability<CAP, CONTEXT> capability, ICapabilityProvider<? super BE, CONTEXT, CAP> provider)
        {
            capabilityProviders.add(new CapabilityData<BE, CAP, CONTEXT>(capability, provider));
            return this;
        }
    }
}