package geni.witherdata.generator;

import java.util.concurrent.CompletableFuture;

import geni.withercore.init.WUTBlocks;
import geni.withercore.init.WUTItems;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

public class WitherUtilsCraftingRecipes extends RecipeProvider {

    public WitherUtilsCraftingRecipes(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(packOutput, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput recipeConsumer)
    {
    	makeMaterialRecipes(recipeConsumer, WUTItems.WITHERSTEEL_INGOT.get(), WUTItems.WITHERSTEEL_NUGGET.get(), WUTBlocks.WITHERSTEEL_BLOCK.get());

		/*
		 * ARMOR
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.STEELARMOR_HELMET.get())
				.pattern("SSS")
				.pattern("S S")
				.define('S', WUTItems.WITHERSTEEL_INGOT.get())
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.STEELARMOR_CHEST.get())
				.pattern("S S")
				.pattern("SSS")
				.pattern("SSS")
				.define('S', WUTItems.WITHERSTEEL_INGOT.get())
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.STEELARMOR_LEGGINGS.get())
				.pattern("SSS")
				.pattern("S S")
				.pattern("S S")
				.define('S', WUTItems.WITHERSTEEL_INGOT.get())
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.STEELARMOR_BOOTS.get())
				.pattern("S S")
				.pattern("S S")
				.define('S', WUTItems.WITHERSTEEL_INGOT.get())
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);*/

		/*
		 * UPGRADES
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.UPCASE.get())
				.pattern("IDI")
				.pattern("IDI")
				.pattern("GG ")
				.define('I', WUTItems.IRON_PLATE.get())
				.define('D', Items.DIAMOND)
				.define('G', Items.GOLDEN_BOOTS)
				.unlockedBy("has_ironplate", has(WUTItems.IRON_PLATE.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTUpgrades.UPGRADEFEATHER.get())
				.pattern("###")
				.pattern("QWQ")
				.pattern("G G")
				.define('#', WUTItems.FEATHER.get())
				.define('Q', Items.QUARTZ)
				.define('W', WUTItems.UPCASE.get())
				.define('G', Items.GLASS)
				.unlockedBy("has_upcase", has(WUTItems.UPCASE.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTUpgrades.UPGRADEJUMP.get())
				.pattern("###")
				.pattern("EWE")
				.pattern("G G")
				.define('#', Items.PISTON)
				.define('E', Items.EMERALD)
				.define('W', WUTItems.UPCASE.get())
				.define('G', Items.GLASS)
				.unlockedBy("has_upcase", has(WUTItems.UPCASE.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTUpgrades.UPGRADESPEED.get())
				.pattern("###")
				.pattern("CWC")
				.pattern("G G")
				.define('#', Items.SUGAR)
				.define('C', Items.CLOCK)
				.define('W', WUTItems.UPCASE.get())
				.define('G', Items.GLASS)
				.unlockedBy("has_upcase", has(WUTItems.UPCASE.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTUpgrades.UPGRADESQUID.get())
				.pattern("###")
				.pattern("IWI")
				.pattern("G G")
				.define('#', Items.PISTON)
				.define('I', Items.INK_SAC)
				.define('W', WUTItems.UPCASE.get())
				.define('G', Items.GLASS)
				.unlockedBy("has_upcase", has(WUTItems.UPCASE.get()))
				.save(recipeConsumer);*/

		/*
		 * MATERIALS
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.SHOVEL.get())
				.pattern("PIP")
				.pattern("PIP")
				.pattern("PIP")
				.define('P', WUTItems.IRON_PLATE.get())
				.define('I', Tags.Items.INGOTS_IRON)
				.unlockedBy("has_plateiron", has(WUTItems.IRON_PLATE.get()))
				.save(recipeConsumer);*/

/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.FAN.get())
				.pattern("P P")
				.pattern(" I ")
				.pattern("P P")
				.define('P', WUTItems.IRON_PLATE.get())
				.define('I', WUTItems.IRON_GEAR.get())
				.unlockedBy("has_plateiron", has(WUTItems.IRON_PLATE.get()))
				.save(recipeConsumer);*/

/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.BLINK_PLATE.get())
				.pattern("RRR")
				.pattern("RPR")
				.pattern("RRR")
				.define('P', WUTItems.IRON_PLATE.get())
				.define('R', Items.REDSTONE)
				.unlockedBy("has_plateiron", has(WUTItems.IRON_PLATE.get()))
				.save(recipeConsumer);*/

/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.ENDER_PEARL)
				.pattern("EEE")
				.pattern("EEE")
				.pattern("EEE")
				.define('E', WUTItems.ENDERPSHARD.get())
				.unlockedBy("has_epearlshard", has(WUTItems.ENDERPSHARD.get()))
				.save(recipeConsumer);*/

