package geni.withercore.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import geni.witherutils.WitherUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class WitherSlotDecorator {

    public static final ResourceLocation SLOT_NORMAL = WitherUtils.loc("textures/gui/slot/slot_normal.png");
    public static final ResourceLocation SLOT_SHADOW = WitherUtils.loc("textures/gui/slot/slot_shadow.png");
    public static final ResourceLocation SLOT_BORDER_IN = WitherUtils.loc("textures/gui/slot/slot_border_in.png");
    public static final ResourceLocation SLOT_BORDER_OUT = WitherUtils.loc("textures/gui/slot/slot_border_out.png");
    public static final ResourceLocation SLOT_LOCKED = WitherUtils.loc("textures/gui/slot/slot_locked.png");
    public static final ResourceLocation SLOT_BACKGROUND = WitherUtils.loc("textures/gui/slot/background.png");

    public static final int SLOT_SIZE = 18;

    public static void renderSlot(GuiGraphics gg, int x, int y) {
        renderSlot(gg, x, y, false);
    }

    public static void renderSlot(GuiGraphics gg, int x, int y, boolean shadow) {
        RenderSystem.enableBlend();
        ResourceLocation tex = shadow ? SLOT_SHADOW : SLOT_NORMAL;
        gg.blit(tex, x - 1, y - 1, 0, 0, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE);
    }

    public static void renderSlotLocked(GuiGraphics gg, int x, int y) {
        RenderSystem.enableBlend();
        gg.blit(SLOT_LOCKED, x - 1, y - 1, 0, 0, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE);
    }

    public static void renderSlotGrid(GuiGraphics gg, int startX, int startY, int columns, int rows) {
        renderSlotGrid(gg, startX, startY, columns, rows, false);
    }

    public static void renderSlotGrid(GuiGraphics gg, int startX, int startY, int columns, int rows, boolean shadow) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                renderSlot(gg, startX + c * SLOT_SIZE, startY + r * SLOT_SIZE, shadow);
            }
        }
    }

    public static void renderPlayerInventory(GuiGraphics gg, int startX, int startY) {
        // Main Inventory: 9 columns, 3 rows
        renderSlotGrid(gg, startX, startY, 9, 3, false);
        // Hotbar: 9 columns, 1 row (standard spacing 4px gap)
        renderSlotGrid(gg, startX, startY + 3 * SLOT_SIZE + 4, 9, 1, false);
    }

    /**
     * Renders 1 single row of 9 hotbar slots at the specified position
     */
    public static void renderPlayerHotbar(GuiGraphics gg, int startX, int startY) {
        renderSlotGrid(gg, startX, startY, 9, 1, false);
    }
}
