package geni.withercore.network.menu;

import geni.withercore.network.menu.payload.SlotPayload;
import geni.witherutils.WitherUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundSetSyncSlotDataPacket(int containerId, short index, SlotPayload payload)
        implements CustomPacketPayload {

    public static Type<ServerboundSetSyncSlotDataPacket> TYPE = new Type<>(WitherUtils.loc("set_slot_data"));

    public static StreamCodec<RegistryFriendlyByteBuf, ServerboundSetSyncSlotDataPacket> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.INT, ServerboundSetSyncSlotDataPacket::containerId, ByteBufCodecs.SHORT,
                    ServerboundSetSyncSlotDataPacket::index, SlotPayload.STREAM_CODEC,
                    ServerboundSetSyncSlotDataPacket::payload, ServerboundSetSyncSlotDataPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
