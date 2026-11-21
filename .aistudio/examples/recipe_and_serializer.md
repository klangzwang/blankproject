## Rezept- & Serializer-Klassen (`AlloyRecipe.java`)

> \[!IMPORTANT\]
> Rezepte werden datengetrieben über JSON geladen und benötigen einen passenden `RecipeSerializer` sowie `Codec`.

* **Zweck**: Definition von benutzerdefinierten Crafting-Mechaniken (z. B. Maschinen-Rezepte mit Energieverbrauch und Verarbeitungszeit).

* **Besonderheiten**: Nutzt `MapCodec` für das Einlesen der JSON-Dateien und `StreamCodec` für die Synchronisation mit dem Client (z. B. für JEI).

* **Architektur**: Implementiert das `Recipe`-Interface inklusive `matches()` für die Überprüfung der Eingangs-Slots.

```java
public record AlloyRecipe(Ingredient inputA, Ingredient inputB, ItemStack output, int energyCost) implements Recipe<RecipeInput> {
    @Override
    public boolean matches(RecipeInput input, Level level) {
        return inputA.test(input.getItem(0)) && inputB.test(input.getItem(1));
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return WUTRecipes.ALLOY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return WUTRecipes.ALLOY_TYPE.get();
    }
}
```
