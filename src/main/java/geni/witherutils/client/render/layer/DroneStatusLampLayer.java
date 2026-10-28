package geni.witherutils.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import geni.witherapi.tags.WitherTags;
import geni.withercore.entity.AbstractDroneEntity;
import geni.withercore.helper.DroneKinematicsHelper;
import geni.witherutils.common.world.entity.drone.DroneDeliverEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;
import java.util.Optional;

/**
 * Dedicated Emissive GeoRenderLayer for the front 'status_lamp' bone of DroneDeliverEntity.
 *
 * Lamp Color Logic:
 * - BLACK (Emissive off / dark): When drone is powered off and falling in mid-air (!onGround() && STATE_OFF),
 *   or during the dark phase of a blink cycle.
 * - YELLOW (Slow blinking): When drone is powered off on the ground and has no battery or an empty battery (0 FE).
 * - WHITE (Steady emissive): When drone is powered off on the ground, has a battery in the slot, and battery > 0 FE.
 * - RED (Fast blinking): When the drone is turning on / spooling up (STATE_STARTUP_CHECK / STATE_TAKEOFF).
 * - LIGHT BLUE (Steady emissive): When the drone is powered on and rotor speed is high (>= HIGH_ROTOR_SPEED_THRESHOLD).
 */
public class DroneStatusLampLayer extends GeoRenderLayer<DroneDeliverEntity> {

    private static final ResourceLocation WHITE_LAMP_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(WitherTags.MODID, "textures/entity/drone/drone_status_lamp.png");

    // ARGB Color Constants
    private static final int COLOR_BLACK_OFF = FastColor.ARGB32.color(255, 18, 20, 24);
    private static final int COLOR_RED_BLINK = FastColor.ARGB32.color(255, 255, 36, 48);
    private static final int COLOR_YELLOW_BLINK = FastColor.ARGB32.color(255, 255, 204, 20);
    private static final int COLOR_WHITE_READY = FastColor.ARGB32.color(255, 248, 252, 255);
    private static final int COLOR_LIGHT_BLUE_ACTIVE = FastColor.ARGB32.color(255, 56, 220, 255);

    public DroneStatusLampLayer(GeoRenderer<DroneDeliverEntity> entityRendererIn) {
        super(entityRendererIn);
    }

    /*@Override
    public void render(PoseStack poseStack, DroneDeliverEntity animatable, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                       float partialTick, int packedLight, int packedOverlay) {

        // 1. Hole das Hauptmodell der Entität und den spezifischen Bone, der mitdrehen soll
        GeoModel<T> mainModel = this.getGeoModelProvider();
        CoreGeoBone mainBone = mainModel.getBone("head").orElse(null); // Name des Bones im Entitätsmodell


        Optional<GeoBone> bodyBoneOpt = bakedModel.getBone("body");



        if (mainBone != null) {
            poseStack.pushPose();

            // 2. Übertrage die Position, Rotation und Skalierung des Entitäts-Bones auf den PoseStack
            RenderUtil.translateMatrixToBone(poseStack, mainBone);
            RenderUtil.translateToPivotPoint(poseStack, mainBone);
            RenderUtil.rotateMatrixAroundBone(poseStack, mainBone);
            RenderUtil.scaleMatrixForBone(poseStack, mainBone);
            RenderUtil.translateAwayFromPivotPoint(poseStack, mainBone);

            // 3. Render das Modell des Layers
            BakedGeoModel layerModel = this.getGeoModelProvider().getBakedModel(LAYER_MODEL);
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(LAYER_TEXTURE));

            this.getRenderer().actuallyRender(
                    poseStack, animatable, layerModel,
                    RenderType.entityCutoutNoCull(LAYER_TEXTURE),
                    bufferSource, buffer, isReRender,
                    partialTick, packedLight, OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F
            );

            poseStack.popPose();
        }
    }*/

