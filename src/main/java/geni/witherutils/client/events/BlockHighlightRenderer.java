package geni.witherutils.client.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import geni.withercore.helper.BlockHighlightHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Modular Block Highlight & F3 Debug Renderer for WitherUtils (NeoForge 1.21.1).
 * Synchronized with live React Inspector settings.
 * Current Preview Target: witherutils:clicker (facing=UP)
 */
/*@EventBusSubscriber(modid = "witherutils", value = Dist.CLIENT)*/
public class BlockHighlightRenderer {

    // =========================================================================
    // MODULAR CONFIGURATION (Direct Fine-Tuning Constants)
    // =========================================================================
    public static final class HighlightSettings {
        // Feature Layer Toggles
        public static boolean KEEP_VANILLA_OUTLINE     = true;
        public static boolean RENDER_CENTER_AXES       = true;
        public static boolean AXES_DISABLE_DEPTH_TEST  = true;
        public static boolean RENDER_CORNER_CALLOUT    = true;
        public static boolean RENDER_DIRECTION_ARROWS  = true;

        // 1. Center 3D Axes (Variante B: Origin at (0.5, 0.5, 0.5))
        public static float CENTER_ORIGIN_X            = 0.5f;
        public static float CENTER_ORIGIN_Y            = 0.5f;
        public static float CENTER_ORIGIN_Z            = 0.5f;
        public static float CENTER_AXIS_LENGTH         = 0.5f; // Half block size = 0.5f

        // 2. Nearest Corner -> Camera-Parallel Callout Line (Always to the Right)
        public static float CALLOUT_RIGHT_LENGTH       = 0.58f;
        public static float CALLOUT_UP_OFFSET          = 0f;
        public static float CORNER_MARKER_RADIUS       = 0.035f;

        // 3. Direction Property Outward Face Arrows
        public static float DIRECTION_ARROW_LENGTH     = 0.46f;
        public static float DIRECTION_ARROW_HEAD_SIZE  = 0.12f;

        // 4. F3 Info Panel Layout & Scale
        public static float INFO_PANEL_TEXT_SCALE      = 0.018f;
        public static float OUTLINE_EXPANSION          = 0.002f;
        public static int   PANEL_BG_COLOR             = 0x90101216;
        public static int   MAX_DISPLAYED_TAGS         = 4;
    }

