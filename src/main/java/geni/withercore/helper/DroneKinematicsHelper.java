package geni.withercore.helper;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Provides aerodynamic spring-damper kinematics, takeoff overshoot bounce,
 * multi-frequency sinusoidal hover-bobbing, and pitch/roll banking calculations.
 */
public final class DroneKinematicsHelper {

    public static final int STARTUP_DURATION_TICKS = 40; // 2.0 seconds (40 ticks)
    public static final int TAKEOFF_DURATION_TICKS = 50; // 2.5 seconds climb with overshoot
    public static final double TARGET_HOVER_HEIGHT = 2.0D; // 2.0 blocks above placement surface

    public static final float MAX_HOVER_ROTOR_SPEED = 28.0F; // rad/tick visual multiplier
    public static final float MAX_CLIMB_ROTOR_SPEED = 38.0F;
    public static final float HIGH_ROTOR_SPEED_THRESHOLD = 21.0F; // Threshold for light-blue status lamp

    public static final int ENERGY_COST_STARTUP_PER_TICK = 5;
    public static final int ENERGY_COST_TAKEOFF_PER_TICK = 20;
    public static final int ENERGY_COST_HOVER_PER_TICK = 10;

    private DroneKinematicsHelper() {}

    /**
     * Evaluates the underdamped spring overshoot offset during the takeoff phase (State 0 in simulation).
     *
     * @param normalizedClimbProgress 0.0f to 1.0f progress through the takeoff state
     * @return target height offset above homeY (smoothly climbs to 2.0m with a damped overshoot bounce)
     */
    public static double calculateTakeoffHeightOffset(float normalizedClimbProgress) {
        float t = Mth.clamp(normalizedClimbProgress, 0.0F, 1.0F);
        double baseClimb = Mth.smoothstep(t) * TARGET_HOVER_HEIGHT;
        double overshootBounce = Math.sin(t * Math.PI * 2.2D) * Math.exp(-t * 2.8D) * 0.38D;
        return baseClimb + overshootBounce;
    }

    /**
     * Calculates multi-frequency sinusoidal vertical hover-bobbing displacement (in blocks).
     *
     * @param ageInTicks entity tick count + partialTick (or tickCount on server)
     * @param entityId   unique seed offset so multiple drones do not bob in identical phase
     * @return vertical bobbing offset in blocks
     */
    public static double calculateHoverBobOffset(float ageInTicks, int entityId) {
        float phaseSeed = (entityId % 31) * 0.37F;
        double primaryWave = Math.sin((ageInTicks * 0.17F) + phaseSeed) * 0.055D;
        double secondaryWave = Math.sin((ageInTicks * 0.39F) + phaseSeed * 1.7F) * 0.022D;
        return primaryWave + secondaryWave;
    }

    /**
     * Calculates subtle organic micro-turbulence pitch angle (in degrees) while hovering.
     */
    public static float calculateHoverTurbulencePitch(float ageInTicks, int entityId) {
        float phaseSeed = (entityId % 23) * 0.51F;
        return (float) (Math.sin((ageInTicks * 0.23F) + phaseSeed) * 2.1D
                + Math.cos((ageInTicks * 0.53F) + phaseSeed) * 0.85D);
    }

    /**
     * Calculates subtle organic micro-turbulence roll angle (in degrees) while hovering.
     */
    public static float calculateHoverTurbulenceRoll(float ageInTicks, int entityId) {
        float phaseSeed = (entityId % 19) * 0.63F;
        return (float) (Math.cos((ageInTicks * 0.19F) + phaseSeed) * 2.4D
                + Math.sin((ageInTicks * 0.47F) + phaseSeed) * 0.95D);
    }

    /**
     * Projects world velocity and acceleration onto the drone's local horizontal axes
     * to compute realistic forward/backward Pitch and lateral Roll banking angles (in degrees).
     *
     * @param velocity     current entity deltaMovement
     * @param acceleration current tick acceleration (deltaVelocity)
     * @param yawDegrees   current horizontal yaw rotation (yRot)
     * @return float[2] where [0] = targetPitchDegrees, [1] = targetRollDegrees
     */
    public static float[] calculateAerodynamicBanking(Vec3 velocity, Vec3 acceleration, float yawDegrees) {
        float yawRad = yawDegrees * Mth.DEG_TO_RAD;
        // Minecraft entity forward vector for yRot: (-sin(yaw), 0, cos(yaw))
        Vec3 forwardDir = new Vec3(-Mth.sin(yawRad), 0.0D, Mth.cos(yawRad));
        Vec3 rightDir = new Vec3(Mth.cos(yawRad), 0.0D, Mth.sin(yawRad));

        double forwardResponse = acceleration.dot(forwardDir) * 2.4D + velocity.dot(forwardDir) * 0.85D;
        double lateralResponse = acceleration.dot(rightDir) * 2.8D + velocity.dot(rightDir) * 0.95D;

        float targetPitch = Mth.clamp((float) (forwardResponse * 32.0D), -34.0F, 34.0F);
        float targetRoll = Mth.clamp((float) (-lateralResponse * 36.0D), -42.0F, 42.0F);

        return new float[]{targetPitch, targetRoll};
    }

    /**
     * Computes damped vertical spring velocity toward a desired Y coordinate.
     */
    public static double computeVerticalSpringVelocity(double currentY, double targetY, double currentVy, double stiffness, double damping) {
        double displacement = targetY - currentY;
        double springForce = displacement * stiffness;
        double dragForce = -currentVy * damping;
        double nextVy = currentVy + (springForce + dragForce) * 0.05D; // 1 tick = 0.05s
        return Mth.clamp(nextVy, -0.45D, 0.45D);
    }
}
