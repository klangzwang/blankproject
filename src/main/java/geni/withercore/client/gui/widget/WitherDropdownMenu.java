package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Modular Dropdown Menu widget with custom trigger box, animated chevron,
 * floating popup options list, outside-click detection, and scroll support.
 */
public class WitherDropdownMenu<T> extends AbstractWidget {

    public static class DropdownItem<T> {
        public final T value;
        public final Component label;
        public final boolean disabled;

        public DropdownItem(T value, Component label) {
            this(value, label, false);
        }

        public DropdownItem(T value, Component label, boolean disabled) {
            this.value = value;
            this.label = label;
            this.disabled = disabled;
        }
    }

    protected final List<DropdownItem<T>> items = new ArrayList<>();
    protected T selectedValue;
    protected boolean isOpen = false;
    protected int maxVisibleItems = 6;
    protected int scrollOffset = 0;
    protected int itemHeight = 14;
    protected Consumer<T> onSelect;
    protected Component placeholder = Component.literal("Select option...");

    public WitherDropdownMenu(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public WitherDropdownMenu(int x, int y, int width, int height, Component placeholder) {
        super(x, y, width, height, Component.empty());
        this.placeholder = placeholder;
    }

    public WitherDropdownMenu<T> addItem(T value, Component label) {
        this.items.add(new DropdownItem<>(value, label));
        return this;
    }

    public WitherDropdownMenu<T> addItem(DropdownItem<T> item) {
        this.items.add(item);
        return this;
    }

    public WitherDropdownMenu<T> setItems(List<DropdownItem<T>> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        this.scrollOffset = 0;
        return this;
    }

    public WitherDropdownMenu<T> setPlaceholder(Component placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public WitherDropdownMenu<T> setOnSelect(Consumer<T> onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    public WitherDropdownMenu<T> setSelectedValue(@Nullable T value) {
        this.selectedValue = value;
        return this;
    }

    public T getSelectedValue() {
        return selectedValue;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public void setMaxVisibleItems(int max) {
        this.maxVisibleItems = Math.max(1, max);
    }

    public int getPopupHeight() {
        int visibleCount = Math.min(items.size(), maxVisibleItems);
        return visibleCount * itemHeight + 2;
    }

    public boolean isMouseOverPopup(double mouseX, double mouseY) {
        if (!isOpen || items.isEmpty()) return false;
        int px = getX();
        int py = getY() + getHeight() + 2;
        int pw = getWidth();
        int ph = getPopupHeight();
        return mouseX >= px && mouseX <= px + pw && mouseY >= py && mouseY <= py + ph;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (isOpen) {
            return super.isMouseOver(mouseX, mouseY) || isMouseOverPopup(mouseX, mouseY);
        }
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isOpen && isMouseOverPopup(mouseX, mouseY)) {
            int maxScroll = Math.max(0, items.size() - maxVisibleItems);
            if (maxScroll > 0) {
                this.scrollOffset = (int) Mth.clamp(this.scrollOffset - scrollY, 0, maxScroll);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (!this.active) return;

        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        // 1. Click on Trigger Box
        if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
            this.isOpen = !this.isOpen;
            return;
        }

        // 2. Click inside Popup List
        if (isOpen && isMouseOverPopup(mouseX, mouseY)) {
            int py = y + h + 2;
            int relY = (int) (mouseY - py);
            int clickedIdx = scrollOffset + (relY / itemHeight);

            if (clickedIdx >= 0 && clickedIdx < items.size()) {
                DropdownItem<T> item = items.get(clickedIdx);
                if (!item.disabled) {
                    this.selectedValue = item.value;
                    this.isOpen = false;
                    if (this.onSelect != null) {
                        this.onSelect.accept(item.value);
                    }
                }
            }
            return;
        }

        // 3. Click outside -> close popup
        if (isOpen) {
            this.isOpen = false;
        }
    }

    @Nullable
    public DropdownItem<T> getSelectedItem() {
        for (DropdownItem<T> item : items) {
            if (Objects.equals(item.value, selectedValue)) {
                return item;
            }
        }
        return null;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        GuiRender render = GuiRender.convert(guiGraphics);
        Font font = render.font();
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        // 1. Trigger Box
        boolean hovered = isHovered();
        int borderCol = isOpen ? 0xFF00E5FF : (hovered ? 0xFF52525B : 0xFF2A2D3B);
        int bgCol = hovered ? 0xFF1E202B : 0xFF141620;

        guiGraphics.fill(x + 1, y + 1, x + w - 1, y + h - 1, bgCol);
        guiGraphics.fill(x, y, x + w, y + 1, borderCol);
        guiGraphics.fill(x, y + h - 1, x + w, y + h, borderCol);
        guiGraphics.fill(x, y + 1, x + 1, y + h - 1, borderCol);
        guiGraphics.fill(x + w - 1, y + 1, x + w, y + h - 1, borderCol);

        // Selected Label or Placeholder
        DropdownItem<T> current = getSelectedItem();
        Component displayMsg = current != null ? current.label : placeholder;
        int textCol = current != null ? 0xFFE4E4E7 : 0xFF71717A;

        // Clip label before chevron arrow
        guiGraphics.enableScissor(x + 4, y, x + w - 16, y + h);
        guiGraphics.drawString(font, displayMsg, x + 4, y + (h - 8) / 2, textCol, false);
        guiGraphics.disableScissor();

        // Chevron Arrow icon (v or ^)
        String chevron = isOpen ? "^" : "v";
        guiGraphics.drawString(font, chevron, x + w - 12, y + (h - 8) / 2, isOpen ? 0xFF00E5FF : 0xFF71717A, false);

        // 2. Popup Options Overlay (if open)
        if (isOpen && !items.isEmpty()) {
            renderPopup(guiGraphics, render, font, mouseX, mouseY);
        }
    }

    protected void renderPopup(GuiGraphics guiGraphics, GuiRender render, Font font, int mouseX, int mouseY) {
        int px = getX();
        int py = getY() + getHeight() + 2;
        int pw = getWidth();
        int ph = getPopupHeight();

        // Push pose to render above local background
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 300);

        // Background & border
        guiGraphics.fill(px, py, px + pw, py + ph, 0xF510121A);
        guiGraphics.fill(px, py, px + pw, py + 1, 0xFF2C2F3D);
        guiGraphics.fill(px, py + ph - 1, px + pw, py + ph, 0xFF2C2F3D);
        guiGraphics.fill(px, py + 1, px + 1, py + ph - 1, 0xFF2C2F3D);
        guiGraphics.fill(px + pw - 1, py + 1, px + pw, py + ph - 1, 0xFF2C2F3D);

        int visibleCount = Math.min(items.size(), maxVisibleItems);
        int itemY = py + 1;

        for (int i = 0; i < visibleCount; i++) {
            int itemIdx = scrollOffset + i;
            if (itemIdx >= items.size()) break;

            DropdownItem<T> item = items.get(itemIdx);
            boolean itemHovered = mouseX >= px && mouseX <= px + pw && mouseY >= itemY && mouseY < itemY + itemHeight;
            boolean isItemActive = Objects.equals(item.value, selectedValue);

            // Item background highlight
            if (itemHovered && !item.disabled) {
                guiGraphics.fill(px + 1, itemY, px + pw - 1, itemY + itemHeight, 0xFF252836);
            }

            // Active indicator bar
            if (isItemActive) {
                guiGraphics.fill(px + 1, itemY, px + 3, itemY + itemHeight, 0xFF00E5FF);
            }

            // Item text
            int itemTextCol = item.disabled ? 0xFF52525B : (itemHovered ? 0xFFFFFFFF : (isItemActive ? 0xFF00E5FF : 0xFFD4D4D8));
            guiGraphics.drawString(font, item.label, px + 6, itemY + (itemHeight - 8) / 2, itemTextCol, false);

            itemY += itemHeight;
        }

        // Scrollbar if more items than maxVisible
        if (items.size() > maxVisibleItems) {
            int sbW = 3;
            int sbX = px + pw - sbW - 1;
            int maxScroll = items.size() - maxVisibleItems;
            int thumbH = Math.max(6, (ph * maxVisibleItems) / items.size());
            int thumbY = py + 1 + (scrollOffset * (ph - thumbH - 2)) / maxScroll;

            guiGraphics.fill(sbX, py + 1, sbX + sbW, py + ph - 1, 0x22FFFFFF);
            guiGraphics.fill(sbX, thumbY, sbX + sbW, thumbY + thumbH, 0xFF00E5FF);
        }

        guiGraphics.pose().popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
