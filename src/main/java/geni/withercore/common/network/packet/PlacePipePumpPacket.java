package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Sent from client to server to mount a pump strictly at normalized arc length splineT in [0, 1]
 * onto an existing FluidPipeBlockEntity.
 */
public record PlacePipePumpPacket(
        BlockPos pipePos,
        float splineT
) implements CustomPacketPayload {

    public static final Type<PlacePipePumpPacket> TYPE = new Type<>(
            WitherUtils.loc("place_pipe_pump")
    );

    public static final StreamCodec<ByteBuf, PlacePipePumpPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            PlacePipePumpPacket::pipePos,
            ByteBufCodecs.FLOAT,
            PlacePipePumpPacket::splineT,
            PlacePipePumpPacket::new
    );

    @Override
    public Type<PlacePipePumpPacket> type() {
        return TYPE;
    }
}
