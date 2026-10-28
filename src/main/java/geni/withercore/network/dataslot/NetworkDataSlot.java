package geni.withercore.network.dataslot;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.UUIDUtil;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public final class NetworkDataSlot<T> {
	
    private final Type<T> type;
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private int lastHash;

    public static CodecType<Byte> BYTE = new CodecType<>(Codec.BYTE, ByteBufCodecs.BYTE.cast());
    public static CodecType<String> STRING = new CodecType<>(Codec.STRING, ByteBufCodecs.STRING_UTF8.cast());
    public static CodecType<Boolean> BOOL = new CodecType<>(Codec.BOOL, ByteBufCodecs.BOOL.cast());
    public static CodecType<Integer> INT = new CodecType<>(Codec.INT, ByteBufCodecs.INT.cast());
    public static CodecType<Long> LONG = new CodecType<>(Codec.LONG, ByteBufCodecs.VAR_LONG.cast());
    public static CodecType<Float> FLOAT = new CodecType<>(Codec.FLOAT, ByteBufCodecs.FLOAT.cast());
    public static CodecType<ResourceLocation> RESOURCE_LOCATION = new CodecType<>(ResourceLocation.CODEC, ResourceLocation.STREAM_CODEC.cast());
    public static CodecType<FluidStack> FLUID_STACK = new CodecType<>(FluidStack.OPTIONAL_CODEC, FluidStack.OPTIONAL_STREAM_CODEC, stack -> stack.hashCode() * 31 + stack.getAmount());
    public static CodecType<ItemStack> ITEM_STACK = new CodecType<>(ItemStack.OPTIONAL_CODEC, ItemStack.OPTIONAL_STREAM_CODEC);
    public static CodecType<BlockPos> BLOCK_POS = new CodecType<>(BlockPos.CODEC, BlockPos.STREAM_CODEC.cast());
    public static CodecType<Direction> DIRECTION = new CodecType<>(Direction.CODEC, Direction.STREAM_CODEC.cast());

    public NetworkDataSlot(Type<T> type, Supplier<T> getter, Consumer<T> setter)
    {
        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    @Nullable
    public Tag save(HolderLookup.Provider lookupProvider, boolean fullUpdate)
    {
        if (doesNeedUpdate() && !fullUpdate) {
            return null;
        }

        T value = getter.get();
        lastHash = type.hash(value);
        return type.save(lookupProvider, value);
    }

    public void parse(HolderLookup.Provider lookupProvider, Tag tag) {
        setter.accept(type.parse(lookupProvider, tag, getter));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        T value = getter.get();
        lastHash = type.hash(value);
        type.write(buf, value);
    }

    public void write(RegistryFriendlyByteBuf buf, T value) {
        type.write(buf, value);
    }

    public void read(RegistryFriendlyByteBuf buf) {
        setter.accept(type.read(buf, getter));
    }

    public boolean doesNeedUpdate() {
        T value = getter.get();
        int hash = type.hash(value);
        return lastHash != hash;
    }

    public interface Type<T> {

        int hash(T value);

        Tag save(HolderLookup.Provider lookupProvider, T value);

        T parse(HolderLookup.Provider lookupProvider, Tag tag, Supplier<T> currentValueSupplier);

        void write(RegistryFriendlyByteBuf buf, T value);

        T read(RegistryFriendlyByteBuf buf, Supplier<T> currentValueSupplier);
    }

    public record CodecType<T>(Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec,
            Function<T, Integer> hashFunction) implements Type<T> {

        public CodecType(Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
            this(codec, streamCodec, Object::hashCode);
        }

        public NetworkDataSlot<T> create(Supplier<T> getter, Consumer<T> setter) {
            return new NetworkDataSlot<>(this, getter, setter);
        }

        public static <T> CodecType<Set<T>> createSet(Codec<T> itemCodec,
                StreamCodec<RegistryFriendlyByteBuf, T> itemStreamCodec) {
            return new CodecType<>(itemCodec.listOf().xmap(ImmutableSet::copyOf, ImmutableList::copyOf),
                    itemStreamCodec.apply(ByteBufCodecs.list()).map(ImmutableSet::copyOf, ImmutableList::copyOf));
        }

        public static <T> CodecType<List<T>> createList(Codec<T> itemCodec,
                StreamCodec<RegistryFriendlyByteBuf, T> itemStreamCodec) {
            return new CodecType<>(itemCodec.listOf(), itemStreamCodec.apply(ByteBufCodecs.list()));
        }

        public static <T, U> CodecType<Map<T, U>> createMap(Codec<T> keyCodec, Codec<U> valueCodec,
                StreamCodec<RegistryFriendlyByteBuf, T> keyStreamCodec,
                StreamCodec<RegistryFriendlyByteBuf, U> valueStreamCodec) {
            return new CodecType<>(Codec.unboundedMap(keyCodec, valueCodec),
                    ByteBufCodecs.map(HashMap::new, keyStreamCodec, valueStreamCodec));
        }

        public int hash(T value) {
            return hashFunction.apply(value);
        }

        public Tag save(HolderLookup.Provider lookupProvider, T value) {
            return codec.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), value).getOrThrow();
        }

        public T parse(HolderLookup.Provider lookupProvider, Tag tag, Supplier<T> currentValueSupplier) {
            return codec.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
        }

        public void write(RegistryFriendlyByteBuf buf, T value) {
            streamCodec.encode(buf, value);
        }

        public T read(RegistryFriendlyByteBuf buf, Supplier<T> currentValueSupplier) {
            return streamCodec.decode(buf);
        }
    }
}
