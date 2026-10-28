package geni.withercore.common.network.packet;

import com.mojang.datafixers.util.Either;
import geni.witherutils.WitherUtils;
import geni.witherutils.client.sound.MovingSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

/**
 * Received on: CLIENT
 * Sent by server to start a new MovingSound playing
 */
public record PacketPlayMovingSound(MovingSounds.Sound sound, MovingSoundFocus source) implements CustomPacketPayload {
    public static final Type<PacketPlayMovingSound> TYPE = new Type<>(WitherUtils.loc("play_moving_sound"));

    public static final StreamCodec<FriendlyByteBuf, PacketPlayMovingSound> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(MovingSounds.Sound.class), PacketPlayMovingSound::sound,
            MovingSoundFocus.STREAM_CODEC, PacketPlayMovingSound::source,
            PacketPlayMovingSound::new
    );

    @Override
    public Type<PacketPlayMovingSound> type() {
        return TYPE;
    }

    public static void handle(PacketPlayMovingSound message, IPayloadContext ctx) {
        if (message.source() != null) message.source().handle(message.sound());
    }

    public record MovingSoundFocus(Either<Integer,BlockPos> entityOrPos) {
        public static final StreamCodec<FriendlyByteBuf, MovingSoundFocus> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.either(ByteBufCodecs.INT, BlockPos.STREAM_CODEC), MovingSoundFocus::entityOrPos,
                MovingSoundFocus::new
        );

        public static MovingSoundFocus of(Entity e) {
            return new MovingSoundFocus(Either.left(e.getId()));
        }

        public static MovingSoundFocus of(int id) {
            return new MovingSoundFocus(Either.left(id));
        }

        public static MovingSoundFocus of(BlockPos pos) {
            return new MovingSoundFocus(Either.right(pos));
        }

        public static MovingSoundFocus of(BlockEntity te) {
            return new MovingSoundFocus(Either.right(te.getBlockPos()));
        }

        public void handle(MovingSounds.Sound sound) {
            entityOrPos
                    .ifLeft(id -> {
                        Entity e = Objects.requireNonNull(Minecraft.getInstance().level).getEntity(id);
                        if (e != null) {
                            MovingSounds.playMovingSound(sound, e);
                        }
                    })
                    .ifRight(pos -> MovingSounds.playMovingSound(sound, pos));
        }
    }
}
