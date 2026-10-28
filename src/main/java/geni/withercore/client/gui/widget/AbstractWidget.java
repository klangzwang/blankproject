package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import geni.witherapi.client.gui.IFancyFontRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public abstract class AbstractWidget extends net.minecraft.client.gui.components.AbstractWidget implements IFancyFontRenderer {

    public AbstractWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        modularRender(guiGraphics, GuiRender.convert(guiGraphics), partialTick, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public abstract void modularRender(GuiGraphics gg, GuiRender render, float partialTicks, int mouseX, int mouseY);


    protected void drawButtonText(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component text = getMessage();
        if (!text.getString().isEmpty()) {
/*            int color = getButtonTextColor(mouseX, mouseY) | Mth.ceil(alpha * 255.0F) << 24;
            IFancyFontRenderer.super.drawScrollingString(guiGraphics, text, getButtonX(), getButtonY(), TextAlignment.CENTER, color, getButtonWidth(),
                    getButtonHeight() + 1, 2, displayButtonTextShadow(), getTimeOpened());*/
        }
    }

    @Override
    public final void drawScrollingString(GuiGraphics graphics, Component text, int x, int y, TextAlignment alignment, int color, int width, int height, int maxLengthPad,
                                          boolean shadow, long msVisible) {
        IFancyFontRenderer.super.drawScrollingString(graphics, text, x, y, alignment, color, width, height, maxLengthPad, shadow, msVisible);
    }

    @Override
    public final void drawScaledScrollingString(GuiGraphics graphics, Component text, int x, int y, TextAlignment alignment, int color, int width, int height, int maxLengthPad,
                                                boolean shadow, float scale, long msVisible) {
        IFancyFontRenderer.super.drawScaledScrollingString(graphics, text, x, y, alignment, color, width, height, maxLengthPad, shadow, scale, msVisible);
    }
}
