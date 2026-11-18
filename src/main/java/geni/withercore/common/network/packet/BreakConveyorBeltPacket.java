package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Sent from client to server when left-clicking a conveyor spline hit via client spline raycasting (Option B).
 */
public record BreakConveyorBeltPacket(BlockPos beltPos) implements CustomPacketPayload {

    public static final Type<BreakConveyorBeltPacket> TYPE = new Type<>(
            WitherUtils.loc("break_conveyor_belt")
    );

    public static final StreamCodec<ByteBuf, BreakConveyorBeltPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BreakConveyorBeltPacket::beltPos,
            BreakConveyorBeltPacket::new
    );

    @Override
    public @NotNull Type<BreakConveyorBeltPacket> type() {
        return TYPE;
    }
}
