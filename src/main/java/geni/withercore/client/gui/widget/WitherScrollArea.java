package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * Scrollable container widget with scissor clipping, smooth mouse wheel scrolling,
 * and an interactive scrollbar.
 */
public class WitherScrollArea extends AbstractWidget {

    @FunctionalInterface
    public interface ScrollContentRenderer {
        void renderScrollContent(GuiGraphics guiGraphics, GuiRender render, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick);
    }

    private int contentHeight = 100;
    private float scrollOffset = 0.0f;
    private int scrollbarWidth = 4;
    private boolean isDraggingThumb = false;
    private double dragStartY = 0.0;
    private float dragStartScroll = 0.0f;
    private ScrollContentRenderer contentRenderer;

    public WitherScrollArea(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public WitherScrollArea setContentHeight(int contentHeight) {
        this.contentHeight = Math.max(0, contentHeight);
        clampScroll();
        return this;
    }

    public WitherScrollArea setContentRenderer(ScrollContentRenderer renderer) {
        this.contentRenderer = renderer;
        return this;
    }

    public float getScrollOffset() {
        return scrollOffset;
    }

    public void setScrollOffset(float offset) {
        this.scrollOffset = offset;
        clampScroll();
    }

    public int getMaxScroll() {
        return Math.max(0, contentHeight - getHeight());
    }

    private void clampScroll() {
        this.scrollOffset = Mth.clamp(this.scrollOffset, 0.0f, (float) getMaxScroll());
    }

    public boolean canScroll() {
        return contentHeight > getHeight();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!isMouseOver(mouseX, mouseY) || !canScroll()) {
            return false;
        }
        // scrollY > 0 is scrolling up, scrollY < 0 is scrolling down
        this.scrollOffset -= (float) (scrollY * 12.0);
        clampScroll();
        return true;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (!canScroll()) return;

        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        int sbX = x + w - scrollbarWidth;

        // Check if clicked directly on the scrollbar track
        if (mouseX >= sbX && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
            int thumbH = getThumbHeight();
            int thumbY = getThumbY();

            if (mouseY >= thumbY && mouseY <= thumbY + thumbH) {
                // Drag start
                isDraggingThumb = true;
                dragStartY = mouseY;
                dragStartScroll = scrollOffset;
            } else {
                // Jump scroll to click position
                float clickRatio = (float) (mouseY - y) / (float) h;
                setScrollOffset(clickRatio * getMaxScroll());
            }
        }
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (isDraggingThumb && canScroll()) {
            int trackH = getHeight() - getThumbHeight();
            if (trackH > 0) {
                double deltaY = mouseY - dragStartY;
                float scrollDelta = (float) (deltaY / (double) trackH) * getMaxScroll();
                setScrollOffset(dragStartScroll + scrollDelta);
            }
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        isDraggingThumb = false;
    }

    private int getThumbHeight() {
        int h = getHeight();
        if (contentHeight <= 0) return h;
        int thumb = Math.round((float) h / (float) contentHeight * h);
        return Math.max(8, Math.min(h, thumb));
    }

    private int getThumbY() {
        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) return getY();
        int trackH = getHeight() - getThumbHeight();
        return getY() + Math.round((scrollOffset / (float) maxScroll) * trackH);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        GuiRender render = GuiRender.convert(guiGraphics);
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        // 1. Scissor Clipping for content
        guiGraphics.enableScissor(x, y, x + w - (canScroll() ? scrollbarWidth + 1 : 0), y + h);

        if (contentRenderer != null) {
            int contentY = y - (int) scrollOffset;
            contentRenderer.renderScrollContent(guiGraphics, render, x, contentY, w - (canScroll() ? scrollbarWidth + 1 : 0), contentHeight, mouseX, mouseY, partialTick);
        }

        guiGraphics.disableScissor();

        // 2. Render Scrollbar if needed
        if (canScroll()) {
            int sbX = x + w - scrollbarWidth;
            // Scrollbar track
            guiGraphics.fill(sbX, y, x + w, y + h, 0x1AFFFFFF);

            // Scrollbar thumb
            int thumbY = getThumbY();
            int thumbH = getThumbHeight();
            boolean thumbHover = mouseX >= sbX && mouseX <= x + w && mouseY >= thumbY && mouseY <= thumbY + thumbH;
            int thumbColor = isDraggingThumb ? 0xFF00E5FF : (thumbHover ? 0xFF71717A : 0xFF3F3F46);

            guiGraphics.fill(sbX, thumbY, x + w, thumbY + thumbH, thumbColor);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
