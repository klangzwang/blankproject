package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modular Carousel widget for cycling through items, recipes, or options with
 * previous/next navigation buttons and interactive dot indicators.
 */
public class WitherCarousel<T> extends AbstractWidget {

    @FunctionalInterface
    public interface ItemRenderer<T> {
        void renderItem(GuiGraphics guiGraphics, GuiRender render, T item, int x, int y, int width, int height, boolean isHovered);
    }

    private final List<T> items = new ArrayList<>();
    private int currentIndex = 0;
    private boolean loop = true;
    private int buttonWidth = 14;
    private ItemRenderer<T> itemRenderer;
    private Consumer<Integer> onIndexChanged;
    private Consumer<T> onItemClick;

    public WitherCarousel(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        // Default text-based item renderer
        this.itemRenderer = (guiGraphics, render, item, ix, iy, iw, ih, hovered) -> {
            String text = item != null ? item.toString() : "-";
            int tw = render.font().width(text);
            int tx = ix + (iw - tw) / 2;
            int ty = iy + (ih - 8) / 2;
            guiGraphics.drawString(render.font(), text, tx, ty, hovered ? 0xFFFFFFFF : 0xFFE4E4E7, false);
        };
    }

    public WitherCarousel<T> addItem(T item) {
        this.items.add(item);
        return this;
    }

    public WitherCarousel<T> setItems(List<T> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        this.currentIndex = 0;
        return this;
    }

    public WitherCarousel<T> setItemRenderer(ItemRenderer<T> renderer) {
        this.itemRenderer = renderer;
        return this;
    }

    public WitherCarousel<T> setOnIndexChanged(Consumer<Integer> onIndexChanged) {
        this.onIndexChanged = onIndexChanged;
        return this;
    }

    public WitherCarousel<T> setOnItemClick(Consumer<T> onItemClick) {
        this.onItemClick = onItemClick;
        return this;
    }

    public WitherCarousel<T> setLoop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public T getCurrentItem() {
        if (items.isEmpty() || currentIndex < 0 || currentIndex >= items.size()) {
            return null;
        }
        return items.get(currentIndex);
    }

    public void setCurrentIndex(int index) {
        if (items.isEmpty()) {
            this.currentIndex = 0;
            return;
        }
        int clamped = loop ? (index % items.size() + items.size()) % items.size() : Math.max(0, Math.min(items.size() - 1, index));
        if (this.currentIndex != clamped) {
            this.currentIndex = clamped;
            if (onIndexChanged != null) {
                onIndexChanged.accept(clamped);
            }
        }
    }

    public void prev() {
        if (!items.isEmpty()) {
            setCurrentIndex(currentIndex - 1);
        }
    }

    public void next() {
        if (!items.isEmpty()) {
            setCurrentIndex(currentIndex + 1);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (!this.active || items.isEmpty()) return;

        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        // 1. Prev Button Click
        if (mouseX >= x && mouseX <= x + buttonWidth && mouseY >= y && mouseY <= y + h - 6) {
            prev();
            return;
        }

        // 2. Next Button Click
        if (mouseX >= x + w - buttonWidth && mouseX <= x + w && mouseY >= y && mouseY <= y + h - 6) {
            next();
            return;
        }

        // 3. Dot Indicators Click (bottom 6px area)
        int dotCount = items.size();
        if (dotCount > 1 && mouseY >= y + h - 6 && mouseY <= y + h) {
            int dotTotalW = dotCount * 6;
            int dotStartX = x + (w - dotTotalW) / 2;
            for (int i = 0; i < dotCount; i++) {
                int dx = dotStartX + i * 6;
                if (mouseX >= dx && mouseX <= dx + 5) {
                    setCurrentIndex(i);
                    return;
                }
            }
        }

        // 4. Center Item Click
        int centerLeft = x + buttonWidth;
        int centerRight = x + w - buttonWidth;
        if (mouseX >= centerLeft && mouseX <= centerRight && mouseY >= y && mouseY <= y + h - 6) {
            if (onItemClick != null) {
                onItemClick.accept(getCurrentItem());
            }
        }
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
        int contentH = h - 6; // Leave 6px for dot indicators

        // Background panel
        guiGraphics.fill(x, y, x + w, y + contentH, 0x9912141C);
        guiGraphics.fill(x, y, x + w, y + 1, 0xFF2A2D3B);
        guiGraphics.fill(x, y + contentH - 1, x + w, y + contentH, 0xFF2A2D3B);
        guiGraphics.fill(x, y, x + 1, y + contentH, 0xFF2A2D3B);
        guiGraphics.fill(x + w - 1, y, x + w, y + contentH, 0xFF2A2D3B);

        // Prev Button (<)
        boolean prevHover = mouseX >= x && mouseX <= x + buttonWidth && mouseY >= y && mouseY <= y + contentH;
        int prevBg = prevHover ? 0xFF2E3242 : 0xFF181A24;
        guiGraphics.fill(x + 1, y + 1, x + buttonWidth - 1, y + contentH - 1, prevBg);
        guiGraphics.drawString(render.font(), "<", x + 4, y + (contentH - 8) / 2, prevHover ? 0xFF00E5FF : 0xFFA1A1AA, false);

        // Next Button (>)
        int nextX = x + w - buttonWidth;
        boolean nextHover = mouseX >= nextX && mouseX <= x + w && mouseY >= y && mouseY <= y + contentH;
        int nextBg = nextHover ? 0xFF2E3242 : 0xFF181A24;
        guiGraphics.fill(nextX + 1, y + 1, x + w - 1, y + contentH - 1, nextBg);
        guiGraphics.drawString(render.font(), ">", nextX + 5, y + (contentH - 8) / 2, nextHover ? 0xFF00E5FF : 0xFFA1A1AA, false);

        // Center Content Area
        int cx = x + buttonWidth;
        int cw = w - (buttonWidth * 2);
        boolean centerHover = mouseX >= cx && mouseX <= cx + cw && mouseY >= y && mouseY <= y + contentH;
        if (!items.isEmpty() && currentIndex < items.size()) {
            T item = items.get(currentIndex);
            if (itemRenderer != null) {
                itemRenderer.renderItem(guiGraphics, render, item, cx, y, cw, contentH, centerHover);
            }
        }

        // Dot Indicators
        int dotCount = items.size();
        if (dotCount > 1) {
            int dotTotalW = dotCount * 6;
            int dotStartX = x + (w - dotTotalW) / 2;
            for (int i = 0; i < dotCount; i++) {
                int dx = dotStartX + i * 6;
                int dy = y + h - 4;
                int dotColor = (i == currentIndex) ? 0xFF00E5FF : 0xFF52525B;
                guiGraphics.fill(dx, dy, dx + 4, dy + 2, dotColor);
            }
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
