package geni.withercore.init;

import geni.withercore.block.WitherBlock;
import geni.withercore.registration.impl.BlockDeferredRegister;
import geni.withercore.registration.impl.BlockRegistryObject;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.level.block.*;
import geni.witherutils.common.world.level.block.fluid.*;
import geni.witherutils.common.world.level.block.network.solar.SolarType;
import geni.witherutils.common.world.level.block.rack.casing.CaseBlock;
import geni.witherutils.common.world.level.block.rack.controller.energy.ControllerEnergyBlock;
import geni.witherutils.common.world.level.block.rack.controller.energy.ControllerEnergyBlockItem;
import geni.witherutils.common.world.level.block.rack.controller.fluid.ControllerFluidBlock;
import geni.witherutils.common.world.level.block.rack.controller.fluid.ControllerFluidBlockItem;
import geni.witherutils.common.world.level.block.rack.controller.item.ControllerItemBlock;
import geni.witherutils.common.world.level.block.rack.controller.item.ControllerItemBlockItem;
import geni.witherutils.common.world.level.block.rack.terminal.TerminalBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class WUTBlocks {

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(WitherUtils.MODID);

	public static final BlockRegistryObject<AnvilBlock, BlockItem> ANVIL = BLOCKS.register("anvil", AnvilBlock::new);
	public static final BlockRegistryObject<CollectorBlock, BlockItem> COLLECTOR = BLOCKS.register("collector", () -> new CollectorBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<AngelBlock, AngelBlockItem> ANGEL = BLOCKS.register("angel", () -> new AngelBlock(WUTBlocks.defaultProps()), block -> new AngelBlockItem(block, new Item.Properties()));
	public static final BlockRegistryObject<WallSensorBlock, BlockItem> WALL_SENSOR = BLOCKS.register("wall_sensor", () -> new WallSensorBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<WitherEarthBlock, BlockItem> WITHEREARTH = BLOCKS.register("wither_earth", () -> new WitherEarthBlock(WUTBlocks.earthProps()));
	public static final BlockRegistryObject<TankReservoirBlock, TankReservoirBlockItem> RESERVOIR = BLOCKS.register("reservoir", () -> new TankReservoirBlock(WUTBlocks.glassProps()), block -> new TankReservoirBlockItem(block, new Item.Properties().stacksTo(1)));
	//public static final BlockRegistryObject<TankSupplyBlock, BlockItem> SUPPLY = BLOCKS.register("supply", () -> new TankSupplyBlock(WUTBlocks.glassProps()), block -> new BlockItem(block, new Item.Properties().stacksTo(1)));
	public static final BlockRegistryObject<DeviceBlock, BlockItem> DEVICE = BLOCKS.register("device", () -> new DeviceBlock(WUTBlocks.deviceProps()));
	public static final BlockRegistryObject<LightBlock, BlockItem> LIGHT = BLOCKS.register("light", () -> new LightBlock(WUTBlocks.defaultProps()));

	public static final BlockRegistryObject<CreativeGeneratorBlock, CreativeGeneratorBlockItem> CREATIVE_GENERATOR = BLOCKS.register("creative_generator", CreativeGeneratorBlock::new, block -> new CreativeGeneratorBlockItem(block, new Item.Properties()));
    
	public static final BlockRegistryObject<WitherBlock, BlockItem> SOLARCASE = BLOCKS.register("solar_case", () -> new WitherBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<SolarPanelBlock, SolarPanelBlockItem> SOLARBASIC = BLOCKS.register("solar_basic", () -> new SolarPanelBlock(SolarType.BASIC, WUTBlocks.glassProps()), block -> new SolarPanelBlockItem(block, new Item.Properties(), SolarType.BASIC));
	public static final BlockRegistryObject<SolarPanelBlock, SolarPanelBlockItem> SOLARADV = BLOCKS.register("solar_adv", () -> new SolarPanelBlock(SolarType.ADVANCED, WUTBlocks.glassProps()), block -> new SolarPanelBlockItem(block, new Item.Properties(), SolarType.ADVANCED));
	public static final BlockRegistryObject<SolarPanelBlock, SolarPanelBlockItem> SOLARULTRA = BLOCKS.register("solar_ultra", () -> new SolarPanelBlock(SolarType.ULTRA, WUTBlocks.glassProps()), block -> new SolarPanelBlockItem(block, new Item.Properties(), SolarType.ULTRA));

	public static final BlockRegistryObject<FloorSensorBlock, BlockItem> FLOORSENSOR = BLOCKS.register("floorsensor", () -> new FloorSensorBlock(WUTBlocks.variant2Props()));

	public static final BlockRegistryObject<WitherBlock, BlockItem> WITHERSTEEL_BLOCK = BLOCKS.register("withersteel_block", () -> new WitherBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<GreenhouseBlock, BlockItem> GREENHOUSE = BLOCKS.register("greenhouse", () -> new GreenhouseBlock(glassProps()));
	public static final BlockRegistryObject<SoulFireBlock, BlockItem> SOULFIRE = BLOCKS.register("soulfire", () -> new SoulFireBlock(soulFireProps()));
	
	public static final BlockRegistryObject<CaseBlock, BlockItem> RACK_CASE = BLOCKS.register("rack_case", () -> new CaseBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<TerminalBlock, BlockItem> RACK_TERMINAL = BLOCKS.register("rack_terminal", () -> new TerminalBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<ControllerItemBlock, ControllerItemBlockItem> RACKITEM_CONTROLLER = BLOCKS.register("rackitem_controller", () -> new ControllerItemBlock(WUTBlocks.variant1Props()), block -> new ControllerItemBlockItem(block, new Item.Properties()));
	public static final BlockRegistryObject<ControllerFluidBlock, ControllerFluidBlockItem> RACKFLUID_CONTROLLER = BLOCKS.register("rackfluid_controller", () -> new ControllerFluidBlock(WUTBlocks.variant1Props()), block -> new ControllerFluidBlockItem(block, new Item.Properties()));
	public static final BlockRegistryObject<ControllerEnergyBlock, ControllerEnergyBlockItem> RACKENERGY_CONTROLLER = BLOCKS.register("rackenergy_controller", () -> new ControllerEnergyBlock(WUTBlocks.variant1Props()), block -> new ControllerEnergyBlockItem(block, new Item.Properties()));

	public static final BlockRegistryObject<DronePlatformBlock, BlockItem> DRONEPLATFORM = BLOCKS.register("droneplatform", () -> new DronePlatformBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<DronePlatformIOBlock, BlockItem> DRONEPLATFORM_IO = BLOCKS.register("droneplatform_io", () -> new DronePlatformIOBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<DronePlatformDockingBlock, BlockItem> DRONE_REDSTONE_EMITTER = BLOCKS.registerNoItem("drone_redstone_emitter", () -> new DronePlatformDockingBlock(Block.Properties.ofFullCopy(Blocks.AIR)));

	/*
	 *
	 * LIQUID
	 *
	 */
	public static final BlockRegistryObject<LiquidBlock, BlockItem> BLUELIMBO = BLOCKS.register("bluelimbo", () -> new BlueLimboBlock(WUTFluids.BLUELIMBO.get(), fluidProps().mapColor(DyeColor.BLUE)));
	public static final BlockRegistryObject<LiquidBlock, BlockItem> COLDSLUSH = BLOCKS.register("coldslush", () -> new ColdSlushBlock(WUTFluids.COLDSLUSH.get(), fluidProps().mapColor(DyeColor.LIGHT_BLUE)));
	public static final BlockRegistryObject<LiquidBlock, BlockItem> EXPERIENCE = BLOCKS.register("experience", () -> new ExperienceBlock(WUTFluids.EXPERIENCE.get(), fluidProps().mapColor(DyeColor.YELLOW)));
	public static final BlockRegistryObject<LiquidBlock, BlockItem> FERTILIZER = BLOCKS.register("fertilizer", () -> new FertilizerBlock(WUTFluids.FERTILIZER.get(), fluidProps().mapColor(DyeColor.WHITE)));
	public static final BlockRegistryObject<LiquidBlock, BlockItem> PORTIUM = BLOCKS.register("portium", () -> new PortiumBlock(WUTFluids.PORTIUM.get(), fluidProps().mapColor(DyeColor.GREEN)));
	public static final BlockRegistryObject<LiquidBlock, BlockItem> REDRESIN = BLOCKS.register("redresin", () -> new RedResinBlock(WUTFluids.REDRESIN.get(), fluidProps().mapColor(DyeColor.RED)));
	public static final BlockRegistryObject<LiquidBlock, BlockItem> SOULFUL = BLOCKS.register("soulful", () -> new SoulfulBlock(WUTFluids.SOULFUL.get(), fluidProps().mapColor(DyeColor.CYAN)));
	public static final BlockRegistryObject<LiquidBlock, BlockItem> WITHERWATER = BLOCKS.register("witherwater", () -> new WitherWaterBlock(WUTFluids.WITHERWATER.get(), fluidProps().mapColor(DyeColor.PURPLE)));

	/*
	 *
	 * DECO
	 *
	 */
	public static final BlockRegistryObject<WitherBlock, BlockItem> BRICKSDARK = BLOCKS.register("bricks_dark", () -> new WitherBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<BricksLavaBlock, BlockItem> BRICKSLAVA = BLOCKS.register("bricks_lava", () -> new BricksLavaBlock(WUTBlocks.lavaProps()));
	
	public static final BlockRegistryObject<SlicedConcreteBlock, BlockItem> SLICEDCONCRETEBLACK = BLOCKS.register("slicedconcrete_black", () -> new SlicedConcreteBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<SlicedConcreteBlock, BlockItem> SLICEDCONCRETEGRAY = BLOCKS.register("slicedconcrete_gray", () -> new SlicedConcreteBlock(WUTBlocks.defaultProps()));
	public static final BlockRegistryObject<SlicedConcreteBlock, BlockItem> SLICEDCONCRETEWHITE = BLOCKS.register("slicedconcrete_white", () -> new SlicedConcreteBlock(WUTBlocks.defaultProps()));

	public static final BlockRegistryObject<SteelBeamBlock, BlockItem> STEEL_BEAM = BLOCKS.register("steel_beam", () -> new SteelBeamBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<SteelPoleBlock, BlockItem> STEEL_POLE = BLOCKS.register("steel_pole", () -> new SteelPoleBlock(WUTBlocks.variant1Props()));
	public static final BlockRegistryObject<RailingBlock, BlockItem> STEEL_RAILING = BLOCKS.register("steel_railing", () -> new RailingBlock(WUTBlocks.defaultProps(), RailingBlock.getPixeledAABB(0,0,0,0,0,0), RailingBlock.getPixeledAABB(0,0,0,16,15.9,1)));
	public static final BlockRegistryObject<CatwalkBlock, CatwalkBlockItem> STEEL_CATWALK = BLOCKS.register("catwalk", () -> new CatwalkBlock(WUTBlocks.variant1Props()), block -> new CatwalkBlockItem(block, new Item.Properties()));

	public static final BlockRegistryObject<CutterBlock, BlockItem> CTM00 = BLOCKS.register("ctm00", () -> new CutterBlock(WUTBlocks.defaultProps(), Blocks.GRAY_CONCRETE, false, false, true, CutterBlock.AthenaBlockType.CTMFULL));

	/*
	 *
	 * DOORS
	 *
	 */
	public static final BlockRegistryObject<DoorsBlock, BlockItem> CASED_DOOR = BLOCKS.register("door_cased", () -> new DoorsBlock(WUTBlocks.doorProps(), DoorsBlock.getPixeledAABB(15,0, 0, 16,16,16), DoorsBlock.getPixeledAABB( 0,0,15, 16,16,16), WUTSounds.DOOROPEN.get(), WUTSounds.DOORCLOSE.get()));
	public static final BlockRegistryObject<DoorsBlock, BlockItem> CREEP_DOOR = BLOCKS.register("door_creep", () -> new DoorsBlock(WUTBlocks.doorProps(), DoorsBlock.getPixeledAABB(15,0, 0, 16,16,16), DoorsBlock.getPixeledAABB( 0,0,15, 16,16,16), WUTSounds.DOOROPEN.get(), WUTSounds.DOORCLOSE.get()));
	public static final BlockRegistryObject<DoorsBlock, BlockItem> LIRON_DOOR = BLOCKS.register("door_liron", () -> new DoorsBlock(WUTBlocks.doorProps(), DoorsBlock.getPixeledAABB(15,0, 0, 16,16,16), DoorsBlock.getPixeledAABB( 0,0,15, 16,16,16), WUTSounds.DOOROPEN.get(), WUTSounds.DOORCLOSE.get()));
	public static final BlockRegistryObject<DoorsBlock, BlockItem> STEEL_DOOR = BLOCKS.register("door_steel", () -> new DoorsBlock(WUTBlocks.doorProps(), DoorsBlock.getPixeledAABB(15,0, 0, 16,16,16), DoorsBlock.getPixeledAABB( 0,0,15, 16,16,16), WUTSounds.DOOROPEN.get(), WUTSounds.DOORCLOSE.get()));
	public static final BlockRegistryObject<DoorsBlock, BlockItem> STRIPED_DOOR = BLOCKS.register("door_striped", () -> new DoorsBlock(WUTBlocks.doorProps(), DoorsBlock.getPixeledAABB(15,0, 0, 16,16,16), DoorsBlock.getPixeledAABB( 0,0,15, 16,16,16), WUTSounds.DOOROPEN.get(), WUTSounds.DOORCLOSE.get()));

	public static final BlockRegistryObject<MetalDoorBlock, BlockItem> METAL_DOOR = BLOCKS.register("door_metal", () -> new MetalDoorBlock(WUTBlocks.variant1Props()));

	public static final BlockRegistryObject<VanishBlock, BlockItem> VANISH_BLOCK = BLOCKS.register("vanish_block", () -> new VanishBlock(BlockBehaviour.Properties.of().strength(3.0f, 9.0f).noOcclusion().isSuffocating((state, level, pos) -> false).isViewBlocking((state, level, pos) -> false)));
	public static final BlockRegistryObject<WitherBlock, BlockItem> LINES = BLOCKS.register("lines", () -> new WitherBlock(WUTBlocks.defaultProps()));

	/*
     * 
     * PROPERTIES
     * 
     */
    public static Block.Properties defaultProps()
    {
        return Block.Properties
        		.of()
                .mapColor(MapColor.METAL)
                .strength(3f, 10f)
                .sound(WUTSounds.STANDARD)
                .requiresCorrectToolForDrops()
                .noOcclusion();
    }

	public static Block.Properties lavaProps()
	{
		return Block.Properties
				.of()
				.mapColor(MapColor.NETHER)
				.requiresCorrectToolForDrops()
				.strength(3.0F, 2000.0F)
				.sound(SoundType.STONE);
	}

	public static Block.Properties plantProps()
	{
		return Block.Properties
				.of()
				.mapColor(MapColor.PLANT)
				.noCollission()
				.randomTicks()
				.instabreak()
				.sound(SoundType.CROP);
	}

	public static Block.Properties doorProps()
	{
		return Block.Properties
				.of()
				.mapColor(MapColor.METAL)
				.noOcclusion()
				.requiresCorrectToolForDrops()
				.strength(1.5F, 6.0F)
				.sound(WUTSounds.STANDARD);
	}

    public static Block.Properties cropProps()
    {
        return Block.Properties
        		.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY);
    }
    
    private static Block.Properties fluidProps()
    {
        return Block.Properties.of()
			    .mapColor(MapColor.WATER)
			    .noCollission()
			    .strength(100f)
			    .noLootTable()
			    .replaceable();
    }
    
    public static Block.Properties variant1Props()
    {
        return Block.Properties
        		.of()
                .mapColor(MapColor.METAL)
                .strength(3f, 10f)
                .sound(WUTSounds.VARIANT1)
                .noOcclusion();
    }
    
    public static Block.Properties variant2Props()
    {
        return Block.Properties
        		.of()
                .mapColor(MapColor.METAL)
                .strength(3f, 10f)
                .sound(WUTSounds.VARIANT2)
                .noOcclusion();
    }
    
    public static Block.Properties glassProps()
    {
        return Block.Properties
                .of()
                .mapColor(MapColor.WATER)
                .strength(0.3F, 2000.0F)
                .sound(WUTSounds.GLASS)
                .noOcclusion();
    }
    
    public static Block.Properties soulFireProps()
    {
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

	public static Block.Properties earthProps()
	{
		return Block.Properties
				.of()
				.mapColor(MapColor.COLOR_BLACK)
				.strength(0.8F, 1200.0F)
				.sound(SoundType.SOUL_SOIL)
				.pushReaction(PushReaction.BLOCK);
	}

	public static Block.Properties deviceProps()
	{
		return Block.Properties
				.of()
				.mapColor(MapColor.METAL)
				.strength(3.5F, 12.0F)
				.sound(WUTSounds.VARIANT1)
				.noOcclusion();
	}
}
