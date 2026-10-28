package geni.withercore.network.packet;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * Either left(entityId)  -> Drohne angedockt, hier ihre aktuelle Entity-ID.
 * Either right(blockPos) -> Referenz auf die Platform-Position (kein Drone).
 * NONE                   -> id = -1, keine Drohne.
 *
 * Either ist nie null, damit umgehen wir das Null-Problem im NetworkDataSlot.
 */
public record DockedDroneTarget(Either<Integer, BlockPos> idOrPos) {

    public static final DockedDroneTarget NONE = new DockedDroneTarget(Either.left(-1));

    public static final Codec<DockedDroneTarget> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.either(Codec.INT, BlockPos.CODEC).fieldOf("id_or_pos").forGetter(DockedDroneTarget::idOrPos)
    ).apply(builder, DockedDroneTarget::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DockedDroneTarget> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.either(ByteBufCodecs.INT, BlockPos.STREAM_CODEC), DockedDroneTarget::idOrPos,
                    DockedDroneTarget::new
            );

    public static DockedDroneTarget forEntityId(int entityId) {
        return new DockedDroneTarget(Either.left(entityId));
    }

    public static DockedDroneTarget forPos(BlockPos pos) {
        return new DockedDroneTarget(Either.right(pos));
    }

    public static DockedDroneTarget none() {
        return NONE;
    }

    public int entityId() {
        return idOrPos.left().orElse(-1);
    }

    public BlockPos pos() {
        return idOrPos.right().orElse(null);
    }

    public boolean isNone() {
        return entityId() < 0;
    }

    /** Löst die Drohne client- wie serverseitig über die Entity-ID auf. O(1). */
    public Entity getEntity(Level level) {
        if (isNone() || level == null) return null;
        return level.getEntity(entityId());
    }
}