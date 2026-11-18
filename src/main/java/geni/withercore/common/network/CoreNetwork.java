package geni.withercore.common.network;

import geni.witherapi.fluid.pipe.PipeAnchor;
import geni.withercore.common.network.dataslot.ClientboundDataSlotChange;
import geni.withercore.common.network.dataslot.ServerboundCDataSlotUpdate;
import geni.withercore.common.network.menu.ClientboundSyncSlotDataPacket;
import geni.withercore.common.network.menu.ServerboundSetSyncSlotDataPacket;
import geni.withercore.common.network.packet.*;
import geni.withercore.common.network.payload.ClientPayloadHandler;
import geni.withercore.common.network.payload.ConveyorPayloadHandler;
import geni.withercore.common.network.payload.ServerPayloadHandler;
import geni.withercore.init.WUTSounds;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.item.PowerCableItem;
import geni.witherutils.common.world.item.pipe.PipeBuildToolItem;
import geni.witherutils.common.world.item.pipe.PipePumpItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = WitherUtils.MODID)
public class CoreNetwork {

	private static final String PROTOCOL_VERSION = "1.0";

	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(WitherUtils.MODID)
				.versioned(PROTOCOL_VERSION)
				.optional();

		registrar.playToClient(EmitParticlePacket.TYPE, EmitParticlePacket.STREAM_CODEC,
				ClientPayloadHandler.getInstance()::handleEmitParticle);

		registrar.playToClient(EmitParticleSplinePacket.TYPE, EmitParticleSplinePacket.STREAM_CODEC,
				ClientPayloadHandler.getInstance()::handleEmitSplineParticle);

		registrar.playToClient(EmitParticlesPacket.TYPE, EmitParticlesPacket.STREAM_CODEC,
				ClientPayloadHandler.getInstance()::handleEmitParticles);

		registrar.playToClient(ServerboundCDataSlotUpdate.TYPE, ServerboundCDataSlotUpdate.STREAM_CODEC,
				ClientPayloadHandler.getInstance()::handleDataSlotUpdate);

		registrar.playToServer(ClientboundDataSlotChange.TYPE, ClientboundDataSlotChange.STREAM_CODEC,
				ServerPayloadHandler.getInstance()::handleDataSlotChange);

		registrar.playToClient(ClientboundSyncSlotDataPacket.TYPE, ClientboundSyncSlotDataPacket.STREAM_CODEC,
				ClientPayloadHandler.getInstance()::handleSyncSlotDataPacket);

		registrar.playToServer(ServerboundSetSyncSlotDataPacket.TYPE, ServerboundSetSyncSlotDataPacket.STREAM_CODEC,
				ServerPayloadHandler.getInstance()::handleSetSyncSlotDataPacket);

		registrar.playToServer(TeleportPacket.TYPE, TeleportPacket.STREAM_CODEC,
				(pkt, ctx) -> TeleportPacket.handle(pkt, (ServerPlayer) ctx.player(), WUTSounds.PLACESTEAM.get()));

		registrar.playToServer(RotateBlockPacket.TYPE, RotateBlockPacket.STREAM_CODEC,
				(pkt, ctx) -> RotateBlockPacket.handle(pkt, (ServerPlayer) ctx.player()));

		registrar.playToServer(ConfigureSlotPacket.TYPE, ConfigureSlotPacket.STREAM_CODEC,
				ConfigureSlotPacket::handle);

		registrar.playToServer(EjectSlotPacket.TYPE, EjectSlotPacket.STREAM_CODEC,
				EjectSlotPacket::handle);

		registrar.playToServer(ServerboundMenuActionPacket.TYPE,
				ServerboundMenuActionPacket.STREAM_CODEC,
				(pkt, ctx) -> ServerboundMenuActionPacket.handle(pkt, (ServerPlayer) ctx.player()));

		//
		// Screen
		registrar.playToServer(EditScreenPacket.TYPE, EditScreenPacket.STREAM_CODEC,
				EditScreenPacket::handle);

		registrar.playToServer(ScreenPowerPacket.TYPE, ScreenPowerPacket.STREAM_CODEC,
				ScreenPowerPacket::handle);

		registrar.playToServer(ScreenImageReadyPacket.TYPE, ScreenImageReadyPacket.STREAM_CODEC,
				ScreenImageReadyPacket::handle);

		//
		// PowerCables
		//
		registrar.playToServer(PlacePowerCablePacket.TYPE, PlacePowerCablePacket.STREAM_CODEC,
				(pkt, ctx) -> ctx.enqueueWork(() -> {
					if (ctx.player() instanceof ServerPlayer sp) {
						Direction placeFacing = Direction.from3DDataValue(pkt.placeFacing3DIndex());
						PowerCableItem.executeServerPlacement(
								sp.serverLevel(),
								sp,
								pkt.startPos(),
								pkt.endPos(),
								pkt.autoPlaceMode(),
								pkt.placePos(),
								placeFacing
						);
					}
				}));