/*		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WUTItems.ENDERPSHARD.get(), 9)
				.requires(Items.ENDER_PEARL)
				.unlockedBy("has_enderpearl", has(Items.ENDER_PEARL))
				.save(recipeConsumer);*/

		/*ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.CARD.get())
				.pattern("GG")
				.pattern("PP")
				.define('G', Items.GOLD_INGOT)
				.define('P', WUTItems.WITHERSTEEL_PLATE.get())
				.unlockedBy("has_plateiron", has(WUTItems.WITHERSTEEL_PLATE.get()))
				.save(recipeConsumer);*/

/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.SPIRAL.get())
				.pattern("D D")
				.pattern(" C ")
				.pattern("D D")
				.define('D', Items.DIAMOND)
				.define('C', WUTBlocks.CASE_BIG.get())
				.unlockedBy("has_case", has(WUTBlocks.CASE_BIG.get()))
				.save(recipeConsumer);*/

		/*ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.ANCHOR.get())
				.pattern("II")
				.pattern("II")
				.define('I', Tags.Items.INGOTS_IRON)
				.unlockedBy("has_iron", has(Items.IRON_INGOT))
				.save(recipeConsumer);*/

		/*
		 * REMOTE
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.REMOTE.get())
				.pattern("X")
				.pattern("X")
				.pattern("P")
				.define('P', WUTItems.IRON_PLATE.get())
				.define('X', Blocks.GRAY_TERRACOTTA)
				.unlockedBy("has_ironplate", has(WUTItems.IRON_PLATE.get()))
				.save(recipeConsumer);*/

		/*
		 * WRENCH
		 */
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.WRENCH.get())
				.pattern("  I")
				.pattern(" I ")
				.pattern("I  ")
				.define('I', WUTItems.WITHERSTEEL_INGOT.get())
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		/*
		 * CUTTER
		 */
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.CUTTER.get())
				.pattern("  I")
				.pattern(" S ")
				.pattern("S  ")
				.define('S', WUTItems.WITHERSTEEL_INGOT.get())
				.define('I', Tags.Items.INGOTS_IRON)
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		/*
		 * SWORD
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.SWORD.get())
				.pattern(" S ")
				.pattern(" S ")
				.pattern(" R ")
				.define('S', WUTItems.SOULISHED_INGOT.get())
				.define('R', WUTTags.Items.RODS)
				.unlockedBy("has_soulishedingot", has(WUTItems.SOULISHED_INGOT.get()))
				.save(recipeConsumer);*/

		/*
		 * WAND
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.WAND.get())
				.pattern(" S ")
				.pattern(" R ")
				.pattern(" R ")
				.define('S', WUTItems.SOULISHED_INGOT.get())
				.define('R', WUTTags.Items.RODS)
				.unlockedBy("has_soulishedingot", has(WUTItems.SOULISHED_INGOT.get()))
				.save(recipeConsumer);*/

		/*
		 * SHIELD
		 */
		/*ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.SHIELDBASIC.get())
				.pattern("WPW")
				.pattern("WWW")
				.pattern(" W ")
				.define('W', WUTItems.WITHERSTEEL_INGOT.get())
				.define('P', Items.OAK_PLANKS)
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);*/

		/*ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.SHIELDHERO.get())
				.pattern("PWP")
				.pattern("PSP")
				.pattern("PWP")
				.define('W', WUTBlocks.WITHERSTEEL_BLOCK.get())
				.define('P', WUTItems.WITHERSTEEL_PLATE.get())
				.define('S', WUTItems.SHIELDBASIC.get())
				.unlockedBy("has_steelplate", has(WUTItems.SHIELDHERO.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.SHIELDROTTEN.get())
				.pattern("WSW")
				.pattern("WWW")
				.pattern(" W ")
				.define('W', WUTItems.WITHERSTEEL_INGOT.get())
				.define('S', Items.WITHER_SKELETON_SKULL)
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.SHIELDENERGY.get())
				.pattern("WCW")
				.pattern("WSW")
				.pattern(" B ")
				.define('W', WUTItems.WITHERSTEEL_INGOT.get())
				.define('C', WUTItems.SOULBANK_CASE.get())
				.define('S', WUTItems.SHIELDBASIC.get())
				.define('B', WUTBlocks.WITHERSTEEL_BLOCK.get())
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);*/

		/*
		 * MACHINES
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.ALLOY_FURNACE.get())
				.pattern("BFB")
				.pattern("FCF")
				.pattern("GSG")
				.define('F', Items.FURNACE)
				.define('B', Items.IRON_BARS)
				.define('C', WUTBlocks.CASE_BIG.get())
				.define('S', WUTItems.SOULBANK_ADVANCED.get())
				.define('G', WUTItems.IRON_GEAR.get())
				.unlockedBy("has_soulbankadvanced", has(WUTItems.SOULBANK_ADVANCED.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.ELECTRO_FURNACE.get())
				.pattern("IFI")
				.pattern("BCB")
				.pattern("GSG")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('F', Items.FURNACE)
				.define('B', Items.IRON_BARS)
				.define('C', WUTBlocks.CASE_BIG.get())
				.define('S', WUTItems.SOULBANK_BASIC.get())
				.define('G', WUTItems.IRON_GEAR.get())
				.unlockedBy("has_soulbankbasic", has(WUTItems.SOULBANK_BASIC.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.WATER_GENERATOR.get())
				.pattern("WSW")
				.pattern("SCS")
				.pattern("GSG")
				.define('W', Items.WATER_BUCKET)
				.define('S', WUTItems.SHOVEL.get())
				.define('C', WUTBlocks.CASE_BIG.get())
				.define('G', WUTItems.IRON_GEAR.get())
				.unlockedBy("has_shovel", has(WUTItems.SHOVEL.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.WIND_GENERATOR.get())
				.pattern("BAB")
				.pattern("FCF")
				.pattern("GAG")
				.define('A', Items.FURNACE)
				.define('B', Items.IRON_BARS)
				.define('F', WUTItems.FAN.get())
				.define('C', WUTBlocks.CASE_BIG.get())
				.define('G', Items.GOLD_INGOT)
				.unlockedBy("has_fan", has(WUTItems.FAN.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.LAVA_GENERATOR.get())
				.pattern("WBW")
				.pattern("BCB")
				.pattern("GBG")
				.define('W', Items.LAVA_BUCKET)
				.define('B', WUTItems.BLINK_PLATE.get())
				.define('C', WUTBlocks.CASE_BIG.get())
				.define('G', WUTItems.IRON_GEAR.get())
				.unlockedBy("has_blinker", has(WUTItems.BLINK_PLATE.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.MINERBASIC.get())
				.pattern("PXP")
				.pattern("GCG")
				.pattern("PXP")
				.define('X', Items.IRON_PICKAXE)
				.define('P', WUTItems.IRON_PLATE.get())
				.define('C', WUTBlocks.CASE_BIG.get())
				.define('G', WUTItems.IRON_GEAR.get())
				.unlockedBy("has_irongear", has(WUTItems.IRON_GEAR.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.MINERADV.get())
				.pattern("MXM")
				.pattern("GCG")
				.pattern("MXM")
				.define('X', Items.DIAMOND_PICKAXE)
				.define('M', WUTBlocks.MINERBASIC.get())
				.define('C', WUTBlocks.CASE_BIG.get())
				.define('G', WUTItems.WITHERSTEEL_GEAR.get())
				.unlockedBy("has_minerbasic", has(WUTBlocks.MINERBASIC.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * TANKS
		 *
		 */
		/*ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.RESERVOIR.get())
				.pattern("IWI")
				.pattern("GCG")
				.pattern("IWI")
				.define('I', WUTItems.WITHERSTEEL_PLATE.get())
				.define('W', Tags.Items.INGOTS_IRON)
				.define('C', WUTItems.WITHERSTEEL_CASE.get())
				.define('G', Items.GLASS)
				.unlockedBy("has_ironplate", has(WUTItems.WITHERSTEEL_PLATE.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * BATTERY
		 *
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.CORE.get())
				.pattern("PWP")
				.pattern("PCP")
				.pattern("PWP")
				.define('P', WUTItems.WITHERSTEEL_PLATE.get())
				.define('W', WUTItems.SOULBANK_ULTRA.get())
				.define('C', WUTBlocks.CASE_BIG.get())
				.unlockedBy("has_steelplate", has(WUTItems.WITHERSTEEL_PLATE.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.PYLON.get())
				.pattern(" r ")
				.pattern("RSR")
				.pattern("wWw")
				.define('r', Items.BLAZE_ROD)
				.define('R', Items.REDSTONE_BLOCK)
				.define('W', WUTBlocks.WITHERSTEEL_BLOCK.get())
				.define('w', WUTItems.WITHERSTEEL_INGOT.get())
				.define('S', WUTItems.SOULBANK_ADVANCED.get())
				.unlockedBy("has_withersteel", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.STAB.get())
				.pattern("W W")
				.pattern("RSR")
				.pattern("W W")
				.define('R', Items.REDSTONE_BLOCK)
				.define('W', WUTItems.WITHERSTEEL_INGOT.get())
				.define('S', WUTItems.SOULBANK_BASIC.get())
				.unlockedBy("has_withersteel", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * SMARTTV
		 *
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.SMARTTV.get())
				.pattern("PPP")
				.pattern("PPP")
				.pattern("BCB")
				.define('B', WUTItems.SOULBANK_ADVANCED.get())
				.define('C', WUTItems.WITHERSTEEL_CASE.get())
				.define('P', Blocks.GLASS_PANE)
				.unlockedBy("has_case", has(WUTItems.WITHERSTEEL_CASE.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * FLOOR SENSOR
		 *
		 */
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.FLOORSENSOR.get())
				.pattern(" R ")
				.pattern("GCG")
				.pattern("PPP")
				.define('R', Items.REDSTONE)
				.define('G', WUTItems.WITHERSTEEL_GEAR.get())
				.define('C', WUTItems.WITHERSTEEL_CASE.get())
				.define('P', Blocks.PISTON)
				.unlockedBy("has_case", has(WUTItems.WITHERSTEEL_CASE.get()))
				.save(recipeConsumer);

		/*
		 *
		 * WALL SENSOR
		 *
		 */
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.WALL_SENSOR.get())
				.pattern("   ")
				.pattern("GCG")
				.pattern("RRR")
				.define('R', Items.REDSTONE)
				.define('G', WUTItems.WITHERSTEEL_GEAR.get())
				.define('C', WUTItems.WITHERSTEEL_CASE.get())
				.unlockedBy("has_smallcase", has(WUTItems.WITHERSTEEL_CASE.get()))
				.save(recipeConsumer);

		/*
		 *
		 * TANK DRUM
		 *
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.TANKDRUM.get())
				.pattern("PBP")
				.pattern("GCG")
				.pattern("PBP")
				.define('P', WUTItems.IRON_PLATE.get())
				.define('G', Items.GLASS_PANE)
				.define('B', Items.WATER_BUCKET)
				.define('C', WUTBlocks.CASE_SMALL.get())
				.unlockedBy("has_case", has(WUTBlocks.CASE_SMALL.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * FARMER
		 *
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.FARMER.get())
				.pattern("CCC")
				.pattern("PSP")
				.pattern("PPP")
				.define('S', WUTItems.SPIRAL.get())
				.define('P', WUTItems.WITHERSTEEL_PLATE.get())
				.define('C', WUTBlocks.CASE_SMALL.get())
				.unlockedBy("has_spiral", has(WUTItems.SPIRAL.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * COLLECTOR
		 *
		 */
		/*ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.COLLECTOR.get())
				.pattern("TTT")
				.pattern("ATA")
				.pattern("PGP")
				.define('G', WUTItems.WITHERSTEEL_GEAR.get())
				.define('P', WUTItems.WITHERSTEEL_PLATE.get())
				.define('T', WUTBlocks.WITHERSTEEL_BLOCK.get())
				.define('A', WUTItems.ANCHOR.get())
				.unlockedBy("has_steelblock", has(WUTBlocks.WITHERSTEEL_BLOCK.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * SPAWNER
		 *
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.SPAWNER.get())
				.pattern("SKS")
				.pattern("SCS")
				.pattern("EBE")
				.define('B', WUTItems.SOULBANK_ADVANCED.get())
				.define('C', WUTBlocks.CASE_SMALL.get())
				.define('K', WUTBlocks.WITHERSTEEL_BLOCK.get())
				.define('S', WUTItems.WITHERSTEEL_INGOT.get())
				.define('E', Items.EMERALD)
				.unlockedBy("has_steelcase", has(WUTBlocks.CASE_SMALL.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * PLACER
		 *
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.PLACER.get())
				.pattern("PXP")
				.pattern("XCX")
				.pattern("PXP")
				.define('X', Items.PISTON)
				.define('P', WUTItems.IRON_PLATE.get())
				.define('C', WUTBlocks.CASE_BIG.get())
				.unlockedBy("has_steelcase", has(WUTBlocks.CASE_BIG.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * COLLECTOR
		 *
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.FISHER.get())
				.pattern("PRP")
				.pattern("BCB")
				.pattern("PKP")
				.define('K', WUTItems.WITHERSTEEL_GEAR.get())
				.define('R', Items.FISHING_ROD)
				.define('B', Items.WATER_BUCKET)
				.define('C', WUTBlocks.CASE_BIG.get())
				.define('P', Items.IRON_INGOT)
				.unlockedBy("has_steelgear", has(WUTItems.WITHERSTEEL_GEAR.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * XP PLATEWIRELESS
		 *
		 */
