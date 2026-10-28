package geni.withercore.network.packet;

import geni.witherapi.lib.Pair;
import geni.withercore.init.WUTSounds;
import geni.withercore.util.SoundUtil;
import geni.witherutils.WitherUtils;
import geni.witherutils.client.events.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public record ShieldHitPacket(int playerId, float shieldPower, float damage) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ShieldHitPacket> TYPE =
            new CustomPacketPayload.Type<>(WitherUtils.loc("shield_hit"));

    public static final StreamCodec<FriendlyByteBuf, ShieldHitPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ShieldHitPacket::playerId,
            ByteBufCodecs.FLOAT, ShieldHitPacket::shieldPower,
            ByteBufCodecs.FLOAT, ShieldHitPacket::damage,
            ShieldHitPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Wird auf dem Client ausgeführt!
    public static void handle(ShieldHitPacket packet) {
        if (Minecraft.getInstance().level == null) return;

        Entity entity = Minecraft.getInstance().level.getEntity(packet.playerId());
        if (entity instanceof Player player) {
            // Schild-Status im ClientEventHandler eintragen
            ClientEventHandler.playerShieldStatus.put(player, new Pair<>(packet.shieldPower(), ClientEventHandler.elapsedTicks));

            SoundUtil.playImpactSound(player, packet.damage());

            // Sound abspielen
            /*player.level().playSound(
                    player,
                    player.getX(), player.getY(), player.getZ(),
                    WUTSounds.SHIELD_STRIKE.get(),
                    SoundSource.PLAYERS,
                    0.9F,
                    player.level().random.nextFloat() * 0.1F + 0.5F + (0.3F * packet.shieldPower())
            );*/
        }
    }
}