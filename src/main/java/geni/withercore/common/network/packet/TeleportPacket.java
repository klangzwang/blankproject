package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.level.block.AngelBlock;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public record TeleportPacket(BlockPos from, BlockPos to) implements CustomPacketPayload {

    public static final Type<TeleportPacket> TYPE = new Type<>(
            WitherUtils.loc("teleport_request")
    );

    public static final StreamCodec<ByteBuf, TeleportPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            TeleportPacket::from,
            BlockPos.STREAM_CODEC,
            TeleportPacket::to,
            TeleportPacket::new
    );

    @Override
    public Type<TeleportPacket> type() {
        return TYPE;
    }

    public static void handle(TeleportPacket message, ServerPlayer player, SoundEvent soundEvent) {
        if (isBadTeleportPacket(message, player)) {
            return;
        }

        ServerLevel world = player.serverLevel();
        BlockPos toPos = message.to();
        BlockState toState = world.getBlockState(message.to());

        double blockYOffset = toState.getBlockSupportShape(world, toPos).max(Direction.Axis.Y);
        double toY = Math.max(toPos.getY(), toPos.getY() + blockYOffset);

        player.teleportTo(
                world,
                toPos.getX() + .5D,
                toY,
                toPos.getZ() + .5D,
                0,
                0
        );

        player.setDeltaMovement(player.getDeltaMovement().multiply(new Vec3(1D, 0D, 1D)));

        world.playSound(null, toPos, soundEvent, SoundSource.BLOCKS, 1F, 1F);
    }

    private static boolean isBadTeleportPacket(TeleportPacket msg, Player player) {
        if (player == null || !player.isAlive())
            return true;

        Level world = player.level();
        BlockPos fromPos = msg.from();
        BlockPos toPos = msg.to();

        if (!world.isLoaded(fromPos) || !world.isLoaded(toPos))
            return true;

        if (player.blockPosition().distManhattan(fromPos) > 6)
            return true;

        if (fromPos.getX() != toPos.getX() || fromPos.getZ() != toPos.getZ())
            return true;

        if (fromPos.getY() == toPos.getY())
            return true;

        /*ElevatorBlockBase fromElevator = getElevator(world.getBlockState(fromPos));
        ElevatorBlockBase toElevator = getElevator(world.getBlockState(toPos));*/

        /*if (fromElevator == null || toElevator == null)
            return true;*/

        if (!isValidPos(world, toPos))
            return true;

        return false; // Config.GENERAL.sameColor.get() && fromElevator.getColor() != toElevator.getColor();
    }

    public static boolean isValidPos(BlockGetter world, BlockPos pos) {
        return !world.getBlockState(pos.above()).isSuffocating(world, pos.above());
    }

    public static AngelBlock getAngel(BlockState blockState) {
        if (blockState.getBlock() instanceof AngelBlock angel) {
            return angel;
        }
        return null;
    }
}
