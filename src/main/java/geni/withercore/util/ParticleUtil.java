package geni.withercore.util;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.PARTICLE;

import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL13;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import geni.witherutils.client.render.effect.particle.MoltenParticleData;
import geni.witherutils.client.render.effect.particle.UpdraftParticleData;

public class ParticleUtil {

    @OnlyIn(Dist.CLIENT)
    public static Particle addParticleDirect(Level world, Particle particle)
    {
        if (world instanceof ClientLevel)
        {
            Minecraft mc = Minecraft.getInstance();
            Camera activerenderinfo = mc.gameRenderer.getMainCamera();
            if (mc != null && activerenderinfo.isInitialized() && mc.particleEngine != null)
            {
                mc.particleEngine.add(particle);
                return particle;
            }
        }
        return null;
    }

    //call with packet
    public enum ERandomChance
    {
        ALWAYS,
        OFTEN,
        FIFTY,
        RARELY
    }

    public enum EParticlePosition
    {
        BLOCKSTATIC,
        BLOCKRANDOM,
        HITRESULT,
        NONE;
    }

    @SuppressWarnings("static-access")
    public static DoubleList getLocation(Level pLevel, Player pPlayer, EParticlePosition position)
    {
        DoubleList xyz = new DoubleArrayList();

        Vec3 vec3 = pPlayer.getEyePosition();
        Vec3 vec31 = vec3.add(pPlayer.calculateViewVector(pPlayer.getXRot(), pPlayer.getYRot()).scale(pPlayer.blockInteractionRange()));
        BlockHitResult blockhitresult = pLevel.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, pPlayer));

        BlockState blockstate = pLevel.getBlockState(blockhitresult.getBlockPos());
        VoxelShape shape = blockstate.getShape(pLevel, blockhitresult.getBlockPos());

        switch(position)
        {
            case BLOCKSTATIC:
                xyz.add(0, (double)blockhitresult.getBlockPos().getX() + 0.5D);
                xyz.add(1, (double)blockhitresult.getBlockPos().getY() + shape.max(blockhitresult.getDirection().getAxis().Y) + 0.1D);
                xyz.add(2, (double)blockhitresult.getBlockPos().getZ() + 0.5D);
                break;
            case BLOCKRANDOM:
                xyz.add(0, (double)blockhitresult.getBlockPos().getX() + pLevel.random.nextDouble());
                xyz.add(1, (double)blockhitresult.getBlockPos().getY() + shape.max(blockhitresult.getDirection().getAxis().Y) + 0.1D);
                xyz.add(2, (double)blockhitresult.getBlockPos().getZ() + pLevel.random.nextDouble());
                break;
            case HITRESULT:
                xyz.add(0, blockhitresult.getLocation().x());
                xyz.add(1, blockhitresult.getLocation().y());
                xyz.add(2, blockhitresult.getLocation().z());
                break;
            case NONE:
                xyz.add(0, pPlayer.blockPosition().getX());
                xyz.add(1, pPlayer.blockPosition().getY() + 0.1D);
                xyz.add(2, pPlayer.blockPosition().getZ());
                break;
        }
        return xyz;
    }

    public static float getChance(Level level, ERandomChance chance)
    {
        float dynamic = 0.1f + level.random.nextInt(3);
        switch(chance)
        {
            case ALWAYS:
                return 1.0f;
            case OFTEN:
                return 0.6f + (dynamic / 10);
            case FIFTY:
                return 0.4f + (dynamic / 10);
            case RARELY:
                return 0.1f + (dynamic / 10);
            default:
                return 0.f;
        }
    }

    public static void playParticleDistrib(LevelAccessor levelacc, BlockPos pPos, ParticleOptions particleIn, int amount, double speed)
    {
        if (levelacc instanceof Level level)
        {
            float particleX = (float) pPos.getX() + 0.5f;
            float particleY = (float) pPos.getY() + 1.75f;
            float particleZ = (float) pPos.getZ() + 0.5f;

            if(!level.isClientSide)
            {
                if(level instanceof ServerLevel serverLevel)
                    serverLevel.sendParticles(particleIn,
                            (double) particleX + Math.random() - 0.5f,
                            (double) particleY + Math.random() - 0.5f,
                            (double) particleZ + Math.random() - 0.5f,
                            amount, 0, 0, 0.01D, speed);
            }
            else
            {
                for(int i = 0; i < amount; i++)
                    level.addParticle(particleIn,
                            (double) particleX + Math.random() - 0.5f,
                            (double) particleY + Math.random() - 0.5f,
                            (double) particleZ + Math.random() - 0.5f, 0, 0.01D, 0);
            }
        }
    }

    public static void playParticleDistrib(LevelAccessor levelacc, Player pPlayer, BlockPos pPos, ParticleOptions particleIn, EParticlePosition position, ERandomChance chance, int amount, int levelevent)
    {
        if (levelacc instanceof Level level)
        {
            double x = getLocation(level, pPlayer, position).getDouble(0);
            double y = getLocation(level, pPlayer, position).getDouble(1);
            double z = getLocation(level, pPlayer, position).getDouble(2);

            for(int i = 0; i < amount; i++)
            {
                if(level.random.nextFloat() < getChance(level, chance))
                {
                    if(!level.isClientSide)
                    {
                        if(level instanceof ServerLevel serverLevel)
                            serverLevel.sendParticles(particleIn, x, y, z, 0, 0, 0, 0.01D + i, 0);
                    }
                    else
                    {
                        level.addParticle(particleIn, x, y, z, 0, 0.01D, 0);
                    }

                    if(levelevent != -1)
                        level.levelEvent(levelevent, pPos, 0);
                }
            }
        }
    }

    /*
     *
     * PARTICLE Templates
     *
     */
    public static void playParticleStarEffect(LevelAccessor levelacc, Player pPlayer, ParticleOptions particleIn, double dirMultiplier, EParticlePosition position)
    {
        if(dirMultiplier == 0)
            dirMultiplier = 0.05D;

        if (levelacc instanceof Level pLevel)
        {
            double x = getLocation(pLevel, pPlayer, position).getDouble(0);
            double y = getLocation(pLevel, pPlayer, position).getDouble(1);
            double z = getLocation(pLevel, pPlayer, position).getDouble(2);

            if(!pLevel.isClientSide)
            {
                if(pLevel instanceof ServerLevel level)
                {
                    level.sendParticles(particleIn, x, y, z, 0, dirMultiplier, -dirMultiplier, 0.0D, 0.2D);
                    level.sendParticles(particleIn, x, y, z, 0, dirMultiplier, -dirMultiplier, dirMultiplier, 0.2D);
                    level.sendParticles(particleIn, x, y, z, 0, dirMultiplier, -dirMultiplier, -dirMultiplier, 0.2D);
                    level.sendParticles(particleIn, x, y, z, 0, -dirMultiplier, -dirMultiplier, 0.0D, 0.2D);
                    level.sendParticles(particleIn, x, y, z, 0, -dirMultiplier, -dirMultiplier, -dirMultiplier, 0.2D);
                    level.sendParticles(particleIn, x, y, z, 0, -dirMultiplier, -dirMultiplier, dirMultiplier, 0.2D);
                    level.sendParticles(particleIn, x, y, z, 0, 0.0D, -dirMultiplier, dirMultiplier, 0.2D);
                    level.sendParticles(particleIn, x, y, z, 0, 0.0D, -dirMultiplier, -dirMultiplier, 0.2D);
                }
            }
            else
            {
                pLevel.addParticle(particleIn, x, y, z, dirMultiplier, -dirMultiplier, 0.0D);
                pLevel.addParticle(particleIn, x, y, z, dirMultiplier, -dirMultiplier, dirMultiplier);
                pLevel.addParticle(particleIn, x, y, z, dirMultiplier, -dirMultiplier, -dirMultiplier);
                pLevel.addParticle(particleIn, x, y, z, -dirMultiplier, -dirMultiplier, 0.0D);
                pLevel.addParticle(particleIn, x, y, z, -dirMultiplier, -dirMultiplier, -dirMultiplier);
                pLevel.addParticle(particleIn, x, y, z, -dirMultiplier, -dirMultiplier, dirMultiplier);
                pLevel.addParticle(particleIn, x, y, z, 0.0D, -dirMultiplier, dirMultiplier);
                pLevel.addParticle(particleIn, x, y, z, 0.0D, -dirMultiplier, -dirMultiplier);
            }
        }
    }

    public static void spawnParticleInASphere(Level level, double x, double y, double z, Supplier<ParticleOptions> type,
                                              int amount, float speed,
                                              float angleVariation, float speedVariation) {
        double azimuthIncrement = Math.PI * (3 - Math.sqrt(5)); // Golden angle

        for (int i = 0; i < amount; i++) {
            double inclination = Math.acos(1 - (2 * (i + 0.5) / amount)); // Angle from the pole
            double azimuth = azimuthIncrement * i; // Rotation around the axis

            if (angleVariation != 0) {
                inclination += level.random.nextFloat() * angleVariation - angleVariation / 2;
                azimuth += level.random.nextFloat() * angleVariation - angleVariation / 2;
            }

            float s = speed;
            if (speedVariation != 0) {
                s += level.random.nextFloat() * speedVariation - speedVariation / 2;
            }

            double vx = s * Math.sin(inclination) * Math.cos(azimuth);
            double vy = s * Math.sin(inclination) * Math.sin(azimuth);
            double vz = s * Math.cos(inclination);

            level.addParticle(type.get(), x, y, z, vx, vy, vz);
        }
    }

    public static void spawnParticleOnBlockShape(Level level, BlockPos pos, ParticleOptions particleOptions,
                                                 UniformInt uniformInt, float maxSpeed) {
        spawnParticleOnBoundingBox(level.getBlockState(pos).getShape(level, pos).bounds().move(pos), level,
                particleOptions, uniformInt, maxSpeed);
    }

    public static void spawnParticleOnBoundingBox(AABB bb, Level level, ParticleOptions particleOptions,
                                                  UniformInt uniformInt, float maxSpeed) {

        RandomSource random = level.random;
        float offset = 0.1f;
        Vec3 blockCenter = new Vec3(bb.minX - 0.5 + (bb.maxX - bb.minX) / 2f, bb.minY - 0.5 + (bb.maxY - bb.minY) / 2f, bb.minZ - 0.5 + (bb.maxZ - bb.minZ) / 2f);
        bb = bb.move(-blockCenter.x, -blockCenter.y, -blockCenter.z);
        //north
        int i = uniformInt.sample(random);
        for (int j = 0; j < i; ++j) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            if (x > bb.minX && x < bb.maxX && y > bb.minY && y < bb.maxY) {
                double dx = maxSpeed * level.random.nextDouble();
                double dy = maxSpeed * level.random.nextDouble();
                double dz = 0;
                level.addParticle(particleOptions, blockCenter.x + x, blockCenter.y + y, blockCenter.z + bb.minZ - offset, dx, dy, dz);
            }
        }
        //south
        i = uniformInt.sample(random);
        for (int j = 0; j < i; ++j) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            if (x > bb.minX && x < bb.maxX && y > bb.minY && y < bb.maxY) {
                double dx = maxSpeed * level.random.nextDouble();
                double dy = maxSpeed * level.random.nextDouble();
                double dz = 0;
                level.addParticle(particleOptions, blockCenter.x() + x, blockCenter.y() + y, blockCenter.z() + bb.maxZ + offset, dx, dy, dz);
            }
        }
        //west
        i = uniformInt.sample(random);
        for (int j = 0; j < i; ++j) {
            double z = random.nextDouble();
            double y = random.nextDouble();
            if (z > bb.minZ && z < bb.maxZ && y > bb.minY && y < bb.maxY) {
                double dx = 0;
                double dy = maxSpeed * level.random.nextDouble();
                double dz = maxSpeed * level.random.nextDouble();
                level.addParticle(particleOptions, blockCenter.x() + bb.minX - offset, blockCenter.y() + y, blockCenter.z() + z, dx, dy, dz);
            }
        }
        //east
        i = uniformInt.sample(random);
        for (int j = 0; j < i; ++j) {
            double z = random.nextDouble();
            double y = random.nextDouble();
            if (z > bb.minZ && z < bb.maxZ && y > bb.minY && y < bb.maxY) {
                double dx = 0;
                double dy = maxSpeed * level.random.nextDouble();
                double dz = maxSpeed * level.random.nextDouble();
                level.addParticle(particleOptions, blockCenter.x() + bb.maxX + offset, blockCenter.y() + y, blockCenter.z() + z, dx, dy, dz);
            }
        }
        //down
        i = uniformInt.sample(random);
        for (int j = 0; j < i; ++j) {
            double x = random.nextDouble();
            double z = random.nextDouble();
            if (x > bb.minX && x < bb.maxX && z > bb.minZ && z < bb.maxZ) {
                double dx = maxSpeed * level.random.nextDouble();
                double dy = 0;
                double dz = maxSpeed * level.random.nextDouble();
                level.addParticle(particleOptions, blockCenter.x() + x, blockCenter.y() + bb.minY - offset, blockCenter.z() + z, dx, dy, dz);
            }
        }
        //up
        i = uniformInt.sample(random);
        for (int j = 0; j < i; ++j) {
            double x = random.nextDouble();
            double z = random.nextDouble();
            if (x > bb.minX && x < bb.maxX && z > bb.minZ && z < bb.maxZ) {
                double dx = maxSpeed * level.random.nextDouble();
                double dy = 0;
                double dz = maxSpeed * level.random.nextDouble();
                level.addParticle(particleOptions, blockCenter.x() + x, blockCenter.y() + bb.maxY + offset, blockCenter.z() + z, dx, dy, dz);
            }
        }
    }


    public static void spawnParticlesOnBlockFaces(Level level, BlockPos pos, ParticleOptions particleOptions,
                                                  UniformInt uniformInt, float minSpeed, float maxSpeed, boolean perpendicular) {
        for (Direction direction : Direction.values()) {
            int i = uniformInt.sample(level.random);

            for (int j = 0; j < i; ++j) {
                spawnParticleOnFace(level, pos, direction, particleOptions, minSpeed, maxSpeed, perpendicular);
            }
        }
    }

    public static void spawnParticleOnFace(Level level, BlockPos pos, Direction direction, ParticleOptions particleOptions,
                                           float minSpeed, float maxSpeed, boolean perpendicular) {
        Vec3 vec3 = Vec3.atCenterOf(pos);
        int i = direction.getStepX();
        int j = direction.getStepY();
        int k = direction.getStepZ();
        double d0 = vec3.x + (i == 0 ? Mth.nextDouble(level.random, -0.5D, 0.5D) : i * 0.6D);
        double d1 = vec3.y + (j == 0 ? Mth.nextDouble(level.random, -0.5D, 0.5D) : j * 0.6D);
        double d2 = vec3.z + (k == 0 ? Mth.nextDouble(level.random, -0.5D, 0.5D) : k * 0.6D);
        double dx;
        double dy;
        double dz;
        if (perpendicular) {
            dx = i * Mth.randomBetween(level.random, minSpeed, maxSpeed);
            dy = j * Mth.randomBetween(level.random, minSpeed, maxSpeed);
            dz = k * Mth.randomBetween(level.random, minSpeed, maxSpeed);
        } else {
            float d = maxSpeed - minSpeed;

            dx = (i == 0) ? (minSpeed + d * level.random.nextDouble()) : 0.0D;
            dy = (j == 0) ? (minSpeed + d * level.random.nextDouble()) : 0.0D;
            dz = (k == 0) ? (minSpeed + d * level.random.nextDouble()) : 0.0D;
        }
        level.addParticle(particleOptions, d0, d1, d2, dx, dy, dz);
    }


    @SuppressWarnings("resource")
    public static void spawnBreakParticles(VoxelShape shape, BlockPos pPos, BlockState pState, Level level) {

        var particleEngine = Minecraft.getInstance().particleEngine;

        shape.forAllBoxes((x0, y0, z0, x1, y1, z1) -> {
            double d1 = Math.min(1.0D, x1 - x0);
            double d2 = Math.min(1.0D, y1 - y0);
            double d3 = Math.min(1.0D, z1 - z0);
            int i = Math.max(2, Mth.ceil(d1 / 0.25D));
            int j = Math.max(2, Mth.ceil(d2 / 0.25D));
            int k = Math.max(2, Mth.ceil(d3 / 0.25D));

            for (int l = 0; l < i; ++l) {
                for (int i1 = 0; i1 < j; ++i1) {
                    for (int j1 = 0; j1 < k; ++j1) {
                        double d4 = (l + 0.5D) / i;
                        double d5 = (i1 + 0.5D) / j;
                        double d6 = (j1 + 0.5D) / k;
                        double d7 = d4 * d1 + x0;
                        double d8 = d5 * d2 + y0;
                        double d9 = d6 * d3 + z0;
                        particleEngine.add(new TerrainParticle((ClientLevel) level, pPos.getX() + d7, pPos.getY() + d8,
                                pPos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, pState, pPos));
                    }
                }
            }
        });
    }

    @ApiStatus.Internal
    public static Supplier<ShaderInstance> particleShader = GameRenderer::getParticleShader;

    public static final ParticleRenderType ADDITIVE_TRANSLUCENCY_RENDER_TYPE = new ParticleRenderType() {

        @SuppressWarnings({ "resource", "deprecation" })
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            RenderSystem.activeTexture(GL13.GL_TEXTURE2);
            RenderSystem.activeTexture(GL13.GL_TEXTURE0);
            //because of custom render type fuckery...

            RenderSystem.setShader(particleShader);
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            return tesselator.begin(VertexFormat.Mode.QUADS, PARTICLE);
        }

        public String toString() {
            return "PARTICLE_SHEET_ADDITIVE_TRANSLUCENT";
        }
    };

