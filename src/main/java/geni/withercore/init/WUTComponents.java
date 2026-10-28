package geni.withercore.init;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import geni.witherapi.soulbank.SoulBankData;
import geni.witherutils.WitherUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class WUTComponents {

    @SuppressWarnings("removal")
    public static final DeferredRegister.DataComponents DATACOMP_TYPES = DeferredRegister.createDataComponents(WitherUtils.MODID);

    public static Supplier<DataComponentType<Float>> SHIELD_DEGEN = DATACOMP_TYPES.registerComponentType("shield_degradation",
            builder -> builder.persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT));

    public static Supplier<DataComponentType<SoulBankData>> SOULBANK_DATA = DATACOMP_TYPES.registerComponentType("soulbank_data",
            builder -> builder.persistent(SoulBankData.CODEC).networkSynchronized(SoulBankData.STREAM_CODEC));

    public static Supplier<DataComponentType<Integer>> ENERGY = DATACOMP_TYPES.registerComponentType("energy",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static Supplier<DataComponentType<Boolean>> HAS_ENERGY = DATACOMP_TYPES.registerComponentType("has_energy",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static Supplier<DataComponentType<Boolean>> TOGGLE = DATACOMP_TYPES.registerComponentType("toggle",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static Supplier<DataComponentType<Integer>> TIMED = DATACOMP_TYPES.registerComponentType("timed",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static Supplier<DataComponentType<ItemContainerContents>> ITEMS = register("items", ItemContainerContents.CODEC, ItemContainerContents.STREAM_CODEC);

    public static Supplier<DataComponentType<FluidStack>> FLUID_STACK = register("fluid_stack", FluidStack.OPTIONAL_CODEC, FluidStack.OPTIONAL_STREAM_CODEC);

    public static Supplier<DataComponentType<BlockPos>> DRONE_DOCK_POS = register("drone_dock_pos", BlockPos.CODEC, BlockPos.STREAM_CODEC);

    public static Supplier<DataComponentType<BlockPos>> DRONE_TARGET_POS = register("drone_target_pos", BlockPos.CODEC, BlockPos.STREAM_CODEC);

    public static Supplier<DataComponentType<Integer>> DRONE_SETUP_STEP = register("drone_setup_step", Codec.INT, ByteBufCodecs.INT);

    private static <T> Supplier<DataComponentType<T>> register(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        return DATACOMP_TYPES.registerComponentType(name, builder -> builder
                .persistent(codec)
                .networkSynchronized(streamCodec)
        );
    }
}
