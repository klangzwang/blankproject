package geni.withercore.util;

import geni.witherapi.orbital.IOrbitalTarget.AirspaceState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

/**
 * Pure math and terrain-sensor utilities for the standalone Orbital Ring System.
 * Keeps physics and raycast math in withercore so both client renderers and server trackers
 * use identical deterministic calculations.
 */
public final class OrbitalRingMath {

    /** Default base distance (in blocks). Inner = 3m, Zone 1 = 6m, Zone 2 = 9m. */
    public static final double DEFAULT_ZONE_DISTANCE = 3.0D;

    /** Vertical offset above the player's feet where the orbital rings hover. */
    public static final double RING_HOVER_HEIGHT_OFFSET = 2.6D;

    /** Maximum distance before an anchor snaps to the player (e.g., after teleport/nether portal). */
    public static final double MAX_ANCHOR_SNAP_DISTANCE = 48.0D;

    /** Number of horizontal compass rays used to detect caves, rooms, or dense forests. */
    private static final int HORIZONTAL_SENSOR_RAYS = 8;

    private OrbitalRingMath() {}

    /**
     * Holds the result of a single physics tick for the delayed ring anchor.
     */
    public record SpringStepResult(Vec3 nextPos, Vec3 nextVel, double lagDistance, boolean snapped) {}

    /**
     * Holds the detailed diagnostic result of an airspace/cover check for tactical logging.
     */
    public record AirspaceEvaluation(
            AirspaceState state,
            int blockedHorizontalRays,
            int verticalClearanceBlocks,
            boolean skyVisible,
            String diagnosticReason
    ) {}

    /**
     * Computes one tick (dt = 0.05s) of non-linear spring-damper physics for the ring anchor.
     *
     * Thought process:
     * If the rings were rigidly attached to player.position(), a flying drone would look
     * artificially pulled on a stick when the player moves. By using a non-linear spring
     * (soft near the player, quadratically stiffer at larger distances) plus velocity drag,
     * the rings lag behind on sprint start, accelerate smoothly, and brake gently on arrival.
     */
    public static SpringStepResult stepAnchorSpringDamper(
            Vec3 currentAnchor,
            Vec3 currentVel,
            Vec3 targetAnchor,
            double dtSeconds
    ) {
        Vec3 toTarget = targetAnchor.subtract(currentAnchor);
        double dist = toTarget.length();

        // Safety guard: if player teleports or respawns far away, snap immediately instead of slingshotting
        if (dist > MAX_ANCHOR_SNAP_DISTANCE) {
            return new SpringStepResult(targetAnchor, Vec3.ZERO, dist, true);
        }

        // Non-linear spring stiffness: gentle lag for small steps, strong catch-up when sprinting away
        double baseStiffness = 3.2D;
        double nonLinearBoost = Mth.clamp(dist * dist * 0.35D, 0.0D, 14.0D);
        double effectiveStiffness = baseStiffness + nonLinearBoost;

        // Damping prevents endless oscillation around the player when stopping
        double damping = 3.6D;

        Vec3 springForce = toTarget.scale(effectiveStiffness);
        Vec3 dragForce = currentVel.scale(-damping);
        Vec3 acceleration = springForce.add(dragForce);

        Vec3 nextVel = currentVel.add(acceleration.scale(dtSeconds));
        // Deadzone clamp so micro-jitter stops completely when resting
        if (dist < 0.02D && nextVel.lengthSqr() < 0.0004D) {
            return new SpringStepResult(targetAnchor, Vec3.ZERO, 0.0D, false);
        }

        Vec3 nextPos = currentAnchor.add(nextVel.scale(dtSeconds));
        return new SpringStepResult(nextPos, nextVel, dist, false);
    }

