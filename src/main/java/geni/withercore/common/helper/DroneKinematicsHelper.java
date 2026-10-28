package geni.withercore.common.helper;

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
     * Evaluates the smooth takeoff climb profile with liftoff ease and subtle overshoot settling.
     *
     * @param normalizedClimbProgress 0.0f to 1.0f progress through takeoff
     * @return target height offset above homeY
     */
    public static double calculateTakeoffHeightOffset(float normalizedClimbProgress) {
        float t = Mth.clamp(normalizedClimbProgress, 0.0F, 1.0F);
        double baseClimb = Mth.smoothstep(t) * TARGET_HOVER_HEIGHT;
        // Damped settling bounce near completion
        double overshootBounce = Math.sin(t * Math.PI * 2.0D) * Math.exp(-t * 3.2D) * 0.22D;
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
        double primaryWave = Math.sin((ageInTicks * 0.14F) + phaseSeed) * 0.045D;
        double secondaryWave = Math.sin((ageInTicks * 0.31F) + phaseSeed * 1.7F) * 0.018D;
        return primaryWave + secondaryWave;
    }

    /**
     * Calculates subtle organic micro-turbulence pitch angle (in degrees) while airborne.
     */
    public static float calculateHoverTurbulencePitch(float ageInTicks, int entityId) {
        float phaseSeed = (entityId % 23) * 0.51F;
        return (float) (Math.sin((ageInTicks * 0.21F) + phaseSeed) * 1.6D
                + Math.cos((ageInTicks * 0.47F) + phaseSeed) * 0.65D);
    }

    /**
     * Calculates subtle organic micro-turbulence roll angle (in degrees) while airborne.
     */
    public static float calculateHoverTurbulenceRoll(float ageInTicks, int entityId) {
        float phaseSeed = (entityId % 19) * 0.63F;
        return (float) (Math.cos((ageInTicks * 0.17F) + phaseSeed) * 1.8D
                + Math.sin((ageInTicks * 0.43F) + phaseSeed) * 0.75D);
    }

    /**
     * Generates a continuous organic 3D drift displacement vector around an anchor point.
     * Uses incommensurate multi-frequency sinusoids to simulate ambient micro-air currents
     * and flight-controller hunting so the drone never sits rigidly frozen.
     */
    public static Vec3 calculateHoverDriftOffset(int tickCount, int entityId) {
        float t = tickCount + (Math.abs(entityId) % 997) * 0.73F;
        double driftX = Math.sin(t * 0.038D) * 0.30D
                + Math.sin(t * 0.091D + 1.3D) * 0.14D
                + Math.cos(t * 0.18D + 2.7D) * 0.05D;

        double driftZ = Math.cos(t * 0.033D + 0.7D) * 0.30D
                + Math.cos(t * 0.079D + 2.1D) * 0.14D
                + Math.sin(t * 0.16D + 0.9D) * 0.05D;

        double driftY = Math.sin(t * 0.052D + 1.1D) * 0.12D
                + Math.cos(t * 0.11D + 0.4D) * 0.05D;

        return new Vec3(driftX, driftY, driftZ);
    }

    /**
     * Projects world velocity and acceleration onto the drone's local horizontal axes
     * to compute realistic forward/backward Pitch and lateral Roll banking angles (in degrees).
     *
     * In quadcopter physics:
     * - Forward acceleration (thrust vector tilt forward) pitches the nose down (negative RotX).
     * - Deceleration / braking pitches the nose up (positive RotX).
     * - Lateral rightward acceleration rolls the right side down (negative RotZ).
     * - Leftward acceleration rolls the left side down (positive RotZ).
     *
     * @param velocity     current entity deltaMovement
     * @param acceleration current tick acceleration (deltaVelocity)
     * @param yawDegrees   current horizontal yaw rotation (yRot)
     * @return float[2] where [0] = targetPitchDegrees, [1] = targetRollDegrees
     */
    public static float[] calculateAerodynamicBanking(Vec3 velocity, Vec3 acceleration, float yawDegrees) {
        float yawRad = yawDegrees * Mth.DEG_TO_RAD;
        // Minecraft entity forward unit vector: (-sin(yaw), 0, cos(yaw))
        Vec3 forwardDir = new Vec3(-Mth.sin(yawRad), 0.0D, Mth.cos(yawRad));
        // Local right unit vector (90 degrees clockwise looking down: forward x up)
        Vec3 rightDir = new Vec3(-Mth.cos(yawRad), 0.0D, -Mth.sin(yawRad));

        double forwardAccel = acceleration.dot(forwardDir);
        double forwardVel = velocity.dot(forwardDir);
        // Acceleration dominates dynamic attitude response; velocity provides drag equilibrium tilt
        double forwardResponse = forwardAccel * 260.0D + forwardVel * 24.0D;

        double rightAccel = acceleration.dot(rightDir);
        double rightVel = velocity.dot(rightDir);
        double rightResponse = rightAccel * 260.0D + rightVel * 24.0D;

        // In the model space:
        // Negative RotX tilts nose down (forward)
        // Negative RotZ tilts right wing down (right)
        float targetPitch = Mth.clamp((float) -forwardResponse, -36.0F, 36.0F);
        float targetRoll = Mth.clamp((float) -rightResponse, -36.0F, 36.0F);

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
