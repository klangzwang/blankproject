package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import geni.withercore.common.helper.TickHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

/**
 * Renders a progress stroke around the rectangular perimeter (border) of an area, slot, or card.
 * Traces clockwise starting from top-left.
 */
public class WitherBorderProgress extends AbstractWidget {

    private final Supplier<Float> progressSupplier;
    private int strokeWidth = 2;
    private int strokeColor = 0xFF00E5FF; // Wither cyan accent
    private int trackColor = 0x22FFFFFF;  // Subtle track outline
    private boolean glow = true;
    private boolean showPercentage = false;

    public WitherBorderProgress(int x, int y, int width, int height, Supplier<Float> progressSupplier) {
        super(x, y, width, height, Component.empty());
        this.progressSupplier = progressSupplier;
    }

    public WitherBorderProgress(int x, int y, int width, int height, Supplier<Integer> current, Supplier<Integer> max) {
        this(x, y, width, height, () -> {
            int m = max.get();
            return m > 0 ? (float) current.get() / (float) m : 0.0f;
        });
    }

    public WitherBorderProgress setStrokeWidth(int strokeWidth) {
        this.strokeWidth = Math.max(1, strokeWidth);
        return this;
    }

    public WitherBorderProgress setColors(int strokeColor, int trackColor) {
        this.strokeColor = strokeColor;
        this.trackColor = trackColor;
        return this;
    }

    public WitherBorderProgress setGlow(boolean glow) {
        this.glow = glow;
        return this;
    }

    public WitherBorderProgress setShowPercentage(boolean showPercentage) {
        this.showPercentage = showPercentage;
        return this;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void modularRender(GuiGraphics guiGraphics, GuiRender render, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float progress = progressSupplier != null ? Mth.clamp(progressSupplier.get(), 0.0f, 1.0f) : 0.0f;
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        int s = strokeWidth;

        // 1. Background Track (Subtle perimeter line)
        if ((trackColor >>> 24) > 0) {
            guiGraphics.fill(x, y, x + w, y + s, trackColor);                     // Top
            guiGraphics.fill(x + w - s, y, x + w, y + h, trackColor);             // Right
            guiGraphics.fill(x, y + h - s, x + w, y + h, trackColor);             // Bottom
            guiGraphics.fill(x, y, x + s, y + h, trackColor);                     // Left
        }

        if (progress <= 0.0f) return;

        // 2. Animated glow pulse
        int activeColor = strokeColor;
        if (glow) {
            float time = TickHelper.getClientTickCount();
            int alphaBase = (strokeColor >>> 24);
            int pulseAlpha = Mth.clamp((int) (alphaBase * (0.85f + 0.15f * Math.sin(time * 0.15f))), 40, 255);
            activeColor = (pulseAlpha << 24) | (strokeColor & 0x00FFFFFF);
        }

        // Perimeter = 2 * (w + h)
        float totalPerimeter = 2.0f * (w + h);
        float currentLength = progress * totalPerimeter;

        // Side 1: Top (x, y) -> (x + w, y)
        float topLen = Math.min(currentLength, (float) w);
        if (topLen > 0) {
            guiGraphics.fill(x, y, x + (int) Math.ceil(topLen), y + s, activeColor);
        }

        // Side 2: Right (x + w, y) -> (x + w, y + h)
        if (currentLength > w) {
            float rightLen = Math.min(currentLength - w, (float) h);
            guiGraphics.fill(x + w - s, y, x + w, y + (int) Math.ceil(rightLen), activeColor);
        }

        // Side 3: Bottom (x + w, y + h) -> (x, y + h) (runs right to left)
        if (currentLength > w + h) {
            float bottomLen = Math.min(currentLength - (w + h), (float) w);
            int bxEnd = x + w;
            int bxStart = bxEnd - (int) Math.ceil(bottomLen);
            guiGraphics.fill(bxStart, y + h - s, bxEnd, y + h, activeColor);
        }

        // Side 4: Left (x, y + h) -> (x, y) (runs bottom to top)
        if (currentLength > 2 * w + h) {
            float leftLen = Math.min(currentLength - (2 * w + h), (float) h);
            int byEnd = y + h;
            int byStart = byEnd - (int) Math.ceil(leftLen);
            guiGraphics.fill(x, byStart, x + s, byEnd, activeColor);
        }

        // 3. Optional Percentage display
        if (showPercentage && w >= 24 && h >= 14) {
            String pctText = (int) (progress * 100.0f) + "%";
            int tw = pctText.length() * 6;
            int tx = x + (w - tw) / 2;
            int ty = y + (h - 8) / 2;
            render.drawString(Component.literal(pctText), tx, ty, 0xFFE4E4E7, false);
        }
    }
}