    @Override
    public void render(PoseStack poseStack, DroneDeliverEntity animatable, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                       float partialTick, int packedLight, int packedOverlay) {

        Optional<GeoBone> lampBoneOpt = bakedModel.getBone("status_lamp");
        if (lampBoneOpt.isEmpty()) {
            return;
        }

        LampVisualState visualState = evaluateLampState(animatable, partialTick);

        // Render only the 'status_lamp' bone localized inside drone_body's transform hierarchy
        GeoBone lampBone = lampBoneOpt.get();
        Optional<GeoBone> bodyBoneOpt = bakedModel.getBone("body");

        poseStack.pushPose();
        if (bodyBoneOpt.isPresent()) {
            /*RenderUtil.prepMatrixForBone(poseStack, bodyBoneOpt.get());*/

            RenderUtil.translateMatrixToBone(poseStack, bodyBoneOpt.get());
            RenderUtil.translateToPivotPoint(poseStack, bodyBoneOpt.get());
            RenderUtil.rotateMatrixAroundBone(poseStack, bodyBoneOpt.get());
            RenderUtil.scaleMatrixForBone(poseStack, bodyBoneOpt.get());
            RenderUtil.translateAwayFromPivotPoint(poseStack, bodyBoneOpt.get());

        }

        RenderType lampRenderType = visualState.emissive()
                ? RenderType.eyes(WHITE_LAMP_TEXTURE)
                : RenderType.entityCutoutNoCull(WHITE_LAMP_TEXTURE);
        VertexConsumer lampConsumer = bufferSource.getBuffer(lampRenderType);
        int light = visualState.emissive() ? LightTexture.FULL_BRIGHT : packedLight;

        getRenderer().renderRecursively(
                poseStack,
                animatable,
                lampBone,
                lampRenderType,
                bufferSource,
                lampConsumer,
                true,
                partialTick,
                light,
                OverlayTexture.NO_OVERLAY,
                visualState.argbColor()
        );

        poseStack.popPose();
    }

    public static LampVisualState evaluateLampState(DroneDeliverEntity drone, float partialTick) {
        float age = drone.tickCount + partialTick;
        int state = drone.getDroneState();

        // 1. Powered ON states
        if (state != AbstractDroneEntity.STATE_OFF) {
            // Light-blue steady when powered on and rotor speed is high
            if (state == AbstractDroneEntity.STATE_HOVERING
                    || drone.getRotorSpeed() >= DroneKinematicsHelper.HIGH_ROTOR_SPEED_THRESHOLD) {
                return new LampVisualState(COLOR_LIGHT_BLUE_ACTIVE, true);
            }

            // Red fast blinking when turning on / spooling up (period = 4 ticks -> 2 ticks ON, 2 ticks OFF)
            boolean fastBlinkOn = ((int) (age * 0.85F) % 2) == 0;
            if (fastBlinkOn) {
                return new LampVisualState(COLOR_RED_BLINK, true);
            } else {
                return new LampVisualState(COLOR_BLACK_OFF, false);
            }
        }

        // 2. Powered OFF while falling in mid-air -> Black (lamp off)
        if (!drone.onGround()) {
            return new LampVisualState(COLOR_BLACK_OFF, false);
        }

        // 3. Powered OFF on the ground with a charged battery in the slot -> Steady White
        if (drone.hasBatteryPower()) {
            return new LampVisualState(COLOR_WHITE_READY, true);
        }

        // 4. Powered OFF on the ground with NO battery or EMPTY battery (0 FE) -> Slow blinking Yellow
        boolean slowBlinkOn = ((drone.tickCount / 14) % 2) == 0;
        if (slowBlinkOn) {
            return new LampVisualState(COLOR_YELLOW_BLINK, true);
        } else {
            return new LampVisualState(COLOR_BLACK_OFF, false);
        }
    }

    public record LampVisualState(int argbColor, boolean emissive) {
    }
}
