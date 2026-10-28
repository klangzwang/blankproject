package geni.withercore.common.network.packet;

import geni.witherutils.WitherUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Received on: CLIENT
 * Sent by server to play a sound at a specific location
 */
public record PacketPlaySound(SoundEvent soundEvent, SoundSource category, float x, float y, float z, float volume, float pitch, boolean distanceDelay) implements CustomPacketPayload {
    public static final Type<PacketPlaySound> TYPE = new Type<>(WitherUtils.loc("play_sound"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketPlaySound> STREAM_CODEC = StreamCodec.of(
            PacketPlaySound::toNetwork,
            PacketPlaySound::fromNetwork
    );

    public PacketPlaySound(SoundEvent soundEvent, SoundSource category, BlockPos pos, float volume, float pitch, boolean distanceDelay) {
        this(soundEvent, category, pos.getX(), pos.getY(), pos.getZ(), volume, pitch, distanceDelay);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, PacketPlaySound packet) {
        SoundEvent.STREAM_CODEC.encode(buffer, Holder.direct(packet.soundEvent));
        buffer.writeEnum(packet.category);
        buffer.writeFloat(packet.x);
        buffer.writeFloat(packet.y);
        buffer.writeFloat(packet.z);
        buffer.writeFloat(packet.volume);
        buffer.writeFloat(packet.pitch);
        buffer.writeBoolean(packet.distanceDelay);
    }

    private static PacketPlaySound fromNetwork(RegistryFriendlyByteBuf buffer) {
        return new PacketPlaySound(SoundEvent.STREAM_CODEC.decode(buffer).value(), buffer.readEnum(SoundSource.class),
                buffer.readFloat(), buffer.readFloat(), buffer.readFloat(),
                buffer.readFloat(), buffer.readFloat(), buffer.readBoolean());
    }

    @Override
    public Type<PacketPlaySound> type() {
        return TYPE;
    }

    public static void handle(PacketPlaySound message, IPayloadContext ctx) {
        ctx.player().level().playLocalSound(
                message.x, message.y, message.z,
                message.soundEvent, message.category,
                message.volume, message.pitch, message.distanceDelay
        );
    }
}
