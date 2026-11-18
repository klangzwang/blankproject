package geni.withercore.client.gui;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.platform.Window;
import geni.witherapi.client.gui.ITickableWidget;
import geni.withercore.client.gui.animation.GuiAnimationHelper;
import geni.withercore.client.shader.SdfShaderConfig;
import geni.withercore.client.shader.SdfShaderRenderer;
import geni.withercore.common.helper.Ticker;
import geni.withercore.common.menu.WitherAbstractMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;

import java.util.Objects;

/**
 * Gemeinsame abstrakte Basisklasse für alle Screens in WitherUtils/WitherCore.
 * Konsolidiert GuiToolkit, SdfShader, Inflow-Animation und modulares Rendering.
 */
public abstract class WitherAbstractScreen<C extends WitherAbstractMenu> extends AbstractContainerScreen<C> {

    protected final ModularRender modularRender;
    protected final GuiToolkit guiToolKit;
    protected SdfShaderConfig shaderConfig;
    protected boolean renderSdfBackground = true;

    protected float borderRadius = 2.0f;
    protected float borderThickness = 0.4f;

    public static boolean DEBUG_MODE = false;
    public static boolean REDUCE_MOTION = false;

    protected boolean useInflowAnimation = true;
    protected int targetLeftPos = 12;
    protected long openTimeMillis = 0;
    protected static final int INFLOW_DURATION_MS = 420;

    public WitherAbstractScreen(C menu, Inventory playerInventory) {
        this(menu, playerInventory, Component.empty());
    }

    public WitherAbstractScreen(C menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.modularRender = new ModularRender();
        this.guiToolKit = new GuiToolkit();

        this.imageWidth = 176;
        this.imageHeight = 166;

        this.shaderConfig = SdfShaderConfig.defaultScreen(this.imageWidth, this.imageHeight);
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

    protected void handleButtonPress(int id) {
        Objects.requireNonNull(this.getMinecraft().gameMode).handleInventoryButtonClick(getMenu().containerId, id);
    }

    public void sendQuickTransferNearby() {
        if (getMenu() instanceof WitherAbstractMenu menu) {
            geni.withercore.client.sound.WitherGuiSounds.playTransfer();
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                    new geni.withercore.common.network.packet.ServerboundMenuActionPacket(
                            menu.containerId,
                            geni.withercore.common.network.packet.ServerboundMenuActionPacket.ActionType.QUICK_TRANSFER_NEARBY,
                            0
                    )
            );
        }
    }

    public void sendWorldDrop(int slotIndex) {
        if (getMenu() instanceof WitherAbstractMenu menu) {
            geni.withercore.client.sound.WitherGuiSounds.playDrop();
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                    new geni.withercore.common.network.packet.ServerboundMenuActionPacket(
                            menu.containerId,
                            geni.withercore.common.network.packet.ServerboundMenuActionPacket.ActionType.WORLD_DROP,
                            slotIndex
                    )
            );
        }
    }

    public void sendTrash(int slotIndex) {
        if (getMenu() instanceof WitherAbstractMenu menu) {
            geni.withercore.client.sound.WitherGuiSounds.playTrash();
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                    new geni.withercore.common.network.packet.ServerboundMenuActionPacket(
                            menu.containerId,
                            geni.withercore.common.network.packet.ServerboundMenuActionPacket.ActionType.TRASH,
                            slotIndex
                    )
            );
        }
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

        modularRender.onRenderAtmosphere(gg, animProgress);

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

    @Override
    protected void renderTooltip(GuiGraphics gg, int mouseX, int mouseY) {
        super.renderTooltip(gg, mouseX, mouseY);

        if (this.hoveredSlot instanceof geni.withercore.common.menu.blockentity.slot.WitherMachineSlot machineSlot) {
            if (machineSlot.isLocked()) {
                Component reason = machineSlot.getLockedReason();
                if (reason == null) {
                    reason = Component.translatable("gui.witherutils.slot_locked");
                }
                gg.renderTooltip(this.font, reason, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderSlot(GuiGraphics gg, net.minecraft.world.inventory.Slot slot) {
        if (!slot.isActive()) {
            return;
        }

        if (slot instanceof geni.withercore.common.menu.blockentity.slot.WitherMachineSlot machineSlot) {
            if (machineSlot.isLocked()) {
                WitherSlotDecorator.renderSlotLocked(gg, slot.x, slot.y);
            }
        }

        if (slot instanceof geni.withercore.common.menu.blockentity.slot.WitherMachineSlotGhost ghostSlot && !slot.hasItem()) {
            long gameTime = minecraft != null && minecraft.level != null ? minecraft.level.getGameTime() : 0;
            net.minecraft.world.item.ItemStack preview = ghostSlot.getActivePreviewStack(gameTime);
            if (!preview.isEmpty()) {
                gg.renderFakeItem(preview, slot.x, slot.y);
                WitherSlotDecorator.renderSlotGhost(gg, slot.x, slot.y);
            }
        }

        super.renderSlot(gg, slot);

        if (DEBUG_MODE) {
            gg.drawString(this.font, String.valueOf(slot.index), slot.x, slot.y, 0xFFFF55, false);
            gg.renderOutline(slot.x, slot.y, 16, 16, 0x88FF5555);
        }
    }

    public float getInflowProgress() {
        if (REDUCE_MOTION || !useInflowAnimation) return 1.0f;
        long elapsed = System.currentTimeMillis() - openTimeMillis;
        return GuiAnimationHelper.clamp(elapsed / (float) INFLOW_DURATION_MS, 0.0f, 1.0f);
    }

    /*
     *
     * MODULAR RENDER
     *
     */
    public class ModularRender {

        public void onRenderAtmosphere(GuiGraphics gg, float animProgress) {
            float eased = GuiAnimationHelper.easeOutCubic(animProgress);
            GuiRender render = GuiRender.convert(gg);

            int slotBoxEnd = leftPos + imageWidth;
            int fadeEnd = Math.max(slotBoxEnd + 60, (int) (width * 0.68f));
            int darkStartColor = (int) (0xE6 * eased) << 24 | 0x08090E;
            int transparentEndColor = 0x0008090E;

            int topBotEnd = Math.max(topPos + 10, (int) (height * 0.68f));

            if (slotBoxEnd > 0) {
                guiToolKit.fill(render, 0, 0, slotBoxEnd * eased, topBotEnd, darkStartColor);
            }
            if (fadeEnd > slotBoxEnd) {
                guiToolKit.gradientFillH(render, slotBoxEnd * eased, 0, fadeEnd * eased, topBotEnd, darkStartColor, transparentEndColor);
            }
        }

        public void onRenderScreen(GuiGraphics gg, float partialTicks, int mouseX, int mouseY) {
            if (renderSdfBackground && shaderConfig != null) {
                SdfShaderRenderer.render(gg, leftPos, topPos, shaderConfig);
            }

            modularRender(gg, GuiRender.convert(gg), guiToolKit, partialTicks, mouseX, mouseY);
        }
    }

    public abstract void modularRender(GuiGraphics gg, GuiRender render, GuiToolkit toolKit, float partialTicks, int mouseX, int mouseY);

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

    public void refreshScreen() {
        Window mw = Minecraft.getInstance().getWindow();
        int i = mw.getGuiScaledWidth();
        int j = mw.getGuiScaledHeight();
        init(Minecraft.getInstance(), i, j);
        renderables.stream().filter(widget -> widget instanceof Tickable).forEach(w -> ((Tickable) w).tick());
    }
}