    @SubscribeEvent
    public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null || mc.player == null) return;

        // Cancel Vanilla outline only if disabled in HighlightSettings
        if (!HighlightSettings.KEEP_VANILLA_OUTLINE) {
            event.setCanceled(true);
        }

        BlockPos pos = event.getTarget().getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return;

        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();

        poseStack.pushPose();
        // Translate local space so (0, 0, 0) is the target block origin
        poseStack.translate(
            pos.getX() - camPos.x,
            pos.getY() - camPos.y,
            pos.getZ() - camPos.z
        );

        if (HighlightSettings.AXES_DISABLE_DEPTH_TEST) {
            RenderSystem.disableDepthTest();
        }

        VertexConsumer lineBuffer = bufferSource.getBuffer(BlockHighlightHelper.LINES);
        Matrix4f poseMatrix = poseStack.last().pose();

        // Step 1: Center 3D Axes in half block size (Variante B)
        if (HighlightSettings.RENDER_CENTER_AXES) {
            renderCenterAxes(poseMatrix, lineBuffer);
        }

        // Step 2: DirectionProperty Outward Face Arrows (from face center)
        if (HighlightSettings.RENDER_DIRECTION_ARROWS) {
            renderDirectionFaceArrows(poseMatrix, lineBuffer, state);
        }

        // Step 3: Nearest Block Corner -> Camera-Parallel Right Line -> F3 Text Panel
        if (HighlightSettings.RENDER_CORNER_CALLOUT) {
            renderNearestCornerCallout(
                poseStack, bufferSource, lineBuffer,
                camera, mc.font, level, pos, state
            );

            if (bufferSource instanceof MultiBufferSource.BufferSource bs) {
                bs.endBatch();
            }
        }

        if (HighlightSettings.AXES_DISABLE_DEPTH_TEST) {
            RenderSystem.enableDepthTest();
        }
        poseStack.popPose();
    }

    // -------------------------------------------------------------------------
    // 1. CENTER AXES (Variante B: (0.5, 0.5, 0.5) -> +Axis)
    // -------------------------------------------------------------------------
    private static void renderCenterAxes(Matrix4f matrix, VertexConsumer lines) {
        BlockHighlightHelper.drawCenterAxesGizmo(
            lines,
            matrix,
            HighlightSettings.CENTER_ORIGIN_X,
            HighlightSettings.CENTER_ORIGIN_Y,
            HighlightSettings.CENTER_ORIGIN_Z,
            HighlightSettings.CENTER_AXIS_LENGTH
        );
    }

    // -------------------------------------------------------------------------
    // 2. DIRECTION PROPERTY FACE ARROWS (Center of Face -> Outward Normal)
    // -------------------------------------------------------------------------
    private static void renderDirectionFaceArrows(Matrix4f matrix, VertexConsumer lines, BlockState state) {
        for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {
            if (entry.getKey() instanceof DirectionProperty && entry.getValue() instanceof Direction dir) {
                BlockHighlightHelper.drawFaceOutwardArrow(
                    lines,
                    matrix,
                    dir,
                    HighlightSettings.DIRECTION_ARROW_LENGTH,
                    HighlightSettings.DIRECTION_ARROW_HEAD_SIZE,
                    1.0f, 0.66f, 0.0f, 1.0f // Gold/Amber F3 Direction Arrow
                );
            }
        }
    }

    // -------------------------------------------------------------------------
    // 3. NEAREST CORNER CALLOUT LINE & F3 INFO PANEL
    // -------------------------------------------------------------------------
    private static void renderNearestCornerCallout(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            VertexConsumer lines,
            Camera camera,
            Font font,
            Level level,
            BlockPos pos,
            BlockState state
    ) {
        Matrix4f matrix = poseStack.last().pose();
        Vec3 camPos = camera.getPosition();

        // Local corner coordinates in [0..1, 0..1, 0..1] closest to camera
        Vector3f localCorner = BlockHighlightHelper.findNearestLocalCorner(pos, camPos);

        // Camera-parallel vectors (Right = always to the right on screen)
        Vector3f camRight = BlockHighlightHelper.getCameraRightVector(camera);
        Vector3f camUp = BlockHighlightHelper.getCameraUpVector(camera);

        float endX = localCorner.x
            + camRight.x * HighlightSettings.CALLOUT_RIGHT_LENGTH
            + camUp.x * HighlightSettings.CALLOUT_UP_OFFSET;
        float endY = localCorner.y
            + camRight.y * HighlightSettings.CALLOUT_RIGHT_LENGTH
            + camUp.y * HighlightSettings.CALLOUT_UP_OFFSET;
        float endZ = localCorner.z
            + camRight.z * HighlightSettings.CALLOUT_RIGHT_LENGTH
            + camUp.z * HighlightSettings.CALLOUT_UP_OFFSET;

        // Draw small highlight cross on the nearest corner vertex
        BlockHighlightHelper.drawVertexMarker(
            lines, matrix,
            localCorner.x, localCorner.y, localCorner.z,
            HighlightSettings.CORNER_MARKER_RADIUS,
            0.98f, 0.75f, 0.14f, 1.0f
        );

        // Draw camera-parallel line from nearest corner to the right
        BlockHighlightHelper.drawLine(
            lines, matrix,
            localCorner.x, localCorner.y, localCorner.z,
            endX, endY, endZ,
            0.98f, 0.75f, 0.14f, 1.0f
        );

        // Build and render the F3 Info Panel at the right end of the line
        List<Component> infoLines = buildBlockInfoLines(level, pos, state, localCorner);


        /*RenderHelper.renderTextInWorld();*/

        BlockHighlightHelper.drawBillboardInfoPanel(
            poseStack, bufferSource, font, camera,
            endX, endY, endZ,
            HighlightSettings.INFO_PANEL_TEXT_SCALE,
            HighlightSettings.PANEL_BG_COLOR,
            infoLines
        );
    }

    // -------------------------------------------------------------------------
    // 4. F3 INFO PANEL CONTENT BUILDER (BlockName, BlockProperties, Other Info)
    // -------------------------------------------------------------------------
    private static List<Component> buildBlockInfoLines(
            Level level,
            BlockPos pos,
            BlockState state,
            Vector3f nearestCorner
    ) {
        List<Component> lines = new ArrayList<>();

        // --- SECTION 1: BlockName ---
        String registryId = state.getBlock().builtInRegistryHolder().key().location().toString();
        lines.add(Component.literal(registryId).withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD));
        lines.add(state.getBlock().getName().copy().withStyle(ChatFormatting.GREEN));

        // --- SECTION 2: BlockProperties ---
        if (!state.getValues().isEmpty()) {
            lines.add(Component.literal("BlockProperties:").withStyle(ChatFormatting.GRAY));
            for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {
                lines.add(BlockHighlightHelper.formatPropertyLine(entry.getKey(), entry.getValue()));
            }
        }

        // --- SECTION 3: Sonstige Informationen ---
        lines.add(Component.literal("Info:").withStyle(ChatFormatting.GRAY));
        lines.add(Component.literal(String.format(" Pos: [%d, %d, %d] (Corner %.0f,%.0f,%.0f)",
            pos.getX(), pos.getY(), pos.getZ(),
            nearestCorner.x, nearestCorner.y, nearestCorner.z
        )).withStyle(ChatFormatting.YELLOW));

        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        lines.add(Component.literal(String.format(" Light: %d (%d sky, %d block)",
            Math.max(skyLight, blockLight), skyLight, blockLight
        )).withStyle(ChatFormatting.AQUA));

        BlockEntity be = level.getBlockEntity(pos);
        if (be != null && be.getType().builtInRegistryHolder() != null) {
            String beId = be.getType().builtInRegistryHolder().key().location().toString();
            lines.add(Component.literal(" BE: " + beId).withStyle(ChatFormatting.GOLD));
        }

        state.getTags()
            .limit(HighlightSettings.MAX_DISPLAYED_TAGS)
            .forEach(tag -> lines.add(
                Component.literal(" #" + tag.location()).withStyle(ChatFormatting.BLUE)
            ));

        return lines;
    }
}