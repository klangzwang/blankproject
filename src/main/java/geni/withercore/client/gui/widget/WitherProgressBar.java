package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import geni.withercore.common.helper.TickHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class WitherProgressBar extends AbstractWidget {

    private final Supplier<Float> progressSupplier; // 0.0f to 1.0f
    private boolean showPercentage = true;
    private int fillColorStart = 0xFF3B82F6;
    private int fillColorEnd = 0xFF60A5FA;
    private int trackColor = 0xFF18181B;
    private int borderColor = 0xFF27272A;

    public WitherProgressBar(int x, int y, int width, int height, Supplier<Float> progressSupplier) {
        super(x, y, width, height, Component.empty());
        this.progressSupplier = progressSupplier;
    }

    public WitherProgressBar(int x, int y, int width, int height, Supplier<Integer> current, Supplier<Integer> max) {
        this(x, y, width, height, () -> {
            int m = max.get();
            return m > 0 ? (float) current.get() / (float) m : 0.0f;
        });
    }

    public WitherProgressBar setShowPercentage(boolean show) {
        this.showPercentage = show;
        return this;
    }

    public WitherProgressBar setColors(int start, int end, int track, int border) {
        this.fillColorStart = start;
        this.fillColorEnd = end;
        this.trackColor = track;
        this.borderColor = border;
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
        int x1 = getX();
        int y1 = getY();
        int x2 = x1 + getWidth();
        int y2 = y1 + getHeight();

        // Track Background
        guiGraphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, trackColor);

        // Border
        guiGraphics.fill(x1, y1, x2, y1 + 1, borderColor);
        guiGraphics.fill(x1, y2 - 1, x2, y2, borderColor);
        guiGraphics.fill(x1, y1 + 1, x1 + 1, y2 - 1, borderColor);
        guiGraphics.fill(x2 - 1, y1 + 1, x2, y2 - 1, borderColor);

        // Progress Bar
        int innerW = getWidth() - 2;
        int fillW = Math.round(innerW * progress);
        if (fillW > 0) {
            int px1 = x1 + 1;
            int px2 = px1 + fillW;
            guiGraphics.fillGradient(px1, y1 + 1, px2, y2 - 1, fillColorStart, fillColorEnd);

            // Subtle animated shimmer / pulse
            float time = TickHelper.getClientTickCount();
            int shimmerAlpha = (int) (25 + 15 * Math.sin(time * 0.1f));
            int shimmerColor = (shimmerAlpha << 24) | 0x00FFFFFF;
            guiGraphics.fill(px1, y1 + 1, px2, y1 + (getHeight() / 2), shimmerColor);
        }

        // Percentage label
        if (showPercentage && getHeight() >= 10) {
            String pctText = (int) (progress * 100.0f) + "%";
            int textW = guiGraphics.bufferSource() != null ? pctText.length() * 6 : 0;
            int tx = x1 + (getWidth() - textW) / 2;
            int ty = y1 + (getHeight() - 8) / 2;
            render.drawString(Component.literal(pctText), tx, ty, 0xFFFFFFFF, true);
        }
    }
}
