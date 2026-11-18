package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Lightweight Server -> Client packet synchronizing active items and arc-length progress on a belt.
 */
public record ConveyorItemsSyncPacket(
        BlockPos beltPos,
        List<SyncedItem> items
) implements CustomPacketPayload {

    public record SyncedItem(ItemStack stack, float s, float prevS, long lastMovedGameTime) {
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncedItem> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, SyncedItem::stack,
                ByteBufCodecs.FLOAT, SyncedItem::s,
                ByteBufCodecs.FLOAT, SyncedItem::prevS,
                ByteBufCodecs.VAR_LONG, SyncedItem::lastMovedGameTime,
                SyncedItem::new
        );
    }

    public static final Type<ConveyorItemsSyncPacket> TYPE = new Type<>(
            WitherUtils.loc("conveyor_items_sync")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ConveyorItemsSyncPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ConveyorItemsSyncPacket::beltPos,
            SyncedItem.STREAM_CODEC.apply(ByteBufCodecs.list()), ConveyorItemsSyncPacket::items,
            ConveyorItemsSyncPacket::new
    );

    @Override
    public @NotNull Type<ConveyorItemsSyncPacket> type() {
        return TYPE;
    }
}
