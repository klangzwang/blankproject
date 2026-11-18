package geni.withercore.common.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Zentraler Helfer für Rezeptabfragen und Zutat-Validierung (Abschnitt 7.1).
 * Greift stets auf den aktuellen RecipeManager des Levels zu (Reload-resistent).
 */
public class RecipeQueryHelper {

    /**
     * Findet alle Rezepte eines Typs, die für das gegebene Eingabe-Item gültig sind.
     */
    public static <I extends RecipeInput, R extends Recipe<I>> List<RecipeHolder<R>> getRecipesForInput(
            Level level, RecipeType<R> type, I input) {
        if (level == null) return List.of();
        return level.getRecipeManager().getRecipesFor(type, input, level);
    }

    /**
     * Findet das erste passende Rezept für die Eingabe.
     */
    public static <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> findFirstMatching(
            Level level, RecipeType<R> type, I input) {
        if (level == null) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(type, input, level);
    }

    /**
     * Liefert alle registrierten Rezepte des gegebenen Typs.
     */
    public static <I extends RecipeInput, R extends Recipe<I>> List<RecipeHolder<R>> getAllRecipes(
            Level level, RecipeType<R> type) {
        if (level == null) return List.of();
        return level.getRecipeManager().getAllRecipesFor(type);
    }

    /**
     * Prüft, ob der Spieler alle Zutaten für ein Rezept im Inventar hat.
     */
    public static boolean hasAllIngredients(Recipe<?> recipe, Inventory playerInv) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        if (ingredients.isEmpty()) return true;

        List<ItemStack> invCopy = new ArrayList<>();
        for (int i = 0; i < playerInv.getContainerSize(); i++) {
            ItemStack stack = playerInv.getItem(i);
            if (!stack.isEmpty()) {
                invCopy.add(stack.copy());
            }
        }

        for (Ingredient ing : ingredients) {
            if (ing.isEmpty()) continue;
            boolean matched = false;
            for (ItemStack item : invCopy) {
                if (!item.isEmpty() && ing.test(item)) {
                    item.shrink(1);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return true;
    }

    /**
     * Ermittelt die Zutaten, die dem Spieler im Inventar fehlen.
     */
    public static List<Ingredient> getMissingIngredients(Recipe<?> recipe, Inventory playerInv) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        List<Ingredient> missing = new ArrayList<>();
        if (ingredients.isEmpty()) return missing;

        List<ItemStack> invCopy = new ArrayList<>();
        for (int i = 0; i < playerInv.getContainerSize(); i++) {
            ItemStack stack = playerInv.getItem(i);
            if (!stack.isEmpty()) {
                invCopy.add(stack.copy());
            }
        }

        for (Ingredient ing : ingredients) {
            if (ing.isEmpty()) continue;
            boolean matched = false;
            for (ItemStack item : invCopy) {
                if (!item.isEmpty() && ing.test(item)) {
                    item.shrink(1);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                missing.add(ing);
            }
        }
        return missing;
    }
}

