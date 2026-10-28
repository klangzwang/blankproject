package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class WitherButton extends AbstractButton {

    public enum Variant {
        PRIMARY(0xFF3B82F6, 0xFF2563EB, 0xFF1D4ED8, 0xFF60A5FA, 0xFFFFFFFF),
        SECONDARY(0xFF27272A, 0xFF3F3F46, 0xFF18181B, 0xFF52525B, 0xFFE4E4E7),
        DANGER(0xFFEF4444, 0xFFDC2626, 0xFFB91C1C, 0xFFF87171, 0xFFFFFFFF),
        GHOST(0x00000000, 0x22FFFFFF, 0x33FFFFFF, 0x00000000, 0xFFD4D4D8),
        OUTLINE(0x00000000, 0x1AFFFFFF, 0x2AFFFFFF, 0xFF52525B, 0xFFD4D4D8);

        public final int normalColor;
        public final int hoverColor;
        public final int activeColor;
        public final int borderColor;
        public final int textColor;

        Variant(int normalColor, int hoverColor, int activeColor, int borderColor, int textColor) {
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            this.activeColor = activeColor;
            this.borderColor = borderColor;
            this.textColor = textColor;
        }
    }

    private final Consumer<WitherButton> onPressAction;
    private Variant variant = Variant.PRIMARY;
    private boolean isSelected = false;

    public WitherButton(int x, int y, int width, int height, Component message, Consumer<WitherButton> onPress) {
        super(x, y, width, height, message);
        this.onPressAction = onPress;
    }

    public WitherButton(int x, int y, int width, int height, Component message, Variant variant, Consumer<WitherButton> onPress) {
        super(x, y, width, height, message);
        this.variant = variant;
        this.onPressAction = onPress;
    }

    public WitherButton setVariant(Variant variant) {
        this.variant = variant;
        return this;
    }

    public Variant getVariant() {
        return variant;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public WitherButton setSelected(boolean selected) {
        this.isSelected = selected;
        return this;
    }

    @Override
    public void onPress() {
        if (this.active && this.onPressAction != null) {
            this.onPressAction.accept(this);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderButtonFace(guiGraphics, GuiRender.convert(guiGraphics), mouseX, mouseY, partialTick);
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
        if (!this.active) {
            bgColor = 0xFF18181B;
        } else if (this.isSelected) {
            bgColor = variant.activeColor;
        } else if (this.isHovered()) {
            bgColor = variant.hoverColor;
        } else {
            bgColor = variant.normalColor;
        }

        int borderColor = this.active ? (this.isHovered() ? 0xFF93C5FD : variant.borderColor) : 0xFF27272A;

        // Render Background
        if ((bgColor >>> 24) > 0) {
            guiGraphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, bgColor);
        }

        // Render Border
        if ((borderColor >>> 24) > 0) {
            guiGraphics.fill(x1 + 1, y1, x2 - 1, y1 + 1, borderColor); // Top
            guiGraphics.fill(x1 + 1, y2 - 1, x2 - 1, y2, borderColor); // Bottom
            guiGraphics.fill(x1, y1 + 1, x1 + 1, y2 - 1, borderColor); // Left
            guiGraphics.fill(x2 - 1, y1 + 1, x2, y2 - 1, borderColor); // Right
        }

        // Draw Text
        Component text = getMessage();
        if (text != null && !text.getString().isEmpty()) {
            int textColor = !this.active ? 0xFF71717A : variant.textColor;
            int textWidth = render.font().width(text);
            int textX = x1 + (getWidth() - textWidth) / 2;
            int textY = y1 + (getHeight() - 8) / 2;
            guiGraphics.drawString(render.font(), text, textX, textY, textColor, false);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }
}
