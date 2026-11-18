package geni.withercore.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import geni.withercore.client.gui.widget.WitherTitle;
import geni.withercore.common.helper.ColorHelper;
import geni.withercore.common.menu.entity.WitherContainerEntityMenu;
import geni.withercore.common.world.entity.WitherEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public abstract class WitherAbstractEntityScreen<C extends WitherContainerEntityMenu<T>, T extends WitherEntity>
        extends WitherAbstractScreen<C> implements GeoRenderer<T> {

    public final T mob;
    protected final GeoModel<T> model;
    protected WitherTitle screenTitle;

    public WitherAbstractEntityScreen(C menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.mob = menu.getMobInstance();

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

    @Override
    protected void init() {
        super.init();

        if (this.shaderConfig != null) {
            this.shaderConfig.setSize(this.imageWidth, this.imageHeight);
            this.shaderConfig.setRadius(4.0f);
            this.shaderConfig.setBorderThickness(1.0f);

            this.shaderConfig.setBgColorTop(0.0f, 0.0f, 0.0f, 0.0f);
            this.shaderConfig.setBgColorBottom(0.0f, 0.0f, 0.0f, 0.0f);

            this.shaderConfig.setBorderColor(ColorHelper.ICE_BLUE.getRGB());

            this.shaderConfig.setShadowOffset(0.0f, 0.0f);
            this.shaderConfig.setShadowBlur(0.0f);
            this.shaderConfig.setShadowColor(0.0f, 0.0f, 0.0f, 0.0f);
        }

        this.screenTitle = new WitherTitle(leftPos, topPos, width, height, Component.literal(getScreenName()));
        this.screenTitle.setLetterSpacing(-0.5f);
        this.screenTitle.setBoxBounds(width / 2, 0, 0, 0);
        addRenderableWidget(screenTitle);
    }

    public T getPathFinderMob() {
        return mob;
    }

    protected abstract String getScreenName();

    public abstract ResourceLocation getModelResourceLocation(T animatable);

    public abstract ResourceLocation getTextureResourceLocation(T animatable);

    public abstract ResourceLocation getAnimationResourceLocation(T animatable);

    public abstract void setCustomAnim(T animatable, long instanceId, AnimationState<T> animationState);

    @Override
    public GeoModel<T> getGeoModel() {
        return this.model;
    }

    @Override
    public T getAnimatable() {
        return this.mob;
    }

    @Override
    public void fireCompileRenderLayersEvent() {
    }

    @Override
    public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return false;
    }

    @Override
    public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
    }

    @Override
    public void updateAnimatedTextureFrame(T animatable) {
    }
}
