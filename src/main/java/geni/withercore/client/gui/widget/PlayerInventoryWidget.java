package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import geni.withercore.client.gui.WitherAbstractScreen;
import geni.withercore.client.gui.WitherSlotDecorator;
import geni.withercore.common.menu.WitherAbstractMenu;
import geni.withercore.common.network.packet.ServerboundMenuActionPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Modulares Widget zur Darstellung des 3x9-Spielerinventars (Abschnitt 6.2).
 * Verwendet die tatsächlichen Menu-Slots und unterstützt Ein-/Ausblenden.
 */
public class PlayerInventoryWidget extends AbstractWidget {

    public static final int WIDTH = 9 * WitherSlotDecorator.SLOT_SIZE;
    public static final int HEIGHT = 3 * WitherSlotDecorator.SLOT_SIZE;

    private final WitherAbstractScreen<?> screen;
    private boolean showLabel = true;

    public PlayerInventoryWidget(int x, int y, WitherAbstractScreen<?> screen) {
        super(x, y, WIDTH, HEIGHT, Component.translatable("container.inventory"));
        this.screen = screen;
    }

    public PlayerInventoryWidget setShowLabel(boolean showLabel) {
        this.showLabel = showLabel;
        return this;
    }

    public boolean isShowLabel() {
        return showLabel;
    }

    public void setInventoryVisible(boolean visible) {
        this.visible = visible;
        if (screen.getMenu() instanceof WitherAbstractMenu menu) {
            menu.setPlayerInvVisible(visible);
            PacketDistributor.sendToServer(new ServerboundMenuActionPacket(
                    menu.containerId,
                    ServerboundMenuActionPacket.ActionType.TOGGLE_PLAYER_INV,
                    visible ? 1 : 0
            ));
        }
    }

    @Override
    public void modularRender(GuiGraphics gg, GuiRender render, float partialTicks, int mouseX, int mouseY) {
        if (!visible) return;

        if (showLabel) {
            gg.drawString(screen.getMinecraft().font, getMessage(), getX(), getY() - 10, 0x404040, false);
        }

        WitherSlotDecorator.renderSlotGrid(gg, getX(), getY(), 9, 3, false);
    }
}

