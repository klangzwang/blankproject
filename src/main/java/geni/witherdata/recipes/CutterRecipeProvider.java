package geni.witherdata.recipes;

import geni.withercore.init.WUTBlocks;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.level.block.CutterBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CutterRecipeProvider extends RecipeProvider {

    public static List<CutterBlock> CUTTERBLOCKS = new ArrayList<>();

    public CutterRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(packOutput, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput)
    {
        for(var blocks : BuiltInRegistries.BLOCK.entrySet())
        {
            var block = blocks.getValue();
            if(block instanceof CutterBlock blk)
            {
                build(blk.asItem(), Ingredient.of(blk.getInputBlock().asItem()), 0, recipeOutput);
            }
        }

        build(Blocks.ACACIA_PLANKS, Ingredient.of(ItemTags.ACACIA_LOGS), 6, recipeOutput);
        build(WUTBlocks.BRICKSDARK.get().asItem(), Ingredient.of(Blocks.STONE_BRICKS), 1, recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, float damage, RecipeOutput recipeOutput)
    {
        build(output, input, damage, "", recipeOutput);
    }

    protected void build(ItemLike output, Ingredient input, float damage, String suffix, RecipeOutput recipeOutput)
    {
        recipeOutput.accept(
            WitherUtils.loc("cutter/" + BuiltInRegistries.ITEM.getKey(output.asItem()).getPath() + suffix),
            new CutterRecipe(input, output.asItem().getDefaultInstance(), damage),
            null);
    }
}
