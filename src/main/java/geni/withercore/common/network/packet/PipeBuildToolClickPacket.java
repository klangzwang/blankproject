package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

/**
 * Sent from client to server when right-clicking with PipeBuildToolItem.
 * Ensures blocks exposing IFluidHandler + a GUI Screen (Tanks, Machines, etc.)
 * cannot intercept right-clicks before the PipeBuildToolItem can bind anchors or build pipes.
 */
public record PipeBuildToolClickPacket(
        BlockPos pos,
        Direction face,
        double hitX,
        double hitY,
        double hitZ,
        boolean mainHand,
        boolean clearOnly
) implements CustomPacketPayload {

    public static final Type<PipeBuildToolClickPacket> TYPE = new Type<>(
            WitherUtils.loc("pipe_build_tool_click")
    );

    public static final StreamCodec<ByteBuf, PipeBuildToolClickPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PipeBuildToolClickPacket decode(ByteBuf buf) {
            BlockPos pos = BlockPos.STREAM_CODEC.decode(buf);
            Direction face = Direction.STREAM_CODEC.decode(buf);
            double hx = buf.readDouble();
            double hy = buf.readDouble();
            double hz = buf.readDouble();
            boolean mainHand = buf.readBoolean();
            boolean clearOnly = buf.readBoolean();
            return new PipeBuildToolClickPacket(pos, face, hx, hy, hz, mainHand, clearOnly);
        }

        @Override
        public void encode(ByteBuf buf, PipeBuildToolClickPacket pkt) {
            BlockPos.STREAM_CODEC.encode(buf, pkt.pos());
            Direction.STREAM_CODEC.encode(buf, pkt.face());
            buf.writeDouble(pkt.hitX());
            buf.writeDouble(pkt.hitY());
            buf.writeDouble(pkt.hitZ());
            buf.writeBoolean(pkt.mainHand());
            buf.writeBoolean(pkt.clearOnly());
        }
    };

    public Vec3 hitLocation() {
        return new Vec3(hitX, hitY, hitZ);
    }

    @Override
    public Type<PipeBuildToolClickPacket> type() {
        return TYPE;
    }
}
