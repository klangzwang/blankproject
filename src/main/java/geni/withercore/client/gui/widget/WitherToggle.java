package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Toggle push-button widget that toggles between pressed and unpressed states,
 * supporting multiple design variants (DEFAULT, OUTLINE, CYAN).
 */
public class WitherToggle extends AbstractButton {

    public enum Variant {
        DEFAULT(0xFF1E2029, 0xFF3F3F46, 0xFF383B4B, 0xFF52525B, 0xFFE4E4E7, 0xFFFFFFFF),
        OUTLINE(0x00000000, 0x3300E5FF, 0xFF3F3F46, 0xFF00E5FF, 0xFFA1A1AA, 0xFF00E5FF),
        CYAN(0xFF161822, 0xFF0B2533, 0xFF2A2D3B, 0xFF00E5FF, 0xFFA1A1AA, 0xFF00E5FF);

        public final int unpressedBg;
        public final int pressedBg;
        public final int unpressedBorder;
        public final int pressedBorder;
        public final int unpressedText;
        public final int pressedText;

        Variant(int unpressedBg, int pressedBg, int unpressedBorder, int pressedBorder, int unpressedText, int pressedText) {
            this.unpressedBg = unpressedBg;
            this.pressedBg = pressedBg;
            this.unpressedBorder = unpressedBorder;
            this.pressedBorder = pressedBorder;
            this.unpressedText = unpressedText;
            this.pressedText = pressedText;
        }
    }

    private boolean pressed = false;
    private Variant variant = Variant.DEFAULT;
    private Consumer<Boolean> onToggle;

    public WitherToggle(int x, int y, int width, int height, Component label, Consumer<Boolean> onToggle) {
        super(x, y, width, height, label);
        this.onToggle = onToggle;
    }

    public WitherToggle(int x, int y, int width, int height, Component label, Variant variant, Consumer<Boolean> onToggle) {
        this(x, y, width, height, label, onToggle);
        this.variant = variant;
    }

    public boolean isPressed() {
        return pressed;
    }

    public WitherToggle setPressed(boolean pressed) {
        this.pressed = pressed;
        return this;
    }

    public WitherToggle setVariant(Variant variant) {
        this.variant = variant;
        return this;
    }

    public WitherToggle setOnToggle(Consumer<Boolean> onToggle) {
        this.onToggle = onToggle;
        return this;
    }

    @Override
    public void onPress() {
        this.pressed = !this.pressed;
        if (this.onToggle != null) {
            this.onToggle.accept(this.pressed);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderButtonFace(guiGraphics, GuiRender.convert(guiGraphics), mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void renderButtonFace(GuiGraphics guiGraphics, GuiRender render, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int x1 = getX();
        int y1 = getY();
        int x2 = x1 + getWidth();
        int y2 = y1 + getHeight();

        int bgColor;
        int borderColor;
        int textColor;

        if (!this.active) {
            bgColor = 0xFF141620;
            borderColor = 0xFF27272A;
            textColor = 0xFF52525B;
        } else if (this.pressed) {
            bgColor = variant.pressedBg;
            borderColor = variant.pressedBorder;
            textColor = variant.pressedText;
        } else if (this.isHovered()) {
            bgColor = (variant.unpressedBg != 0) ? (variant.unpressedBg + 0x00080808) : 0x22FFFFFF;
            borderColor = 0xFF71717A;
            textColor = 0xFFFFFFFF;
        } else {
            bgColor = variant.unpressedBg;
            borderColor = variant.unpressedBorder;
            textColor = variant.unpressedText;
        }

        // Background
        if ((bgColor >>> 24) > 0) {
            guiGraphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, bgColor);
        }

        // Border
        if ((borderColor >>> 24) > 0) {
            guiGraphics.fill(x1, y1, x2, y1 + 1, borderColor);
            guiGraphics.fill(x1, y2 - 1, x2, y2, borderColor);
            guiGraphics.fill(x1, y1 + 1, x1 + 1, y2 - 1, borderColor);
            guiGraphics.fill(x2 - 1, y1 + 1, x2, y2 - 1, borderColor);
        }

        // Active indicator dot or accent
        if (pressed && variant == Variant.CYAN) {
            guiGraphics.fill(x1 + 2, y1 + 2, x1 + 4, y2 - 2, 0xFF00E5FF);
        }

        // Text
        Component msg = getMessage();
        if (msg != null && !msg.getString().isEmpty()) {
            int textW = render.font().width(msg);
            int textX = x1 + (getWidth() - textW) / 2;
            int textY = y1 + (getHeight() - 8) / 2;
            guiGraphics.drawString(render.font(), msg, textX, textY, textColor, false);
        }
    }
}
