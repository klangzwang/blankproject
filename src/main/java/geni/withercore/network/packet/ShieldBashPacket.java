package geni.withercore.network.packet;

import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.item.ShieldSteelItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public record ShieldBashPacket(boolean isCounter) implements CustomPacketPayload {

    public static final Type<ShieldBashPacket> TYPE = new Type<>(
            WitherUtils.loc("shield_bash")
    );

    public static final StreamCodec<ByteBuf, ShieldBashPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ShieldBashPacket::isCounter,
            ShieldBashPacket::new
    );

    public ShieldBashPacket() {
        this(false);
    }

    @Override
    public Type<ShieldBashPacket> type() {
        return TYPE;
    }

    public static void handle(ShieldBashPacket packet, ServerPlayer player) {
        ItemStack shieldStack = ItemStack.EMPTY;

        if (player.getUseItem().getItem() instanceof ShieldSteelItem) {
            shieldStack = player.getUseItem();
        } else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ShieldSteelItem) {
            shieldStack = player.getItemInHand(InteractionHand.OFF_HAND);
        } else if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ShieldSteelItem) {
            shieldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        }

        if (shieldStack.isEmpty()) {
            return;
        }

        if (player.getCooldowns().isOnCooldown(shieldStack.getItem())) {
            return;
        }

        // Energy shield check
        if (shieldStack.getItem() instanceof ShieldSteelItem.Energy energyShield) {
            int energy = energyShield.getEnergyStored(shieldStack);
            if (energy < 500) {
                return;
            }
            energyShield.extractEnergy(shieldStack, 500, false);
        }

        if (packet.isCounter()) {
            executeCounterBash(player, (ShieldSteelItem) shieldStack.getItem(), shieldStack);
        } else {
            executeDashBash(player, (ShieldSteelItem) shieldStack.getItem(), shieldStack);
        }
    }

    public static void executeDashBash(ServerPlayer player, ShieldSteelItem shieldItem, ItemStack shieldStack) {
        ServerLevel level = player.serverLevel();
        Vec3 look = player.getLookAngle();

        // Massive frontal ram cone
        AABB hitBox = player.getBoundingBox().inflate(1.8D, 0.8D, 1.8D).move(look.scale(1.4D));
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, hitBox, e -> e != player && e.isAlive() && !player.isAlliedTo(e));

        String[] comicWords = {"BOOM!", "BAM!", "POW!"};
        String comicWord = comicWords[player.getRandom().nextInt(comicWords.length)];

        for (LivingEntity target : targets) {
            float damage = 15.0F;
            target.hurt(player.damageSources().playerAttack(player), damage);
            target.knockback(2.4D, -look.x, -look.z);
            target.setDeltaMovement(target.getDeltaMovement().add(0, 0.42D, 0));
            target.hurtMarked = true;

            // Send comic word and damage numbers
            ComicHitPacket hitPacket = new ComicHitPacket(
                    target.getX(),
                    target.getY() + 1.0D,
                    target.getZ(),
                    damage,
                    comicWord,
                    0 // style 0 = fiery red/orange
            );
            PacketDistributor.sendToPlayersTrackingEntity(target, hitPacket);
            PacketDistributor.sendToPlayer(player, hitPacket);
        }

        // Sounds
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.5F, 0.7F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.8F, 0.85F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundSource.PLAYERS, 1.3F, 0.8F);

        // Visual effects
        level.sendParticles(ParticleTypes.EXPLOSION, player.getX() + look.x * 1.3, player.getY() + 1.0, player.getZ() + look.z * 1.3, 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.CRIT, player.getX() + look.x * 1.3, player.getY() + 0.9, player.getZ() + look.z * 1.3, 25, 0.4, 0.3, 0.4, 0.25);
        level.sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + look.x * 1.1, player.getY() + 0.8, player.getZ() + look.z * 1.1, 3, 0.3, 0.1, 0.3, 0);

        player.getCooldowns().addCooldown(shieldStack.getItem(), 45);
    }

    public static void executeCounterBash(ServerPlayer player, ShieldSteelItem shieldItem, ItemStack shieldStack) {
        ServerLevel level = player.serverLevel();

        // Radial counter blast around player (player stood ground)
        AABB hitBox = player.getBoundingBox().inflate(2.4D, 1.0D, 2.4D);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, hitBox, e -> e != player && e.isAlive() && !player.isAlliedTo(e));

        String comicWord = "DUNG!";

        for (LivingEntity target : targets) {
            float damage = 18.0F;
            target.hurt(player.damageSources().playerAttack(player), damage);

            // Knock back outward from player
            Vec3 away = target.position().subtract(player.position()).normalize();
            target.knockback(2.8D, -away.x, -away.z);
            target.setDeltaMovement(target.getDeltaMovement().add(0, 0.5D, 0));
            target.hurtMarked = true;

            // Send comic word and damage numbers (style 1 = electric cyan counter)
            ComicHitPacket hitPacket = new ComicHitPacket(
                    target.getX(),
                    target.getY() + 1.1D,
                    target.getZ(),
                    damage,
                    comicWord,
                    1
            );
            PacketDistributor.sendToPlayersTrackingEntity(target, hitPacket);
            PacketDistributor.sendToPlayer(player, hitPacket);
        }

        // Distinct Parry Clash Sounds
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 1.2F, 1.6F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.4F, 1.5F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.3F, 1.4F);

        // Visual effects
        level.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1.0, player.getZ(), 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.5, 0.5, 0.5, 0.2);

        player.getCooldowns().addCooldown(shieldStack.getItem(), 30);
    }
}
