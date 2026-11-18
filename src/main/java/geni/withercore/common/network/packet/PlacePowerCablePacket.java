package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * C2S payload sent when completing a power cable connection or Auto-Connect Pole/Plug placement.
 */
public record PlacePowerCablePacket(
        BlockPos startPos,
        BlockPos endPos,
        int autoPlaceModeOrdinal,
        BlockPos placePos,
        int placeFacing3DIndex
) implements CustomPacketPayload {

    public enum AutoPlaceMode {
        NONE,
        POLE,
        PLUG;

        public static AutoPlaceMode fromOrdinal(int ordinal) {
            AutoPlaceMode[] values = values();
            if (ordinal >= 0 && ordinal < values.length) {
                return values[ordinal];
            }
            return NONE;
        }
    }

    public PlacePowerCablePacket(
            BlockPos startPos,
            BlockPos endPos,
            AutoPlaceMode mode,
            BlockPos placePos,
            int placeFacing3DIndex
    ) {
        this(startPos, endPos, mode.ordinal(), placePos, placeFacing3DIndex);
    }

    public AutoPlaceMode autoPlaceMode() {
        return AutoPlaceMode.fromOrdinal(autoPlaceModeOrdinal);
    }

    public static final Type<PlacePowerCablePacket> TYPE = new Type<>(
            WitherUtils.loc("place_power_cable")
    );

    public static final StreamCodec<ByteBuf, PlacePowerCablePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PlacePowerCablePacket::startPos,
            BlockPos.STREAM_CODEC, PlacePowerCablePacket::endPos,
            ByteBufCodecs.VAR_INT, PlacePowerCablePacket::autoPlaceModeOrdinal,
            BlockPos.STREAM_CODEC, PlacePowerCablePacket::placePos,
            ByteBufCodecs.VAR_INT, PlacePowerCablePacket::placeFacing3DIndex,
            PlacePowerCablePacket::new
    );

    @Override
    public Type<PlacePowerCablePacket> type() {
        return TYPE;
    }
}
