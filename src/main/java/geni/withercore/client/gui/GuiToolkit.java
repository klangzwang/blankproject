package geni.withercore.client.gui;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import geni.withercore.client.shader.SdfShaderConfig;
import geni.withercore.client.shader.SdfShaderRenderer;
import geni.withercore.common.helper.TickHelper;
import geni.witherutils.WitherUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiToolkit {

    protected Vector2i position;
    protected Vector2i size;

    /*
     *
     * SIZE AND POSITION
     *
     */
    public Vector2i getPosition() {
        return new Vector2i().add(this.position.x, this.position.y);
    }
    public Vector2i getSize() {
        return new Vector2i().add(this.size.x, this.size.y);
    }
    public int leftPos() {
        return this.getPosition().x;
    }
    public int topPos() {
        return this.getPosition().y;
    }
    public int rightPos() {
        return this.getPosition().x + width();
    }
    public int bottomPos() {
        return this.getPosition().y + height();
    }
    public int width() {
        return this.getSize().x;
    }
    public int height() {
        return this.getSize().y;
    }

    /*
     *
     * TEXT
     *
     */
    public void drawScaledText(GuiRender render, String text, float x, float y, float scale, int color, boolean hasShadow, float depth)
    {
        PoseStack matrix = render.pose();
        float yAdd = 4 - (scale * 8) / 2F;
        matrix.pushPose();
        matrix.translate(x, y + yAdd, depth);
        matrix.scale(scale, scale, scale);
        render.drawString(text, 0, 0, color, hasShadow);
        matrix.popPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public void drawScaledText(GuiRender render, String text, float x, float y, float scale, int color, boolean hasShadow, float depth, float blendAnimValue)
    {
        PoseStack matrix = render.pose();
        float yAdd = 4 - (scale * 8) / 2F;
        matrix.pushPose();
        matrix.translate(x, y + yAdd, depth);
        matrix.scale(scale, scale, scale);
        render.drawString(text, 0, 0, color, hasShadow);
        matrix.popPose();
        RenderSystem.setShaderColor(1, 1, 1, blendAnimValue);
    }

    public void drawStraightLine(GuiRender render, float x, float y, float length, float thickness, Color color) {
        fill(render, x, y, x + length, y + thickness, color.getRGB());
    }

    /*
     *
     * SLOTS
     *
     */
    public void renderSlot(GuiGraphics gg, float x, float y, int pixelscale, float renderscale, float depth, boolean shadow)
    {
        String slotType = shadow ? "textures/gui/slot/slot_shadow.png" : "textures/gui/slot/slot_normal.png";

        PoseStack matrix = gg.pose();
        matrix.pushPose();
        matrix.translate(leftPos() + x, topPos() + y, depth);
        matrix.scale(renderscale, renderscale, renderscale);
        gg.blit(WitherUtils.loc(slotType), 0, 0, 0, 0, pixelscale, pixelscale, pixelscale, pixelscale);
        matrix.popPose();
    }

    public void renderSlots(GuiGraphics gg, float x, float y, int xslots, int yslots, int pixelscale, float renderscale, float depth, boolean shadow)
    {
        String slotType = shadow ? "textures/gui/slot/slot_shadow.png" : "textures/gui/slot/slot_normal.png";

        PoseStack matrix = gg.pose();
        matrix.pushPose();
        matrix.translate(leftPos() + x, topPos() + y, depth);
        for(int xrow = 0; xrow < xslots; xrow++) {
            for(int ycolumn = 0; ycolumn < yslots; ycolumn++) {
                matrix.scale(renderscale, renderscale, renderscale);
                gg.blit(WitherUtils.loc(slotType), xrow * pixelscale, ycolumn * pixelscale, 0, 0, pixelscale, pixelscale, pixelscale, pixelscale);
            }
        }
        matrix.popPose();
    }

    /*
     *
     * WINDOW AND PANELS
     *
     */
    public void renderSdfPanel(GuiGraphics gg, float x, float y, float width, float height, SdfShaderConfig config) {
        config.setSize(width, height);
        SdfShaderRenderer.render(gg, leftPos() + x, topPos() + y, config);
    }

    public void renderSdfPanel(GuiGraphics gg, float x, float y, float width, float height) {
        renderSdfPanel(gg, x, y, width, height, SdfShaderConfig.defaultScreen(width, height));
    }

    public void renderSdfCard(GuiGraphics gg, float x, float y, float width, float height) {
        renderSdfPanel(gg, x, y, width, height, SdfShaderConfig.card(width, height));
    }

    public void renderSdfTooltip(GuiGraphics gg, float x, float y, float width, float height) {
        renderSdfPanel(gg, x, y, width, height, SdfShaderConfig.tooltip(width, height));
    }

    public void standardWindow(GuiRender render, GuiGraphics gg, int backgroundColourTop, int backgroundColourBottom, int borderColourTop, int borderColourBottom, boolean noGradientFillV) {

        fill(render, leftPos() + 1, topPos(), rightPos() - 1, topPos() + 1, backgroundColourTop);
        fill(render, leftPos() + 1, bottomPos() - 1, rightPos() - 1, bottomPos(), backgroundColourBottom);
        gradientFillV(render, leftPos(), topPos() + 1, leftPos() + 1, bottomPos() - 1, backgroundColourTop, backgroundColourBottom);
        gradientFillV(render, rightPos() - 1, topPos() + 1, rightPos(), bottomPos() - 1, backgroundColourTop, backgroundColourBottom);

        if (noGradientFillV) {
            gradientFillV(render, leftPos() + 1, topPos() + 1, rightPos() - 1, bottomPos() - 1, backgroundColourTop, backgroundColourBottom);
        }

        gradientFillV(render, leftPos() + 1, topPos() + 1, leftPos() + 2, bottomPos() - 1, borderColourTop, borderColourBottom);
        gradientFillV(render, rightPos() - 2, topPos() + 1, rightPos() - 1, bottomPos() - 1, borderColourTop, borderColourBottom);
        fill(render, leftPos() + 2, topPos() + 1, rightPos() - 2, topPos() + 2, borderColourTop);
        fill(render, leftPos() + 2, bottomPos() - 2, rightPos() - 2, bottomPos() - 1, borderColourBottom);

        gg.blit(WitherUtils.loc("textures/gui/slot/slot_normal.png"), (int) (leftPos() - 18), (int) (topPos() - 18), 0, 0, 18, 18, 18, 18);
    }

    /*
     *
     * WIDGETS
     *
     */
    public void renderWidgetEnergy(Screen screen, AbstractContainerMenu menu, GuiGraphics gg, int x, int y, int width, int height, IEnergyStorage storage)
    {
        if (storage.getMaxEnergyStored() <= 0)
        {
            renderToolTip(screen, menu, gg, leftPos() + 8, topPos() + 23 + height, storage);
            renderBank(gg);
            return;
        }

        float filledVolume = storage.getEnergyStored() / (float) storage.getMaxEnergyStored();

        gg.blit(WitherUtils.loc("textures/gui/energy_bar.png"), leftPos() + x, topPos() + y, 16, 0, width, height, 32, height);
        float pct = Math.min(filledVolume, 1.0F);
        gg.blit(WitherUtils.loc("textures/gui/energy_bar.png"), leftPos() + x, topPos() + y, 0, 0, width, height - (int) (height * pct), 32, height);

        renderToolTip(screen, menu, gg, leftPos() + 8, topPos() + 23 + height, storage);
    }

    public void renderBank(GuiGraphics gg)
    {
        gg.pose().pushPose();
        gg.blit(WitherUtils.loc("textures/gui/energy_bar_locked.png"), leftPos(), topPos(), 0, 0, 16, 40, 16, 40);
        gg.pose().popPose();

        gg.pose().pushPose();
        float time = TickHelper.getClientTickCount();
        double offset = Math.sin(time * 4.0D / 12.0D) / 10.0D;

        gg.pose().translate(0.0D, 0.0D + offset * 45, 0.0D);
        gg.blit(WitherUtils.loc("textures/item/soulbank/soulbank.png"), leftPos(), topPos() + 36, 0, 0, 16, 16, 16, 16);
        gg.pose().popPose();
    }

    public void renderToolTip(Screen screen, AbstractContainerMenu menu, GuiGraphics gg, int mouseX, int mouseY, IEnergyStorage storage)
    {
        if(screen.isMouseOver(mouseX, mouseY))
        {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable(ChatFormatting.AQUA + "BankSlot Info"));
            list.add(Component.translatable(ChatFormatting.WHITE + "SoulBank not found"));
            gg.renderComponentTooltip(screen.getMinecraft().font, list, mouseX, mouseY);
        }
    }

    /*
     *
     * MODULAR GRADIENTS (CodeChickenLib GuiRender)
     *
     */
    public void fill(GuiRender render, double xMin, double yMin, double xMax, double yMax, int colour) {
        if (xMax < xMin) {
            double min = xMax;
            xMax = xMin;
            xMin = min;
        }
        if (yMax < yMin) {
            double min = yMax;
            yMax = yMin;
            yMin = min;
        }

        Matrix4f mat = render.pose().last().pose();
        VertexConsumer buffer = render.buffers().getBuffer(GuiRender.SOLID);
        buffer.addVertex(mat, (float) xMax, (float) yMax, 0).setColor(colour);
        buffer.addVertex(mat, (float) xMax, (float) yMin, 0).setColor(colour);
        buffer.addVertex(mat, (float) xMin, (float) yMin, 0).setColor(colour);
        buffer.addVertex(mat, (float) xMin, (float) yMax, 0).setColor(colour);
        render.flush();
    }

    public void gradientFillV(GuiRender render, double xMin, double yMin, double xMax, double yMax, int topColour, int bottomColour) {
        VertexConsumer buffer = render.buffers().getBuffer(GuiRender.SOLID);
        float sA = a(topColour);
        float sR = r(topColour);
        float sG = g(topColour);
        float sB = b(topColour);
        float eA = a(bottomColour);
        float eR = r(bottomColour);
        float eG = g(bottomColour);
        float eB = b(bottomColour);
        Matrix4f mat = render.pose().last().pose();
        buffer.addVertex(mat, (float) xMax, (float) yMax, 0).setColor(eR, eG, eB, eA);
        buffer.addVertex(mat, (float) xMax, (float) yMin, 0).setColor(sR, sG, sB, sA);
        buffer.addVertex(mat, (float) xMin, (float) yMin, 0).setColor(sR, sG, sB, sA);
        buffer.addVertex(mat, (float) xMin, (float) yMax, 0).setColor(eR, eG, eB, eA);
        render.flush();
    }

    /**
     * Renders a smooth horizontal gradient using CodeChickenLib GuiRender
     */
    public void gradientFillH(GuiRender render, double xMin, double yMin, double xMax, double yMax, int leftColour, int rightColour) {
        VertexConsumer buffer = render.buffers().getBuffer(GuiRender.SOLID);
        float lA = a(leftColour);
        float lR = r(leftColour);
        float lG = g(leftColour);
        float lB = b(leftColour);
        float rA = a(rightColour);
        float rR = r(rightColour);
        float rG = g(rightColour);
        float rB = b(rightColour);
        Matrix4f mat = render.pose().last().pose();
        buffer.addVertex(mat, (float) xMax, (float) yMax, 0).setColor(rR, rG, rB, rA);
        buffer.addVertex(mat, (float) xMax, (float) yMin, 0).setColor(rR, rG, rB, rA);
        buffer.addVertex(mat, (float) xMin, (float) yMin, 0).setColor(lR, lG, lB, lA);
        buffer.addVertex(mat, (float) xMin, (float) yMax, 0).setColor(lR, lG, lB, lA);
        render.flush();
    }

    private static float r(int argb) {
        return (argb >> 16 & 255) / 255F;
    }

    private static float g(int argb) {
        return (argb >> 8 & 255) / 255F;
    }

    private static float b(int argb) {
        return (argb & 255) / 255F;
    }

    private static float a(int argb) {
        return (argb >>> 24) / 255F;
    }

    /*
     *
     * PERIMETER & BORDER HELPERS
     *
     */
    public void drawBoxBorder(GuiGraphics gg, int x, int y, int width, int height, int strokeWidth, int color) {
        int s = Math.max(1, strokeWidth);
        gg.fill(x, y, x + width, y + s, color);                      // Top
        gg.fill(x + width - s, y, x + width, y + height, color);      // Right
        gg.fill(x, y + height - s, x + width, y + height, color);      // Bottom
        gg.fill(x, y, x + s, y + height, color);                      // Left
    }

    public void drawBorderProgress(GuiGraphics gg, int x, int y, int width, int height, float progress, int strokeWidth, int color, int trackColor) {
        int s = Math.max(1, strokeWidth);
        // Track
        if ((trackColor >>> 24) > 0) {
            drawBoxBorder(gg, x, y, width, height, s, trackColor);
        }

        if (progress <= 0.0f) return;

        float clamped = Math.max(0.0f, Math.min(1.0f, progress));
        float perimeter = 2.0f * (width + height);
        float activeLen = clamped * perimeter;

        // Top
        float topL = Math.min(activeLen, (float) width);
        if (topL > 0) {
            gg.fill(x, y, x + (int) Math.ceil(topL), y + s, color);
        }

        // Right
        if (activeLen > width) {
            float rightL = Math.min(activeLen - width, (float) height);
            gg.fill(x + width - s, y, x + width, y + (int) Math.ceil(rightL), color);
        }

        // Bottom (right to left)
        if (activeLen > width + height) {
            float bottomL = Math.min(activeLen - (width + height), (float) width);
            int endX = x + width;
            int startX = endX - (int) Math.ceil(bottomL);
            gg.fill(startX, y + height - s, endX, y + height, color);
        }

        // Left (bottom to top)
        if (activeLen > 2 * width + height) {
            float leftL = Math.min(activeLen - (2 * width + height), (float) height);
            int endY = y + height;
            int startY = endY - (int) Math.ceil(leftL);
            gg.fill(x, startY, x + s, endY, color);
        }
    }
}

