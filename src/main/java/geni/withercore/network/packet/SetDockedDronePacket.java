package geni.withercore.network.packet;

import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.level.block.entity.DronePlatformDockingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * Server -> Client: setzt den aktuellen Dock-Zustand einer Platform.
 * Enthält nur primitive Werte, kein null, kein UUID.
 */
public record SetDockedDronePacket(BlockPos platformPos, DockedDroneTarget target)
        implements CustomPacketPayload {

    public static final Type<SetDockedDronePacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "set_docked_drone"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetDockedDronePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, SetDockedDronePacket::platformPos,
                    DockedDroneTarget.STREAM_CODEC, SetDockedDronePacket::target,
                    SetDockedDronePacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** Wird auf der Client-Seite im Payload-Handler aufgerufen. */
    public static void handle(SetDockedDronePacket msg, Level level) {
        if (level.getBlockEntity(msg.platformPos()) instanceof DronePlatformDockingBlockEntity be) {
            be.setClientDockedTarget(msg.target());
        }
    }
}