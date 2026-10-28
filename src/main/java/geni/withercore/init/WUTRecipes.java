package geni.withercore.init;

import java.util.function.Supplier;

import geni.withercore.recipe.RecipeTypeSerializerPair;
import geni.witherutils.WitherUtils;
import geni.witherdata.recipes.AnvilRecipe;
import geni.witherdata.recipes.CutterRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WUTRecipes {

	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, WitherUtils.MODID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, WitherUtils.MODID);

    public static final RecipeTypeSerializerPair<AnvilRecipe, AnvilRecipe.Serializer> ANVIL = register("anvil", AnvilRecipe.Serializer::new);
    public static final RecipeTypeSerializerPair<CutterRecipe, CutterRecipe.Serializer> CUTTER = register("cutter", CutterRecipe.Serializer::new);

	private static <I extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<I>> registerType(String name)
    {
        return RECIPE_TYPES.register(name, () -> RecipeType.simple(WitherUtils.loc(name)));
    }

	private static <R extends Recipe<?>, S extends RecipeSerializer<? extends R>> RecipeTypeSerializerPair<R, S> register(String name, Supplier<S> serializerFactory)
    {
        var type = RECIPE_TYPES.<RecipeType<R>>register(name, () -> RecipeType.simple(WitherUtils.loc(name)));
        var serializer = RECIPE_SERIALIZERS.register(name, serializerFactory);
        return new RecipeTypeSerializerPair<>(type, serializer);
    }
}
