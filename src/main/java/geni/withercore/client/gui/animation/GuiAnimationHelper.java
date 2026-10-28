package geni.withercore.client.gui.animation;

/**
 * Mathematischer Animations-Helper für Minecraft NeoForge GUIs.
 * Entspricht 1:1 dem GuiAnimationSystem des Frontend-Clients.
 */
public class GuiAnimationHelper {

    public static float linear(float t) {
        return t;
    }

    public static float easeOutCubic(float t) {
        float f = t - 1.0f;
        return f * f * f + 1.0f;
    }

    public static float easeInCubic(float t) {
        return t * t * t;
    }

    public static float easeInOutQuad(float t) {
        return t < 0.5f ? 2.0f * t * t : -1.0f + (4.0f - 2.0f * t) * t;
    }

    public static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float f = t - 1.0f;
        return 1.0f + c3 * f * f * f + c1 * f * f;
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float smoothstep(float min, float max, float value) {
        float x = Math.max(0.0f, Math.min(1.0f, (value - min) / (max - min)));
        return x * x * (3.0f - 2.0f * x);
    }
}
