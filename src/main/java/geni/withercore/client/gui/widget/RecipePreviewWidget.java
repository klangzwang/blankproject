package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import geni.withercore.client.gui.WitherSlotDecorator;
import geni.withercore.common.recipe.RecipeQueryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Modulares Widget für Rezeptvorschauen (Abschnitt 7.2).
 * Zeigt Input-Zutaten, Output und hebt fehlende Zutaten visuell hervor.
 */
public class RecipePreviewWidget extends AbstractWidget {

    @Nullable
    private RecipeHolder<?> activeRecipe;
    private final Inventory playerInventory;

    public RecipePreviewWidget(int x, int y, int width, int height, Inventory playerInventory) {
        super(x, y, width, height, Component.translatable("gui.witherutils.recipe_preview"));
        this.playerInventory = playerInventory;
    }

    public void setRecipe(@Nullable RecipeHolder<?> recipe) {
        this.activeRecipe = recipe;
    }

    @Nullable
    public RecipeHolder<?> getRecipe() {
        return activeRecipe;
    }

    @Override
    public void modularRender(GuiGraphics gg, GuiRender render, float partialTicks, int mouseX, int mouseY) {
        if (!visible || activeRecipe == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Recipe<?> recipe = activeRecipe.value();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        List<Ingredient> missing = RecipeQueryHelper.getMissingIngredients(recipe, playerInventory);

        long gameTime = mc.level.getGameTime();

        int startX = getX();
        int startY = getY();

        // 1. Zutaten-Slots rendern
        for (int i = 0; i < ingredients.size(); i++) {
            int slotX = startX + i * 20;
            int slotY = startY;

            Ingredient ing = ingredients.get(i);
            boolean isMissing = missing.contains(ing);

            if (isMissing) {
                WitherSlotDecorator.renderSlotLocked(gg, slotX, slotY);
            } else {
                WitherSlotDecorator.renderSlot(gg, slotX, slotY);
            }

            ItemStack[] matching = ing.getItems();
            if (matching.length > 0) {
                int cycleIdx = (int) ((gameTime / 30) % matching.length);
                ItemStack displayStack = matching[cycleIdx];
                gg.renderFakeItem(displayStack, slotX, slotY);

                if (isMissing) {
                    // Rotes Warn-Overlay für fehlende Zutaten
                    gg.fill(slotX, slotY, slotX + 16, slotY + 16, 0x44FF0000);
                }
            }
        }

        // 2. Pfeil / Trenner
        int arrowX = startX + ingredients.size() * 20 + 4;
        gg.drawString(mc.font, "->", arrowX, startY + 4, 0xAAAAAA, false);

        // 3. Ergebnis-Slot rendern
        int outX = arrowX + 16;
        WitherSlotDecorator.renderSlot(gg, outX, startY);
        ItemStack result = recipe.getResultItem(mc.level.registryAccess());
        if (!result.isEmpty()) {
            gg.renderFakeItem(result, outX, startY);
            gg.renderItemDecorations(mc.font, result, outX, startY);
        }
    }
}

