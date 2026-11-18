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

public record EditScreenPacket(BlockPos pos, String url) implements CustomPacketPayload {

    public static final Type<EditScreenPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "edit_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EditScreenPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, EditScreenPacket::pos,
                    ByteBufCodecs.STRING_UTF8, EditScreenPacket::url,
                    EditScreenPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EditScreenPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            String url = payload.url();
            if (url == null || url.length() > 2048) return;

            Level level = player.level();
            BlockPos pos = payload.pos();
            if (!level.isLoaded(pos)) return;
            if (!player.mayBuild()) return;
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > ScreenBlockEntity.REMOTE_MAX_DISTANCE_SQR) return;
            if (!(level.getBlockEntity(pos) instanceof ScreenBlockEntity screen)) return;
            if (!screen.isWallPowered()) return;

            screen.applyImageIdToWall(url.trim());
        });
    }
}
