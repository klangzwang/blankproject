package geni.withercore.common.network.payload;

import geni.withercore.common.network.packet.BreakConveyorBeltPacket;
import geni.withercore.common.network.packet.ConveyorItemsSyncPacket;
import geni.withercore.common.network.packet.PlaceConveyorPacket;
import geni.withercore.common.network.packet.PlaceConveyorPolePacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Decoupled network dispatcher in withercore connecting CoreNetwork payloads to witherutils handlers.
 */
public final class ConveyorPayloadHandler {

    @Nullable
    private static BiConsumer<ServerPlayer, PlaceConveyorPolePacket> placePoleHandler;

    @Nullable
    private static BiConsumer<ServerPlayer, PlaceConveyorPacket> placeConveyorHandler;

    @Nullable
    private static BiConsumer<ServerPlayer, BreakConveyorBeltPacket> breakConveyorHandler;

    @Nullable
    private static Consumer<ConveyorItemsSyncPacket> clientItemsSyncHandler;

    private ConveyorPayloadHandler() {}

    public static void registerServerHandlers(
            BiConsumer<ServerPlayer, PlaceConveyorPolePacket> placePole,
            BiConsumer<ServerPlayer, PlaceConveyorPacket> placeConveyor,
            BiConsumer<ServerPlayer, BreakConveyorBeltPacket> breakConveyor
    ) {
        placePoleHandler = placePole;
        placeConveyorHandler = placeConveyor;
        breakConveyorHandler = breakConveyor;
    }

    public static void registerPlacePoleHandler(BiConsumer<ServerPlayer, PlaceConveyorPolePacket> placePole) {
        placePoleHandler = placePole;
    }

    public static void registerPlaceConveyorHandler(BiConsumer<ServerPlayer, PlaceConveyorPacket> placeConveyor) {
        placeConveyorHandler = placeConveyor;
    }

    public static void registerBreakConveyorHandler(BiConsumer<ServerPlayer, BreakConveyorBeltPacket> breakConveyor) {
        breakConveyorHandler = breakConveyor;
    }

    public static void registerClientItemsSyncHandler(Consumer<ConveyorItemsSyncPacket> handler) {
        clientItemsSyncHandler = handler;
    }

    public static void handlePlacePole(final PlaceConveyorPolePacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp && placePoleHandler != null) {
                placePoleHandler.accept(sp, packet);
            }
        });
    }

    public static void handlePlaceConveyor(final PlaceConveyorPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp && placeConveyorHandler != null) {
                placeConveyorHandler.accept(sp, packet);
            }
        });
    }

    public static void handleBreakConveyor(final BreakConveyorBeltPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sp && breakConveyorHandler != null) {
                breakConveyorHandler.accept(sp, packet);
            }
        });
    }

    public static void handleItemsSync(final ConveyorItemsSyncPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (clientItemsSyncHandler != null) {
                clientItemsSyncHandler.accept(packet);
            }
        });
    }
}