//        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.XPPLATE.get())
//	        .pattern("BBB")
//	        .pattern("BCB")
//	        .pattern("BBB")
//	        .define('B', Items.LAVA_BUCKET)
//	        .define('C', WUTBlocks.CASE_SMALL.get())
//	        .unlockedBy("has_smallcase", has(WUTBlocks.CASE_SMALL.get()))
//	        .save(recipeConsumer);
//
//        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.XPWIRELESS.get())
//	        .pattern("BBB")
//	        .pattern("BCB")
//	        .pattern("BBB")
//	        .define('B', Items.WATER_BUCKET)
//	        .define('C', WUTBlocks.CASE_BIG.get())
//	        .unlockedBy("has_case", has(WUTBlocks.CASE_BIG.get()))
//	        .save(recipeConsumer);

		/*
		 *
		 * STEEL_CATWALK
		 *
		 */
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.STEEL_CATWALK.get())
				.pattern("SSS")
				.pattern("BBB")
				.define('B', Items.IRON_BARS)
				.define('S', WUTItems.WITHERSTEEL_INGOT.get())
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		/*
		 *
		 * GREENHOUSE
		 *
		 */
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.GREENHOUSE.get())
				.pattern("XIX")
				.pattern("IGI")
				.pattern("XIX")
				.define('I', WUTItems.WITHERSTEEL_INGOT.get())
				.define('G', Items.WHITE_STAINED_GLASS)
				.define('X', Items.IRON_BARS)
				.unlockedBy("has_anchor", has(Items.IRON_BARS))
				.save(recipeConsumer);

		/*
		 *
		 * RAILING
		 *
		 */
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.STEEL_RAILING.get())
				.pattern("   ")
				.pattern("I I")
				.pattern("III")
				.define('I', WUTItems.WITHERSTEEL_INGOT.get())
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		/*
		 *
		 * CLICKER
		 *
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.CLICKER.get())
				.pattern("PXP")
				.pattern("XCX")
				.pattern("PXP")
				.define('X', Items.IRON_PICKAXE)
				.define('P', WUTItems.IRON_PLATE.get())
				.define('C', WUTBlocks.CASE_BIG.get())
				.unlockedBy("has_bigcase", has(WUTBlocks.CASE_BIG.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * FLOODGATE
		 *
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.FLOODGATE.get())
				.pattern("#W#")
				.pattern("WDW")
				.pattern("#W#")
				.define('#', WUTItems.IRON_PLATE.get())
				.define('W', Items.WATER_BUCKET)
				.define('D', Blocks.DISPENSER)
				.unlockedBy("has_ironplate", has(WUTItems.IRON_PLATE.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * TOTEM
		 *
		 */
