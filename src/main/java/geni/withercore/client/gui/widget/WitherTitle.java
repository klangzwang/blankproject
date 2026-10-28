package geni.withercore.client.gui.widget;

import geni.withercore.client.gui.WitherTextHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/**
 * WitherTitle - Zentrales Titel-Widget für WitherCore & WitherUtils Screens.
 * 
 * Eigenschaften:
 * - Zentriert den Titel immer mittig oben in der Screenbox.
 * - Zeichnet vom ersten Buchstaben eine Hightech-Verbindungslinie:
 *   Zuerst waagerecht bis zur Hälfte der Distanz zur Screenbox, 
 *   und dann senkrecht zur oberen Kante / oberen rechten Ecke.
 * - Voll integriert mit dem {@link WitherTextHelper} für echten Zeichenabstand (Letter-Spacing),
 *   Blinken, Wave-Effekt und sanftes Hin- und Her-Bewegen (Sway).
 */
public class WitherTitle extends AbstractWidget {

    public enum LineTarget {
        TOP_RIGHT_CORNER,
        TOP_LEFT_CORNER
    }

    private int boxX;
    private int boxY;
    private int boxWidth;
    private int boxHeight;

    @Nullable
    private Component subtitle;

    private final WitherTextHelper.TextParams titleParams;
    private final WitherTextHelper.TextParams subtitleParams;

    private boolean showLine = true;
    private int lineColor = 0x6638BDF8;         // Subtiles Zyan / Light Blue (WitherTech)
    private int lineAccentColor = 0xFF38BDF8;   // Volle Akzentfarbe für Eck-Punkte
    private LineTarget lineTarget = LineTarget.TOP_RIGHT_CORNER;
    private int lineOffsetY = 4;                // Vertikaler Versatz der Linie auf Höhe des ersten Buchstabens

    public WitherTitle(int boxX, int boxY, int boxWidth, Component title) {
        this(boxX, boxY, boxWidth, 166, title);
    }

    public WitherTitle(int boxX, int boxY, int boxWidth, int boxHeight, Component title) {
        super(boxX, boxY + 6, boxWidth, 14, title);
        this.boxX = boxX;
        this.boxY = boxY;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;

        // Default Title Styling
        this.titleParams = WitherTextHelper.TextParams.create()
                .spacing(1.2f)
                .color(0xFFE2E8F0)
                .dropShadow(true)
                .shadowColor(0x77000000)
                .scale(1.0f);

        // Default Subtitle Styling
        this.subtitleParams = WitherTextHelper.TextParams.create()
                .spacing(0.8f)
                .color(0xFF94A3B8)
                .dropShadow(true)
                .shadowColor(0x66000000)
                .scale(0.75f);
    }

    public WitherTitle setTitle(Component title) {
        setMessage(title);
        return this;
    }

