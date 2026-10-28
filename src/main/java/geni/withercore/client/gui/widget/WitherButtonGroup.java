package geni.withercore.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WitherButtonGroup extends AbstractWidget {

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    private final List<WitherButton> buttons = new ArrayList<>();
    private final Orientation orientation;
    private final int spacing;
    private int selectedIndex = -1;
    private Consumer<Integer> onSelectionChanged;

    public WitherButtonGroup(int x, int y, Orientation orientation, int spacing) {
        super(x, y, 0, 0, Component.empty());
        this.orientation = orientation;
        this.spacing = spacing;
    }

    public WitherButtonGroup addButton(Component label, WitherButton.Variant variant) {
        int index = buttons.size();
        WitherButton btn = new WitherButton(0, 0, 40, 20, label, variant, b -> select(index));
        buttons.add(btn);
        layoutButtons();
        return this;
    }

    public WitherButtonGroup addButton(WitherButton button) {
        int index = buttons.size();
        buttons.add(button);
        layoutButtons();
        return this;
    }

    public WitherButtonGroup setOnSelectionChanged(Consumer<Integer> listener) {
        this.onSelectionChanged = listener;
        return this;
    }

    public void select(int index) {
        this.selectedIndex = index;
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setSelected(i == index);
        }
        if (onSelectionChanged != null) {
            onSelectionChanged.accept(index);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public List<WitherButton> getButtons() {
        return buttons;
    }

    private void layoutButtons() {
        int curX = getX();
        int curY = getY();
        int totalW = 0;
        int totalH = 0;

        for (int i = 0; i < buttons.size(); i++) {
            WitherButton btn = buttons.get(i);
            btn.setPosition(curX, curY);

            if (orientation == Orientation.HORIZONTAL) {
                curX += btn.getWidth() + spacing;
                totalW += btn.getWidth() + (i > 0 ? spacing : 0);
                totalH = Math.max(totalH, btn.getHeight());
            } else {
                curY += btn.getHeight() + spacing;
                totalH += btn.getHeight() + (i > 0 ? spacing : 0);
                totalW = Math.max(totalW, btn.getWidth());
            }
        }

        this.width = totalW;
        this.height = totalH;
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        layoutButtons();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (WitherButton btn : buttons) {
            btn.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible) return false;
        for (WitherButton btn : buttons) {
            if (btn.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
