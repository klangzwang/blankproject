package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class WitherCheckbox extends AbstractWidget {

    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;
    private static final int BOX_SIZE = 14;

    public WitherCheckbox(int x, int y, Component label, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(x, y, BOX_SIZE, BOX_SIZE, label);
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (this.active && getter != null && setter != null) {
            setter.accept(!getter.get());
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void modularRender(GuiGraphics guiGraphics, GuiRender render, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        boolean isChecked = getter != null && getter.get();
        int bx1 = getX();
        int by1 = getY() + (getHeight() - BOX_SIZE) / 2;
        int bx2 = bx1 + BOX_SIZE;
        int by2 = by1 + BOX_SIZE;

        int boxBg;
        if (!this.active) {
            boxBg = 0xFF18181B;
        } else if (isChecked) {
            boxBg = this.isHovered() ? 0xFF2563EB : 0xFF3B82F6;
        } else {
            boxBg = this.isHovered() ? 0xFF3F3F46 : 0xFF27272A;
        }

        int borderColor = this.active ? (this.isHovered() ? 0xFF93C5FD : 0xFF52525B) : 0xFF3F3F46;

        // Background
        guiGraphics.fill(bx1 + 1, by1 + 1, bx2 - 1, by2 - 1, boxBg);

        // Border
        guiGraphics.fill(bx1, by1, bx2, by1 + 1, borderColor);
        guiGraphics.fill(bx1, by2 - 1, bx2, by2, borderColor);
        guiGraphics.fill(bx1, by1 + 1, bx1 + 1, by2 - 1, borderColor);
        guiGraphics.fill(bx2 - 1, by1 + 1, bx2, by2 - 1, borderColor);

        // Checkmark
        if (isChecked) {
            int checkColor = this.active ? 0xFFFFFFFF : 0xFFA1A1AA;
            guiGraphics.fill(bx1 + 3, by1 + 6, bx1 + 5, by1 + 10, checkColor);
            guiGraphics.fill(bx1 + 5, by1 + 8, bx1 + 10, by1 + 10, checkColor);
            guiGraphics.fill(bx1 + 8, by1 + 4, bx1 + 10, by1 + 8, checkColor);
        }

        // Label
        Component message = getMessage();
        if (message != null && !message.getString().isEmpty()) {
            int labelX = bx2 + 6;
            int labelY = by1 + (BOX_SIZE - 8) / 2;
            int textColor = this.active ? (this.isHovered() ? 0xFFFFFFFF : 0xFFD4D4D8) : 0xFF71717A;
            render.drawString(message, labelX, labelY, textColor, false);
        }
    }
}
