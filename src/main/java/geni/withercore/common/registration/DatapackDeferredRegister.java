package geni.withercore.common.registration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.StructureModifier;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DatapackDeferredRegister<T> extends DeferredMapCodecRegister<T> {

    public static DatapackDeferredRegister<BiomeModifier> biomeModifiers(String modid)
    {
        return new DatapackDeferredRegister<>(modid, NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, NeoForgeRegistries.Keys.BIOME_MODIFIERS);
    }

    public static DatapackDeferredRegister<StructureModifier> structureModifiers(String modid)
    {
        return new DatapackDeferredRegister<>(modid, NeoForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, NeoForgeRegistries.Keys.STRUCTURE_MODIFIERS);
    }

    private final ResourceKey<Registry<T>> datapackRegistryName;

    public DatapackDeferredRegister(String modid, ResourceKey<? extends Registry<MapCodec<? extends T>>> serializerRegistryName,
          ResourceKey<Registry<T>> datapackRegistryName)
    {
        this(modid, serializerRegistryName, datapackRegistryName, DeferredMapCodecHolder::new);
    }

    public DatapackDeferredRegister(String modid, ResourceKey<? extends Registry<MapCodec<? extends T>>> serializerRegistryName,
          ResourceKey<Registry<T>> datapackRegistryName, Function<ResourceKey<MapCodec<? extends T>>, ? extends DeferredMapCodecHolder<T, ? extends T>> holderCreator)
    {
        super(serializerRegistryName, modid, holderCreator);
        this.datapackRegistryName = datapackRegistryName;
    }

    public void createAndRegisterDatapack(IEventBus bus, Codec<T> directCodec, @Nullable Codec<T> networkCodec)
    {
        register(bus);
        bus.addListener((DataPackRegistryEvent.NewRegistry event) -> event.dataPackRegistry(datapackRegistryName, directCodec, networkCodec));
    }

    public ResourceKey<T> dataKey(String name)
    {
        return ResourceKey.create(datapackRegistryName, ResourceLocation.fromNamespaceAndPath(getNamespace(), name));
    }
}
