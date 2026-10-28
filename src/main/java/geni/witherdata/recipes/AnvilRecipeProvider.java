package geni.witherdata.recipes;

import java.util.concurrent.CompletableFuture;

import geni.witherutils.WitherUtils;
import geni.withercore.init.WUTItems;
import geni.withercore.init.WUTSounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class AnvilRecipeProvider extends RecipeProvider {

    public AnvilRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput finishedRecipeConsumer)
    {
    	build(Ingredient.of(Items.BONE), Items.BONE_MEAL, 3, SoundEvents.BONE_BLOCK_BREAK, 3, 2, finishedRecipeConsumer);
    	build(Ingredient.of(Items.IRON_INGOT), WUTItems.WITHERSTEEL_NUGGET, 3, WUTSounds.HAMMERHIT.get(), 4, 0, finishedRecipeConsumer);
    	build(Ingredient.of(WUTItems.WITHERSTEEL_INGOT), WUTItems.WITHERSTEEL_PLATE, 6, WUTSounds.HAMMERHIT.get(), 1, 0, finishedRecipeConsumer);
    	build(Ingredient.of(WUTItems.WITHERSTEEL_PLATE), WUTItems.WITHERSTEEL_GEAR, 6, WUTSounds.HAMMERHIT.get(), 1, 0, finishedRecipeConsumer);
    	build(Ingredient.of(WUTItems.WITHERSTEEL_NUGGET), WUTItems.WITHERSTEEL_ROD, 3, WUTSounds.HAMMERHIT.get(), 1, 0, finishedRecipeConsumer);
    	build(Ingredient.of(Items.BLAZE_ROD), Items.BLAZE_POWDER, 2, WUTSounds.HAMMERHIT.get(), 2, 1, finishedRecipeConsumer);
    	build(Ingredient.of(Items.GLASS_BOTTLE), Items.GLASS, 1, WUTSounds.HAMMERHIT.get(), 2, 1, finishedRecipeConsumer);
    	build(Ingredient.of(Items.CAULDRON), Items.IRON_INGOT, 9, WUTSounds.HAMMERHIT.get(), 6, 1, finishedRecipeConsumer);
    	build(Ingredient.of(ItemTags.WOOL), Items.STRING, 1, WUTSounds.HAMMERHIT.get(), 4, 1, finishedRecipeConsumer);
    	build(Ingredient.of(Items.NETHERITE_INGOT), Items.NETHERITE_SCRAP, 9, WUTSounds.HAMMERHIT.get(), 3, 1, finishedRecipeConsumer);
    	build(Ingredient.of(Items.FURNACE), Items.COBBLESTONE, 3, WUTSounds.HAMMERHIT.get(), 8, 1, finishedRecipeConsumer);
    	build(Ingredient.of(Items.CHEST), Items.OAK_PLANKS, 3, WUTSounds.HAMMERHIT.get(), 8, 1, finishedRecipeConsumer);
    	build(Ingredient.of(Items.LADDER), Items.STICK, 2, WUTSounds.HAMMERHIT.get(), 7, 1, finishedRecipeConsumer);
    	build(Ingredient.of(Items.GLOWSTONE), Items.GLOWSTONE_DUST, 1, WUTSounds.HAMMERHIT.get(), 4, 0, finishedRecipeConsumer);
    	build(Ingredient.of(Items.CLAY), Items.CLAY_BALL, 1, WUTSounds.HAMMERHIT.get(), 4, 0, finishedRecipeConsumer);
    	build(Ingredient.of(Items.STONE_BRICKS), Items.STONE, 3, WUTSounds.HAMMERHIT.get(), 4, 1, finishedRecipeConsumer);
    }

    protected void build(Ingredient input, ItemLike output, int hitCounter, SoundEvent sound, int count, int bonus, RecipeOutput recipeOutput)
    {
        build(input, output, hitCounter, sound, count, bonus, "", recipeOutput);
    }

    protected void build(Ingredient input, ItemLike output, int hitCounter, SoundEvent sound, int count, int bonus, String suffix, RecipeOutput recipeOutput)
    {
        recipeOutput.accept(
            WitherUtils.loc("anvil/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath() + suffix),
            new AnvilRecipe(input, output.asItem().getDefaultInstance(), hitCounter, sound, count, bonus),
            null);
    }
}
