package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class WitherSlider extends AbstractWidget {

    private final double minValue;
    private final double maxValue;
    private final double step;
    private final Supplier<Double> getter;
    private final Consumer<Double> setter;
    private Function<Double, Component> valueFormatter;

    private boolean isDragging = false;

    public WitherSlider(int x, int y, int width, int height, Component label,
                        double minValue, double maxValue, double step,
                        Supplier<Double> getter, Consumer<Double> setter) {
        super(x, y, width, height, label);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
        this.getter = getter;
        this.setter = setter;
        this.valueFormatter = val -> Component.literal(String.format("%.1f", val));
    }

    public WitherSlider setValueFormatter(Function<Double, Component> formatter) {
        this.valueFormatter = formatter;
        return this;
    }

    private double getCurrentValue() {
        return getter != null ? getter.get() : minValue;
    }

    private double getNormalizedValue() {
        return Mth.clamp((getCurrentValue() - minValue) / (maxValue - minValue), 0.0, 1.0);
    }

    private void updateValueFromMouse(double mouseX) {
        double relativeX = mouseX - (getX() + 4);
        double trackWidth = getWidth() - 8;
        double pct = Mth.clamp(relativeX / trackWidth, 0.0, 1.0);
        double rawVal = minValue + pct * (maxValue - minValue);

        if (step > 0.0) {
            rawVal = Math.round(rawVal / step) * step;
        }
        rawVal = Mth.clamp(rawVal, minValue, maxValue);

        if (setter != null) {
            setter.accept(rawVal);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (this.active) {
            this.isDragging = true;
            updateValueFromMouse(mouseX);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.active && this.isDragging) {
            updateValueFromMouse(mouseX);
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void modularRender(GuiGraphics guiGraphics, GuiRender render, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int x1 = getX();
        int y1 = getY();
        int x2 = x1 + getWidth();
        int y2 = y1 + getHeight();

        // Background Track
        int trackHeight = 4;
        int trackY1 = y1 + (getHeight() - trackHeight) / 2;
        int trackY2 = trackY1 + trackHeight;

        guiGraphics.fill(x1, trackY1, x2, trackY2, 0xFF27272A);

        // Active Track (Fill)
        int fillWidth = (int) ((getWidth() - 8) * getNormalizedValue());
        int fillX2 = x1 + 4 + fillWidth;
        int fillColor = this.active ? (this.isHovered() ? 0xFF60A5FA : 0xFF3B82F6) : 0xFF52525B;
        guiGraphics.fill(x1 + 4, trackY1, fillX2, trackY2, fillColor);

        // Thumb / Knob
        int thumbSize = getHeight() - 4;
        int thumbX = x1 + 4 + fillWidth - thumbSize / 2;
        int thumbY = y1 + 2;

        int thumbBg = this.active ? (isHovered() || isDragging ? 0xFFFFFFFF : 0xFFE4E4E7) : 0xFF71717A;
        int thumbBorder = isHovered() || isDragging ? 0xFF3B82F6 : 0xFF27272A;

        guiGraphics.fill(thumbX, thumbY, thumbX + thumbSize, thumbY + thumbSize, thumbBg);
        guiGraphics.fill(thumbX, thumbY, thumbX + thumbSize, thumbY + 1, thumbBorder);
        guiGraphics.fill(thumbX, thumbY + thumbSize - 1, thumbX + thumbSize, thumbY + thumbSize, thumbBorder);
        guiGraphics.fill(thumbX, thumbY, thumbX + 1, thumbY + thumbSize, thumbBorder);
        guiGraphics.fill(thumbX + thumbSize - 1, thumbY, thumbX + thumbSize, thumbY + thumbSize, thumbBorder);

        // Value text or label
        Component label = getMessage();
        Component valText = valueFormatter != null ? valueFormatter.apply(getCurrentValue()) : Component.empty();
        if (label != null && !label.getString().isEmpty()) {
            render.drawString(label, x1, y1 - 10, 0xFFA1A1AA, false);
            int valW = guiGraphics.bufferSource() != null ? 30 : 0;
            render.drawString(valText, x2 - valW, y1 - 10, 0xFFFFFFFF, false);
        }
    }
}
