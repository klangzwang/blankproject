package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Sent from client to server when placing a SupportPole via the hologram preview
 * (carrying the Crouch-locked block position, wall/ceiling orientation, and Shift+Wheel 90° facing).
 */
public record PlaceConveyorPolePacket(
        BlockPos targetPos,
        Direction clickedFace,
        Direction facing,
        boolean ceiling,
        boolean offHand
) implements CustomPacketPayload {

    public static final Type<PlaceConveyorPolePacket> TYPE = new Type<>(
            WitherUtils.loc("place_conveyor_pole")
    );

    public static final StreamCodec<ByteBuf, PlaceConveyorPolePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PlaceConveyorPolePacket::targetPos,
            Direction.STREAM_CODEC, PlaceConveyorPolePacket::clickedFace,
            Direction.STREAM_CODEC, PlaceConveyorPolePacket::facing,
            ByteBufCodecs.BOOL, PlaceConveyorPolePacket::ceiling,
            ByteBufCodecs.BOOL, PlaceConveyorPolePacket::offHand,
            PlaceConveyorPolePacket::new
    );

    @Override
    public @NotNull Type<PlaceConveyorPolePacket> type() {
        return TYPE;
    }
}
