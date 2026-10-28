package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import geni.withercore.client.shader.SdfShaderConfig;
import geni.withercore.client.shader.SdfShaderRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class WitherCard extends AbstractWidget {

    @Nullable
    private SdfShaderConfig shaderConfig;
    private boolean useShader = true;
    private int headerHeight = 20;
    private int headerBgColor = 0xFF18181B;
    private int bodyBgColor = 0xFF09090B;
    private int borderColor = 0xFF27272A;

    public WitherCard(int x, int y, int width, int height, Component title) {
        super(x, y, width, height, title);
        this.shaderConfig = SdfShaderConfig.card(width, height);
    }

    public WitherCard setUseShader(boolean useShader) {
        this.useShader = useShader;
        return this;
    }

    public WitherCard setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
        return this;
    }

    public SdfShaderConfig getShaderConfig() {
        return shaderConfig;
    }

    public WitherCard setShaderConfig(SdfShaderConfig config) {
        this.shaderConfig = config;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void modularRender(GuiGraphics gg, GuiRender render, float partialTicks, int mouseX, int mouseY) {
        int x1 = getX();
        int y1 = getY();
        int x2 = x1 + getWidth();
        int y2 = y1 + getHeight();

        if (useShader && shaderConfig != null) {
            shaderConfig.setSize(getWidth(), getHeight());
            SdfShaderRenderer.render(gg, x1, y1, shaderConfig);
        } else {
            // Fallback rendering
            gg.fill(x1, y1, x2, y2, bodyBgColor);
            gg.fill(x1, y1, x2, y1 + headerHeight, headerBgColor);

            // Border
            gg.fill(x1, y1, x2, y1 + 1, borderColor);
            gg.fill(x1, y2 - 1, x2, y2, borderColor);
            gg.fill(x1, y1, x1 + 1, y2, borderColor);
            gg.fill(x2 - 1, y1, x2, y2, borderColor);
        }

        // Title Header
        Component title = getMessage();
        if (title != null && !title.getString().isEmpty()) {
            RenderSystem.enableBlend();
            render.drawString(title, x1 + 8, y1 + (headerHeight - 8) / 2, 0xFFE4E4E7, false);
            // Header divider line
            gg.fill(x1 + 4, y1 + headerHeight, x2 - 4, y1 + headerHeight + 1, 0xFF3F3F46);
        }
    }
}
