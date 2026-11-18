package geni.withercore.client.block.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import geni.withercore.common.helper.ColorHelper;
import geni.withercore.common.world.level.block.entity.WitherBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public abstract class AbstractGeoBlockRenderer<T extends WitherBlockEntity> extends GeoBlockRenderer<T> {

    protected ItemRenderer itemRenderer;
    protected BlockRenderDispatcher blockRenderer;

    protected final BlockEntityType<? extends T> blockEntityType;
    protected final GeoModel<T> model;

    protected AbstractGeoBlockRenderer(BlockEntityType<? extends T> blockEntityType) {
        super(blockEntityType);

        this.blockEntityType = blockEntityType;

        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();

        this.model = new GeoModel<T>() {
            @Override
            public ResourceLocation getModelResource(T animatable) {
                return getModelResourceLocation(animatable);
            }

            @Override
            public ResourceLocation getTextureResource(T animatable) {
                return getTextureResourceLocation(animatable);
            }

            @Override
            public ResourceLocation getAnimationResource(T animatable) {
                return getAnimationResourceLocation(animatable);
            }

            @Override
            public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
                setCustomAnim(animatable, instanceId, animationState);
            }
        };

        if(hasConnectionLayer()) {
            addRenderLayer(new ConnectorIOLayer(this));
        }
    }

    public abstract ResourceLocation getModelResourceLocation(T animatable);

    public abstract ResourceLocation getTextureResourceLocation(T animatable);

    public abstract ResourceLocation getAnimationResourceLocation(T animatable);

    public abstract void setCustomAnim(T animatable, long instanceId, AnimationState<T> animationState);

    @Override
    public GeoModel<T> getGeoModel() {
        return this.model;
    }

    public boolean hasConnectionLayer() { return false; };

    @Override
    public void defaultRender(PoseStack poseStack, T animatable, MultiBufferSource bufferSource,
            @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick,
            int packedLight) {
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (animatable.getLevel() != null) {
            if (player != null) {
                /*
                 * matrix.pushPose();
                 * matrix.translate(0.5, 1.5, 0.5);
                 * matrix.scale(1f, -1f, -1f);
                 */
                render(poseStack, animatable, bufferSource, renderType, buffer, mc, player.clientLevel, player, yaw,
                        partialTick, packedLight);
                /* matrix.popPose(); */
            }
        }
    }

    public abstract void render(
            PoseStack poseStack,
            T animatable,
            MultiBufferSource bufferSource,
            @Nullable RenderType renderType,
            @Nullable VertexConsumer buffer,
            Minecraft mc,
            ClientLevel level,
            LocalPlayer player,
            float yaw,
            float partialTick,
            int packedLight);

    @Override
    public boolean shouldRenderOffScreen(WitherBlockEntity be) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}

class ConnectorIOLayer<T extends WitherBlockEntity> extends GeoRenderLayer<T> {

    private static final ResourceLocation MODEL_RESOURCE =
            ResourceLocation.fromNamespaceAndPath("witherutils", "geo/block/factory_connector.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE =
            ResourceLocation.fromNamespaceAndPath("witherutils", "textures/block/factory_connector.png");
    private static final ResourceLocation ANIM_RESOURCE =
            ResourceLocation.fromNamespaceAndPath("witherutils", "animations/block/factory_connector.animation.json");
    private static final ResourceLocation EMISSIVE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("witherutils", "textures/block/emissive/blue.png");

    protected final GeoModel<T> model;

    public ConnectorIOLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);

