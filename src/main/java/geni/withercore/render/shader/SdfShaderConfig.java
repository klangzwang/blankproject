package geni.withercore.render.shader;

public class SdfShaderConfig {

    private float rectWidth;
    private float rectHeight;

    private float[] bgColorTop = new float[]{0.18f, 0.19f, 0.23f, 0.95f};
    private float[] bgColorBottom = new float[]{0.11f, 0.12f, 0.15f, 0.95f};
    private float[] borderColor = new float[]{0.35f, 0.38f, 0.48f, 1.0f};

    private float radius = 8.0f;
    private float borderThickness = 2.0f;

    private float[] shadowOffset = new float[]{0.0f, 6.0f};
    private float shadowBlur = 12.0f;
    private float[] shadowColor = new float[]{0.0f, 0.0f, 0.0f, 0.6f};

    public SdfShaderConfig() {
        this(176, 166);
    }

    public SdfShaderConfig(float width, float height) {
        this.rectWidth = width;
        this.rectHeight = height;
    }

    public static SdfShaderConfig defaultScreen(float width, float height) {
        return new SdfShaderConfig(width, height);
    }

    public static SdfShaderConfig card(float width, float height) {
        SdfShaderConfig config = new SdfShaderConfig(width, height);
        config.setRadius(6.0f);
        config.setBorderThickness(1.5f);
        config.setBgColorTop(0.14f, 0.15f, 0.19f, 0.92f);
        config.setBgColorBottom(0.09f, 0.10f, 0.13f, 0.92f);
        config.setBorderColor(0.28f, 0.30f, 0.40f, 0.85f);
        config.setShadowOffset(0.0f, 3.0f);
        config.setShadowBlur(8.0f);
        config.setShadowColor(0.0f, 0.0f, 0.0f, 0.45f);
        return config;
    }

    public static SdfShaderConfig tooltip(float width, float height) {
        SdfShaderConfig config = new SdfShaderConfig(width, height);
        config.setRadius(4.0f);
        config.setBorderThickness(1.0f);
        config.setBgColorTop(0.08f, 0.08f, 0.12f, 0.96f);
        config.setBgColorBottom(0.05f, 0.05f, 0.08f, 0.96f);
        config.setBorderColor(0.40f, 0.25f, 0.65f, 1.0f);
        config.setShadowOffset(0.0f, 4.0f);
        config.setShadowBlur(10.0f);
        config.setShadowColor(0.0f, 0.0f, 0.0f, 0.7f);
        return config;
    }

    // Getters and Setters
    public float getRectWidth() {
        return rectWidth;
    }

    public SdfShaderConfig setRectWidth(float rectWidth) {
        this.rectWidth = rectWidth;
        return this;
    }

    public float getRectHeight() {
        return rectHeight;
    }

    public SdfShaderConfig setRectHeight(float rectHeight) {
        this.rectHeight = rectHeight;
        return this;
    }

    public SdfShaderConfig setSize(float width, float height) {
        this.rectWidth = width;
        this.rectHeight = height;
        return this;
    }

    public float[] getBgColorTop() {
        return bgColorTop;
    }

    public SdfShaderConfig setBgColorTop(float r, float g, float b, float a) {
        this.bgColorTop = new float[]{r, g, b, a};
        return this;
    }

    public SdfShaderConfig setBgColorTop(int argb) {
        this.bgColorTop = argbToFloat4(argb);
        return this;
    }

    public float[] getBgColorBottom() {
        return bgColorBottom;
    }

    public SdfShaderConfig setBgColorBottom(float r, float g, float b, float a) {
        this.bgColorBottom = new float[]{r, g, b, a};
        return this;
    }

    public SdfShaderConfig setBgColorBottom(int argb) {
        this.bgColorBottom = argbToFloat4(argb);
        return this;
    }

    public float[] getBorderColor() {
        return borderColor;
    }

    public SdfShaderConfig setBorderColor(float r, float g, float b, float a) {
        this.borderColor = new float[]{r, g, b, a};
        return this;
    }

    public SdfShaderConfig setBorderColor(int argb) {
        this.borderColor = argbToFloat4(argb);
        return this;
    }

    public float getRadius() {
        return radius;
    }

    public SdfShaderConfig setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public float getBorderThickness() {
        return borderThickness;
    }

    public SdfShaderConfig setBorderThickness(float borderThickness) {
        this.borderThickness = borderThickness;
        return this;
    }

    public float[] getShadowOffset() {
        return shadowOffset;
    }

    public SdfShaderConfig setShadowOffset(float x, float y) {
        this.shadowOffset = new float[]{x, y};
        return this;
    }

    public float getShadowBlur() {
        return shadowBlur;
    }

    public SdfShaderConfig setShadowBlur(float shadowBlur) {
        this.shadowBlur = shadowBlur;
        return this;
    }

    public float[] getShadowColor() {
        return shadowColor;
    }

    public SdfShaderConfig setShadowColor(float r, float g, float b, float a) {
        this.shadowColor = new float[]{r, g, b, a};
        return this;
    }

    public SdfShaderConfig setShadowColor(int argb) {
        this.shadowColor = argbToFloat4(argb);
        return this;
    }

    private static float[] argbToFloat4(int argb) {
        float a = ((argb >> 24) & 0xFF) / 255.0f;
        float r = ((argb >> 16) & 0xFF) / 255.0f;
        float g = ((argb >> 8) & 0xFF) / 255.0f;
        float b = (argb & 0xFF) / 255.0f;
        return new float[]{r, g, b, a};
    }
}
