package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import geni.withercore.client.gui.WitherSlotDecorator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Modulares Widget zur Darstellung der 1x9-Hotbar (Abschnitt 6.3).
 * Verwendet die tatsächlichen Hotbar-Slots des Spielers.
 */
public class HotbarWidget extends AbstractWidget {

    public static final int WIDTH = 9 * WitherSlotDecorator.SLOT_SIZE;
    public static final int HEIGHT = WitherSlotDecorator.SLOT_SIZE;

    public HotbarWidget(int x, int y) {
        super(x, y, WIDTH, HEIGHT, Component.empty());
    }

    @Override
    public void modularRender(GuiGraphics gg, GuiRender render, float partialTicks, int mouseX, int mouseY) {
        if (!visible) return;

        WitherSlotDecorator.renderPlayerHotbar(gg, getX(), getY());
    }
}

