package geni.withercore.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import geni.withercore.client.gui.animation.GuiAnimationHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * WitherTextHelper - High-End Text & Word Rendering Helper für NeoForge 1.21.1.
 * 
 * Bietet komfortables Rendering von Wörtern und Schriftzügen mit:
 * - Echtem Zeichenabstand (Letter-Spacing / Tracking)
 * - Minecraft Parametern (Drop-Shadow, Custom Shadow Color, Alpha, Skalierung)
 * - Sanfter Hin- und Herbewegung (Sway / Floating)
 * - Wellen-Animation pro Buchstabe (Wave per Character)
 * - Blinken & Breathing (Pulsierende Alpha / Helligkeit)
 * - Farbverläufen (Gradient) über das Wort
 */
public class WitherTextHelper {

    public enum AnimationType {
        NONE,
        SWAY,   // Sanftes Hin- und Herbewegen des gesamten Wortes (Sinus-Schweben)
        WAVE,   // Harmonische Wellenbewegung pro Buchstabe (Phasenverschiebung)
        BLINK   // Weiches Blinken / Breathing (Alpha-Oszillation)
    }

    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT
    }

    /**
     * Parameter-Klasse mit Fluent API zur einfachen Konfiguration.
     */
    public static class TextParams {
        private float spacing = 0.0f;
        private int color = 0xFFFFFFFF;
        private boolean dropShadow = true;
        private int shadowColor = 0x55000000;
        private float scale = 1.0f;
        private float alpha = 1.0f;
        private Alignment alignment = Alignment.LEFT;
        private AnimationType animation = AnimationType.NONE;
        private float animSpeed = 1.0f;
        private float animAmplitude = 2.0f;
        private boolean swayHorizontal = true;
        private boolean useGradient = false;
        private int gradientStart = 0xFFFFFFFF;
        private int gradientEnd = 0xFF38BDF8;

        public static TextParams create() {
            return new TextParams();
        }

        public TextParams spacing(float spacing) {
            this.spacing = spacing;
            return this;
        }

        public TextParams color(int color) {
            this.color = color;
            return this;
        }

        public TextParams dropShadow(boolean dropShadow) {
            this.dropShadow = dropShadow;
            return this;
        }

        public TextParams shadowColor(int shadowColor) {
            this.shadowColor = shadowColor;
            return this;
        }

        public TextParams scale(float scale) {
            this.scale = scale;
            return this;
        }

        public TextParams alpha(float alpha) {
            this.alpha = GuiAnimationHelper.clamp(alpha, 0.0f, 1.0f);
            return this;
        }

        public TextParams alignment(Alignment alignment) {
            this.alignment = alignment;
            return this;
        }

        public TextParams animation(AnimationType animation) {
            this.animation = animation;
            return this;
        }

        public TextParams animation(AnimationType animation, float speed, float amplitude) {
            this.animation = animation;
            this.animSpeed = speed;
            this.animAmplitude = amplitude;
            return this;
        }

        public TextParams swayHorizontal(boolean swayHorizontal) {
            this.swayHorizontal = swayHorizontal;
            return this;
        }

        public TextParams gradient(int startColor, int endColor) {
            this.useGradient = true;
            this.gradientStart = startColor;
            this.gradientEnd = endColor;
            return this;
        }

        // Getter
        public float getSpacing() { return spacing; }
        public int getColor() { return color; }
        public boolean hasDropShadow() { return dropShadow; }
        public int getShadowColor() { return shadowColor; }
        public float getScale() { return scale; }
        public float getAlpha() { return alpha; }
        public Alignment getAlignment() { return alignment; }
        public AnimationType getAnimation() { return animation; }
        public float getAnimSpeed() { return animSpeed; }
        public float getAnimAmplitude() { return animAmplitude; }
        public boolean isSwayHorizontal() { return swayHorizontal; }
        public boolean isUseGradient() { return useGradient; }
        public int getGradientStart() { return gradientStart; }
        public int getGradientEnd() { return gradientEnd; }
    }

    /**
     * Berechnet die exakte Gesamtbreite des Texts unter Berücksichtigung des Zeichenabstands.
     */
    public static float getWidth(Font font, String text, float letterSpacing) {
        if (text == null || text.isEmpty()) return 0;
        float baseWidth = font.width(text);
        if (letterSpacing == 0 || text.length() <= 1) {
            return baseWidth;
        }
        return baseWidth + (text.length() - 1) * letterSpacing;
    }

    public static float getWidth(Font font, Component component, float letterSpacing) {
        return getWidth(font, component.getString(), letterSpacing);
    }

    /**
     * Zeichnet einen Text zentriert an gegebener X-Koordinate.
     */
    public static void drawWordCentered(GuiGraphics graphics, Font font, String text, float centerX, float y, TextParams params) {
        float width = getWidth(font, text, params.getSpacing()) * params.getScale();
        float startX = centerX - (width / 2.0f);
        drawWord(graphics, font, text, startX, y, params);
    }

    public static void drawWordCentered(GuiGraphics graphics, Font font, Component component, float centerX, float y, TextParams params) {
        drawWordCentered(graphics, font, component.getString(), centerX, y, params);
    }

    /**
     * Hauptmethode zum Zeichnen eines Worts mit allen WitherCore-Parametern.
     */
    public static void drawWord(GuiGraphics graphics, Font font, String text, float x, float y, TextParams params) {
        if (text == null || text.isEmpty()) return;

        long time = System.currentTimeMillis();
        float timeSec = time / 1000.0f;
        float speed = params.getAnimSpeed();
        float amplitude = params.getAnimAmplitude();

        // 1. Globale Wort-Offsets (z.B. bei SWAY)
        float globalOffsetX = 0;
        float globalOffsetY = 0;
        float currentAlpha = params.getAlpha();

        if (params.getAnimation() == AnimationType.SWAY) {
            float sway = Mth.sin(timeSec * speed * 2.5f) * amplitude;
            if (params.isSwayHorizontal()) {
                globalOffsetX += sway;
            } else {
                globalOffsetY += sway;
            }
        } else if (params.getAnimation() == AnimationType.BLINK) {
            // Weiches Atmen / Pulsieren zwischen 0.35 und 1.0 Alpha
            float pulse = 0.5f + 0.5f * Mth.sin(timeSec * speed * 4.0f);
            currentAlpha *= (0.35f + 0.65f * pulse);
        }

        // Fast-Path: Standard-Text ohne Spacing, Skalierung oder Wave
        if (params.getSpacing() == 0.0f && params.getScale() == 1.0f && params.getAnimation() != AnimationType.WAVE && !params.isUseGradient()) {
            int finalColor = applyAlpha(params.getColor(), currentAlpha);
            graphics.drawString(font, text, (int)(x + globalOffsetX), (int)(y + globalOffsetY), finalColor, params.hasDropShadow());
            return;
        }

        // Zeichenweises Zeichnen für Spacing, Wave & Gradient
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x + globalOffsetX, y + globalOffsetY, 0);

        if (params.getScale() != 1.0f) {
            pose.scale(params.getScale(), params.getScale(), 1.0f);
        }

        float cursorX = 0;
        int len = text.length();

        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);
            int charWidth = font.width(charStr);

            // Lokales Zeichen-Offset (z.B. bei WAVE)
            float charOffsetY = 0;
            if (params.getAnimation() == AnimationType.WAVE) {
                float wavePhase = (timeSec * speed * 3.5f) + (i * 0.45f);
                charOffsetY = Mth.sin(wavePhase) * amplitude;
            }

            // Farbe interpolieren (falls Gradient aktiv)
            int charColor = params.getColor();
            if (params.isUseGradient() && len > 1) {
                float t = (float) i / (float) (len - 1);
                charColor = lerpColor(params.getGradientStart(), params.getGradientEnd(), t);
            }
            charColor = applyAlpha(charColor, currentAlpha);

            pose.pushPose();
            pose.translate(cursorX, charOffsetY, 0);

            // Schatten zeichnen
            if (params.hasDropShadow()) {
                int shadowCol = applyAlpha(params.getShadowColor(), currentAlpha);
                graphics.drawString(font, charStr, 1, 1, shadowCol, false);
            }

            // Zeichen zeichnen
            graphics.drawString(font, charStr, 0, 0, charColor, false);

            pose.popPose();

            cursorX += charWidth + params.getSpacing();
        }

        pose.popPose();
    }

    public static void drawWord(GuiGraphics graphics, Font font, Component component, float x, float y, TextParams params) {
        drawWord(graphics, font, component.getString(), x, y, params);
    }

    /**
     * Hilfsmethode zur Farbmodulation mit Alpha.
     */
    public static int applyAlpha(int argb, float alphaFactor) {
        int a = (argb >> 24) & 0xFF;
        if (a == 0) a = 0xFF; // Falls RGB ohne Alpha übergeben wurde
        a = (int) (a * GuiAnimationHelper.clamp(alphaFactor, 0.0f, 1.0f));
        return (a << 24) | (argb & 0x00FFFFFF);
    }

    /**
     * Lineare Farbinterpolation zwischen zwei ARGB-Farben.
     */
    public static int lerpColor(int colA, int colB, float t) {
        t = GuiAnimationHelper.clamp(t, 0.0f, 1.0f);
        int aA = (colA >> 24) & 0xFF, rA = (colA >> 16) & 0xFF, gA = (colA >> 8) & 0xFF, bA = colA & 0xFF;
        int aB = (colB >> 24) & 0xFF, rB = (colB >> 16) & 0xFF, gB = (colB >> 8) & 0xFF, bB = colB & 0xFF;

        int a = (int) GuiAnimationHelper.lerp(aA, aB, t);
        int r = (int) GuiAnimationHelper.lerp(rA, rB, t);
        int g = (int) GuiAnimationHelper.lerp(gA, gB, t);
        int b = (int) GuiAnimationHelper.lerp(bA, bB, t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
