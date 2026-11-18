package geni.withercore.init;

import geni.withercore.common.registration.impl.BlockDeferredRegister;
import geni.withercore.common.registration.impl.BlockRegistryObject;
import geni.withercore.common.world.level.block.WitherBlock;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.level.block.*;
import geni.witherutils.common.world.level.block.controller.SlotControllerBlock;
import geni.witherutils.common.world.level.block.conveyor.ConveyorBeltBlock;
import geni.witherutils.common.world.level.block.conveyor.SupportPoleBlock;
import geni.witherutils.common.world.level.block.conveyor.SupportPoleBlockItem;
import geni.witherutils.common.world.level.block.fluid.ColdSlushBlock;
import geni.witherutils.common.world.level.block.pipe.FluidPipeBlock;
import geni.witherutils.common.world.level.block.pipe.PipePoleBlock;
import geni.witherutils.common.world.level.block.rack.casing.CaseBlock;
import geni.witherutils.common.world.level.block.rack.controller.energy.ControllerEnergyBlock;
import geni.witherutils.common.world.level.block.rack.controller.energy.ControllerEnergyBlockItem;
import geni.witherutils.common.world.level.block.rack.controller.fluid.ControllerFluidBlock;
import geni.witherutils.common.world.level.block.rack.controller.fluid.ControllerFluidBlockItem;
import geni.witherutils.common.world.level.block.rack.controller.item.ControllerItemBlock;
import geni.witherutils.common.world.level.block.rack.controller.item.ControllerItemBlockItem;
import geni.witherutils.common.world.level.block.rack.terminal.TerminalBlock;
import geni.witherutils.common.world.level.block.screen.ScreenBlock;
import geni.witherutils.common.world.level.block.solar.SolarType;
import geni.witherutils.common.world.level.block.state.StreetLampPart;
import geni.witherutils.common.world.level.block.steel.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.HashMap;
import java.util.Map;

public class WUTBlocks {

	public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(WitherUtils.MODID);

	public static final BlockRegistryObject<AnvilBlock, BlockItem> ANVIL = BLOCKS.register("anvil", AnvilBlock::new);
	public static final BlockRegistryObject<AngelBlock, AngelBlockItem> ANGEL = BLOCKS.register("angel",
			() -> new AngelBlock(WUTBlocks.defaultProps()), block -> new AngelBlockItem(block, new Item.Properties()));

	public static final BlockRegistryObject<CreativeGeneratorBlock, CreativeGeneratorBlockItem> CREATIVE_GENERATOR = BLOCKS
			.register("creative_generator", CreativeGeneratorBlock::new,
					block -> new CreativeGeneratorBlockItem(block, new Item.Properties()));

	public static final BlockRegistryObject<ScreenBlock, BlockItem> SCREEN = BLOCKS.register("screen",
			() -> new ScreenBlock(WUTBlocks.defaultProps()
					.lightLevel(state -> ScreenBlock.getLightEmissionForStage(state.getValue(ScreenBlock.LIGHT_STAGE)))));

