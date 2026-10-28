package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Radio button group widget supporting mutually exclusive selection with
 * glowing cyan radio bullets, labels, descriptions, and callbacks.
 */
public class WitherRadioGroup<T> extends AbstractWidget {

    public static class RadioOption<T> {
        public final T value;
        public final Component label;
        @Nullable
        public final Component description;
        public final boolean disabled;

        public RadioOption(T value, Component label) {
            this(value, label, null, false);
        }

        public RadioOption(T value, Component label, @Nullable Component description) {
            this(value, label, description, false);
        }

        public RadioOption(T value, Component label, @Nullable Component description, boolean disabled) {
            this.value = value;
            this.label = label;
            this.description = description;
            this.disabled = disabled;
        }
    }

    private final List<RadioOption<T>> options = new ArrayList<>();
    private T selectedValue;
    private int itemSpacing = 4;
    private int itemHeight = 16;
    private Consumer<T> onSelect;

    public WitherRadioGroup(int x, int y, int width) {
        super(x, y, width, 0, Component.empty());
    }

    public WitherRadioGroup<T> addOption(T value, Component label) {
        return addOption(new RadioOption<>(value, label));
    }

    public WitherRadioGroup<T> addOption(T value, Component label, Component description) {
        return addOption(new RadioOption<>(value, label, description));
    }

    public WitherRadioGroup<T> addOption(RadioOption<T> option) {
        this.options.add(option);
        recalculateHeight();
        return this;
    }

    public WitherRadioGroup<T> setOnSelect(Consumer<T> onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    public WitherRadioGroup<T> setSelectedValue(T value) {
        this.selectedValue = value;
        return this;
    }

    public T getSelectedValue() {
        return selectedValue;
    }

    public void select(T value) {
        if (!Objects.equals(this.selectedValue, value)) {
            this.selectedValue = value;
            if (this.onSelect != null) {
                this.onSelect.accept(value);
            }
        }
    }

    private void recalculateHeight() {
        int totalH = 0;
        for (RadioOption<T> opt : options) {
            totalH += (opt.description != null ? itemHeight + 8 : itemHeight) + itemSpacing;
        }
        this.height = Math.max(16, totalH);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (!this.active || options.isEmpty()) return;

        int curY = getY();
        for (RadioOption<T> opt : options) {
            int optH = opt.description != null ? itemHeight + 8 : itemHeight;
            if (mouseY >= curY && mouseY <= curY + optH && mouseX >= getX() && mouseX <= getX() + getWidth()) {
                if (!opt.disabled) {
                    select(opt.value);
                }
                return;
            }
            curY += optH + itemSpacing;
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        GuiRender render = GuiRender.convert(guiGraphics);
        int curY = getY();
        int x = getX();
        int w = getWidth();

        for (RadioOption<T> opt : options) {
            int optH = opt.description != null ? itemHeight + 8 : itemHeight;
            boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= curY && mouseY <= curY + optH && !opt.disabled;
            boolean isChecked = Objects.equals(this.selectedValue, opt.value);

            // Outer radio box (8x8)
            int rx = x + 2;
            int ry = curY + 2;
            int outerBorder = opt.disabled ? 0xFF27272A : (hovered ? 0xFF52525B : 0xFF3F3F46);
            int outerBg = opt.disabled ? 0xFF141620 : 0xFF181A24;

            // Radio bullet outer frame
            guiGraphics.fill(rx, ry, rx + 8, ry + 8, outerBg);
            guiGraphics.fill(rx, ry, rx + 8, ry + 1, outerBorder);
            guiGraphics.fill(rx, ry + 7, rx + 8, ry + 8, outerBorder);
            guiGraphics.fill(rx, ry, rx + 1, ry + 8, outerBorder);
            guiGraphics.fill(rx + 7, ry, rx + 8, ry + 8, outerBorder);

            // Active glowing inner dot
            if (isChecked) {
                int dotColor = opt.disabled ? 0xFF71717A : 0xFF00E5FF;
                guiGraphics.fill(rx + 2, ry + 2, rx + 6, ry + 6, dotColor);
            }

            // Label
            int labelX = rx + 12;
            int labelColor = opt.disabled ? 0xFF52525B : (hovered ? 0xFFFFFFFF : 0xFFE4E4E7);
            guiGraphics.drawString(render.font(), opt.label, labelX, ry, labelColor, false);

            // Description
            if (opt.description != null) {
                int descY = ry + 9;
                int descColor = opt.disabled ? 0xFF3F3F46 : 0xFF71717A;
                guiGraphics.drawString(render.font(), opt.description, labelX, descY, descColor, false);
            }

            curY += optH + itemSpacing;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