        this.model = new GeoModel<T>() {
            @Override
            public ResourceLocation getModelResource(T animatable) {

                return MODEL_RESOURCE;
            }

            @Override
            public ResourceLocation getTextureResource(T animatable) {

                return TEXTURE_RESOURCE;
            }

            @Override
            public ResourceLocation getAnimationResource(T animatable) {
                return ANIM_RESOURCE;
            }

            @Override
            public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
                super.setCustomAnimations(animatable, instanceId, animationState);

                for (Direction dir : Direction.values()) {
                    String dirName = dir.getName().toLowerCase();
                    GeoBone rootPortBone = getAnimationProcessor().getBone(dirName + "_port");
                    GeoBone collarBone = getAnimationProcessor().getBone(dirName + "_collar");

                    // 4 Mechanical Clamp Latches
                    GeoBone clampTop = getAnimationProcessor().getBone(dirName + "_clamp_top");
                    GeoBone clampBottom = getAnimationProcessor().getBone(dirName + "_clamp_bottom");
                    GeoBone clampLeft = getAnimationProcessor().getBone(dirName + "_clamp_left");
                    GeoBone clampRight = getAnimationProcessor().getBone(dirName + "_clamp_right");

                    if (rootPortBone == null) continue;

                    // Ports configured with capabilities are visible immediately on block placement!
                    rootPortBone.setHidden(false);

                    // Dynamic Iris Collar Scaling (0.2f - 0.8f)
                    /*float scale = animatable.getRenderScale(dir);
                    if (collarBone != null) {
                        switch (dir.getAxis()) {
                            case X -> {
                                collarBone.setScaleX(1.0f);
                                collarBone.setScaleY(scale);
                                collarBone.setScaleZ(scale);
                            }
                            case Y -> {
                                collarBone.setScaleX(scale);
                                collarBone.setScaleY(1.0f);
                                collarBone.setScaleZ(scale);
                            }
                            case Z -> {
                                collarBone.setScaleX(scale);
                                collarBone.setScaleY(scale);
                                collarBone.setScaleZ(1.0f);
                            }
                        }
                    }*/

                    // 4-Clamp Latch Animation:
                    // clampProgress: 0.0f = snapped shut gripping neighbor, 1.0f = sprung open
                    /*float clampProg = animatable.getRenderClamp(dir);
                    float openAngleRad = clampProg * (float) Math.toRadians(35.0); // 35 degrees open

                    if (clampTop != null) clampTop.setRotX(-openAngleRad);
                    if (clampBottom != null) clampBottom.setRotX(openAngleRad);
                    if (clampLeft != null) clampLeft.setRotY(openAngleRad);
                    if (clampRight != null) clampRight.setRotY(-openAngleRad);*/
                }
            }
        };
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                       float partialTick, int packedLight, int packedOverlay)
    {
        RenderType emissiveType = RenderType.eyes(EMISSIVE_TEXTURE);
        VertexConsumer emissiveConsumer = bufferSource.getBuffer(emissiveType);

        // Iterate through each directional indicator bone and apply dynamic tinting
        for (Direction dir : Direction.values()) {
            /*if (!animatable.isSideConnected(dir)) continue;*/

            String boneName = dir.getName().toLowerCase() + "_indicator";
            GeoBone indicatorBone = getGeoModel().getAnimationProcessor().getBone(boneName);

            if (indicatorBone != null && !indicatorBone.isHidden()) {
                /*ConnectionState state = animatable.getConnectionState(dir);*/

                /*float r = state.getRed();
                float g = state.getGreen();
                float b = state.getBlue();
                float a = 1.0f;*/

                // Pulsate slightly if in active INPUT or OUTPUT state
                /*if (state.isActive() && animatable.getLevel() != null) {
                    float time = animatable.getLevel().getGameTime() + partialTick;
                    float pulse = 0.85f + 0.15f * (float) Math.sin(time * state.getPulseFrequency());
                    r *= pulse;
                    g *= pulse;
                    b *= pulse;
                }*/

                // Full-bright lightmap (15 << 20 | 15 << 4 = 15728880)
                int fullBright = 15728880;

                getRenderer().renderRecursively(poseStack, animatable, indicatorBone, emissiveType,
                        bufferSource, emissiveConsumer, false, partialTick, fullBright, packedOverlay, ColorHelper.CYAN.getRGB());
            }
        }
    }
}
