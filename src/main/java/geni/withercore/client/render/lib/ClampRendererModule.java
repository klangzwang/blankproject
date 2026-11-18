package geni.withercore.client.render.lib;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import geni.witherutils.client.BasicModels;
import geni.witherutils.common.world.level.block.FloorSensorBlock;
import geni.witherutils.common.world.level.block.rack.controller.item.ControllerItemBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.ClientHooks;
import org.joml.Matrix4f;

/**
 * Modularer Renderer für Greifarm-Klammern.
 * Kann an jeden BlockEntityRenderer angehängt werden.
 * 
 * Logik:
 * 1. Ermittelt das VoxelShape des Blocks in 'facing'-Richtung
 * 2. Berechnet die äussersten Werte (min/max) des Shapes
 * 3. Positioniert die Klammern exakt an diesen Grenzen
 */
public class ClampRendererModule {

    private final Direction facing;
    private final float clampHeight;
    private final float clampWidth;
    private final float clampDepth;

    public ClampRendererModule(Direction facing, float clampHeight, float clampWidth, float clampDepth) {
        this.facing = facing;
        this.clampHeight = clampHeight;
        this.clampWidth = clampWidth;
        this.clampDepth = clampDepth;
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource,
                       Level level, BlockPos blockPos, float partialTick) {

        BlockState state = level.getBlockState(blockPos);
        if (!state.hasProperty(ControllerItemBlock.FACING))
            return;

        Direction direction = state.getValue(ControllerItemBlock.FACING);
        BlockPos targetPos = blockPos.relative(direction.getOpposite());
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.isAir())
            return;

        VoxelShape shape = targetState.getShape(level, targetPos);
        ClampBounds bounds = getOutermostBounds(shape);

        /*System.out.println(bounds);*/

        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);

        // 2. Je nach 'facing'-Richtung um die Y-Achse (bzw. entsprechende Achsen) drehen
        switch (direction) {
            case NORTH -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
            case SOUTH -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(0.0F));
            case WEST  -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
            case EAST  -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(270.0F));
            case UP    -> {
                // Falls der Block auch nach oben/unten schauen kann:
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90.0F));
            }
            case DOWN  -> {
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(270.0F));
            }
        }

        // Zurückverschieben aus dem Mittelpunkt
        poseStack.translate(-0.5, -0.5, -0.5);

        // Optional: Falls du das Modell basierend auf den Bounds ('bounds') verschieben willst
        poseStack.translate(bounds.minX(), bounds.minY(), bounds.minZ());

        VertexConsumer vertexBuilder = bufferSource.getBuffer(RenderType.solid());
        Minecraft.getInstance().getItemRenderer().renderModelLists(BasicModels.SNAP_ARM.getModel(),
                ItemStack.EMPTY, -1, OverlayTexture.NO_OVERLAY, poseStack, vertexBuilder);

        poseStack.popPose();
    }

    /**
     * Enthält die min/max Werte des VoxelShapes in allen Achsen.
     */
    public record ClampBounds(
            double minX, double maxX,
            double minY, double maxY,
            double minZ, double maxZ
    ) {
        public double widthX()  { return maxX - minX; }
        public double widthZ()  { return maxZ - minZ; }
        public double height()  { return maxY - minY; }
    }

    /**
     * Ermittelt die äussersten Werte des VoxelShapes.
     * Für komplexere Shapes kann hier über shape.forAllBoxes() iteriert werden.
     */
    private ClampBounds getOutermostBounds(VoxelShape shape) {
        AABB aabb = shape.bounds();
        return new ClampBounds(
                aabb.minX, aabb.maxX,
                aabb.minY, aabb.maxY,
                aabb.minZ, aabb.maxZ
        );
    }

    /**
     * Alternative: Präzisere Bounds durch Iteration aller Boxen im VoxelShape.
     * Nützlich wenn das Shape nicht konvex ist.
     */
    private ClampBounds getPreciseBounds(VoxelShape shape) {
        double[] b = new double[] {
            Double.MAX_VALUE, -Double.MAX_VALUE,
            Double.MAX_VALUE, -Double.MAX_VALUE,
            Double.MAX_VALUE, -Double.MAX_VALUE
        };

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            b[0] = Math.min(b[0], x1); b[1] = Math.max(b[1], x2);
            b[2] = Math.min(b[2], y1); b[3] = Math.max(b[3], y2);
            b[4] = Math.min(b[4], z1); b[5] = Math.max(b[5], z2);
        });

        return new ClampBounds(b[0], b[1], b[2], b[3], b[4], b[5]);
    }

    public static void renderSnaparmModel(ClampBounds bounds, BakedModel model, ItemDisplayContext transformType,
                                          boolean leftHanded, PoseStack matrixStack, MultiBufferSource renderTypeBuffer,
                                          int lightTexture, int overlayTexture)
    {

    }
}