package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WitherTabs extends AbstractWidget {

    public static class TabEntry {
        public final Component label;
        public final int id;

        public TabEntry(int id, Component label) {
            this.id = id;
            this.label = label;
        }
    }

    private final List<TabEntry> tabs = new ArrayList<>();
    private int selectedTab = 0;
    private Consumer<Integer> onTabChanged;

    public WitherTabs(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public WitherTabs addTab(int id, Component label) {
        tabs.add(new TabEntry(id, label));
        return this;
    }

    public WitherTabs setOnTabChanged(Consumer<Integer> listener) {
        this.onTabChanged = listener;
        return this;
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public void selectTab(int index) {
        if (index >= 0 && index < tabs.size() && index != selectedTab) {
            this.selectedTab = index;
            if (onTabChanged != null) {
                onTabChanged.accept(tabs.get(index).id);
            }
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (!this.active || tabs.isEmpty()) return;

        int tabWidth = getWidth() / tabs.size();
        int relativeX = (int) (mouseX - getX());
        int clickedIdx = relativeX / tabWidth;

        if (clickedIdx >= 0 && clickedIdx < tabs.size()) {
            selectTab(clickedIdx);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void modularRender(GuiGraphics guiGraphics, GuiRender render, float partialTicks, int mouseX, int mouseY) {
        if (tabs.isEmpty()) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int x1 = getX();
        int y1 = getY();
        int tabW = getWidth() / tabs.size();
        int tabH = getHeight();

        // Bottom border line across all tabs
        guiGraphics.fill(x1, y1 + tabH - 1, x1 + getWidth(), y1 + tabH, 0xFF27272A);

        for (int i = 0; i < tabs.size(); i++) {
            TabEntry tab = tabs.get(i);
            int tx1 = x1 + i * tabW;
            int tx2 = tx1 + tabW;
            boolean isSelected = (i == selectedTab);
            boolean isHovered = mouseX >= tx1 && mouseX < tx2 && mouseY >= y1 && mouseY < y1 + tabH;

            // Background
            int bg = isSelected ? 0xFF18181B : (isHovered ? 0xFF141416 : 0x00000000);
            if ((bg >>> 24) > 0) {
                guiGraphics.fill(tx1, y1, tx2, y1 + tabH - 1, bg);
            }

            // Active underline indicator
            if (isSelected) {
                guiGraphics.fill(tx1, y1 + tabH - 2, tx2, y1 + tabH, 0xFF3B82F6);
            }

            // Tab Text
            int textColor = isSelected ? 0xFFFFFFFF : (isHovered ? 0xFFD4D4D8 : 0xFFA1A1AA);
            int textW = guiGraphics.bufferSource() != null ? 8 * tab.label.getString().length() : 0;
            int textX = tx1 + (tabW - textW) / 2;
            int textY = y1 + (tabH - 8) / 2;
            render.drawString(tab.label, textX, textY, textColor, false);
        }
    }
}
