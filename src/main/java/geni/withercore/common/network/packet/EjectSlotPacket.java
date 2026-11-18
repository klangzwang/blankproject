package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.level.block.controller.SlotControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EjectSlotPacket(BlockPos pos, int slotIndex) implements CustomPacketPayload {

    public static final Type<EjectSlotPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "eject_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EjectSlotPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, EjectSlotPacket::pos,
                    ByteBufCodecs.VAR_INT, EjectSlotPacket::slotIndex,
                    EjectSlotPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EjectSlotPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            Level level = player.level();
            BlockPos pos = payload.pos();

            if (!level.isLoaded(pos)) return;
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) return;
            if (!(level.getBlockEntity(pos) instanceof SlotControllerBlockEntity controller)) return;

            controller.manualEjectSlot(payload.slotIndex());
        });
    }
}

