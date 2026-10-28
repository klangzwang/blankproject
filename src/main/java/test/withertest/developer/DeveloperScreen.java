package test.withertest.developer;

import codechicken.lib.gui.modular.lib.GuiRender;
import geni.withercore.client.gui.*;
import geni.withercore.client.gui.widget.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class DeveloperScreen extends WitherAbstractEntityScreen<DeveloperMenu, DeveloperBlockEntity> {

    private int activeTab = 0;

    private WitherTabs tabs;
    private WitherButtonGroup triggerGroup;
    private WitherSlider scanWidthSlider;
    private WitherSwitch previewSwitch;
    private WitherCheckbox soundCheckbox;
    private WitherGuiObjectRenderer objectRenderer;

    public DeveloperScreen(DeveloperMenu gui, Inventory inv, Component title) {
        super(gui, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        updateWidgetVisibility();
    }

    private void updateWidgetVisibility() {
        boolean isTab0 = (activeTab == 0);
        boolean isTab1 = (activeTab == 1);

        if (triggerGroup != null) triggerGroup.visible = isTab0;
        if (scanWidthSlider != null) scanWidthSlider.visible = isTab0;
        if (previewSwitch != null) previewSwitch.visible = isTab1;
        if (soundCheckbox != null) soundCheckbox.visible = isTab1;
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        super.render(gg, mouseX, mouseY, partialTick);
    }

    @Override
    public void modularRender(GuiGraphics gg, GuiRender render, GuiToolkit toolKit, float partialTicks, int mouseX, int mouseY) {
        WitherSlotDecorator.renderSlot(gg, leftPos + 150, topPos + 6, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    protected String getScreenName() {
        return "DEVELOPER";
    }
}
