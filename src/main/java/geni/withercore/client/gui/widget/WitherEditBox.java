package geni.withercore.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class WitherEditBox extends EditBox {

    private final Consumer<String> setter;
    
	private static final int KEY_DELETE = 261;
	private static final int KEY_BACKSPACE = 259;

	public WitherEditBox(Font fontIn, int xIn, int yIn, int widthIn, Consumer<String> setter)
	{
		super(fontIn, xIn, yIn, widthIn, 18, Component.empty());
		this.setBordered(true);
		this.setVisible(true);
		this.setTextColor(16777215);
        this.setter = setter;
	}
	
	@Override
	public void setFocused(boolean setFocused)
	{
		super.setFocused(setFocused);
		saveValue();
	}

	@Override
	public boolean charTyped(char chr, int p)
	{
		boolean worked = super.charTyped(chr, p);
		if (worked)
		{
			saveValue();
		}
		return worked;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		boolean spr = super.keyPressed(keyCode, scanCode, modifiers);
		if (keyCode == KEY_BACKSPACE || keyCode == KEY_DELETE)
		{
			saveValue();
		}
		return spr;
	}

	private void saveValue()
	{
		setter.accept(getValue());
	}

	private List<Component> tooltip;

	public List<Component> getTooltips()
	{
		return tooltip;
	}

	public void setTooltip(String tt)
	{
		if (tooltip == null) {
			tooltip = new ArrayList<>();
		}
		this.tooltip.add(Component.translatable(tt));
	}
}