/*		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.TOTEM.get())
				.pattern(" R ")
				.pattern("ICI")
				.pattern("ISI")
				.define('S', WUTItems.WITHERSTEEL_GEAR.get())
				.define('C', WUTBlocks.CASE_BIG.get())
				.define('I', WUTItems.WITHERSTEEL_INGOT.get())
				.define('R', Items.REDSTONE)
				.unlockedBy("has_bigcase", has(WUTBlocks.CASE_BIG.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * RACK
		 *
		 */
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.RACK_CASE.get())
				.pattern("IXI")
				.pattern("XCX")
				.pattern("IXI")
				.define('C', WUTItems.WITHERSTEEL_CASE.get())
				.define('I', WUTItems.WITHERSTEEL_INGOT.get())
				.define('X', Items.IRON_BARS)
				.unlockedBy("has_bigcase", has(WUTItems.WITHERSTEEL_CASE.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.RACKITEM_CONTROLLER.get())
				.pattern("EDE")
				.pattern("TCT")
				.pattern("EDE")
				.define('C', WUTItems.WITHERSTEEL_CASE.get())
				.define('D', Items.DIAMOND_BLOCK)
				.define('E', Items.EMERALD)
				.define('T', Items.CHEST)
				.unlockedBy("has_smallcase", has(WUTItems.WITHERSTEEL_CASE.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.RACKFLUID_CONTROLLER.get())
				.pattern("EDE")
				.pattern("WCW")
				.pattern("EDE")
				.define('C', WUTItems.WITHERSTEEL_CASE.get())
				.define('D', Items.DIAMOND_BLOCK)
				.define('E', Items.EMERALD)
				.define('W', Items.WATER_BUCKET)
				.unlockedBy("has_smallcase", has(WUTItems.WITHERSTEEL_CASE.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.RACKENERGY_CONTROLLER.get())
				.pattern("EDE")
				.pattern("RCR")
				.pattern("EDE")
				.define('C', WUTItems.WITHERSTEEL_CASE.get())
				.define('D', Items.DIAMOND_BLOCK)
				.define('E', Items.EMERALD)
				.define('R', Blocks.REDSTONE_BLOCK)
				.unlockedBy("has_smallcase", has(WUTItems.WITHERSTEEL_CASE.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.RACK_TERMINAL.get())
				.pattern("SSS")
				.pattern("PPP")
				.define('S', WUTItems.WITHERSTEEL_INGOT.get())
				.define('P', WUTItems.WITHERSTEEL_PLATE.get())
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		/*
		 *
		 * POLES
		 *
		 */
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.STEEL_POLE.get())
				.pattern("I")
				.pattern("I")
				.pattern("I")
				.define('I', WUTItems.WITHERSTEEL_INGOT.get())
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.STEEL_BEAM.get())
				.pattern("OIO")
				.pattern(" I ")
				.pattern("OIO")
				.define('I', WUTItems.WITHERSTEEL_INGOT.get())
				.define('O', Blocks.GRAY_CONCRETE)
				.unlockedBy("has_steelingot", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		/*
		 *
		 * DOOR CYCLING RECIPES
		 *
		 */
		/*ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.METAL_DOOR.get())
				.pattern("   ")
				.pattern("WWW")
				.pattern("SSS")
				.define('W', WUTItems.WITHERSTEEL_INGOT.get())
				.define('S', WUTBlocks.STEEL_DOOR.get())
				.unlockedBy("has_withersteel", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.STEEL_DOOR.get())
				.pattern("SS ")
				.pattern("SS ")
				.pattern("SS ")
				.define('S', WUTItems.WITHERSTEEL_INGOT.get())
				.unlockedBy("has_withersteel", has(WUTItems.WITHERSTEEL_INGOT.get()))
				.save(recipeConsumer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WUTBlocks.CASED_DOOR.get())
				.requires(WUTBlocks.STEEL_DOOR.get())
				.unlockedBy("has_steeldoor", has(WUTBlocks.STEEL_DOOR.get()))
				.save(recipeConsumer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WUTBlocks.CREEP_DOOR.get())
				.requires(WUTBlocks.CASED_DOOR.get())
				.unlockedBy("has_caseddoor", has(WUTBlocks.CASED_DOOR.get()))
				.save(recipeConsumer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WUTBlocks.LIRON_DOOR.get())
				.requires(WUTBlocks.CREEP_DOOR.get())
				.unlockedBy("has_creepdoor", has(WUTBlocks.CREEP_DOOR.get()))
				.save(recipeConsumer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, WUTBlocks.STRIPED_DOOR.get())
				.requires(WUTBlocks.LIRON_DOOR.get())
				.unlockedBy("has_lirondoor", has(WUTBlocks.LIRON_DOOR.get()))
				.save(recipeConsumer);*/

		/*
		 *
		 * DECO
		 *
		 */
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.BRICKSLAVA.get(), 16)
				.pattern("BLB")
				.pattern("LBL")
				.pattern("BLB")
				.define('L', Items.LAVA_BUCKET)
				.define('B', WUTBlocks.BRICKSDARK.get())
				.unlockedBy("has_bricks", has(WUTBlocks.BRICKSDARK.get()))
				.save(recipeConsumer);

		/*
		 *
		 * ANGELBLOCK
		 *
		 */
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.ANGEL.get())
				.pattern("FGF")
				.pattern("GCG")
				.pattern("FGF")
				.define('G', Items.GOLD_NUGGET)
				.define('F', Items.FEATHER)
				.define('C', WUTItems.WITHERSTEEL_CASE.get())
				.unlockedBy("has_case", has(WUTItems.WITHERSTEEL_CASE.get()))
				.save(recipeConsumer);

        /*
    	 * 
    	 * HAMMER
    	 * 
    	 */
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.HAMMER.get())
	        .pattern("  I")
	        .pattern("RRB")
	        .pattern("  I")
	        .define('R', Tags.Items.RODS)
	        .define('I', Tags.Items.INGOTS_IRON)
	        .define('B', Items.IRON_BLOCK)
	        .unlockedBy("has_iron", has(Items.IRON_INGOT))
	        .save(recipeConsumer);

        /*
         * 
         * CASE
         * 
         */
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.WITHERSTEEL_CASE.get())
	        .pattern("CPC")
	        .pattern("P P")
	        .pattern("CPC")
	        .define('P', WUTItems.WITHERSTEEL_PLATE.get())
	        .define('C', Items.COAL)
	        .unlockedBy("has_steel", has(WUTItems.WITHERSTEEL_INGOT.get()))
	        .save(recipeConsumer);

        /*
         * SOULBANKS
         */
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.SOULBANK_CASE.get())
            .pattern(" G ")
            .pattern("CPC")
            .pattern("R R")
            .define('G', Items.GOLD_INGOT)
            .define('P', WUTItems.WITHERSTEEL_PLATE.get())
            .define('C', Items.COPPER_INGOT)
            .define('R', Tags.Items.RODS)
            .unlockedBy("has_rod", has(Tags.Items.RODS))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.SOULBANK_BASIC.get())
            .pattern(" GR")
            .pattern("GBG")
            .pattern("RG ")
            .define('B', WUTItems.SOULBANK_CASE.get())
            .define('G', Items.GOLD_NUGGET)
            .define('R', Items.REDSTONE)
            .unlockedBy("has_soulbankcase", has(WUTItems.SOULBANK_CASE.get()))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.SOULBANK_ADVANCED.get())
            .pattern(" S ")
            .pattern("BCB")
            .pattern(" S ")
            .define('S', Items.DIAMOND)
            .define('C', Items.COAL)
            .define('B', WUTItems.SOULBANK_BASIC.get())
            .unlockedBy("has_soulbankbasic", has(WUTItems.SOULBANK_BASIC.get()))
            .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTItems.SOULBANK_ULTRA.get())
            .pattern(" S ")
            .pattern("ACA")
            .pattern(" S ")
            .define('S', WUTItems.WITHERSTEEL_INGOT.get())
            .define('C', Items.SHROOMLIGHT)
            .define('A', WUTItems.SOULBANK_ADVANCED.get())
            .unlockedBy("has_soulbankadvanced", has(WUTItems.SOULBANK_ADVANCED.get()))
            .save(recipeConsumer);
        
        /*
         * 
         * SOLARPANEL
         * 
         */
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.SOLARCASE.get())
	        .pattern("   ")
	        .pattern("BPB")
	        .pattern("CDC")
	        .define('C', WUTItems.SOULBANK_CASE.get())
	        .define('B', Items.IRON_BARS)
	        .define('P', WUTItems.WITHERSTEEL_PLATE.get())
	        .define('D', Items.DAYLIGHT_DETECTOR)
	        .unlockedBy("has_soulbankcase", has(WUTItems.SOULBANK_CASE.get()))
	        .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.SOLARBASIC.get())
	        .pattern("   ")
	        .pattern("GGG")
	        .pattern("BCB")
	        .define('G', Items.GLASS_PANE)
	        .define('B', WUTItems.SOULBANK_BASIC.get())
	        .define('C', WUTBlocks.SOLARCASE.get())
	        .unlockedBy("has_solarcase", has(WUTBlocks.SOLARCASE.get()))
	        .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.SOLARADV.get())
	        .pattern("   ")
	        .pattern("GGG")
	        .pattern("ACA")
	        .define('G', Items.GLASS_PANE)
	        .define('A', WUTItems.SOULBANK_ADVANCED.get())
	        .define('C', WUTBlocks.SOLARBASIC.get())
	        .unlockedBy("has_solarbasic", has(WUTBlocks.SOLARBASIC.get()))
	        .save(recipeConsumer);
        
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WUTBlocks.SOLARULTRA.get())
	        .pattern("   ")
	        .pattern("GGG")
	        .pattern("UCU")
	        .define('G', Items.GLASS_PANE)
	        .define('U', WUTItems.SOULBANK_ULTRA.get())
	        .define('C', WUTBlocks.SOLARADV.get())
	        .unlockedBy("has_solaradv", has(WUTBlocks.SOLARADV.get()))
	        .save(recipeConsumer);
    }

    /*
     * 
     * SORTOF
     * 
     */
    private void makeMaterialRecipes(RecipeOutput recipeConsumer, Item ingot, Item nugget, Block block)
    {
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, ingot, 9)
            .requires(block.asItem())
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
            .save(recipeConsumer);
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.MISC, nugget, 9)
            .requires(ingot)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
            .save(recipeConsumer);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, block)
            .pattern("III")
            .pattern("III")
            .pattern("III")
            .define('I', ingot)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(block.asItem()))
            .save(recipeConsumer);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, ingot)
            .pattern("NNN")
            .pattern("NNN")
            .pattern("NNN")
            .define('N', nugget)
            .unlockedBy("has_ingredient", InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
            .save(recipeConsumer, nugget.toString() + "_to_ingot");
    }
}
