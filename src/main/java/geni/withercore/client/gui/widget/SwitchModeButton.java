package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SwitchModeButton<T> extends AbstractButton {

    private final Supplier<T> getter;
    private final Consumer<T> setter;

    private T previousValue;

    public SwitchModeButton(int x, int y, int width, int height, Supplier<T> getter, Consumer<T> setter) {
        super(x, y, width, height, Component.empty());

        this.getter = getter;
        this.setter = setter;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void onPress() {
        if (getter != null && setter != null) {
            T currentValue = getter.get();
            if (currentValue instanceof Integer val) {
                int nextVal = (val % 3) + 1;
                setter.accept((T) Integer.valueOf(nextVal));
            } else {
                setter.accept(currentValue);
            }
        }
    }

    @Override
    public void renderButtonFace(GuiGraphics guiGraphics, GuiRender render, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        int currentMode = (int) getter.get();
        String text = "Mode: " + currentMode;

        int textWidth = render.font().width(text);

        int textX = this.getX() + (this.width - textWidth) / 2;
        int textY = this.getY() + (this.height - 8) / 2;

        render.drawString(text, textX, textY, 0xFFFFFFFF);

        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
