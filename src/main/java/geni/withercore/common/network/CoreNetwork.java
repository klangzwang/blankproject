package geni.withercore.common.network;

import geni.withercore.common.network.dataslot.ClientboundDataSlotChange;
import geni.withercore.common.network.dataslot.ServerboundCDataSlotUpdate;
import geni.withercore.common.network.menu.ClientboundSyncSlotDataPacket;
import geni.withercore.common.network.menu.ServerboundSetSyncSlotDataPacket;
import geni.withercore.common.network.packet.PacketPlayMovingSound;
import geni.withercore.common.network.packet.PacketPlaySound;
import geni.withercore.common.network.packet.RotateBlockPacket;
import geni.withercore.common.network.packet.TeleportPacket;
import geni.withercore.common.network.payload.ClientPayloadHandler;
import geni.withercore.common.network.payload.ServerPayloadHandler;
import geni.withercore.init.WUTSounds;
import geni.witherutils.WitherUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.PacketFlow;
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

		registrar.playToClient(PacketPlaySound.TYPE, PacketPlaySound.STREAM_CODEC,
				PacketPlaySound::handle);

		registrar.playToClient(PacketPlayMovingSound.TYPE, PacketPlayMovingSound.STREAM_CODEC,
				PacketPlayMovingSound::handle);
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
