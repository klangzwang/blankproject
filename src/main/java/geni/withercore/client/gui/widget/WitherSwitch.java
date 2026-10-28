package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import geni.witherapi.client.gui.ITickableWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class WitherSwitch extends AbstractWidget implements ITickableWidget {

    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;
    private float knobPosition = 0.0f; // 0.0 = off, 1.0 = on

    public WitherSwitch(int x, int y, int width, int height, Component label, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(x, y, width, height, label);
        this.getter = getter;
        this.setter = setter;
        if (getter != null) {
            this.knobPosition = getter.get() ? 1.0f : 0.0f;
        }
    }

    public WitherSwitch(int x, int y, Component label, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        this(x, y, 32, 16, label, getter, setter);
    }

    @Override
    public void tickWidget() {
        if (getter != null) {
            boolean target = getter.get();
            float targetPos = target ? 1.0f : 0.0f;
            this.knobPosition += (targetPos - this.knobPosition) * 0.35f;
            if (Math.abs(targetPos - this.knobPosition) < 0.01f) {
                this.knobPosition = targetPos;
            }
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (this.active && getter != null && setter != null) {
            boolean newVal = !getter.get();
            setter.accept(newVal);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void modularRender(GuiGraphics guiGraphics, GuiRender render, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        boolean isOn = getter != null && getter.get();
        int x1 = getX();
        int y1 = getY();
        int switchW = Math.min(getWidth(), 36);
        int switchH = getHeight();
        int x2 = x1 + switchW;
        int y2 = y1 + switchH;

        // Track background
        int trackColor;
        if (!this.active) {
            trackColor = 0xFF27272A;
        } else if (isOn) {
            trackColor = this.isHovered() ? 0xFF2563EB : 0xFF3B82F6;
        } else {
            trackColor = this.isHovered() ? 0xFF3F3F46 : 0xFF27272A;
        }

        int borderColor = this.isHovered() ? 0xFF60A5FA : 0xFF52525B;

        // Render Track
        guiGraphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, trackColor);
        guiGraphics.fill(x1 + 1, y1, x2 - 1, y1 + 1, borderColor);
        guiGraphics.fill(x1 + 1, y2 - 1, x2 - 1, y2, borderColor);
        guiGraphics.fill(x1, y1 + 1, x1 + 1, y2 - 1, borderColor);
        guiGraphics.fill(x2 - 1, y1 + 1, x2, y2 - 1, borderColor);

        // Knob
        int knobSize = switchH - 4;
        int minKnobX = x1 + 2;
        int maxKnobX = x2 - knobSize - 2;
        int knobX = Math.round(minKnobX + (maxKnobX - minKnobX) * knobPosition);
        int knobY = y1 + 2;

        int knobColor = this.active ? 0xFFFFFFFF : 0xFFA1A1AA;
        guiGraphics.fill(knobX, knobY, knobX + knobSize, knobY + knobSize, knobColor);

        // Optional Label next to switch
        Component message = getMessage();
        if (message != null && !message.getString().isEmpty()) {
            int labelX = x2 + 6;
            int labelY = y1 + (switchH - 8) / 2;
            int textColor = this.active ? (this.isHovered() ? 0xFFFFFFFF : 0xFFD4D4D8) : 0xFF71717A;
            render.drawString(message, labelX, labelY, textColor, false);
        }
    }
}