		registrar.playToClient(PowerGridSyncPacket.TYPE, PowerGridSyncPacket.STREAM_CODEC,
				ClientPayloadHandler.getInstance()::handlePowerGridSync);

		//
		// ConveyorBelts
		//
		registrar.playToServer(PlaceConveyorPolePacket.TYPE, PlaceConveyorPolePacket.STREAM_CODEC,
				ConveyorPayloadHandler::handlePlacePole);

		registrar.playToServer(PlaceConveyorPacket.TYPE, PlaceConveyorPacket.STREAM_CODEC,
				ConveyorPayloadHandler::handlePlaceConveyor);

		registrar.playToServer(BreakConveyorBeltPacket.TYPE, BreakConveyorBeltPacket.STREAM_CODEC,
				ConveyorPayloadHandler::handleBreakConveyor);

		registrar.playToClient(ConveyorItemsSyncPacket.TYPE, ConveyorItemsSyncPacket.STREAM_CODEC,
				ConveyorPayloadHandler::handleItemsSync);

		//
		// Pipes
		//
		registrar.playToServer(PlaceFluidPipePacket.TYPE, PlaceFluidPipePacket.STREAM_CODEC,
				(pkt, ctx) -> ctx.enqueueWork(() -> {
					ServerPlayer sp = (ServerPlayer) ctx.player();
					PipeAnchor start = PipeAnchor.fromNbt(pkt.startAnchorTag());
					PipeAnchor end = PipeAnchor.fromNbt(pkt.endAnchorTag());
					Direction facing = Direction.from2DDataValue(pkt.endPoleFacingIndex());
					PipeBuildToolItem.executePlacement(sp.serverLevel(), sp, start, end, pkt.placeEndPole(), pkt.endPolePos(), facing);
				}));

		registrar.playToServer(PlacePipePumpPacket.TYPE, PlacePipePumpPacket.STREAM_CODEC,
				(pkt, ctx) -> ctx.enqueueWork(() -> {
					ServerPlayer sp = (ServerPlayer) ctx.player();
					PipePumpItem.executePumpPlacement(sp.serverLevel(), sp, pkt.pipePos(), pkt.splineT());
				}));

		registrar.playToServer(PipeBuildToolClickPacket.TYPE, PipeBuildToolClickPacket.STREAM_CODEC,
				(pkt, ctx) -> ctx.enqueueWork(() -> {
					ServerPlayer sp = (ServerPlayer) ctx.player();
					PipeBuildToolItem.handleServerClick(
							sp.serverLevel(),
							sp,
							pkt.mainHand(),
							pkt.pos(),
							pkt.face(),
							pkt.hitLocation(),
							pkt.clearOnly()
					);
				}));

		registrar.playToServer(RemovePipePumpPacket.TYPE, RemovePipePumpPacket.STREAM_CODEC,
				(pkt, ctx) -> ctx.enqueueWork(() -> {
					ServerPlayer sp = (ServerPlayer) ctx.player();
					PipePumpItem.executePumpRemoval(sp.serverLevel(), sp, pkt.pipePos(), pkt.pumpIndex());
				}));
	}

	public static void sendToAll(CustomPacketPayload message) {
		PacketDistributor.sendToAllPlayers(message);
	}

	public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, message);
	}

	public static void sendToAllTracking(CustomPacketPayload message, Entity entity) {
		PacketDistributor.sendToPlayersTrackingEntity(entity, message);
	}

	public static void sendToAllTracking(CustomPacketPayload message, Level level, BlockPos pos) {
		if (level instanceof ServerLevel serverLevel) {
			PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(pos), message);
		}
	}

	public static void sendToAllTracking(CustomPacketPayload message, BlockEntity te) {
		if (te.getLevel() != null) {
			sendToAllTracking(message, te.getLevel(), te.getBlockPos());
		}
	}

	public static void sendToDimension(CustomPacketPayload message, ServerLevel level) {
		PacketDistributor.sendToPlayersInDimension(level, message);
	}

	public static void sendToServer(CustomPacketPayload message) {
		PacketDistributor.sendToServer(message);
	}

	public static void sendNonLocal(CustomPacketPayload packet) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			if (server.isDedicatedServer()) {
				sendToAll(packet);
			} else {
				for (ServerPlayer player : server.getPlayerList().getPlayers()) {
					if (!player.server.isSingleplayerOwner(player.getGameProfile())) {
						sendToPlayer(packet, player);
					}
				}
			}
		}
	}

	public static void sendNonLocal(ServerPlayer player, CustomPacketPayload packet) {
		if (!player.server.isSingleplayerOwner(player.getGameProfile())) {
			sendToPlayer(packet, player);
		}
	}
}
