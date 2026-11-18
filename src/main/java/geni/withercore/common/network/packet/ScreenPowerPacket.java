package geni.withercore.common.network.packet;

import geni.withercore.common.util.SoundUtil;
import geni.withercore.init.WUTSounds;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.level.block.screen.ScreenBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ScreenPowerPacket(BlockPos pos, boolean powered) implements CustomPacketPayload {

    public static final Type<ScreenPowerPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "screen_power"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ScreenPowerPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ScreenPowerPacket::pos,
                    ByteBufCodecs.BOOL, ScreenPowerPacket::powered,
                    ScreenPowerPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ScreenPowerPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            Level level = player.level();
            BlockPos pos = payload.pos();
            if (!level.isLoaded(pos)) return;
            if (!player.mayBuild()) return;
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > ScreenBlockEntity.REMOTE_MAX_DISTANCE_SQR) return;
            if (!(level.getBlockEntity(pos) instanceof ScreenBlockEntity screen)) return;

            if (payload.powered() && screen.getWallEnergy() <= 0) {
                SoundUtil.playServerSound(level, pos, WUTSounds.DRONEBEEPOFF.get(), 0.5F, 1.0F);
                player.displayClientMessage(Component.literal("§cScreen hat keinen Strom (0 / 50.000 FE)"), true);
                return;
            }

            screen.setWallPower(payload.powered());
            SoundUtil.playServerSound(
                    level,
                    pos,
                    payload.powered() ? WUTSounds.DRONEBEEPON.get() : WUTSounds.DRONEBEEPOFF.get(),
                    0.5F,
                    1.0F
            );
        });
    }
}