    public WitherTitle setSubtitle(@Nullable Component subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public WitherTitle setLetterSpacing(float spacing) {
        this.titleParams.spacing(spacing);
        return this;
    }

    public WitherTitle setAnimation(WitherTextHelper.AnimationType animation) {
        this.titleParams.animation(animation);
        return this;
    }

    public WitherTitle setAnimation(WitherTextHelper.AnimationType animation, float speed, float amplitude) {
        this.titleParams.animation(animation, speed, amplitude);
        return this;
    }

    public WitherTitle setTextColor(int color) {
        this.titleParams.color(color);
        return this;
    }

    public WitherTitle setLineColor(int color) {
        this.lineColor = color;
        return this;
    }

    public WitherTitle setLineAccentColor(int color) {
        this.lineAccentColor = color;
        return this;
    }

    public WitherTitle setShowLine(boolean showLine) {
        this.showLine = showLine;
        return this;
    }

    public WitherTitle setLineTarget(LineTarget target) {
        this.lineTarget = target;
        return this;
    }

    public WitherTitle setBoxBounds(int boxX, int boxY, int boxWidth, int boxHeight) {
        this.boxX = boxX;
        this.boxY = boxY;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        setX(boxX);
        setWidth(boxWidth);
        return this;
    }

    public WitherTextHelper.TextParams getTitleParams() {
        return titleParams;
    }

    public WitherTextHelper.TextParams getSubtitleParams() {
        return subtitleParams;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        Component title = getMessage();
        String titleStr = title.getString();
        float titleWidth = WitherTextHelper.getWidth(font, titleStr, titleParams.getSpacing()) * titleParams.getScale();

        // 1. Zentrierte Positionierung des Titels oben
        int centerX = boxX + (boxWidth / 2);
        int titleY = getY();

        // Startpunkt am ersten Buchstaben
        float firstCharX = centerX - (titleWidth / 2.0f);
        float lineStartY = titleY + lineOffsetY;

        // 2. Hightech Verbindungslinie zeichnen
        if (showLine) {
            renderConnectingLine(guiGraphics, firstCharX, lineStartY);
        }

        // 3. Titel mit WitherTextHelper rendern
        WitherTextHelper.drawWordCentered(guiGraphics, font, titleStr, centerX, titleY, titleParams);

        // 4. Optionaler Subtitle
        if (subtitle != null && !subtitle.getString().isEmpty()) {
            int subY = titleY + 11;
            WitherTextHelper.drawWordCentered(guiGraphics, font, subtitle.getString(), centerX, subY, subtitleParams);
        }
    }

    /**
     * Zeichnet die Verbindungslinie:
     * Start am ersten Buchstaben -> waagerecht bis zur Hälfte der Distanz ->
     * senkrecht zur oberen Kante / oberen rechten Ecke der Screenbox.
     */
    private void renderConnectingLine(GuiGraphics guiGraphics, float startX, float startY) {
        float targetX;
        float targetY = boxY; // Obere Kante der Screenbox

        if (lineTarget == LineTarget.TOP_RIGHT_CORNER) {
            targetX = boxX + boxWidth;
        } else {
            targetX = boxX;
        }

        // Hälfte der Distanz zwischen erstem Buchstaben und der Ziel-Screenboxkante
        float deltaX = targetX - startX;
        float midX = startX + (deltaX / 2.0f);

        int ix1 = Math.round(startX);
        int iy1 = Math.round(startY);
        int imidX = Math.round(midX);
        int itargetX = Math.round(targetX);
        int itargetY = Math.round(targetY);

        // A. Kleiner Start-Akzentpunkt am ersten Buchstaben (1x1 bzw. 2x2 px)
        guiGraphics.fill(ix1 - 2, iy1 - 1, ix1, iy1 + 1, lineAccentColor);

        // B. Horizontaler Schenkel: vom ersten Buchstaben bis zur halben Distanz
        if (ix1 < imidX) {
            guiGraphics.hLine(ix1, imidX, iy1, lineColor);
        } else {
            guiGraphics.hLine(imidX, ix1, iy1, lineColor);
        }

        // C. Vertikaler Schenkel: von halber Distanz senkrecht hoch zur oberen Kante
        if (itargetY < iy1) {
            guiGraphics.vLine(imidX, itargetY, iy1, lineColor);
        } else {
            guiGraphics.vLine(imidX, iy1, itargetY, lineColor);
        }

        // D. Kanten-Schenkel zur oberen Ecke (falls Knickpunkt noch nicht an der Ecke ist)
        if (imidX < itargetX) {
            guiGraphics.hLine(imidX, itargetX, itargetY, lineColor);
        } else if (imidX > itargetX) {
            guiGraphics.hLine(itargetX, imidX, itargetY, lineColor);
        }

        // E. End-Akzentpunkt an der oberen Ecke der Screenbox
        guiGraphics.fill(itargetX - 2, itargetY, itargetX, itargetY + 2, lineAccentColor);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }
}
