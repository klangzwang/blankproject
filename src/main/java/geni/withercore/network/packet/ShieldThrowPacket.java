package geni.withercore.network.packet;

import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.entity.projectile.ThrownSteelShield;
import geni.witherutils.common.world.item.ShieldSteelItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public record ShieldThrowPacket() implements CustomPacketPayload {

    public static final Type<ShieldThrowPacket> TYPE = new Type<>(
            WitherUtils.loc("shield_throw")
    );

    public static final StreamCodec<ByteBuf, ShieldThrowPacket> STREAM_CODEC = StreamCodec.unit(
            new ShieldThrowPacket()
    );

    @Override
    public Type<ShieldThrowPacket> type() {
        return TYPE;
    }

    public static void handle(ShieldThrowPacket packet, ServerPlayer player) {
        ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!(mainHandStack.getItem() instanceof ShieldSteelItem)) {
            return;
        }

        if (player.getCooldowns().isOnCooldown(mainHandStack.getItem())) {
            return;
        }

        int originSlot = player.getInventory().selected;
        boolean offhandEmpty = player.getItemInHand(InteractionHand.OFF_HAND).isEmpty();

        ItemStack throwCopy = mainHandStack.copy();
        throwCopy.setCount(1);
        mainHandStack.shrink(1);

        ThrownSteelShield thrownShield = new ThrownSteelShield(player.level(), player, throwCopy, originSlot, offhandEmpty);
        Vec3 look = player.getLookAngle();
        thrownShield.setPos(player.getX(), player.getEyeY() - 0.15D, player.getZ());
        thrownShield.setDeltaMovement(look.scale(1.35D));
        player.level().addFreshEntity(thrownShield);

        // Sound effects
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.1F, 1.3F);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.2F, 1.1F);

        player.getCooldowns().addCooldown(throwCopy.getItem(), 30);
    }
}