    /**
     * Evaluates whether the surrounding terrain at the ring anchor has enough room for orbital flight,
     * or if the target has fled into cover (cave, house, dense forest, underwater).
     *
     * Thought process:
     * Instead of letting a drone suffocate in cave walls or teleporting like a vanilla wolf,
     * the rings themselves act as an "Airspace Sensor". When blocked, the rings turn red
     * and signal any future drone to enter Overwatch (wait high above) until the rings report CLEAR again.
     */
    public static AirspaceEvaluation evaluateAirspace(
            Level level,
            BlockPos playerBlockPos,
            Vec3 anchorPos,
            double zoneDistance
    ) {
        // 1. Check if player or ring anchor is submerged in water/lava
        BlockPos anchorBlockPos = BlockPos.containing(anchorPos);
        if (!level.getFluidState(playerBlockPos).isEmpty() || !level.getFluidState(anchorBlockPos).isEmpty()) {
            return new AirspaceEvaluation(
                    AirspaceState.BLOCKED_COVER, 8, 0, false,
                    "Target/Anchor submerged in fluid"
            );
        }

        // 2. Check vertical headroom above the player (detects low roofs, tunnels, caves)
        int verticalClearance = 0;
        int maxVerticalCheck = 8;
        for (int dy = 1; dy <= maxVerticalCheck; dy++) {
            BlockPos checkPos = playerBlockPos.above(dy);
            if (!level.getBlockState(checkPos).getCollisionShape(level, checkPos).isEmpty()) {
                break;
            }
            verticalClearance++;
        }

        boolean canSeeSky = level.canSeeSky(playerBlockPos.above());

        // If ceiling is lower than 4 blocks and no sky is visible, target is inside a narrow tunnel/room
        if (verticalClearance < 4 && !canSeeSky) {
            return new AirspaceEvaluation(
                    AirspaceState.BLOCKED_COVER, 0, verticalClearance, false,
                    "Low indoor/cave ceiling (" + verticalClearance + "m headroom, no sky)"
            );
        }

        // 3. Cast 8 horizontal sensor rays outward from anchorCenter to Zone 2 radius (3x ZoneDistance)
        double maxProbeRadius = zoneDistance * 2.6D;
        int blockedRays = 0;

        for (int i = 0; i < HORIZONTAL_SENSOR_RAYS; i++) {
            double angle = (Math.PI * 2.0D * i) / HORIZONTAL_SENSOR_RAYS;
            Vec3 rayEnd = new Vec3(
                    anchorPos.x + Math.cos(angle) * maxProbeRadius,
                    anchorPos.y,
                    anchorPos.z + Math.sin(angle) * maxProbeRadius
            );

            BlockHitResult hit = level.clip(new ClipContext(
                    anchorPos,
                    rayEnd,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    CollisionContext.empty()
            ));

            if (hit.getType() != HitResult.Type.MISS) {
                blockedRays++;
            }
        }

        // Evaluate combined horizontal + vertical sensor metrics
        if (blockedRays >= 5 || (blockedRays >= 3 && !canSeeSky && verticalClearance < 6)) {
            return new AirspaceEvaluation(
                    AirspaceState.BLOCKED_COVER, blockedRays, verticalClearance, canSeeSky,
                    "Heavy lateral obstruction (" + blockedRays + "/8 rays blocked, sky=" + canSeeSky + ")"
            );
        }

        if (blockedRays >= 2 || verticalClearance < 5) {
            return new AirspaceEvaluation(
                    AirspaceState.CONFINED_WARNING, blockedRays, verticalClearance, canSeeSky,
                    "Confined terrain (" + blockedRays + "/8 rays blocked, headroom=" + verticalClearance + "m)"
            );
        }

        return new AirspaceEvaluation(
                AirspaceState.CLEAR, blockedRays, verticalClearance, canSeeSky,
                "Open airspace (" + blockedRays + "/8 rays blocked)"
        );
    }

    /**
     * Computes a 3D point on a horizontal orbit ring around the anchor center.
     */
    public static Vec3 sampleRingPoint(Vec3 anchorCenter, double radius, float angleRadians) {
        return new Vec3(
                anchorCenter.x + Math.cos(angleRadians) * radius,
                anchorCenter.y,
                anchorCenter.z + Math.sin(angleRadians) * radius
        );
    }
}
