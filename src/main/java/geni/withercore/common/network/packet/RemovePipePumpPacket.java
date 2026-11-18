package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Sent from client to server when dismantling an embedded pump via raycast hit without breaking the pipe.
 */
public record RemovePipePumpPacket(
        BlockPos pipePos,
        int pumpIndex
) implements CustomPacketPayload {

    public static final Type<RemovePipePumpPacket> TYPE = new Type<>(
            WitherUtils.loc("remove_pipe_pump")
    );

    public static final StreamCodec<ByteBuf, RemovePipePumpPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            RemovePipePumpPacket::pipePos,
            ByteBufCodecs.VAR_INT,
            RemovePipePumpPacket::pumpIndex,
            RemovePipePumpPacket::new
    );

    @Override
    public Type<RemovePipePumpPacket> type() {
        return TYPE;
    }
}
