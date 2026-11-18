package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.level.block.screen.ScreenBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ScreenImageReadyPacket(BlockPos pos, boolean ready) implements CustomPacketPayload {

    public static final Type<ScreenImageReadyPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "screen_image_ready"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ScreenImageReadyPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ScreenImageReadyPacket::pos,
                    ByteBufCodecs.BOOL, ScreenImageReadyPacket::ready,
                    ScreenImageReadyPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ScreenImageReadyPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            Level level = player.level();
            BlockPos pos = payload.pos();
            if (!level.isLoaded(pos)) return;
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 130.0D * 130.0D) {
                return;
            }
            if (!(level.getBlockEntity(pos) instanceof ScreenBlockEntity screen)) return;

            screen.setWallImageReady(payload.ready());
        });
    }
}
