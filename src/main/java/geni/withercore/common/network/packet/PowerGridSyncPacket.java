package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import geni.witherutils.common.power.PowerCableEdge;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * S2C payload synchronizing all power cable edges in a dimension to clients.
 */
public record PowerGridSyncPacket(
        ResourceKey<Level> dimension,
        List<PowerCableEdge> edges
) implements CustomPacketPayload {

    public static final Type<PowerGridSyncPacket> TYPE = new Type<>(
            WitherUtils.loc("power_grid_sync")
    );

    public static final StreamCodec<ByteBuf, PowerGridSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), PowerGridSyncPacket::dimension,
            PowerCableEdge.STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)), PowerGridSyncPacket::edges,
            PowerGridSyncPacket::new
    );

    @Override
    public Type<PowerGridSyncPacket> type() {
        return TYPE;
    }
}
