package geni.withercore.common.network.packet;

import geni.withercore.common.util.BlockUtil;
import geni.witherutils.WitherUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public record RotateBlockPacket(BlockPos pos, Direction side, InteractionHand hand) implements CustomPacketPayload {

  public static final Type<RotateBlockPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "wrench_rotation"));

  public static final StreamCodec<RegistryFriendlyByteBuf, RotateBlockPacket> STREAM_CODEC = StreamCodec.composite(
          BlockPos.STREAM_CODEC, RotateBlockPacket::pos,
          Direction.STREAM_CODEC, RotateBlockPacket::side,
          NeoForgeStreamCodecs.enumCodec(InteractionHand.class), RotateBlockPacket::hand,
          RotateBlockPacket::new
  );

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }

  public static void handle(RotateBlockPacket message, ServerPlayer player) {
    Level world = player.level();

    boolean success = BlockUtil.rotateBlockValidState(world, message.pos, message.side);

    if (success) {
      world.playSound(
              null,
              message.pos,
              SoundEvents.BLAZE_HURT,
              SoundSource.PLAYERS,
              0.1F + world.random.nextFloat() * 0.1F,
              0.6F + world.random.nextFloat() * 0.2F
      );
    }
  }
}