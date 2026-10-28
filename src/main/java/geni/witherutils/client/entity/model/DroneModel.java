package geni.witherutils.client.entity.model;

import geni.witherapi.tags.WitherTags;
import geni.withercore.entity.AbstractDroneEntity;
import geni.witherutils.common.world.entity.drone.DroneDeliverEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

/**
 * GeckoLib 4 GeoModel for DroneDeliverEntity.
 * Dynamically drives the 4 independent rotor blades (counter-rotating quadcopter configuration),
 * applies Pitch/Roll aerodynamic banking angles, and renders sinusoidal hover-bobbing on the body bone.
 */
public class DroneModel extends GeoModel<DroneDeliverEntity> {

    private static final ResourceLocation MODEL_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(WitherTags.MODID, "geo/entity/drone/wasp.geo.json");
    private static final ResourceLocation MODEL_RESOURCE_FOLDED =
            ResourceLocation.fromNamespaceAndPath(WitherTags.MODID, "geo/entity/drone/wasp_folded.geo.json");
    private static final ResourceLocation TEXTURE_OFF_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(WitherTags.MODID, "textures/entity/drone/wasp_off.png");
    private static final ResourceLocation TEXTURE_ON_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(WitherTags.MODID, "textures/entity/drone/wasp.png");
    private static final ResourceLocation ANIMATION_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(WitherTags.MODID, "animations/entity/drone/wasp.animation.json");

    @Override
    public ResourceLocation getModelResource(DroneDeliverEntity animatable) {
        return animatable.getDroneState() == AbstractDroneEntity.STATE_OFF ? MODEL_RESOURCE_FOLDED : MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(DroneDeliverEntity animatable) {
        return animatable.isLightsEnabled() ? TEXTURE_ON_RESOURCE : TEXTURE_OFF_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(DroneDeliverEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void setCustomAnimations(DroneDeliverEntity animatable, long instanceId, AnimationState<DroneDeliverEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        /*foldingStartup();*/

        float partialTick = animationState.getPartialTick();

        // 1. Apply aerodynamic Pitch, Roll, and sinusoidal Hover-Bobbing to main body bone
        GeoBone bodyBone = getAnimationProcessor().getBone("body");
        if (bodyBone != null) {
            float pitchRad = animatable.getLerpedBodyPitch(partialTick) * Mth.DEG_TO_RAD;
            float rollRad = animatable.getLerpedBodyRoll(partialTick) * Mth.DEG_TO_RAD;
            float bobPixels = animatable.getLerpedHoverBobOffset(partialTick) * 16.0F;

            bodyBone.setRotX(pitchRad);
            bodyBone.setRotZ(rollRad);
            bodyBone.setPosY(bobPixels);
        }

        GeoBone batteryBone = getAnimationProcessor().getBone("battery_pack");
        if (batteryBone != null) {
            batteryBone.setHidden(!animatable.hasBattery());
        }

        GeoBone lampBone = getAnimationProcessor().getBone("status_lamp");
        if (lampBone != null) {
            lampBone.setRotX(bodyBone.getRotX());
            lampBone.setRotY(bodyBone.getRotY());
            lampBone.setRotZ(bodyBone.getRotZ());
        }

        // 2. Rotate all 4 quadcopter propellers (counter-rotating pairs: FL/RR clockwise, FR/RL counter-clockwise)
        /*float rotorAngleRad = animatable.getLerpedRotorAngle(partialTick) * Mth.DEG_TO_RAD;

        rotateRotorBone("rotor1", rotorAngleRad);
        rotateRotorBone("rotor2", -rotorAngleRad);
        rotateRotorBone("rotor3", -rotorAngleRad);
        rotateRotorBone("rotor4", rotorAngleRad);*/

        /*float propRotation = Mth.lerp(partialTicks, drone.oldPropRotation, drone.propRotation);*/
    }

    private void rotateRotorBone(String boneName, float angleYRad) {
        GeoBone rotor = getAnimationProcessor().getBone(boneName);
        if (rotor != null) {
            rotor.setRotY(angleYRad * 4);
        }
    }

    private void foldingStartup() {

        GeoBone rackfl = getAnimationProcessor().getBone("rackfl");
        GeoBone rackfr = getAnimationProcessor().getBone("rackfr");
        GeoBone rackrl = getAnimationProcessor().getBone("rackrl");
        GeoBone rackrr = getAnimationProcessor().getBone("rackrr");

        if (rackfl != null) {
            rackfl.setRotZ(60);
        }
        if (rackfr != null) {
            rackfr.setRotZ(-60);
        }
        if (rackrl != null) {
            rackrl.setRotZ(-60);
        }
        if (rackrr != null) {
            rackrr.setRotZ(60);
        }
    }
}
