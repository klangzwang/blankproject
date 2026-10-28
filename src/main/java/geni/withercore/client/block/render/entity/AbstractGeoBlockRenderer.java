package geni.withercore.client.block.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import geni.withercore.common.world.level.block.entity.WitherBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

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
    }

    public abstract ResourceLocation getModelResourceLocation(T animatable);

    public abstract ResourceLocation getTextureResourceLocation(T animatable);

    public abstract ResourceLocation getAnimationResourceLocation(T animatable);

    public abstract void setCustomAnim(T animatable, long instanceId, AnimationState<T> animationState);

    @Override
    public GeoModel<T> getGeoModel() {
        return this.model;
    }

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
