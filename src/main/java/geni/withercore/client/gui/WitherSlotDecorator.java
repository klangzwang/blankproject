package geni.withercore.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import geni.witherutils.WitherUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class WitherSlotDecorator {

    public static final int SLOT_SIZE = 18;

    // Farbdefinitionen im ARGB-Format (Hexadezimal: AARRGGBB)
    private static final int COLOR_BACKGROUND = 0xFF8B8B8B; // Dunkles Grau für das innere Feld
    private static final int COLOR_SHADOW     = 0xFF373737; // Sehr dunkles Grau / Schwarz (oben & links)
    private static final int COLOR_BORDER_IN  = 0xFFFFFFFF; // Weiß / Hell (unten & rechts)
    private static final int COLOR_LOCKED     = 0xFF550000; // Rötlicher Ton für gesperrte Slots
    private static final int COLOR_GHOST      = 0x664A4E5A; // Dezenter Halbtransparenter Ton für Ghost-Slots
    private static final int COLOR_GHOST_BORDER = 0x996C7284;

    public static void renderSlot(GuiGraphics gg, int x, int y) {
        renderSlot(gg, x, y, false);
    }

    public static void renderSlot(GuiGraphics gg, int x, int y, boolean shadow) {
        int px = x - 1;
        int py = y - 1;

        // 1. Äußerer Rahmen / Schatten (Vanilla-Minecraft-Stil: Oben/Links dunkel, Unten/Rechts hell)
        // Oben & Links (Schatten)
        gg.fill(px, py, px + SLOT_SIZE, py + 1, COLOR_SHADOW);     // Oberster Rand
        gg.fill(px, py + 1, px + 1, py + SLOT_SIZE, COLOR_SHADOW); // Linker Rand

        // Unten & Rechts (Lichteinfall / Border)
        gg.fill(px, py + SLOT_SIZE - 1, px + SLOT_SIZE, py + SLOT_SIZE, COLOR_BORDER_IN); // Unterster Rand
        gg.fill(px + SLOT_SIZE - 1, py, px + SLOT_SIZE, py + SLOT_SIZE, COLOR_BORDER_IN); // Rechter Rand

        // 2. Innerer Hintergrund des Slots
        gg.fill(px + 1, py + 1, px + SLOT_SIZE - 1, py + SLOT_SIZE - 1,
                shadow ? 0xFF222222 : COLOR_BACKGROUND);
    }

    public static void renderSlotLocked(GuiGraphics gg, int x, int y) {
        int px = x - 1;
        int py = y - 1;

        // Standard-Rahmen zeichnen
        gg.fill(px, py, px + SLOT_SIZE, py + 1, COLOR_SHADOW);
        gg.fill(px, py + 1, px + 1, py + SLOT_SIZE, COLOR_SHADOW);
        gg.fill(px, py + SLOT_SIZE - 1, px + SLOT_SIZE, py + SLOT_SIZE, COLOR_BORDER_IN);
        gg.fill(px + SLOT_SIZE - 1, py, px + SLOT_SIZE, py + SLOT_SIZE, COLOR_BORDER_IN);

        // Spezieller Hintergrund für gesperrte Slots
        gg.fill(px + 1, py + 1, px + SLOT_SIZE - 1, py + SLOT_SIZE - 1, COLOR_LOCKED);
    }

    public static void renderSlotGhost(GuiGraphics gg, int x, int y) {
        int px = x - 1;
        int py = y - 1;

        // Geisterhafter Rahmen
        gg.fill(px, py, px + SLOT_SIZE, py + 1, COLOR_GHOST_BORDER);
        gg.fill(px, py + 1, px + 1, py + SLOT_SIZE, COLOR_GHOST_BORDER);
        gg.fill(px, py + SLOT_SIZE - 1, px + SLOT_SIZE, py + SLOT_SIZE, COLOR_GHOST_BORDER);
        gg.fill(px + SLOT_SIZE - 1, py, px + SLOT_SIZE, py + SLOT_SIZE, COLOR_GHOST_BORDER);

        // Geisterhafter Hintergrund
        gg.fill(px + 1, py + 1, px + SLOT_SIZE - 1, py + SLOT_SIZE - 1, COLOR_GHOST);
    }

    public static void renderSlotHover(GuiGraphics gg, int x, int y) {
        int px = x - 1;
        int py = y - 1;
        // Sanftes weißes Aufhellen bei Mouse-Hover
        gg.fill(px + 1, py + 1, px + SLOT_SIZE - 1, py + SLOT_SIZE - 1, 0x33FFFFFF);
    }

    public static void renderSlotFocus(GuiGraphics gg, int x, int y, int borderColor) {
        int px = x - 1;
        int py = y - 1;
        gg.fill(px, py, px + SLOT_SIZE, py + 1, borderColor);
        gg.fill(px, py + 1, px + 1, py + SLOT_SIZE, borderColor);
        gg.fill(px, py + SLOT_SIZE - 1, px + SLOT_SIZE, py + SLOT_SIZE, borderColor);
        gg.fill(px + SLOT_SIZE - 1, py, px + SLOT_SIZE, py + SLOT_SIZE, borderColor);
    }

    public static void renderSlotDisabled(GuiGraphics gg, int x, int y) {
        int px = x - 1;
        int py = y - 1;
        // Abdunklung für deaktivierte Slots
        gg.fill(px + 1, py + 1, px + SLOT_SIZE - 1, py + SLOT_SIZE - 1, 0x88000000);
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

    public static void renderPlayerHotbar(GuiGraphics gg, int startX, int startY) {
        renderSlotGrid(gg, startX, startY, 9, 1, false);
    }
}
