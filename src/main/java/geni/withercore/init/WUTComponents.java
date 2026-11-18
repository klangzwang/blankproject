package geni.withercore.init;

import com.mojang.serialization.Codec;
import geni.witherapi.fluid.pipe.PipeAnchor;
import geni.witherapi.power.PowerEndpointRef;
import geni.witherapi.soulbank.SoulBankData;
import geni.witherapi.spline.BeltTier;
import geni.witherapi.spline.SplineAnchorPoint;
import geni.witherutils.WitherUtils;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class WUTComponents {

    @SuppressWarnings("removal")
    public static final DeferredRegister.DataComponents DATACOMP_TYPES = DeferredRegister.createDataComponents(WitherUtils.MODID);

    public static Supplier<DataComponentType<SoulBankData>> SOULBANK_DATA = DATACOMP_TYPES.registerComponentType("soulbank_data",
            builder -> builder.persistent(SoulBankData.CODEC).networkSynchronized(SoulBankData.STREAM_CODEC));

    public static Supplier<DataComponentType<SplineAnchorPoint>> SPLINE_ANCHOR = DATACOMP_TYPES.registerComponentType("spline_anchor",
            builder -> builder.persistent(SplineAnchorPoint.CODEC).networkSynchronized(SplineAnchorPoint.STREAM_CODEC));

    public static Supplier<DataComponentType<BeltTier>> BELT_TIER = DATACOMP_TYPES.registerComponentType("belt_tier",
            builder -> builder.persistent(BeltTier.CODEC).networkSynchronized(BeltTier.STREAM_CODEC));

    public static Supplier<DataComponentType<PowerEndpointRef>> PENDING_POWER_ANCHOR = DATACOMP_TYPES.registerComponentType("pending_power_anchor",
            builder -> builder.persistent(PowerEndpointRef.CODEC).networkSynchronized(PowerEndpointRef.STREAM_CODEC));

    public static Supplier<DataComponentType<Integer>> ENERGY = DATACOMP_TYPES.registerComponentType("energy",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static Supplier<DataComponentType<Boolean>> HAS_ENERGY = DATACOMP_TYPES.registerComponentType("has_energy",
            builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static Supplier<DataComponentType<ItemContainerContents>> ITEMS = register("items", ItemContainerContents.CODEC, ItemContainerContents.STREAM_CODEC);

    public static Supplier<DataComponentType<net.neoforged.neoforge.fluids.SimpleFluidContent>> FLUID = register("fluid",
            net.neoforged.neoforge.fluids.SimpleFluidContent.CODEC,
            net.neoforged.neoforge.fluids.SimpleFluidContent.STREAM_CODEC);

    public static Supplier<DataComponentType<PipeAnchor>> PIPE_ANCHOR = register("pipe_anchor",
            PipeAnchor.CODEC,
            PipeAnchor.STREAM_CODEC);
    private static <T> Supplier<DataComponentType<T>> register(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        return DATACOMP_TYPES.registerComponentType(name, builder -> builder
                .persistent(codec)
                .networkSynchronized(streamCodec)
        );
    }
}
