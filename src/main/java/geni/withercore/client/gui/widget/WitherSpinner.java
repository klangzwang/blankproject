package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import geni.witherapi.client.gui.ITickableWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

/**
 * Animated rotating loading spinner widget with partialTicks rotation,
 * configurable sizes, color themes, and optional status label.
 */
public class WitherSpinner extends AbstractWidget implements ITickableWidget {

    public enum Variant {
        CYAN(0xFF00E5FF, 0x4000E5FF),
        AMBER(0xFFF59E0B, 0x40F59E0B),
        EMERALD(0xFF10B981, 0x4010B981),
        WHITE(0xFFFFFFFF, 0x40FFFFFF);

        public final int color;
        public final int trackColor;

        Variant(int color, int trackColor) {
            this.color = color;
            this.trackColor = trackColor;
        }
    }

    public enum Size {
        SM(10),
        DEFAULT(14),
        LG(20);

        public final int diameter;

        Size(int diameter) {
            this.diameter = diameter;
        }
    }

    private Variant variant = Variant.CYAN;
    private Size size = Size.DEFAULT;
    private float spinAngle = 0.0f;

    public WitherSpinner(int x, int y) {
        this(x, y, Size.DEFAULT, Variant.CYAN, null);
    }

    public WitherSpinner(int x, int y, Size size, Variant variant, @Nullable Component label) {
        super(x, y, size.diameter, size.diameter, label != null ? label : Component.empty());
        this.size = size;
        this.variant = variant;
        recalculateSize();
    }

    public WitherSpinner setVariant(Variant variant) {
        this.variant = variant;
        return this;
    }

    public WitherSpinner setSize(Size size) {
        this.size = size;
        recalculateSize();
        return this;
    }

    public WitherSpinner setLabel(@Nullable Component label) {
        setMessage(label != null ? label : Component.empty());
        recalculateSize();
        return this;
    }

    private void recalculateSize() {
        int d = size.diameter;
        this.height = d;
        if (!getMessage().getString().isEmpty()) {
            this.width = d + 6 + (getMessage().getString().length() * 6);
        } else {
            this.width = d;
        }
    }

    @Override
    public void tickWidget() {
        this.spinAngle = (this.spinAngle + 18.0f) % 360.0f;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int d = size.diameter;
        float radius = (d / 2.0f) - 1.5f;
        int cx = getX() + (d / 2);
        int cy = getY() + (d / 2);

        // Smooth visual rotation using partialTick
        float currentAngle = (this.spinAngle + (partialTick * 18.0f)) % 360.0f;

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(cx, cy, 0);
        pose.mulPose(Axis.ZP.rotationDegrees(currentAngle));

        // Render 8 fading circular dot nodes around the perimeter
        int nodeCount = 8;
        int baseR = (variant.color >> 16) & 0xFF;
        int baseG = (variant.color >> 8) & 0xFF;
        int baseB = variant.color & 0xFF;

        for (int i = 0; i < nodeCount; i++) {
            double angleRad = (i * 2.0 * Math.PI) / nodeCount;
            float nx = (float) (Math.cos(angleRad) * radius);
            float ny = (float) (Math.sin(angleRad) * radius);

            // Leading node has highest alpha, trailing node fades out
            float alphaFactor = (float) (i + 1) / (float) nodeCount;
            int alpha = (int) (alphaFactor * 255.0f);
            int nodeColor = (alpha << 24) | (baseR << 16) | (baseG << 8) | baseB;

            int dotSize = (size == Size.LG) ? 2 : 1;
            guiGraphics.fill((int) (nx - dotSize), (int) (ny - dotSize), (int) (nx + dotSize + 1), (int) (ny + dotSize + 1), nodeColor);
        }

        pose.popPose();

        // Optional label
        Component label = getMessage();
        if (!label.getString().isEmpty()) {
            GuiRender render = GuiRender.convert(guiGraphics);
            int tx = getX() + d + 4;
            int ty = getY() + (d - 8) / 2;
            guiGraphics.drawString(render.font(), label, tx, ty, 0xFFE4E4E7, false);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
