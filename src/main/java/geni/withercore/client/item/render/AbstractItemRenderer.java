package geni.withercore.client.item.render;

import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.item.IItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import geni.withercore.common.helper.TickHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractItemRenderer implements IItemRenderer {

    protected ItemRenderer itemRenderer;

    protected AbstractItemRenderer()
    {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void renderItem(ItemStack stack, ItemDisplayContext ctx, PoseStack mStack, MultiBufferSource source, int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player != null) {
            float partialTick = TickHelper.getPartialTick();
            BakedModel model = IItemRenderer.super.applyTransform(ctx, mStack, false);

            float swingProgress = player.getAttackAnim(partialTick);

            boolean isLeftHand = (ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
            InteractionHand hand = isLeftHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;

            boolean isUsing = player.isUsingItem() && player.getUseItem() == stack;
            float useDuration = isUsing ? (stack.getUseDuration(player) - (player.getUseItemRemainingTicks() - partialTick + 1.0F)) : 0.0F;

            renderItem(stack, model, partialTick, mStack, source, mc, player.clientLevel, player, packedLight, packedOverlay, ctx, swingProgress, hand, isUsing, useDuration, player.isSprinting());
        }
    }

    public abstract void renderItem(
            ItemStack stack,
            BakedModel model,
            float partialTick,
            PoseStack matrix,
            MultiBufferSource buffer,
            Minecraft mc,
            ClientLevel level,
            LocalPlayer player,
            int packedLight,
            int packedOverlay,
            ItemDisplayContext context,
            float swingProgress,
            InteractionHand hand,
            boolean isUsing,
            float useProgressRatio,
            boolean isSprinting
    );

    @Override
    public BakedModel applyTransform(ItemDisplayContext context, PoseStack pStack, boolean leftFlip) {

        if (context == ItemDisplayContext.GUI)
        {
            pStack.mulPose(Axis.YP.rotationDegrees(270));
            pStack.mulPose(Axis.XP.rotationDegrees(-45));
        }
        else if (context == ItemDisplayContext.GROUND)
        {
            pStack.translate(0.0, 0.15, 0.0);
            pStack.scale(0.5F, 0.5F, 0.5F);
        }
        else if (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
        {
            pStack.translate(0.0, 0.2, 0.0);
            pStack.mulPose(Axis.XP.rotationDegrees(-25));
        }
        else if (context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
        {
            pStack.translate(0.0, 0.3, 0.0);
        }

        return this;
    }

    @Override
    public @Nullable PerspectiveModelState getModelState() {
        return null;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }
}
