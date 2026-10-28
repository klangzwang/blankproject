package geni.witherutils.client.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import geni.withercore.init.WUTParticles;
import geni.withercore.math.Vec3D;
import geni.withercore.util.ParticleUtil;
import geni.witherutils.client.entity.model.DroneModel;
import geni.witherutils.client.render.effect.particle.DownwashParticle;
import geni.witherutils.client.render.layer.DroneStatusLampLayer;
import geni.witherutils.common.world.entity.drone.DroneDeliverEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes; // Oder dein eigener Partikel-Typ
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class DroneRenderer extends GeoEntityRenderer<DroneDeliverEntity> {

    private static final String[] PARTICLE_BONES = {
            "particlefl", "particlefr", "particlerl", "particlerr"
    };

    public DroneRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DroneModel());
        this.shadowRadius = 0.5F;
        this.scaleWidth = 1.0f;
        this.scaleHeight = 1.0f;

        this.addRenderLayer(new AutoGlowingGeoLayer<>(this) {
            @Override
            public void render(PoseStack poseStack, DroneDeliverEntity animatable, BakedGeoModel bakedModel,
                               RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                               float partialTick, int packedLight, int packedOverlay) {
                if (animatable.isLightsEnabled()) {
                    super.render(poseStack, animatable, bakedModel, renderType, bufferSource, buffer,
                            partialTick, packedLight, packedOverlay);
                }
            }
        });

        /*this.addRenderLayer(new DroneStatusLampLayer(this));*/
    }

    @Override
    public void render(DroneDeliverEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        /*if (entity.getDroneState() == DroneDeliverEntity.STATE_OFF) {
            return;
        }*/

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if (entity.level().isClientSide() && entity.isFlying()) {
            /*spawnParticles(entity);*/
        }
    }

    private Vec3 getBoneWorldPosition(DroneDeliverEntity entity, PoseStack poseStack, GeoBone bone) {
        Vector4f localPos = new Vector4f(0, 0, 0, 1.0f);
        bone.getWorldSpaceMatrix().transform(localPos);
        Vec3 offset = new Vec3(localPos.x(), localPos.y(), localPos.z())
                .yRot((float) -Math.toRadians(entity.getYRot()));
        return entity.position().add(offset);
    }

    private static final Vec3[] ROTOR_OFFSETS = new Vec3[] {
            new Vec3( 0.5D, -0.10D,  0.5D),
            new Vec3(-0.5D, -0.10D,  0.5D),
            new Vec3( 0.5D, -0.10D, -0.5D),
            new Vec3(-0.5D, -0.10D, -0.5D)
    };

    private void spawnRotorParticles(DroneDeliverEntity entity, PoseStack poseStack) {
        double heightAboveGround = getGroundDistance(entity);
        float heightFactor = Mth.clamp((float) (heightAboveGround / 10.0D), 0.2F, 1.5F);

        float spawnChance = 0.8F - (heightFactor * 0.4F) * 5;
        if (entity.getRandom().nextFloat() > spawnChance) {
            return;
        }

        float yawRad = (float) -Math.toRadians(entity.getYRot());

        for (Vec3 offset : ROTOR_OFFSETS) {
            Vec3 rotatedOffset = offset.yRot(yawRad);
            Vec3 spawnPos = entity.position().add(rotatedOffset);

            entity.level().addParticle(
                    ParticleTypes.CLOUD,
                    spawnPos.x,
                    spawnPos.y,
                    spawnPos.z,
                    0.0D,
                    -0.25f,
                    0.0D);
        }
    }
    /**
     * Hilfsmethode: Misst die Distanz von der Drohne nach unten bis zum ersten soliden Block.
     */
    private double getGroundDistance(DroneDeliverEntity entity) {
        int maxCheckDistance = 16;
        for (int i = 1; i <= maxCheckDistance; i++) {
            if (!entity.level().getBlockState(entity.blockPosition().below(i)).isAir()) {
                return i - (entity.getY() % 1.0D);
            }
        }
        return maxCheckDistance;
    }

    @OnlyIn(Dist.CLIENT)
    public void spawnParticles(Entity ent)
    {
        RandomSource rand = RandomSource.create();

        Vec3D spawn;
        Vec3D dest;

        for (int i = 0; i <= 10; i++)
        {
            spawn = getParticleSpawn(rand, ent);
            dest = getParticleDest(rand, ent);

            ent.level().addParticle(WUTParticles.EXPERIENCEORB.get(),
                    spawn.x,
                    spawn.y,
                    spawn.z,
                    dest.x, dest.y, dest.z);
        }

        if (rand.nextInt(Math.max(1, 5 - 10)) == 0)
        {
            spawn = getParticleSpawn(rand, ent);
            dest = getParticleDest(rand, ent);

            ent.level().addParticle(WUTParticles.EXPERIENCEORB.get(),
                    spawn.x,
                    spawn.y,
                    spawn.z,
                    dest.x, dest.y, dest.z);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private Vec3D getParticleSpawn(RandomSource random, Entity ent)
    {
        double range = 1;
        return new Vec3D(ent.blockPosition()).add(
                (random.nextFloat() - 0.0F) * range,
                (random.nextFloat() - 0.25F) * range,
                (random.nextFloat() - 0.0F) * range);
    }

    @OnlyIn(Dist.CLIENT)
    private Vec3D getParticleDest(RandomSource random, Entity ent)
    {
        return Vec3D.getCenter(ent.getOnPos()).add(
                (0.0F),
                (1.0F),
                (0.0F));
    }
}