	public static final BlockRegistryObject<WitherBlock, BlockItem> SOLARCASE = BLOCKS.register("solar_case",
			() -> new WitherBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<SolarPanelBlock, SolarPanelBlockItem> SOLARBASIC = BLOCKS.register(
			"solar_basic", () -> new SolarPanelBlock(SolarType.BASIC, WUTBlocks.glassProps()),
			block -> new SolarPanelBlockItem(block, new Item.Properties(), SolarType.BASIC));
	public static final BlockRegistryObject<SolarPanelBlock, SolarPanelBlockItem> SOLARADV = BLOCKS.register(
			"solar_adv", () -> new SolarPanelBlock(SolarType.ADVANCED, WUTBlocks.glassProps()),
			block -> new SolarPanelBlockItem(block, new Item.Properties(), SolarType.ADVANCED));
	public static final BlockRegistryObject<SolarPanelBlock, SolarPanelBlockItem> SOLARULTRA = BLOCKS.register(
			"solar_ultra", () -> new SolarPanelBlock(SolarType.ULTRA, WUTBlocks.glassProps()),
			block -> new SolarPanelBlockItem(block, new Item.Properties(), SolarType.ULTRA));

	public static final BlockRegistryObject<FloorSensorBlock, BlockItem> FLOORSENSOR = BLOCKS.register("floorsensor",
			() -> new FloorSensorBlock(WUTBlocks.variant2Props()));
	public static final BlockRegistryObject<WallSensorBlock, BlockItem> WALL_SENSOR = BLOCKS.register("wall_sensor",
			() -> new WallSensorBlock(WUTBlocks.defaultProps()));

	public static final BlockRegistryObject<WitherBlock, BlockItem> WITHERSTEEL_BLOCK = BLOCKS
			.register("withersteel_block", () -> new WitherBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<GreenhouseBlock, BlockItem> GREENHOUSE = BLOCKS.register("greenhouse",
			() -> new GreenhouseBlock(glassProps()));

	public static final BlockRegistryObject<SoulFireBlock, BlockItem> SOULFIRE = BLOCKS.register("soulfire",
			() -> new SoulFireBlock(soulFireProps()));

	public static final BlockRegistryObject<CaseBlock, BlockItem> RACK_CASE = BLOCKS.register("rack_case",
			() -> new CaseBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<TerminalBlock, BlockItem> RACK_TERMINAL = BLOCKS.register("rack_terminal",
			() -> new TerminalBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<ControllerItemBlock, ControllerItemBlockItem> RACKITEM_CONTROLLER = BLOCKS
			.register("rackitem_controller", () -> new ControllerItemBlock(WUTBlocks.variant1Props()),
					block -> new ControllerItemBlockItem(block, new Item.Properties()));
	public static final BlockRegistryObject<ControllerFluidBlock, ControllerFluidBlockItem> RACKFLUID_CONTROLLER = BLOCKS
			.register("rackfluid_controller", () -> new ControllerFluidBlock(WUTBlocks.variant1Props()),
					block -> new ControllerFluidBlockItem(block, new Item.Properties()));
	public static final BlockRegistryObject<ControllerEnergyBlock, ControllerEnergyBlockItem> RACKENERGY_CONTROLLER = BLOCKS
			.register("rackenergy_controller", () -> new ControllerEnergyBlock(WUTBlocks.variant1Props()),
					block -> new ControllerEnergyBlockItem(block, new Item.Properties()));

	/*
	 *
	 * DECO
	 *
	 */
	public static final BlockRegistryObject<WitherBlock, BlockItem> BRICKSDARK = BLOCKS.register("bricks_dark",
			() -> new WitherBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<BricksLavaBlock, BlockItem> BRICKSLAVA = BLOCKS.register("bricks_lava",
			() -> new BricksLavaBlock(WUTBlocks.lavaProps()));

	public static final BlockRegistryObject<SlicedConcreteBlock, BlockItem> SLICEDCONCRETEBLACK = BLOCKS
			.register("slicedconcrete_black", () -> new SlicedConcreteBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<SlicedConcreteBlock, BlockItem> SLICEDCONCRETEGRAY = BLOCKS
			.register("slicedconcrete_gray", () -> new SlicedConcreteBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<SlicedConcreteBlock, BlockItem> SLICEDCONCRETEWHITE = BLOCKS
			.register("slicedconcrete_white", () -> new SlicedConcreteBlock(WUTBlocks.defaultProps()));

	public static final BlockRegistryObject<SteelBeamBlock, BlockItem> STEEL_BEAM = BLOCKS.register("steel_beam",
			() -> new SteelBeamBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<SteelPoleBlock, BlockItem> STEEL_POLE = BLOCKS.register("steel_pole",
			() -> new SteelPoleBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<SteelRailingBlock, BlockItem> STEEL_RAILING = BLOCKS.register("steel_railing",
			() -> new SteelRailingBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<SteelCatwalkBlock, SteelCatwalkBlockItem> STEEL_CATWALK = BLOCKS.register("steel_catwalk",
			() -> new SteelCatwalkBlock(WUTBlocks.variant1Props()),
			block -> new SteelCatwalkBlockItem(block, new Item.Properties()));

	public static final BlockRegistryObject<HydroReservoirBlock, BlockItem> HYDRO_RESERVOIR = BLOCKS.register("hydro_reservoir",
			HydroReservoirBlock::new);
	public static final BlockRegistryObject<HydroTurbineBlock, BlockItem> HYDRO_TURBINE = BLOCKS.register("hydro_turbine",
			HydroTurbineBlock::new);

	public static final BlockRegistryObject<PipePoleBlock, BlockItem> PIPE_POLE = BLOCKS.register("pipe_pole",
			() -> new PipePoleBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<FluidPipeBlock, BlockItem> FLUID_PIPE = BLOCKS.register("fluid_pipe",
			() -> new FluidPipeBlock(WUTBlocks.variant1Props().noLootTable()));

	public static final BlockRegistryObject<SupportPoleBlock, SupportPoleBlockItem> CONVEYOR_STAND = BLOCKS.register("conveyor_stand",
			() -> new SupportPoleBlock(WUTBlocks.variant1Props()),
			block -> new SupportPoleBlockItem(block, new Item.Properties()));
	public static final BlockRegistryObject<ConveyorBeltBlock, BlockItem> CONVEYOR_BELT = BLOCKS.register("conveyor_belt",
			() -> new ConveyorBeltBlock(WUTBlocks.variant1Props()));

	/*
	 *
	 * POWER
	 *
	 */
	public static final BlockRegistryObject<PowerPlugBlock, BlockItem> POWER_PLUG = BLOCKS.register("power_plug",
			() -> new PowerPlugBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<PowerPoleBlock, BlockItem> POWER_POLE = BLOCKS.register("power_pole",
			() -> new PowerPoleBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<StreetLampBlock, BlockItem> STREET_LAMP = BLOCKS.register("street_lamp",
			() -> new StreetLampBlock(WUTBlocks.variant1Props()
					.lightLevel(state -> state.getValue(StreetLampBlock.PART) == StreetLampPart.UPPER && state.getValue(StreetLampBlock.LIT) ? 15 : 0)));
	public static final BlockRegistryObject<WallLampBlock, BlockItem> WALL_LAMP = BLOCKS.register("wall_lamp",
			() -> new WallLampBlock(WUTBlocks.variant1Props()
					.lightLevel(state -> state.getValue(WallLampBlock.LIT) ? 15 : 0)));
	public static final BlockRegistryObject<CeilingLampBlock, BlockItem> CEILING_LAMP = BLOCKS.register("ceiling_lamp",
			() -> new CeilingLampBlock(WUTBlocks.variant1Props()
					.lightLevel(state -> state.getValue(CeilingLampBlock.LIT) ? 15 : 0)));

	/*
	 * 
	 * PROPERTIES
	 * 
	 */
	public static Block.Properties defaultProps() {
		return Block.Properties
				.of()
				.mapColor(MapColor.METAL)
				.strength(3f, 10f)
				.sound(WUTSounds.STANDARD)
				.requiresCorrectToolForDrops()
				.noOcclusion();
	}

	public static Block.Properties lavaProps() {
		return Block.Properties
				.of()
				.mapColor(MapColor.NETHER)
				.requiresCorrectToolForDrops()
				.strength(3.0F, 2000.0F)
				.sound(SoundType.STONE);
	}

	public static Block.Properties plantProps() {
		return Block.Properties
				.of()
				.mapColor(MapColor.PLANT)
				.noCollission()
				.randomTicks()
				.instabreak()
				.sound(SoundType.CROP);
	}

	public static Block.Properties doorProps() {
		return Block.Properties
				.of()
				.mapColor(MapColor.METAL)
				.noOcclusion()
				.requiresCorrectToolForDrops()
				.strength(1.5F, 6.0F)
				.sound(WUTSounds.STANDARD);
	}

	public static Block.Properties cropProps() {
		return Block.Properties
				.of()
				.mapColor(MapColor.PLANT)
				.noCollission()
				.randomTicks()
				.instabreak()
				.sound(SoundType.CROP)
				.pushReaction(PushReaction.DESTROY);
	}

	private static Block.Properties fluidProps() {
		return Block.Properties.of()
				.mapColor(MapColor.WATER)
				.noCollission()
				.strength(100f)
				.noLootTable()
				.replaceable();
	}

	public static Block.Properties variant1Props() {
		return Block.Properties
				.of()
				.mapColor(MapColor.METAL)
				.strength(3f, 10f)
				.sound(WUTSounds.VARIANT1)
				.noOcclusion();
	}

	public static Block.Properties variant2Props() {
		return Block.Properties
				.of()
				.mapColor(MapColor.METAL)
				.strength(3f, 10f)
				.sound(WUTSounds.VARIANT2)
				.noOcclusion();
	}

	public static Block.Properties glassProps() {
		return Block.Properties
				.of()
				.mapColor(MapColor.WATER)
				.strength(0.3F, 2000.0F)
				.sound(WUTSounds.GLASS)
				.noOcclusion();
	}

	public static Block.Properties soulFireProps() {
		return Block.Properties
				.of()
				.hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true)
				.mapColor(MapColor.COLOR_LIGHT_BLUE)
				.replaceable()
				.noCollission()
				.instabreak()
				.lightLevel(p_50884_ -> 10)
				.sound(SoundType.WOOL)
				.pushReaction(PushReaction.DESTROY);
	}

	/*
	 *
	 * CTMBLOCKS
	 *
	 */
	public static final Map<String, BlockRegistryObject<CutterBlock, CutterBlockItem>> CTM_CONCRETE = new HashMap<>();
	public static final Map<String, BlockRegistryObject<CutterBlock, CutterBlockItem>> CTM_DIRT = new HashMap<>();
	public static final Map<String, BlockRegistryObject<CutterBlock, CutterBlockItem>> CTM_GLASS = new HashMap<>();
	public static final Map<String, BlockRegistryObject<CutterBlock, CutterBlockItem>> CTM_METAL = new HashMap<>();
	public static final Map<String, BlockRegistryObject<CutterBlock, CutterBlockItem>> CTM_STONE = new HashMap<>();
	public static final Map<String, BlockRegistryObject<CutterBlock, CutterBlockItem>> CTM_WOOD = new HashMap<>();

	static {
		Map<String, Integer> ctmMaterials = Map.of(
				"concrete", 10,
				"dirt", 6,
				"glass", 3,
				"metal", 11,
				"stone", 12,
				"wood", 10);

		for (Map.Entry<String, Integer> entry : ctmMaterials.entrySet()) {
			String material = entry.getKey();
			int count = entry.getValue();

			for (int i = 0; i < count; i++) {
				String name = material + "_" + "ctm" + String.format("%02d", i);

				Block.Properties props = switch (material) {
					case "concrete" -> WUTBlocks.ctmConcreteProps();
					case "dirt" -> WUTBlocks.ctmDirtProps();
					case "glass" -> WUTBlocks.ctmGlassProps();
					case "metal" -> WUTBlocks.ctmMetalProps();
					case "stone" -> WUTBlocks.ctmStoneProps();
					default -> WUTBlocks.ctmWoodProps();
				};

				BlockRegistryObject<CutterBlock, CutterBlockItem> blockObj = BLOCKS.register(name,
						() -> new CutterBlock(props), block -> new CutterBlockItem(block, new Item.Properties()));

				switch (material) {
					case "concrete" -> CTM_CONCRETE.put(name, blockObj);
					case "dirt" -> CTM_DIRT.put(name, blockObj);
					case "glass" -> CTM_GLASS.put(name, blockObj);
					case "metal" -> CTM_METAL.put(name, blockObj);
					case "stone" -> CTM_STONE.put(name, blockObj);
					default -> CTM_WOOD.put(name, blockObj);
				}
			}
		}
	}

	/*
	 *
	 * CTM PROPERTIES
	 *
	 */
	public static Block.Properties ctmConcreteProps() {
		return Block.Properties
				.of()
				.mapColor(MapColor.COLOR_GRAY)
				.strength(2.0F, 6.0F)
				.sound(SoundType.STONE);
	}
	
	public static Block.Properties ctmDirtProps() {
		return Block.Properties
				.of()
				.mapColor(MapColor.DIRT)
				.strength(0.5F, 0.5F)
				.sound(SoundType.GRASS);
	}

	public static Block.Properties ctmGlassProps() {
		return Block.Properties
				.of()
				.mapColor(MapColor.ICE)
				.strength(0.3F, 0.3F)
				.sound(SoundType.GLASS)
				.noOcclusion();
	}
	
	public static Block.Properties ctmMetalProps() {
		return Block.Properties
				.of()
				.mapColor(MapColor.METAL)
				.strength(5.0F, 6.0F)
				.sound(WUTSounds.VARIANT1);
	}

	public static Block.Properties ctmStoneProps() {
		return Block.Properties
				.of()
				.mapColor(MapColor.STONE)
				.strength(1.5F, 6.0F)
				.sound(SoundType.STONE);
	}

	public static Block.Properties ctmWoodProps() {
		return Block.Properties
				.of()
				.mapColor(MapColor.WOOD)
				.strength(2.0F, 3.0F)
				.sound(SoundType.WOOD);
	}

	/*
	 *
	 * LIQUID
	 *
	 */
	public static final BlockRegistryObject<LiquidBlock, BlockItem> COLDSLUSH = BLOCKS.register("coldslush",
			() -> new ColdSlushBlock(WUTFluids.COLDSLUSH, fluidProps().noLootTable().liquid().mapColor(DyeColor.LIGHT_BLUE)));

	/*
	 *
	 * SLOT CONTROLLER
	 *
	 */
	public static final BlockRegistryObject<SlotControllerBlock, BlockItem> SLOT_CONTROLLER = BLOCKS.register(
			"slot_controller", () -> new SlotControllerBlock(WUTBlocks.defaultProps()));
}
