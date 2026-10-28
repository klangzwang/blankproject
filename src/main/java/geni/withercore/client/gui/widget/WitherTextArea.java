package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * Multi-line text area widget for text input, scripts, notes, or descriptions.
 * Supports cursor movement, text wrapping, newlines (Enter), and backspace/delete.
 */
public class WitherTextArea extends AbstractWidget {

    private String text = "";
    private Component placeholder = Component.empty();
    private int cursorPosition = 0;
    private int maxCharacters = 2048;
    private Consumer<String> onTextChanged;
    private int textColor = 0xFFF4F4F5;
    private int placeholderColor = 0xFF71717A;
    private int borderColor = 0xFF2A2D3B;
    private int focusBorderColor = 0xFF00E5FF;
    private int backgroundColor = 0xFF12141C;
    private int scrollY = 0;

    public WitherTextArea(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public WitherTextArea(int x, int y, int width, int height, Component placeholder) {
        super(x, y, width, height, Component.empty());
        this.placeholder = placeholder;
    }

    public String getText() {
        return text;
    }

    public WitherTextArea setText(String text) {
        this.text = text != null ? text : "";
        this.cursorPosition = Mth.clamp(this.cursorPosition, 0, this.text.length());
        return this;
    }

    public WitherTextArea setPlaceholder(Component placeholder) {
        this.placeholder = placeholder != null ? placeholder : Component.empty();
        return this;
    }

    public WitherTextArea setOnTextChanged(Consumer<String> listener) {
        this.onTextChanged = listener;
        return this;
    }

    public WitherTextArea setMaxCharacters(int max) {
        this.maxCharacters = Math.max(1, max);
        return this;
    }

    private void notifyChange() {
        if (onTextChanged != null) {
            onTextChanged.accept(text);
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!isFocused() || !this.active) return false;

        if (this.text.length() < maxCharacters && (Character.isDefined(codePoint) || codePoint == '\n')) {
            insertText(Character.toString(codePoint));
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused() || !this.active) return false;

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (cursorPosition > 0 && !text.isEmpty()) {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
                notifyChange();
            }
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DELETE) {
            if (cursorPosition < text.length()) {
                text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                notifyChange();
            }
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (this.text.length() < maxCharacters) {
                insertText("\n");
            }
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
            cursorPosition = Math.max(0, cursorPosition - 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            cursorPosition = Math.min(text.length(), cursorPosition + 1);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_HOME) {
            cursorPosition = 0;
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_END) {
            cursorPosition = text.length();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void insertText(String str) {
        text = text.substring(0, cursorPosition) + str + text.substring(cursorPosition);
        cursorPosition += str.length();
        notifyChange();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean inside = isMouseOver(mouseX, mouseY);
        setFocused(inside);
        if (inside && button == 0) {
            // Position cursor at end by default on click
            this.cursorPosition = text.length();
            return true;
        }
        return inside;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isMouseOver(mouseX, mouseY)) {
            scrollY = (int) Mth.clamp(scrollY - scrollY * 10, 0, Math.max(0, getLineCount() * 10 - getHeight()));
            return true;
        }
        return false;
    }

    private int getLineCount() {
        return text.split("\n", -1).length;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        GuiRender render = GuiRender.convert(guiGraphics);
        Font font = render.font();
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        // 1. Background
        guiGraphics.fill(x, y, x + w, y + h, backgroundColor);

        // 2. Border (Highlighted if focused)
        int currentBorder = isFocused() ? focusBorderColor : borderColor;
        guiGraphics.fill(x, y, x + w, y + 1, currentBorder);
        guiGraphics.fill(x, y + h - 1, x + w, y + h, currentBorder);
        guiGraphics.fill(x, y, x + 1, y + h, currentBorder);
        guiGraphics.fill(x + w - 1, y, x + w, y + h, currentBorder);

        // 3. Scissor clipping for text
        guiGraphics.enableScissor(x + 3, y + 3, x + w - 3, y + h - 3);

        int textStartX = x + 4;
        int textStartY = y + 4;

        if (text.isEmpty() && !placeholder.getString().isEmpty() && !isFocused()) {
            guiGraphics.drawString(font, placeholder, textStartX, textStartY, placeholderColor, false);
        } else {
            String[] lines = text.split("\n", -1);
            int curCharCount = 0;
            int cursorLine = 0;
            int cursorCol = 0;

            for (int i = 0; i < lines.length; i++) {
                int lineLen = lines[i].length();
                if (cursorPosition >= curCharCount && cursorPosition <= curCharCount + lineLen) {
                    cursorLine = i;
                    cursorCol = cursorPosition - curCharCount;
                }
                curCharCount += lineLen + 1; // +1 for newline
            }

            int lineY = textStartY - scrollY;
            for (int i = 0; i < lines.length; i++) {
                if (lineY + 10 >= y && lineY <= y + h) {
                    guiGraphics.drawString(font, lines[i], textStartX, lineY, textColor, false);

                    // Render blinking cursor on this line
                    if (isFocused() && i == cursorLine) {
                        boolean blink = (System.currentTimeMillis() / 450) % 2 == 0;
                        if (blink) {
                            String sub = lines[i].substring(0, Math.min(cursorCol, lines[i].length()));
                            int curX = textStartX + font.width(sub);
                            guiGraphics.fill(curX, lineY, curX + 1, lineY + 9, focusBorderColor);
                        }
                    }
                }
                lineY += 10;
            }
        }

        guiGraphics.disableScissor();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
