package geni.withercore.common.menu.blockentity.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Ein spezialisierter Slot für Rezept- und Ergebnis-Vorschauen (Abschnitt 4.2 & 7.2).
 * Verhält sich wie ein GhostSlot, sodass keine echten Items versehentlich erzeugt oder entnommen werden.
 */
public class WitherRecipeSlot extends WitherMachineSlotGhost {

    @Nullable
    private RecipeHolder<?> currentRecipe;

    public WitherRecipeSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public void setRecipe(@Nullable RecipeHolder<?> recipe, ItemStack previewStack) {
        this.currentRecipe = recipe;
        set(previewStack);
    }

    @Nullable
    public RecipeHolder<?> getCurrentRecipe() {
        return currentRecipe;
    }
}

