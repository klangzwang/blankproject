package geni.withercore.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import geni.withercore.client.gui.animation.GuiAnimationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

/**
 * Modulare Klasse zum Rendern von freischwebenden 3D-Objekten (Blöcke, Items, Maschinen)
 * im Freiraum neben dem Container-Screen (vor dem JEI-Bereich).
 */
public class WitherGuiObjectRenderer {

    public static final float DEFAULT_YAW = 0.0f;
    public static final float DEFAULT_PITCH = 0.0f;
    public static final float MAX_PITCH = 45.0f;

    private float yaw = DEFAULT_YAW;
    private float pitch = DEFAULT_PITCH;
    private boolean isDragging = false;
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private long lastClickTime = 0;

    private Component mainTitle;
    private Component subTitle;

    public WitherGuiObjectRenderer(Component mainTitle, Component subTitle) {
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
    }

    public void resetCamera() {
        this.yaw = DEFAULT_YAW;
        this.pitch = DEFAULT_PITCH;
    }

    public void setTitles(Component mainTitle, Component subTitle) {
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
    }

    /**
     * Rendert das freischwebende 3D-Objekt mit schwebendem Titel in der dynamischen Mitte.
     */
    public void render(GuiGraphics gg, int screenWidth, int screenHeight, int leftPos, int imageWidth, float animProgress, ItemStack renderStack) {
        float eased = GuiAnimationHelper.easeOutCubic(animProgress);

        // Dynamische Mitte zwischen (leftPos + imageWidth) und dem Beginn des JEI-Bereichs (bei ~68% Bildschirmbreite)
        int minX = leftPos + imageWidth;
        int maxX = Math.max(minX + 80, (int) (screenWidth * 0.68f));
        int centerX = (minX + maxX) / 2;
        int centerY = screenHeight / 2;

        // Einflug-Offset: Startet hinter der Slot-Box und gleitet zeitgleich in die dynamische Mitte
        float startOffset = -(imageWidth + 400);
        float currentOffset = GuiAnimationHelper.lerp(startOffset, 0, eased);

        PoseStack pose = gg.pose();
        pose.pushPose();
        pose.translate(centerX + currentOffset, centerY, 0);

        // 1. Freischwebender Titel & Subtitel über dem 3D-Objekt
        renderHeader(gg, 0, -84);

        // 2. 3D-Modell Projektionsmatrix
        pose.pushPose();
        // Leichtes Atmen / Schweben für organische Bewegung
        float floatOffset = (float) Math.sin((System.currentTimeMillis() % 6000L) / 6000.0 * Math.PI * 2.0) * 3.0f;
        pose.translate(0, floatOffset, 150);

        // Maßstab des 3D-Objekts
        float scale = 196.0f;
        pose.scale(scale, -scale, scale);

        // Rotation mit der Maus (begrenzt auf max. +/- 45 Grad Neigung)
        pose.mulPose(Axis.XP.rotationDegrees(pitch));
        pose.mulPose(Axis.YP.rotationDegrees(yaw));

        // Studio-Licht Setup
        Lighting.setupFor3DItems();

        if (renderStack != null && !renderStack.isEmpty()) {
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    renderStack,
                    ItemDisplayContext.GUI,
                    0xF000F0,
                    OverlayTexture.NO_OVERLAY,
                    pose,
                    bufferSource,
                    Minecraft.getInstance().level,
                    0
            );
            bufferSource.endBatch();
        }

        Lighting.setupForFlatItems();
        pose.popPose();

        pose.popPose();
    }

    private void renderHeader(GuiGraphics gg, int x, int y) {
        Font font = Minecraft.getInstance().font;

        if (mainTitle != null) {
            int mainWidth = font.width(mainTitle);
            gg.drawString(font, mainTitle, x - mainWidth / 2, y, Color.WHITE.getRGB(), true);
        }

        if (subTitle != null) {
            int subWidth = font.width(subTitle);
            gg.drawString(font, subTitle, x - subWidth / 2, y + 12, 0xFF00E5FF, true);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            long now = System.currentTimeMillis();
            if (now - lastClickTime < 280) {
                // Doppelklick setzt Kamera zurück
                resetCamera();
            }
            lastClickTime = now;
            isDragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
            return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging && button == 0) {
            double deltaX = mouseX - lastMouseX;
            double deltaY = mouseY - lastMouseY;
            lastMouseX = mouseX;
            lastMouseY = mouseY;

            // Horizontale Drehung mit natürlicher Richtung
            this.yaw += (float) (deltaX * 0.75);

            // Vertikale Neigung: Strikt begrenzt auf maximal +/- 45 Grad
            this.pitch = GuiAnimationHelper.clamp(this.pitch + (float) (deltaY * 0.75), -MAX_PITCH, MAX_PITCH);
            return true;
        }
        return false;
    }
}
