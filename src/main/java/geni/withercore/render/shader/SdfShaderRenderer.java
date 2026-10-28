package geni.withercore.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import geni.withercore.init.WUTShaders;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;

public class SdfShaderRenderer {

    public static void render(GuiGraphics gg, float x, float y, SdfShaderConfig config) {
        render(gg, x, y, 0.0f, config);
    }

    public static void render(GuiGraphics gg, float x, float y, float z, SdfShaderConfig config) {
        ShaderInstance shader = WUTShaders.getRoundedRectShader();

        if (shader == null) {
            renderFallback(gg, x, y, z, config);
            return;
        }

        float pad = Math.max(
                config.getShadowBlur() * 2.5f + Math.max(Math.abs(config.getShadowOffset()[0]), Math.abs(config.getShadowOffset()[1])),
                1.0f
        );

        float x1 = x - pad;
        float y1 = y - pad;
        float x2 = x + config.getRectWidth() + pad;
        float y2 = y + config.getRectHeight() + pad;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        // Update uniforms
        if (shader.safeGetUniform("RectSize") != null) {
            shader.safeGetUniform("RectSize").set(config.getRectWidth(), config.getRectHeight());
        }
        if (shader.safeGetUniform("BgColorTop") != null) {
            shader.safeGetUniform("BgColorTop").set(config.getBgColorTop());
        }
        if (shader.safeGetUniform("BgColorBottom") != null) {
            shader.safeGetUniform("BgColorBottom").set(config.getBgColorBottom());
        }
        if (shader.safeGetUniform("BorderColor") != null) {
            shader.safeGetUniform("BorderColor").set(config.getBorderColor());
        }
        if (shader.safeGetUniform("Radius") != null) {
            shader.safeGetUniform("Radius").set(config.getRadius());
        }
        if (shader.safeGetUniform("BorderThickness") != null) {
            shader.safeGetUniform("BorderThickness").set(config.getBorderThickness());
        }
        if (shader.safeGetUniform("ShadowOffset") != null) {
            shader.safeGetUniform("ShadowOffset").set(config.getShadowOffset());
        }
        if (shader.safeGetUniform("ShadowBlur") != null) {
            shader.safeGetUniform("ShadowBlur").set(config.getShadowBlur());
        }
        if (shader.safeGetUniform("ShadowColor") != null) {
            shader.safeGetUniform("ShadowColor").set(config.getShadowColor());
        }

        shader.apply();
        RenderSystem.setShader(() -> shader);

        Matrix4f matrix = gg.pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        bufferBuilder.addVertex(matrix, x1, y2, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 1.0f);
        bufferBuilder.addVertex(matrix, x2, y2, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 1.0f);
        bufferBuilder.addVertex(matrix, x2, y1, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(1.0f, 0.0f);
        bufferBuilder.addVertex(matrix, x1, y1, z).setColor(1.0f, 1.0f, 1.0f, 1.0f).setUv(0.0f, 0.0f);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        shader.clear();
        RenderSystem.enableDepthTest();
    }

    private static void renderFallback(GuiGraphics gg, float x, float y, float z, SdfShaderConfig config) {
        int x1 = (int) x;
        int y1 = (int) y;
        int x2 = (int) (x + config.getRectWidth());
        int y2 = (int) (y + config.getRectHeight());

        // Shadow fallback
        int shadowColor = float4ToArgb(config.getShadowColor());
        gg.fill(x1 + (int) config.getShadowOffset()[0] + 2, y1 + (int) config.getShadowOffset()[1] + 2,
                x2 + (int) config.getShadowOffset()[0] + 2, y2 + (int) config.getShadowOffset()[1] + 2, shadowColor);

        // Border fallback
        int borderColor = float4ToArgb(config.getBorderColor());
        gg.fill(x1 - 1, y1 - 1, x2 + 1, y2 + 1, borderColor);

        // Body gradient fallback
        int topColor = float4ToArgb(config.getBgColorTop());
        int bottomColor = float4ToArgb(config.getBgColorBottom());
        gg.fillGradient(x1, y1, x2, y2, topColor, bottomColor);
    }

    private static int float4ToArgb(float[] c) {
        int a = Math.round(c[3] * 255.0f) & 0xFF;
        int r = Math.round(c[0] * 255.0f) & 0xFF;
        int g = Math.round(c[1] * 255.0f) & 0xFF;
        int b = Math.round(c[2] * 255.0f) & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
