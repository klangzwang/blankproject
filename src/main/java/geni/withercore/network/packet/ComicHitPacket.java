package geni.withercore.network.packet;

import geni.witherutils.WitherUtils;
import geni.witherutils.client.render.effect.ComicPopupManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public record ComicHitPacket(double x, double y, double z, float damage, String comicWord, int style) implements CustomPacketPayload {

    public static final Type<ComicHitPacket> TYPE = new Type<>(
            WitherUtils.loc("comic_hit")
    );

    public static final StreamCodec<ByteBuf, ComicHitPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, ComicHitPacket::x,
            ByteBufCodecs.DOUBLE, ComicHitPacket::y,
            ByteBufCodecs.DOUBLE, ComicHitPacket::z,
            ByteBufCodecs.FLOAT, ComicHitPacket::damage,
            ByteBufCodecs.STRING_UTF8, ComicHitPacket::comicWord,
            ByteBufCodecs.VAR_INT, ComicHitPacket::style,
            ComicHitPacket::new
    );

    @Override
    public Type<ComicHitPacket> type() {
        return TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(ComicHitPacket packet) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        // Colors: style 0 = fiery red/orange (bash), style 1 = electric cyan/gold (parry), style 2 = yellow/magenta (frisbee)
        int wordColor = switch (packet.style()) {
            case 1 -> 0xFF38BDF8; // Cyan parry clash
            case 2 -> 0xFFFACC15; // Yellow frisbee slice
            default -> 0xFFFF4500; // Fiery Orange-Red bash
        };

        // 1. Comic Action Word (e.g., "POW!", "BOOM!", "BAM!", "DUNG!")
        if (!packet.comicWord().isEmpty()) {
            ComicPopupManager.addPopup(
                    packet.x(),
                    packet.y() + 0.45D,
                    packet.z(),
                    packet.comicWord(),
                    wordColor,
                    true
            );
        }

        // 2. Damage point number (e.g. "-15.0")
        if (packet.damage() > 0.0F) {
            String dmgStr = String.format("-%.1f", packet.damage());
            ComicPopupManager.addPopup(
                    packet.x() + (level.random.nextDouble() - 0.5D) * 0.25D,
                    packet.y() + 0.95D,
                    packet.z() + (level.random.nextDouble() - 0.5D) * 0.25D,
                    dmgStr,
                    0xFFFFFFFF,
                    false
            );
        }

        // Particle sparks at hit coordinates
        for (int i = 0; i < 8; i++) {
            level.addParticle(
                    packet.style() == 1 ? ParticleTypes.ELECTRIC_SPARK : ParticleTypes.CRIT,
                    packet.x(),
                    packet.y() + 0.5D,
                    packet.z(),
                    (level.random.nextDouble() - 0.5D) * 0.15D,
                    level.random.nextDouble() * 0.1D,
                    (level.random.nextDouble() - 0.5D) * 0.15D
            );
        }
    }
}
