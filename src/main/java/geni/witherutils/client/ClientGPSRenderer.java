package geni.witherutils.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import geni.withercore.math.CatmullRomSpline3D;
import geni.witherutils.common.world.item.GPSToolItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientGPSRenderer {

    @SubscribeEvent
    public void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null)
            return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        ItemStack gpsStack = mainHand.getItem() instanceof GPSToolItem ? mainHand : (offHand.getItem() instanceof GPSToolItem ? offHand : ItemStack.EMPTY);

        if (gpsStack.isEmpty())
            return;

        GPSToolItem gpsItem = (GPSToolItem) gpsStack.getItem();
        List<BlockPos> waypoints = gpsItem.getStoredPositions(gpsStack);
        if (waypoints.isEmpty())
            return;

        boolean isLoop = gpsItem.isClosedLoop(gpsStack);
        Vec3 camPos = event.getCamera().getPosition();
        PoseStack matrix = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        matrix.pushPose();
        matrix.translate(-camPos.x, -camPos.y, -camPos.z);

        VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());

        // 1. Rendere geschmeidige Catmull-Rom Spline-Kurve
        if (waypoints.size() >= 2) {
            List<Vec3> vecPoints = new ArrayList<>();
            for (BlockPos pos : waypoints) {
                vecPoints.add(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
            }

            CatmullRomSpline3D spline = new CatmullRomSpline3D(vecPoints, isLoop, 16);
            renderSplineCurve(lines, matrix, spline, isLoop ? 0.4F : 0.2F, isLoop ? 1.0F : 0.9F, 1.0F, 0.95F);
        }

        // 2. Rendere Wegpunkt-Boxen (Knotenpunkte)
        for (int i = 0; i < waypoints.size(); i++) {
            BlockPos wp = waypoints.get(i);
            float r = (i == 0) ? 0.3F : (i == waypoints.size() - 1 ? 1.0F : 0.2F);
            float g = (i == 0) ? 1.0F : (i == waypoints.size() - 1 ? 0.4F : 0.85F);
            float b = (i == 0) ? 0.3F : (i == waypoints.size() - 1 ? 0.2F : 1.0F);
            renderWaypointBox(lines, matrix, wp, r, g, b, 0.85F);
        }

        matrix.popPose();

        // 3. Rendere 3D-Text Marker über den Wegpunkten
        for (int i = 0; i < waypoints.size(); i++) {
            BlockPos wp = waypoints.get(i);
            double wx = wp.getX() + 0.5 - camPos.x;
            double wy = wp.getY() + 1.25 - camPos.y;
            double wz = wp.getZ() + 0.5 - camPos.z;

            matrix.pushPose();
            matrix.translate(wx, wy, wz);
            matrix.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
            matrix.scale(-0.02F, -0.02F, 0.02F);

            String label = "#" + (i + 1);
            float halfWidth = -mc.font.width(label) / 2.0F;
            Matrix4f mat = matrix.last().pose();

            int color = (i == 0) ? 0x55FF55 : (i == waypoints.size() - 1 ? 0xFFA040 : 0x55FFFF);
            mc.font.drawInBatch(label, halfWidth, 0, 553648127, false, mat, bufferSource, Font.DisplayMode.SEE_THROUGH, 0x80000000, 15728880);
            mc.font.drawInBatch(label, halfWidth, 0, color, false, mat, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);

            matrix.popPose();
        }
    }

    private static void renderSplineCurve(VertexConsumer lines, PoseStack matrix, CatmullRomSpline3D spline, float r, float g, float b, float a) {
        double totalLen = spline.getTotalLength();
        double stepSize = 0.25; // Abtast-Schrittweite in Blöcken für das Rendering

        Vec3 prevPos = spline.getPointAtDistance(0.0);

        for (double d = stepSize; d <= totalLen + 0.001; d += stepSize) {
            Vec3 currentPos = spline.getPointAtDistance(d);
            renderLineSegment(lines, matrix, prevPos, currentPos, r, g, b, a);
            prevPos = currentPos;
        }
    }

    private static void renderLineSegment(VertexConsumer lines, PoseStack matrix, Vec3 p1, Vec3 p2, float r, float g, float b, float a) {
        var pose = matrix.last().pose();

        Vec3 dir = p2.subtract(p1).normalize();
        float nx = (float) dir.x;
        float ny = (float) dir.y;
        float nz = (float) dir.z;

        lines.addVertex(pose, (float) p1.x, (float) p1.y, (float) p1.z).setColor(r, g, b, a).setNormal(nx, ny, nz);
        lines.addVertex(pose, (float) p2.x, (float) p2.y, (float) p2.z).setColor(r, g, b, a).setNormal(nx, ny, nz);
    }

    private static void renderWaypointBox(VertexConsumer lines, PoseStack matrix, BlockPos pos, float r, float g, float b, float a) {
        var pose = matrix.last().pose();
        float x1 = (float) pos.getX() + 0.05F;
        float y1 = (float) pos.getY() + 0.05F;
        float z1 = (float) pos.getZ() + 0.05F;

        float x2 = (float) pos.getX() + 0.95F;
        float y2 = (float) pos.getY() + 0.95F;
        float z2 = (float) pos.getZ() + 0.95F;

        edge(lines, pose, x1, y1, z1, x2, y1, z1, r, g, b, a);
        edge(lines, pose, x2, y1, z1, x2, y1, z2, r, g, b, a);
        edge(lines, pose, x2, y1, z2, x1, y1, z2, r, g, b, a);
        edge(lines, pose, x1, y1, z2, x1, y1, z1, r, g, b, a);

        edge(lines, pose, x1, y2, z1, x2, y2, z1, r, g, b, a);
        edge(lines, pose, x2, y2, z1, x2, y2, z2, r, g, b, a);
        edge(lines, pose, x2, y2, z2, x1, y2, z2, r, g, b, a);
        edge(lines, pose, x1, y2, z2, x1, y2, z1, r, g, b, a);

        edge(lines, pose, x1, y1, z1, x1, y2, z1, r, g, b, a);
        edge(lines, pose, x2, y1, z1, x2, y2, z1, r, g, b, a);
        edge(lines, pose, x2, y1, z2, x2, y2, z2, r, g, b, a);
        edge(lines, pose, x1, y1, z2, x1, y2, z2, r, g, b, a);
    }

    private static void edge(VertexConsumer consumer, Matrix4f pose, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        consumer.addVertex(pose, x1, y1, z1).setColor(r, g, b, a).setNormal(0, 1, 0);
        consumer.addVertex(pose, x2, y2, z2).setColor(r, g, b, a).setNormal(0, 1, 0);
    }
}