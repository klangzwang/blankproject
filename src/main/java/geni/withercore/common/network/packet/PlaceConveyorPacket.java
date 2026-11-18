package geni.withercore.common.network.packet;

import geni.witherapi.spline.BeltTier;
import geni.witherapi.spline.SplineAnchorPoint;
import geni.witherutils.WitherUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Packet sent from client to server when completing a 2-click conveyor belt connection
 * between two valid IItemHandler anchors (SupportPole sockets or container faces).
 */
public record PlaceConveyorPacket(
        SplineAnchorPoint startAnchor,
        SplineAnchorPoint endAnchor,
        BeltTier tier
) implements CustomPacketPayload {

    public static final Type<PlaceConveyorPacket> TYPE = new Type<>(
            WitherUtils.loc("place_conveyor")
    );

    public static final StreamCodec<ByteBuf, PlaceConveyorPacket> STREAM_CODEC = StreamCodec.composite(
            SplineAnchorPoint.STREAM_CODEC, PlaceConveyorPacket::startAnchor,
            SplineAnchorPoint.STREAM_CODEC, PlaceConveyorPacket::endAnchor,
            BeltTier.STREAM_CODEC, PlaceConveyorPacket::tier,
            PlaceConveyorPacket::new
    );

    @Override
    public @NotNull Type<PlaceConveyorPacket> type() {
        return TYPE;
    }
}
