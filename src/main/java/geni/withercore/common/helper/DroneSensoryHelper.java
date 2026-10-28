package geni.withercore.common.helper;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DroneSensoryHelper {

    private static final double SENSOR_RANGE = 4.5D;

    public record ObstacleResult(boolean obstacleDetected, Vec3 recommendedCorrection) {}

    /**
     * Casts 5 sensory rays around the drone's current movement trajectory to detect obstacles in real-time.
     * Checks forward, downward, upward, left-flank, and right-flank.
     */
    public static ObstacleResult evaluateSurroundings(Mob drone, Vec3 motion) {
        Level level = drone.level();
        Vec3 droneEye = drone.getEyePosition();
        Vec3 forwardDir = motion.lengthSqr() > 0.001D ? motion.normalize() : drone.getViewVector(1.0F);

        // 1. Direct forward ray
        Vec3 forwardEnd = droneEye.add(forwardDir.scale(SENSOR_RANGE));
        BlockHitResult forwardHit = level.clip(new ClipContext(droneEye, forwardEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, drone));

        // 2. Downward ground collision ray
        Vec3 downEnd = droneEye.add(0, -2.5D, 0);
        BlockHitResult downHit = level.clip(new ClipContext(droneEye, downEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, drone));

        // 3. Upward clearance ray
        Vec3 upEnd = droneEye.add(0, 2.5D, 0);
        BlockHitResult upHit = level.clip(new ClipContext(droneEye, upEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, drone));

        // 4. Lateral Left & Right vectors
        Vec3 lateralCross = forwardDir.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 leftEnd = droneEye.add(lateralCross.scale(2.0D)).add(forwardDir.scale(2.5D));
        Vec3 rightEnd = droneEye.subtract(lateralCross.scale(2.0D)).add(forwardDir.scale(2.5D));

        BlockHitResult leftHit = level.clip(new ClipContext(droneEye, leftEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, drone));
        BlockHitResult rightHit = level.clip(new ClipContext(droneEye, rightEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, drone));

        Vec3 correction = Vec3.ZERO;
        boolean detected = false;

        // Ground proximity avoidance (prevent drone from scraping ground)
        if (downHit.getType() != HitResult.Type.MISS) {
            double dist = droneEye.distanceTo(downHit.getLocation());
            if (dist < 1.8D) {
                correction = correction.add(0, 0.15D * (1.8D - dist), 0);
                detected = true;
            }
        }

        // Direct forward collision avoidance (divert upwards and laterally)
        if (forwardHit.getType() != HitResult.Type.MISS) {
            detected = true;
            Vec3 hitNormal = Vec3.atLowerCornerOf(forwardHit.getDirection().getNormal());
            correction = correction.add(hitNormal.scale(0.25D)).add(0, 0.2D, 0);
        }

        // Left flank avoidance (push right)
        if (leftHit.getType() != HitResult.Type.MISS) {
            detected = true;
            correction = correction.subtract(lateralCross.scale(0.18D));
        }

        // Right flank avoidance (push left)
        if (rightHit.getType() != HitResult.Type.MISS) {
            detected = true;
            correction = correction.add(lateralCross.scale(0.18D));
        }

        // Ceiling avoidance
        if (upHit.getType() != HitResult.Type.MISS) {
            detected = true;
            correction = correction.add(0, -0.15D, 0);
        }

        return new ObstacleResult(detected, correction);
    }

    /**
     * Checks if a target location has clear line of flight.
     */
    public static boolean hasLineOfFlight(Level level, Vec3 start, Vec3 target, Mob entity) {
        BlockHitResult hit = level.clip(new ClipContext(start, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        return hit.getType() == HitResult.Type.MISS;
    }
}