//  @OnlyIn(Dist.CLIENT)
//  private void updateVisual()
//  {
//  	if(loopDetector++ >= 31)
//  		loopDetector = 0;
//
//      Vec3D spawn = new Vec3D(worldPosition);
//      spawn.add(0.5, 0.5, 0.5);
//
//      double rand = level.random.nextInt(100) / 12D;
//      double randOffset = rand * (Math.PI * 2D);
//
//      double offsetX = Math.cos((loopDetector / 1440D * Math.PI) * 90) * 1;
//      double offsetZ = -Math.sin((loopDetector / 1440D * Math.PI) * 90) * 1;
//
    ////  	spawn.add(offsetX * 1.2, (TickHelper.getPartialTick() / 180D * Math.PI) * 90, offsetY * 1.2);
//      spawn.add(offsetX, 1, offsetZ);
//
//      Vector3 target = Vector3.fromBlockPosCenter(worldPosition);
//
//      target.add(offsetX, 0, offsetZ);
//
//      level.addAlwaysVisibleParticle(new DataParticleType.DataParticleData(WUTParticles.STARS.get(), 255, 255, 255, 255, 31), spawn.x, spawn.y, spawn.z, target.x, target.y, target.z);
//  }

    public static void spawnMoltenEmber(Level level, double x, double y, double z)
    {
        spawnMoltenEmber(level, x, y, z, MoltenParticleData.DEFAULT);
    }

    public static void spawnMoltenEmber(Level level, double x, double y, double z, MoltenParticleData data)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            serverLevel.sendParticles(data, x, y, z, 1, 0.02D * data.variation(), 0.02D, 0.02D * data.variation(), 0.01D * data.speed());
        }
        else
        {
            level.addParticle(data, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    public static void spawnMoltenEmber(Level level, double x, double y, double z, int count, MoltenParticleData data)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            serverLevel.sendParticles(data, x, y, z, count, 0.1D * data.variation(), 0.05D, 0.1D * data.variation(), 0.02D * data.speed());
        }
        else
        {
            for (int i = 0; i < count; i++)
            {
                level.addParticle(data, x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public static void spawnUpdraft(Level level, double x, double y, double z)
    {
        spawnUpdraft(level, x, y, z, UpdraftParticleData.DEFAULT);
    }

    public static void spawnUpdraft(Level level, double x, double y, double z, UpdraftParticleData data)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            serverLevel.sendParticles(data, x, y, z, 1, 0.05D * data.variation(), 0.05D, 0.05D * data.variation(), 0.02D * data.speed());
        }
        else
        {
            level.addParticle(data, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    public static void spawnUpdraftColumn(Level level, double x, double y, double z, double height, int count, UpdraftParticleData data)
    {
        double step = height / Math.max(1, count);
        for (int i = 0; i < count; i++)
        {
            double offsetY = step * i;
            if (level instanceof ServerLevel serverLevel)
            {
                serverLevel.sendParticles(data, x, y + offsetY, z, 1, 0.08D * data.variation(), 0.02D, 0.08D * data.variation(), 0.02D * data.speed());
            }
            else
            {
                level.addParticle(data, x, y + offsetY, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    /*
     *
     * DOWNWASH & ROTOR TRAIL UTIL METHODS
     *
     */

    /**
     * Spawnt einen einzelnen Downwash-Kondensstreifen unter einer Koordinate.
     */
    public static void spawnDownwash(Level level, double x, double y, double z, ParticleOptions particleData, double downwashSpeed) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particleData, x, y, z, 1, 0.01D, downwashSpeed, 0.01D, 0.02D);
        } else {
            level.addParticle(particleData, x, y, z, 0.0D, downwashSpeed, 0.0D);
        }
    }

    /**
     * Spawnt Downwash-Kondensstreifen an mehreren Rotor-Positionen gleichzeitig (z. B. Quadcopter).
     *
     * @param level          Das Level
     * @param rotorPositions Liste oder Array aller Rotor-Weltpositionen
     * @param particleData   Das Downwash-ParticleOptions Objekt
     * @param downwashSpeed  Abwärtsgeschwindigkeit (z. B. -0.8 bis -1.2)
     */
    public static void spawnQuadcopterDownwash(Level level, Vec3[] rotorPositions, ParticleOptions particleData, double downwashSpeed) {
        for (Vec3 pos : rotorPositions) {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(particleData, pos.x, pos.y, pos.z, 1, 0.01D, downwashSpeed, 0.01D, 0.02D);
            } else {
                level.addParticle(particleData, pos.x, pos.y, pos.z, 0.0D, downwashSpeed, 0.0D);
            }
        }
    }
}
