package geni.withercore.helper;

import codechicken.lib.math.MathHelper;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import geni.withercore.util.FacingUtil;
import geni.witherutils.WitherUtils;
import geni.witherutils.client.render.type.WUTRenderType;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class RenderHelper {

    public static void renderTextInWorld(GuiGraphics guiGraphics, double offsetX, double offsetY, double offsetZ, NonNullList<String> text, int color) {
        PoseStack matrices = guiGraphics.pose();
        matrices.pushPose();
        matrices.translate(offsetX, offsetY, offsetZ);
        matrices.scale(-1.0F, -1.0F, 1.0F);
        matrices.mulPose(Axis.YP.rotationDegrees((Minecraft.getInstance()).cameraEntity.getYRot()));
        matrices.scale(0.01F, 0.01F, 0.01F);
        Font font = (Minecraft.getInstance()).font;
        int y = 0;
        for (String s : text) {
            guiGraphics.drawString(font, s, 0, y, color);
            y += 10;
        }
        matrices.popPose();
    }

    public static float[] getWheelColor(float pos) {
        if (pos < 85.0F)
            return new float[] { pos * 3.0F, 255.0F - pos * 3.0F, 0.0F };
        if (pos < 170.0F)
            return new float[] { 255.0F - (pos -= 85.0F) * 3.0F, 0.0F, pos * 3.0F };
        return new float[] { 0.0F, (pos -= 170.0F) * 3.0F, 255.0F - pos * 3.0F };
    }

    public static void renderHitOutline(PoseStack poseStack, VertexConsumer consumer, Entity entity, double camX, double camY, double camZ, Level level, BlockPos pos, BlockState state) {
        renderShape(poseStack, consumer, state.getShape((BlockGetter)level, pos, CollisionContext.of(entity)), pos
                .getX() - camX, pos
                .getY() - camY, pos
                .getZ() - camZ, 0.0F, 0.0F, 0.0F, 0.4F);
    }

    private static void renderShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha) {
        PoseStack.Pose posestack$pose = poseStack.last();
        shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {
            float f = (float)(maxX - minX);
            float f1 = (float)(maxY - minY);
            float f2 = (float)(maxZ - minZ);
            float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
            f /= f3;
            f1 /= f3;
            f2 /= f3;
            consumer.addVertex(posestack$pose.pose(), (float)(minX + x), (float)(minY + y), (float)(minZ + z)).setColor(red, green, blue, alpha).setNormal(posestack$pose, f, f1, f2);
            consumer.addVertex(posestack$pose, (float)(maxX + x), (float)(maxY + y), (float)(maxZ + z)).setColor(red, green, blue, alpha).setNormal(posestack$pose, f, f1, f2);
        });
    }

    /*
     *
     * EMISSIVE
     *
     */
    public static final ResourceLocation EMISSIVE = WitherUtils.loc("textures/block/emissive/blue.png");

    public static ResourceLocation[] reslocs = new ResourceLocation[]
            {
                    WitherUtils.loc("textures/block/emissive/red.png"),
                    WitherUtils.loc("textures/block/emissive/green.png"),
                    WitherUtils.loc("textures/block/emissive/yellow.png")
            };
    public static final Map<Integer, ResourceLocation> LIGHT_POWER_MAP = Util.make(
            new HashMap<>(), i -> {
                for(int j = 0; j < 3; j++)
                    i.put(j, reslocs[j]);
            });

    /**
     * Rotates the given poseStack in a pivot, around an axis for the given degrees.
     *
     * @param poseStack The PoseStack needed to rotate
     * @param pivot     The Point used as center for the rotation
     * @param axis      The axis around which is rotated
     * @param angle     The angle which it is rotated
     * @param degrees   If the angle is given in degrees
     */
    public static void rotateAroundPivot(PoseStack poseStack, Vector3f pivot, Axis axis, float angle, boolean degrees)
    {
        poseStack.translate(pivot.x(), pivot.y(), pivot.z());
        if (degrees) {
            angle = (float) Math.toRadians(angle);
        }

        poseStack.mulPose(axis.rotation(angle));
        poseStack.translate(-pivot.x(), -pivot.y(), -pivot.z());
    }

    public static Matrix4 rotateAroundPivot(Matrix4 mat, PoseStack poseStack, Vector3f pivot, Axis axis, float angle, boolean degrees)
    {
        poseStack.translate(pivot.x(), pivot.y(), pivot.z());
        if (degrees)
        {
            angle = (float) Math.toRadians(angle);
        }
        poseStack.mulPose(axis.rotation(angle));
        poseStack.translate(-pivot.x(), -pivot.y(), -pivot.z());
        Matrix4 matCh = new Matrix4(poseStack);
        mat.apply(matCh);
        return mat;
    }

    public static Matrix4 scaleFromChangedPivot(Matrix4 mat, PoseStack poseStack, Vector3f pivot,
                                                boolean fromXAxis, boolean fromYAxis, boolean fromZAxis, float amount, boolean test)
    {
        amount = 1f;
        poseStack.translate(pivot.x(), pivot.y(), pivot.z());
        poseStack.scale(!fromXAxis ? 1.f : amount, !fromYAxis ? 1.f : amount, !fromZAxis ? 1.f : amount);
        poseStack.translate(-pivot.x(), -pivot.y(), -pivot.z());
        Matrix4 matCh = new Matrix4(poseStack);
        mat.apply(matCh);
        return mat;
    }

    public static void rotateAroundPivot(Matrix4 mat, Vector3f pivot, Vector3 axis, float angle, boolean degrees)
    {
        mat.translate(pivot.x(), pivot.y(), pivot.z());
        if (degrees)
        {
            angle = (float) Math.toRadians(angle);
        }
        mat.rotate(angle * MathHelper.torad, axis);
        mat.translate(-pivot.x(), -pivot.y(), -pivot.z());
    }

    private static final Map<Direction, Quaternionf> ROTATE_FOR_FACING = Util.make(
            new EnumMap<>(Direction.class), m -> {
                for(Direction facing : FacingUtil.BY_HORIZONTAL_INDEX)
                    m.put(facing, new Quaternionf().rotateY(Mth.DEG_TO_RAD*(180 + facing.toYRot())));
            }
    );

    protected static void rotateForFacingNoCentering(PoseStack stack, Direction facing)
    {
        stack.mulPose(ROTATE_FOR_FACING.get(facing));
    }

    public static void rotateToPlayerFacing(PoseStack matrixStack)
    {
        matrixStack.mulPose(Axis.YP.rotationDegrees(180F - Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));
        matrixStack.mulPose(Axis.XP.rotationDegrees(180F - Minecraft.getInstance().gameRenderer.getMainCamera().getXRot()));
    }

    protected static void rotateForFacing(PoseStack stack, Direction facing)
    {
        stack.translate(0.5, 0.5, 0.5);
        rotateForFacingNoCentering(stack, facing);
        stack.translate(-0.5, -0.5, -0.5);
    }

    public void translateFromPivot(Matrix4 mat,
                                   float offsetPivotX, float offsetPivotY, float offsetPivotZ,
                                   float newOffsetX, float newOffsetY, float newOffsetZ)
    {
        mat.translate(offsetPivotX, offsetPivotY, offsetPivotZ);
        mat.translate(newOffsetX, newOffsetY, newOffsetZ);
        mat.translate(-offsetPivotX, -offsetPivotY, -offsetPivotZ);
    }

    public void rotateFromPivot(Matrix4 mat,
                                float offsetPivotX, float offsetPivotY, float offsetPivotZ,
                                float rotateAmount, Vector3 rotateAxis)
    {
        mat.translate(offsetPivotX, offsetPivotY, offsetPivotZ);
        mat.rotate(rotateAmount * MathHelper.torad, rotateAxis);
        mat.translate(-offsetPivotX, -offsetPivotY, -offsetPivotZ);
    }

    /*
     *
     * TEXTURES
     *
     */
    public static TextureAtlasSprite getBlockTexture(String string)
    {
        return getBlockTexture(ResourceLocation.withDefaultNamespace(string));
    }
    public static TextureAtlasSprite getBlockTexture(ResourceLocation location)
    {
        return getTexture(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "block/" + location.getPath()));
    }
    public static TextureAtlasSprite getTexture(ResourceLocation location)
    {
        return getTextureMap().getSprite(location);
    }

    @SuppressWarnings("deprecation")
    public static TextureAtlas getTextureMap()
    {
        return Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
    }

    /*
     *
     * TESSELATEBLOCK
     *
     */
    public static void renderModel(BlockEntity te, BakedModel model, PoseStack matrix, MultiBufferSource buffer, int overlayLight, RenderType renderType)
    {
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(
                te.getLevel(), model,
                te.getBlockState(),
                te.getBlockPos(),
                matrix,
                buffer.getBuffer(renderType),
                true,
                RandomSource.create(),
                te.getBlockState().getSeed(te.getBlockPos()),
                overlayLight,
                ModelData.EMPTY,
                renderType
        );
    }

    /*
     *
     * NONFACING
     *
     */
    public static void renderSpecialModel(BakedModel model, ItemDisplayContext transformType, boolean leftHanded, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int color, int lightTexture, int overlayTexture, RenderType renderType)
    {
        matrixStack.pushPose();
        ClientHooks.handleCameraTransforms(matrixStack, model, transformType, leftHanded);

        if(!model.isCustomRenderer())
        {
            VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(renderType);
            Minecraft.getInstance().getItemRenderer().renderModelLists(model, ItemStack.EMPTY, lightTexture, overlayTexture, matrixStack, vertexBuilder);
        }
        matrixStack.popPose();
    }

    /*
     *
     * FACING
     *
     */
    public static void renderSpecialFacingModel(BakedModel model, ItemDisplayContext transformType, boolean leftHanded, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int color, int lightTexture, int overlayTexture, RenderType renderType, @Nullable Direction facing)
    {
        ClientHooks.handleCameraTransforms(matrixStack, model, transformType, leftHanded);
        if(!model.isCustomRenderer())
        {
            matrixStack.pushPose();

            double x = Vector3.CENTER.x - 0.5F;
            double y = Vector3.CENTER.y - 0.5F;
            double z = Vector3.CENTER.z - 0.5F;

            matrixStack.translate(x, y, z);

            if(facing != null)
            {
                matrixStack.translate(0.5, 0.5, 0.5);
                if(facing == Direction.NORTH)
                {
                }
                else if(facing == Direction.SOUTH)
                {
                    matrixStack.mulPose(Axis.YN.rotationDegrees(180));
                }
                else if(facing == Direction.EAST)
                {
                    matrixStack.mulPose(Axis.YN.rotationDegrees(90));
                }
                else if(facing == Direction.WEST)
                {
                    matrixStack.mulPose(Axis.YP.rotationDegrees(90));
                }
                else if(facing == Direction.UP)
                {
                    matrixStack.mulPose(Axis.XP.rotationDegrees(90));
                }
                else if(facing == Direction.DOWN)
                {
                    matrixStack.mulPose(Axis.XN.rotationDegrees(90));
                }
                matrixStack.translate(-0.5, -0.5, -0.5);
            }
            VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(renderType);
            Minecraft.getInstance().getItemRenderer().renderModelLists(model, ItemStack.EMPTY, lightTexture, overlayTexture, matrixStack, vertexBuilder);

            matrixStack.popPose();
        }
    }

    /*
     *
     * FACING ROTATION OFFSET
     *
     */
    public static void renderSpecialFacingModel(BakedModel model, ItemDisplayContext transformType, boolean leftHanded, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int color, int lightTexture, int overlayTexture, RenderType renderType, @Nullable Direction facing, float rotation, float offset)
    {
        ClientHooks.handleCameraTransforms(matrixStack, model, transformType, leftHanded);
        if(!model.isCustomRenderer())
        {
            matrixStack.pushPose();

            double x = Vector3.CENTER.x - 0.5F;
            double y = Vector3.CENTER.y - 0.5F;
            double z = Vector3.CENTER.z - 0.5F;

            matrixStack.translate(x, y, z);

            if(facing != null)
            {
                matrixStack.translate(0.5, 0.5, 0.5);
                if(facing == Direction.NORTH)
                {
                    matrixStack.translate(0.0, 0.0, -offset);
                }
                else if(facing == Direction.SOUTH)
                {
                    matrixStack.translate(0.0, 0.0, offset);
                    matrixStack.mulPose(Axis.YN.rotationDegrees(180));
                }
                else if(facing == Direction.EAST)
                {
                    matrixStack.translate(offset, 0.0, 0.0);
                    matrixStack.mulPose(Axis.YN.rotationDegrees(90));
                }
                else if(facing == Direction.WEST)
                {
                    matrixStack.translate(-offset, 0.0, 0.0);
                    matrixStack.mulPose(Axis.YP.rotationDegrees(90));
                }
                else if(facing == Direction.UP)
                {
                    matrixStack.translate(0.0, offset, 0.0);
                    matrixStack.mulPose(Axis.XP.rotationDegrees(90));
                }
                else if(facing == Direction.DOWN)
                {
                    matrixStack.translate(0.0, -offset, 0.0);
                    matrixStack.mulPose(Axis.XN.rotationDegrees(90));
                }
                matrixStack.mulPose(Axis.XN.rotationDegrees(rotation));
                matrixStack.translate(-0.5, -0.5, -0.5);
            }
            VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(renderType);
            Minecraft.getInstance().getItemRenderer().renderModelLists(model, ItemStack.EMPTY, lightTexture, overlayTexture, matrixStack, vertexBuilder);

            matrixStack.popPose();
        }
    }

    /*
     *
     * RENDER CUBE TRANSPARENT _ new float[]{1.0f, 0.f, 0.f} example - red
     *
     */
    public static void renderCubeColoredTransparent(PoseStack matrix, MultiBufferSource getter, int combinedLight, float[] colorIn, float alphaIn)
    {
        float xMin, xMax, yMin, yMax, zMin, zMax = 0;

        xMax = 1.984375F;
        yMax = 0.984375F;
        zMax = 1.984375F;
        xMin = 0.015625F;
        zMin = 0.015625F;
        yMin = 0.015625F;

        float red = colorIn[0];
        float green = colorIn[1];
        float blue = colorIn[2];

        VertexConsumer buffer = getter.getBuffer(WUTRenderType.GHOST);
        TextureAtlasSprite textureAtlasSprite = getBlockTexture(BuiltInRegistries.BLOCK.getKey(Blocks.MAGENTA_CONCRETE));

        float uMin = textureAtlasSprite.getU0();
        float uMax = textureAtlasSprite.getU1();
        float vMin = textureAtlasSprite.getV0();
        float vMax = textureAtlasSprite.getV1();

        float vHeight = vMax - vMin;

        addVertexWithUV(buffer, matrix, xMax, yMax, zMax, uMax, vMin, red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMax, yMax, zMin, uMin, vMin, red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMin, yMax, zMin, uMin, vMax, red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMin, yMax, zMax, uMax, vMax, red, green, blue, alphaIn, combinedLight);

        addVertexWithUV(buffer, matrix, xMax, yMin, zMin, uMax, vMin, red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMin, yMin, zMin, uMin, vMin, red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMin, yMax, zMin, uMin, vMin + (vHeight * yMax), red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMax, yMax, zMin, uMax, vMin + (vHeight * yMax), red, green, blue, alphaIn, combinedLight);

        addVertexWithUV(buffer, matrix, xMax, yMin, zMax, uMin, vMin, red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMax, yMax, zMax, uMin, vMin + (vHeight * yMax), red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMin, yMax, zMax, uMax, vMin + (vHeight * yMax), red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMin, yMin, zMax, uMax, vMin, red, green, blue, alphaIn, combinedLight);

        addVertexWithUV(buffer, matrix, xMax, yMin, zMin, uMin, vMin, red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMax, yMax, zMin, uMin, vMin + (vHeight * yMax), red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMax, yMax, zMax, uMax, vMin + (vHeight * yMax), red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMax, yMin, zMax, uMax, vMin, red, green, blue, alphaIn, combinedLight);

        addVertexWithUV(buffer, matrix, xMin, yMin, zMax, uMin, vMin, red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMin, yMax, zMax, uMin, vMin + (vHeight * yMax), red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMin, yMax, zMin, uMax, vMin + (vHeight * yMax), red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMin, yMin, zMin, uMax, vMin, red, green, blue, alphaIn, combinedLight);

        addVertexWithUV(buffer, matrix, xMax, yMin, zMin, uMax, vMin, red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMax, yMin, zMax, uMin, vMin, red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMin, yMin, zMax, uMin, vMax, red, green, blue, alphaIn, combinedLight);
        addVertexWithUV(buffer, matrix, xMin, yMin, zMin, uMax, vMax, red, green, blue, alphaIn, combinedLight);
    }

    /*
     *
     * TEXT
     *
     */
    public void renderTextAboveCamera(PoseStack matrix, MultiBufferSource renderer, int overlayLight, Component text, float maxScale, Minecraft mc)
    {
        float opacity = mc.options.getBackgroundOpacity(0.25F);
        int j = (int) (opacity * 255.0F) << 24;
        float halfWidth = -mc.font.width(text) / 2;
        Matrix4f positionMatrix;
        matrix.pushPose();
        matrix.translate(0.5D, 1.25D, 0.5D);
        matrix.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        matrix.scale(-0.010F, -0.010F, 0.010F);
        positionMatrix = matrix.last().pose();
        mc.font.drawInBatch(text, halfWidth, 0, 553648127, false, positionMatrix, renderer, Font.DisplayMode.SEE_THROUGH, j, overlayLight);
        mc.font.drawInBatch(text, halfWidth, 0, -1, false, positionMatrix, renderer, Font.DisplayMode.NORMAL, 0, overlayLight);
        matrix.popPose();
    }

    /*
     *
     * FLUIDTANK
     *
     */
    public void renderFluidTank(IFluidHandler fluidHandler, BlockEntity te, PoseStack matrixStack, MultiBufferSource renderer, int combinedLight, float alpha, float minX, float minY, float maxX, float minZ, float maxZ, float maxY)
    {
        if(!fluidHandler.getFluidInTank(0).isEmpty())
        {
            float fluidLevel = fluidHandler.getFluidInTank(0).getAmount();
            if(fluidLevel > 0)
            {
                float height = (0.96875F / fluidHandler.getTankCapacity(0)) * fluidHandler.getFluidInTank(0).getAmount();
                render(te, matrixStack, renderer, combinedLight, fluidHandler.getFluidInTank(0), alpha, minX, minY, minZ, maxX, maxY, maxZ, height);
            }
        }
    }
    public void render(BlockEntity te, PoseStack matrixStack, MultiBufferSource renderer, int combinedLight, FluidStack fluidStack, float alpha, float minX, float minY, float minZ, float maxX, float maxHeight, float maxZ, float height)
    {
        var fluidExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        int fluidColor = fluidExtensions.getTintColor();

        TextureAtlasSprite fluidStillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidExtensions.getStillTexture());

        float red = (fluidColor >> 16 & 0xFF) / 255.0F;
        float green = (fluidColor >> 8 & 0xFF) / 255.0F;
        float blue = (fluidColor & 0xFF) / 255.0F;

        if(height >= maxHeight)
        {
            height = maxHeight;
        }

        float uMin = fluidStillSprite.getU0();
        float uMax = fluidStillSprite.getU1();
        float vMin = fluidStillSprite.getV0();
        float vMax = fluidStillSprite.getV1();

        float vHeight = vMax - vMin;

        VertexConsumer buffer = renderer.getBuffer(RenderType.SOLID);

        addVertexWithUV(buffer, matrixStack, maxX, height, maxZ, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, maxX, height, minZ, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, minX, height, minZ, uMin, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, minX, height, maxZ, uMax, vMax, red, green, blue, alpha, combinedLight);

        addVertexWithUV(buffer, matrixStack, maxX, minY, minZ, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, minX, minY, minZ, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, minX, height, minZ, uMin, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, maxX, height, minZ, uMax, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);

        addVertexWithUV(buffer, matrixStack, maxX, minY, maxZ, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, maxX, height, maxZ, uMin, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, minX, height, maxZ, uMax, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, minX, minY, maxZ, uMax, vMin, red, green, blue, alpha, combinedLight);

        addVertexWithUV(buffer, matrixStack, maxX, minY, minZ, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, maxX, height, minZ, uMin, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, maxX, height, maxZ, uMax, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, maxX, minY, maxZ, uMax, vMin, red, green, blue, alpha, combinedLight);

        addVertexWithUV(buffer, matrixStack, minX, minY, maxZ, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, minX, height, maxZ, uMin, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, minX, height, minZ, uMax, vMin + (vHeight * height), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, minX, minY, minZ, uMax, vMin, red, green, blue, alpha, combinedLight);

        addVertexWithUV(buffer, matrixStack, maxX, minY, minZ, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, maxX, minY, maxZ, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, minX, minY, maxZ, uMin, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, minX, minY, minZ, uMax, vMax, red, green, blue, alpha, combinedLight);
    }

    /*
     *
     * ADDVERTEX
     *
     */
    private static void addVertexWithUV(VertexConsumer buffer, PoseStack matrixStack, float x, float y, float z, float u, float v, float red, float green, float blue, float alpha, int combinedLight)
    {
        buffer.addVertex(matrixStack.last().pose(), x / 2f, y, z / 2f).setColor(red, green, blue, alpha).setUv(u, v).setUv2(combinedLight, 240).setNormal(1, 0, 0);
    }
    public static void addQuad(Matrix4f matrix, PoseStack.Pose normal, VertexConsumer buffer, int combinedLight, int combinedOverlay, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2)
    {
        addVertex(matrix, normal, buffer, combinedLight, combinedOverlay, x2, y1, z, u1, v1);
        addVertex(matrix, normal, buffer, combinedLight, combinedOverlay, x2, y2, z, u1, v2);
        addVertex(matrix, normal, buffer, combinedLight, combinedOverlay, x1, y2, z, u2, v2);
        addVertex(matrix, normal, buffer, combinedLight, combinedOverlay, x1, y1, z, u2, v1);
    }
    private static void addVertex(Matrix4f matrix, PoseStack.Pose normal, VertexConsumer buffer, int combinedLight, int combinedOverlay, float x, float y, float z, float u, float v)
    {
        buffer.addVertex(matrix, x, y, z).setColor(1f, 1f, 1f, 1f).setUv(u, v).setOverlay(combinedOverlay).setLight(combinedLight).setNormal(normal, 0, 1, 0);
    }
}
