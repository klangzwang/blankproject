package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.level.block.controller.SlotControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ConfigureSlotPacket(BlockPos pos, int slotIndex, boolean reserved, List<ResourceLocation> allowedItemIds) implements CustomPacketPayload {

    public static final Type<ConfigureSlotPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "configure_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigureSlotPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ConfigureSlotPacket::pos,
                    ByteBufCodecs.VAR_INT, ConfigureSlotPacket::slotIndex,
                    ByteBufCodecs.BOOL, ConfigureSlotPacket::reserved,
                    ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), ConfigureSlotPacket::allowedItemIds,
                    ConfigureSlotPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ConfigureSlotPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            Level level = player.level();
            BlockPos pos = payload.pos();

            if (!level.isLoaded(pos)) return;
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) return;
            if (!(level.getBlockEntity(pos) instanceof SlotControllerBlockEntity controller)) return;

            Set<Item> allowedItems = new HashSet<>();
            for (ResourceLocation id : payload.allowedItemIds()) {
                if (BuiltInRegistries.ITEM.containsKey(id)) {
                    allowedItems.add(BuiltInRegistries.ITEM.get(id));
                }
            }

            controller.setReservation(payload.slotIndex(), payload.reserved(), allowedItems);
        });
    }
}

