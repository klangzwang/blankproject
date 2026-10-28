package geni.withercore.registration.impl;

import com.mojang.serialization.Codec;
import java.util.UUID;
import java.util.function.UnaryOperator;

import geni.withercore.registration.WUTDeferredHolder;
import geni.withercore.registration.WUTDeferredRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;

public class DataComponentDeferredRegister extends WUTDeferredRegister<DataComponentType<?>> {

    public DataComponentDeferredRegister(String namespace) {
        super(Registries.DATA_COMPONENT_TYPE, namespace);
    }

    public <TYPE> WUTDeferredHolder<DataComponentType<?>, DataComponentType<TYPE>> simple(String name, UnaryOperator<DataComponentType.Builder<TYPE>> operator) {
        return register(name, () -> operator.apply(DataComponentType.builder()).build());
    }

    public WUTDeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> registerBoolean(String name) {
        return simple(name, builder -> builder.persistent(Codec.BOOL)
              .networkSynchronized(ByteBufCodecs.BOOL));
    }

    public WUTDeferredHolder<DataComponentType<?>, DataComponentType<Integer>> registerNonNegativeInt(String name) {
        return simple(name, builder -> builder.persistent(ExtraCodecs.POSITIVE_INT)
              .networkSynchronized(ByteBufCodecs.VAR_INT));
    }

    public WUTDeferredHolder<DataComponentType<?>, DataComponentType<Integer>> registerInt(String name) {
        return simple(name, builder -> builder.persistent(Codec.INT)
              .networkSynchronized(ByteBufCodecs.VAR_INT));
    }

    public WUTDeferredHolder<DataComponentType<?>, DataComponentType<UUID>> registerUUID(String name) {
        return simple(name, builder -> builder.persistent(UUIDUtil.CODEC)
              .networkSynchronized(UUIDUtil.STREAM_CODEC));
    }

    public WUTDeferredHolder<DataComponentType<?>, DataComponentType<Component>> registerComponent(String name) {
        return simple(name, builder -> builder.persistent(ComponentSerialization.FLAT_CODEC)
              .networkSynchronized(ComponentSerialization.STREAM_CODEC)
              .cacheEncoding());
    }

    public <TYPE> WUTDeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<TYPE>>> registerResourceKey(String name,
          ResourceKey<? extends Registry<TYPE>> registryKey) {
        return simple(name, builder -> builder.persistent(ResourceKey.codec(registryKey))
              .networkSynchronized(ResourceKey.streamCodec(registryKey)));
    }
}