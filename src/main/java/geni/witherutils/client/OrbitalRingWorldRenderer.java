package geni.witherutils.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import geni.witherapi.orbital.IOrbitalTarget.ActiveZone;
import geni.witherapi.orbital.IOrbitalTarget.AirspaceState;
import geni.witherapi.tags.WitherTags;
import geni.withercore.util.OrbitalRingMath;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.orbital.OrbitalRingManager;
import geni.witherutils.common.orbital.OrbitalRingTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * Permanent Dev/Tactical 3D World Renderer for the standalone Orbital Target-Lock Ring System.
 *
 * Thought process:
 * Renders all 3 concentric zones (Inner, Zone 1 Offensive, Zone 2 Defensive) plus the
 * interpolated active orbit ring and anchor tether directly in 3D world space.
 * Color-codes the active ring by AirspaceState (Green = Clear, Yellow = Confined, Red = Cave/Cover)
 * so we can visually verify every aspect of the ring brain before attaching a single drone.
 */
@EventBusSubscriber(modid = WitherUtils.MODID, value = Dist.CLIENT)
public final class OrbitalRingWorldRenderer {

    /** Number of line segments per full 360-degree orbit circle. */
    private static final int RING_SEGMENTS = 64;

    private OrbitalRingWorldRenderer() {}

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) {
            return;
        }

        OrbitalRingTracker tracker = OrbitalRingManager.getTracker(player);
        if (!tracker.isTargetLocked()) {
            return;
        }

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 cameraPos = event.getCamera().getPosition();

        // Interpolated delayed ring anchor center
        Vec3 anchorWorld = tracker.getOrbitalAnchorPos(partialTick);
        Vec3 anchorRel = anchorWorld.subtract(cameraPos);

        // Interpolated actual player hover reference (to visualize the spring-damper lag tether)
        Vec3 playerWorld = new Vec3(
                Mth.lerp(partialTick, player.xo, player.getX()),
                Mth.lerp(partialTick, player.yo, player.getY()) + OrbitalRingMath.RING_HOVER_HEIGHT_OFFSET,
                Mth.lerp(partialTick, player.zo, player.getZ())
        );
        Vec3 playerRel = playerWorld.subtract(cameraPos);

        double baseDist = tracker.getZoneDistance();
        double innerRadius = baseDist;
        double zone1Radius = baseDist * ActiveZone.ZONE_1_OFFENSIVE.getRadiusMultiplier();
        double zone2Radius = baseDist * ActiveZone.ZONE_2_DEFENSIVE.getRadiusMultiplier();
        double currentActiveRadius = tracker.getCurrentOrbitRadius(partialTick);

        AirspaceState airspace = tracker.getAirspaceState();
        ActiveZone activeZone = tracker.getActiveZone();

        // Select tactical telemetry color based on Airspace Clearance
        float activeR = airspace == AirspaceState.BLOCKED_COVER ? 0.94F : (airspace == AirspaceState.CONFINED_WARNING ? 0.96F : 0.13F);
        float activeG = airspace == AirspaceState.BLOCKED_COVER ? 0.27F : (airspace == AirspaceState.CONFINED_WARNING ? 0.62F : 0.77F);
        float activeB = airspace == AirspaceState.BLOCKED_COVER ? 0.27F : (airspace == AirspaceState.CONFINED_WARNING ? 0.04F : 0.37F);

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer lineBuffer = bufferSource.getBuffer(RenderType.lines());

        poseStack.pushPose();
        PoseStack.Pose pose = poseStack.last();

        // 1. Draw Spring-Damper Tether Line (Player -> Delayed Ring Anchor) & Anchor Cross
        drawSegment(pose, lineBuffer, playerRel, anchorRel, 0.38F, 0.75F, 0.98F, 0.65F);
        drawAnchorCross(pose, lineBuffer, anchorRel, 0.28D, activeR, activeG, activeB, 0.90F);

        // 2. Inner Ring (1x ZoneDistance): Dashed subtle cyan safety boundary
        drawCircle(pose, lineBuffer, anchorRel, innerRadius, RING_SEGMENTS, true,
                0.38F, 0.65F, 0.98F, 0.40F);

        // 3. Zone 1 Ring (2x ZoneDistance - Offensive): Highlighted when Zone 1 is selected
        float z1Alpha = (activeZone == ActiveZone.ZONE_1_OFFENSIVE) ? 0.55F : 0.22F;
        drawCircle(pose, lineBuffer, anchorRel, zone1Radius, RING_SEGMENTS, false,
                activeR, activeG, activeB, z1Alpha);

        // 4. Zone 2 Ring (3x ZoneDistance - Defensive): Highlighted when Zone 2 is selected
        float z2Alpha = (activeZone == ActiveZone.ZONE_2_DEFENSIVE) ? 0.55F : 0.22F;
        drawCircle(pose, lineBuffer, anchorRel, zone2Radius, RING_SEGMENTS, false,
                activeR, activeG, activeB, z2Alpha);

        // 5. Dynamic Active Orbit Ring (smoothly glides between Zone 1 and Zone 2 during transitions)
        drawCircle(pose, lineBuffer, anchorRel, currentActiveRadius, RING_SEGMENTS, false,
                activeR, activeG, activeB, 0.98F);

        poseStack.popPose();
        bufferSource.endBatch(RenderType.lines());
    }

    /**
     * Renders a horizontal 3D circle around centerRel. Supports dashed segments for the inner boundary ring.
     */
    private static void drawCircle(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            Vec3 centerRel,
            double radius,
            int segments,
            boolean dashed,
            float r, float g, float b, float a
    ) {
        for (int i = 0; i < segments; i++) {
            if (dashed && (i % 2 != 0)) {
                continue;
            }
            float a0 = (float) ((Math.PI * 2.0D * i) / segments);
            float a1 = (float) ((Math.PI * 2.0D * (i + 1)) / segments);

            Vec3 p0 = OrbitalRingMath.sampleRingPoint(centerRel, radius, a0);
            Vec3 p1 = OrbitalRingMath.sampleRingPoint(centerRel, radius, a1);

            drawSegment(pose, consumer, p0, p1, r, g, b, a);
        }
    }

    /**
     * Renders a small 3D crosshair at the delayed ring anchor center.
     */
    private static void drawAnchorCross(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            Vec3 centerRel,
            double halfSize,
            float r, float g, float b, float a
    ) {
        drawSegment(pose, consumer,
                centerRel.add(-halfSize, 0.0D, 0.0D),
                centerRel.add(halfSize, 0.0D, 0.0D),
                r, g, b, a);
        drawSegment(pose, consumer,
                centerRel.add(0.0D, 0.0D, -halfSize),
                centerRel.add(0.0D, 0.0D, halfSize),
                r, g, b, a);
    }

    /**
     * Emits a single 3D line segment with normalized direction vector required by NeoForge RenderType.lines().
     */
    private static void drawSegment(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            Vec3 from,
            Vec3 to,
            float r, float g, float b, float a
    ) {
        Vec3 dir = to.subtract(from).normalize();
        float nx = (float) dir.x;
        float ny = (float) dir.y;
        float nz = (float) dir.z;

        consumer.addVertex(pose, (float) from.x, (float) from.y, (float) from.z)
                .setColor(r, g, b, a)
                .setNormal(pose, nx, ny, nz);
        consumer.addVertex(pose, (float) to.x, (float) to.y, (float) to.z)
                .setColor(r, g, b, a)
                .setNormal(pose, nx, ny, nz);
    }
}
