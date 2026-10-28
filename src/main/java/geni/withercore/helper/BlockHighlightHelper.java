package geni.withercore.helper;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.OptionalDouble;

/**
 * Reusable geometry, camera-vector & F3 formatting helpers in withercore/helper.
 */
public final class BlockHighlightHelper {

    public static final RenderType LINES = RenderType.create(
            "lines",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            1536,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
                    .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderType.ITEM_ENTITY_TARGET)
                    .setWriteMaskState(RenderType.COLOR_WRITE)
                    .setDepthTestState(RenderType.NO_DEPTH_TEST)
                    .setCullState(RenderType.NO_CULL)
                    .createCompositeState(false)
    );

    private BlockHighlightHelper() {}

    /**
     * Finds the closest of the 8 block corners [0|1, 0|1, 0|1] relative to the camera.
     */
    public static Vector3f findNearestLocalCorner(BlockPos pos, Vec3 camPos) {
        float bestX = 0f, bestY = 0f, bestZ = 0f;
        double minDistSq = Double.MAX_VALUE;

        for (int dx = 0; dx <= 1; dx++) {
            for (int dy = 0; dy <= 1; dy++) {
                for (int dz = 0; dz <= 1; dz++) {
                    double wx = pos.getX() + dx;
                    double wy = pos.getY() + dy;
                    double wz = pos.getZ() + dz;
                    double distSq = camPos.distanceToSqr(wx, wy, wz);
                    if (distSq < minDistSq) {
                        minDistSq = distSq;
                        bestX = dx;
                        bestY = dy;
                        bestZ = dz;
                    }
                }
            }
        }
        return new Vector3f(bestX, bestY, bestZ);
    }

    /**
     * Returns a normalized vector pointing strictly to the right of the camera view plane.
     */
    public static Vector3f getCameraRightVector(Camera camera) {
        return new Vector3f(camera.getLeftVector()).negate().normalize();
    }

    /**
     * Returns a normalized vector pointing strictly up in the camera view plane.
     */
    public static Vector3f getCameraUpVector(Camera camera) {
        return new Vector3f(camera.getUpVector()).normalize();
    }

    /**
     * Draws the 3 RGB axes starting at (ox, oy, oz) with length 'axisLength' (Variante B).
     */
    public static void drawCenterAxesGizmo(
            VertexConsumer vc, Matrix4f mat,
            float ox, float oy, float oz, float axisLength
    ) {
        // +X Axis (Red)
        drawLine(vc, mat, ox, oy, oz, ox + axisLength, oy, oz, 0.94f, 0.27f, 0.27f, 1.0f);
        // +Y Axis (Green)
        drawLine(vc, mat, ox, oy, oz, ox, oy + axisLength, oz, 0.13f, 0.77f, 0.37f, 1.0f);
        // +Z Axis (Blue)
        drawLine(vc, mat, ox, oy, oz, ox, oy, oz + axisLength, 0.23f, 0.51f, 0.96f, 1.0f);
    }

    /**
     * Draws an outward 3D arrow starting at the center of the given block face.
     * Example: Direction.UP -> starts at (0.5, 1.0, 0.5) and points to (0.5, 1.0 + length, 0.5).
     */
    public static void drawFaceOutwardArrow(
            VertexConsumer vc, Matrix4f mat,
            Direction dir, float length, float headSize,
            float r, float g, float b, float a
    ) {
        float nx = dir.getStepX();
        float ny = dir.getStepY();
        float nz = dir.getStepZ();

        // Face center coordinates in local block space [0..1]
        float startX = 0.5f + nx * 0.5f;
        float startY = 0.5f + ny * 0.5f;
        float startZ = 0.5f + nz * 0.5f;

        // Arrow tip coordinates
        float tipX = startX + nx * length;
        float tipY = startY + ny * length;
        float tipZ = startZ + nz * length;

        // Main shaft
        drawLine(vc, mat, startX, startY, startZ, tipX, tipY, tipZ, r, g, b, a);

        // Orthogonal vectors for 3D arrowhead wings
        float perp1X = ny != 0 ? 1f : 0f;
        float perp1Y = nx != 0 ? 1f : 0f;
        float perp1Z = (nx == 0 && ny == 0) ? 0f : 0f;
        if (nz != 0) { perp1X = 1f; perp1Y = 0f; }

        float perp2X = (ny * perp1Z) - (nz * perp1Y);
        float perp2Y = (nz * perp1X) - (nx * perp1Z);
        float perp2Z = (nx * perp1Y) - (ny * perp1X);

        float baseX = tipX - nx * headSize;
        float baseY = tipY - ny * headSize;
        float baseZ = tipZ - nz * headSize;
        float wingSpan = headSize * 0.55f;

        // 4 arrowhead fins
        drawLine(vc, mat, tipX, tipY, tipZ, baseX + perp1X * wingSpan, baseY + perp1Y * wingSpan, baseZ + perp1Z * wingSpan, r, g, b, a);
        drawLine(vc, mat, tipX, tipY, tipZ, baseX - perp1X * wingSpan, baseY - perp1Y * wingSpan, baseZ - perp1Z * wingSpan, r, g, b, a);
        drawLine(vc, mat, tipX, tipY, tipZ, baseX + perp2X * wingSpan, baseY + perp2Y * wingSpan, baseZ + perp2Z * wingSpan, r, g, b, a);
        drawLine(vc, mat, tipX, tipY, tipZ, baseX - perp2X * wingSpan, baseY - perp2Y * wingSpan, baseZ - perp2Z * wingSpan, r, g, b, a);
    }

    /**
     * Draws a small 3D cross marker at the nearest block corner vertex.
     */
    public static void drawVertexMarker(
            VertexConsumer vc, Matrix4f mat,
            float x, float y, float z, float radius,
            float r, float g, float b, float a
    ) {
        drawLine(vc, mat, x - radius, y, z, x + radius, y, z, r, g, b, a);
        drawLine(vc, mat, x, y - radius, z, x, y + radius, z, r, g, b, a);
        drawLine(vc, mat, x, y, z - radius, x, y, z + radius, r, g, b, a);
    }

    /**
     * Renders a camera-facing F3 text panel anchored at (anchorX, anchorY, anchorZ).
     */
    public static void drawBillboardInfoPanel(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            Font font,
            Camera camera,
            float anchorX, float anchorY, float anchorZ,
            float textScale,
            int backgroundColor,
            List<Component> lines
    ) {
        Minecraft mc = Minecraft.getInstance();

        int lineHeight = 10;
        int startY = -(lines.size() * lineHeight) / 2;

        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);

            float halfWidth = -mc.font.width(line) / 2;
            Matrix4f positionMatrix;

            poseStack.pushPose();

            poseStack.translate(anchorX, anchorY, anchorZ);
            poseStack.mulPose(camera.rotation());
            poseStack.scale(0.0025F, -0.0025F, 0.0025F);

            positionMatrix = poseStack.last().pose();

            mc.font.drawInBatch(line, halfWidth, startY + (i * lineHeight), 553648127, false, positionMatrix, bufferSource, Font.DisplayMode.SEE_THROUGH, backgroundColor, 0xF000F0);
            mc.font.drawInBatch(line, halfWidth, startY + (i * lineHeight), -1, false, positionMatrix, bufferSource, Font.DisplayMode.NORMAL, 0, 0xF000F0);

            poseStack.popPose();
        }
    }

    public static void renderString3d(Component str, float x, float y, int color, PoseStack matrixStack, MultiBufferSource buffer, boolean dropShadow, boolean disableDepthTest) {
        Font fr = Minecraft.getInstance().font;
        fr.drawInBatch(str, x, y, color, dropShadow, matrixStack.last().pose(), buffer, disableDepthTest ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
    }

    public static void renderString3d(FormattedCharSequence str, float x, float y, int color, PoseStack matrixStack, MultiBufferSource buffer, boolean dropShadow, boolean disableDepthTest) {
        Font fr = Minecraft.getInstance().font;
        fr.drawInBatch(str, x, y, color, dropShadow, matrixStack.last().pose(), buffer, disableDepthTest ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
    }

    /**
     * Formats a BlockState property with authentic F3 colors.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static MutableComponent formatPropertyLine(Property property, Comparable value) {
        String name = property.getName();
        String valStr = property.getName(value);

        ChatFormatting valueColor = ChatFormatting.YELLOW;
        if (property instanceof BooleanProperty) {
            valueColor = Boolean.TRUE.equals(value) ? ChatFormatting.GREEN : ChatFormatting.RED;
        } else if (property instanceof DirectionProperty) {
            valueColor = ChatFormatting.GOLD;
        } else if (property instanceof IntegerProperty) {
            valueColor = ChatFormatting.AQUA;
        }

        return Component.literal(" " + name + ": ")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal(valStr).withStyle(valueColor));
    }

    public static void drawLine(
            VertexConsumer vc, Matrix4f mat,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float r, float g, float b, float a
    ) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len > 1.0e-5f) {
            dx /= len;
            dy /= len;
            dz /= len;
        } else {
            dx = 1f; dy = 0f; dz = 0f;
        }
        vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a).setNormal(dx, dy, dz);
        vc.addVertex(mat, x2, y2, z2).setColor(r, g, b, a).setNormal(dx, dy, dz);
    }
}