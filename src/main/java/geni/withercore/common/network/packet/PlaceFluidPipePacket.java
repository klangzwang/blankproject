package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Sent from client to server to build a spline fluid pipe between two anchors
 * (optionally auto-placing a PipePoleBlock at the end anchor).
 */
public record PlaceFluidPipePacket(
        CompoundTag startAnchorTag,
        CompoundTag endAnchorTag,
        boolean placeEndPole,
        BlockPos endPolePos,
        int endPoleFacingIndex
) implements CustomPacketPayload {

    public static final Type<PlaceFluidPipePacket> TYPE = new Type<>(
            WitherUtils.loc("place_fluid_pipe")
    );

    public static final StreamCodec<ByteBuf, PlaceFluidPipePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            PlaceFluidPipePacket::startAnchorTag,
            ByteBufCodecs.COMPOUND_TAG,
            PlaceFluidPipePacket::endAnchorTag,
            ByteBufCodecs.BOOL,
            PlaceFluidPipePacket::placeEndPole,
            BlockPos.STREAM_CODEC,
            PlaceFluidPipePacket::endPolePos,
            ByteBufCodecs.VAR_INT,
            PlaceFluidPipePacket::endPoleFacingIndex,
            PlaceFluidPipePacket::new
    );

    @Override
    public Type<PlaceFluidPipePacket> type() {
        return TYPE;
    }
}
