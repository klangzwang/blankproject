package geni.withercore.client.gui;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.platform.Window;
import geni.witherapi.client.gui.ITickableWidget;
import geni.withercore.client.gui.animation.GuiAnimationHelper;
import geni.withercore.client.shader.SdfShaderConfig;
import geni.withercore.client.shader.SdfShaderRenderer;
import geni.withercore.common.helper.Ticker;
import geni.withercore.common.menu.item.WitherItemMenu;
import geni.withercore.common.world.item.WitherItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;

import java.util.Objects;

public abstract class WitherAbstractItemScreen<C extends WitherItemMenu<T>, T extends WitherItem>
        extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<C> {

    public final T itemInstance;
    protected final ItemStack itemStack; // Alternativ zum BlockEntity: Das Item, welches das Menü geöffnet hat

    private final ModularRender modularRender;
    private final GuiToolkit guiToolKit;
    protected SdfShaderConfig shaderConfig;
    protected boolean renderSdfBackground = true;

    protected float borderRadius = 4.0f;
    protected float borderThickness = 0.8f;

    // INFLOW ANIMATION & GRADIENT CONFIGURATION
    protected boolean useInflowAnimation = true;
    protected int targetLeftPos = 12; // 12px Abstand zum linken Rand
    protected long openTimeMillis = 0;
    protected static final int INFLOW_DURATION_MS = 420;

    public WitherAbstractItemScreen(C menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.itemInstance = menu.getItemInstance();

        this.itemStack = playerInventory.getSelected();

        this.modularRender = new ModularRender();
        this.guiToolKit = new GuiToolkit();

        this.imageWidth = 176;
        this.imageHeight = 166;

        this.shaderConfig = SdfShaderConfig.defaultScreen(this.imageWidth, this.imageHeight);
        this.shaderConfig.setRadius(borderRadius);
        this.shaderConfig.setBorderThickness(borderThickness);
    }

    @Override
    protected void init() {
        super.init();

        this.openTimeMillis = System.currentTimeMillis();

        if (useInflowAnimation) {
            this.leftPos = targetLeftPos;
        } else {
            this.leftPos = (this.width - this.imageWidth) / 2;
        }
        this.topPos = (this.height - this.imageHeight) / 2;

        this.guiToolKit.position = new Vector2i(this.leftPos, this.topPos);
        this.guiToolKit.size = new Vector2i(this.imageWidth, this.imageHeight);
        if (this.shaderConfig != null) {
            this.shaderConfig.setSize(this.imageWidth, this.imageHeight);
            this.shaderConfig.setRadius(borderRadius);
            this.shaderConfig.setBorderThickness(borderThickness);
        }
    }

    public SdfShaderConfig getShaderConfig() {
        return shaderConfig;
    }

    public void setShaderConfig(SdfShaderConfig shaderConfig) {
        this.shaderConfig = shaderConfig;
    }

    public boolean isRenderSdfBackground() {
        return renderSdfBackground;
    }

    public void setRenderSdfBackground(boolean renderSdfBackground) {
        this.renderSdfBackground = renderSdfBackground;
    }

    public T getItem() {
        return itemInstance;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    protected void handleButtonPress(int id) {
        Objects.requireNonNull(this.getMinecraft().gameMode).handleInventoryButtonClick(getMenu().containerId, id);
    }

    /*
     *
     * RENDERING
     *
     */
    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        float animProgress = getInflowProgress();
        float eased = GuiAnimationHelper.easeOutCubic(animProgress);
        float currentOffset = useInflowAnimation ? GuiAnimationHelper.lerp(-(leftPos + imageWidth + 60), 0, eased) : 0;

        // 1. Atmosphärischen Gradienten von links rendern
        modularRender.onRenderAtmosphere(gg, animProgress);

        // 2. Screen mit flüssiger Inflow-Transformation von links hereingleiten lassen
        gg.pose().pushPose();
        if (useInflowAnimation && currentOffset != 0) {
            gg.pose().translate(currentOffset, 0, 0);
        }

        renderBackground(gg, mouseX, mouseY, partialTick);
        super.render(gg, mouseX, mouseY, partialTick);
        renderTooltip(gg, mouseX, mouseY);

        gg.pose().popPose();
    }

    @Override
    public void renderBackground(GuiGraphics gg, int mouseX, int mouseY, float partialTick) {
        modularRender.onRenderScreen(gg, partialTick, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    public float getInflowProgress() {
        if (!useInflowAnimation)
            return 1.0f;
        long elapsed = System.currentTimeMillis() - openTimeMillis;
        return GuiAnimationHelper.clamp(elapsed / (float) INFLOW_DURATION_MS, 0.0f, 1.0f);
    }

    /*
     *
     * MODULAR RENDER
     *
     */
    public class ModularRender {

        void onRenderAtmosphere(GuiGraphics gg, float animProgress) {
            float eased = GuiAnimationHelper.easeOutCubic(animProgress);
            GuiRender render = GuiRender.convert(gg);

            int slotBoxEnd = leftPos + imageWidth;
            int fadeEnd = Math.max(slotBoxEnd + 60, (int) (width * 0.68f));

            int darkStartColor = (int) (0xE6 * eased) << 24 | 0x08090E;
            int transparentEndColor = 0x0008090E;

            if (slotBoxEnd > 0) {
                guiToolKit.fill(render, 0, 0, slotBoxEnd, height, darkStartColor);
            }

            if (fadeEnd > slotBoxEnd) {
                guiToolKit.gradientFillH(render, slotBoxEnd, 0, fadeEnd, height, darkStartColor, transparentEndColor);
            }
        }

        void onRenderScreen(GuiGraphics gg, float partialTicks, int mouseX, int mouseY) {
            if (renderSdfBackground && shaderConfig != null) {
                SdfShaderRenderer.render(gg, leftPos, topPos, shaderConfig);
            }

            modularRender(gg, GuiRender.convert(gg), guiToolKit, partialTicks, mouseX, mouseY);
        }
    }

    public abstract void modularRender(GuiGraphics gg, GuiRender render, GuiToolkit toolKit, float partialTicks,
            int mouseX, int mouseY);

    @Override
    public void containerTick() {
        super.containerTick();

        for (Renderable renderable : renderables) {
            if (renderable instanceof ITickableWidget t) {
                t.tickWidget();
            }
        }
    }

    public void modularTick(Ticker ticker) {
        if (ticker.ended()) {
            assert this.minecraft != null;
            assert this.minecraft.player != null;
            this.minecraft.player.closeContainer();
            this.minecraft.popGuiLayer();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void removed() {
        super.removed();
    }

    void refreshScreen() {
        Window mw = Minecraft.getInstance().getWindow();
        int i = mw.getGuiScaledWidth();
        int j = mw.getGuiScaledHeight();
        init(Minecraft.getInstance(), i, j);
        renderables.stream().filter(widget -> widget instanceof Tickable).forEach(w -> ((Tickable) w).tick());
    }

    @Override
    public void onClose() {
        super.onClose();
    }
}