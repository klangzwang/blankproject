package geni.withercore.init;

import geni.withercore.common.registration.impl.TileEntityTypeDeferredRegister;
import geni.withercore.common.registration.impl.TileEntityTypeRegistryObject;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.level.block.entity.*;
import geni.witherutils.common.world.level.block.rack.casing.CaseBlockEntity;
import geni.witherutils.common.world.level.block.rack.controller.energy.ControllerEnergyBlockEntity;
import geni.witherutils.common.world.level.block.rack.controller.fluid.ControllerFluidBlockEntity;
import geni.witherutils.common.world.level.block.rack.controller.item.ControllerItemBlockEntity;
import geni.witherutils.common.world.level.block.rack.terminal.TerminalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class WUTBlockEntities {

        public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(
                        WitherUtils.MODID);


        public static final TileEntityTypeRegistryObject<HydroReservoirBlockEntity> HYDRO_RESERVOIR = TILE_ENTITY_TYPES
                .mekBuilder(WUTBlocks.HYDRO_RESERVOIR, (pos, state) -> new HydroReservoirBlockEntity(pos, state)).build();
        public static final TileEntityTypeRegistryObject<HydroTurbineBlockEntity> HYDRO_TURBINE = TILE_ENTITY_TYPES
                .mekBuilder(WUTBlocks.HYDRO_TURBINE, (pos, state) -> new HydroTurbineBlockEntity(pos, state)).build();




        public static final TileEntityTypeRegistryObject<AnvilBlockEntity> ANVIL = TILE_ENTITY_TYPES
                        .mekBuilder(WUTBlocks.ANVIL, (pos, state) -> new AnvilBlockEntity(pos, state)).build();
        public static final TileEntityTypeRegistryObject<WallSensorBlockEntity> WALLSENSOR = TILE_ENTITY_TYPES
                        .mekBuilder(WUTBlocks.WALL_SENSOR, (pos, state) -> new WallSensorBlockEntity(pos, state))
                        .build();

        public static final TileEntityTypeRegistryObject<CreativeGeneratorBlockEntity> CREATIVE_GENERATOR = TILE_ENTITY_TYPES
                        .mekBuilder(WUTBlocks.CREATIVE_GENERATOR,
                                        (pos, state) -> new CreativeGeneratorBlockEntity(pos, state))
                        .build();

        public static final TileEntityTypeRegistryObject<FloorSensorBlockEntity> FLOORSENSOR = TILE_ENTITY_TYPES
                        .mekBuilder(WUTBlocks.FLOORSENSOR, (pos, state) -> new FloorSensorBlockEntity(pos, state))
                        .build();

        public static final TileEntityTypeRegistryObject<SolarPanelBlockEntity.Basic> SOLARBASIC_BE = TILE_ENTITY_TYPES
                        .builder(WUTBlocks.SOLARBASIC, SolarPanelBlockEntity.Basic::new).build();
        public static final TileEntityTypeRegistryObject<SolarPanelBlockEntity.Advanced> SOLARADV_BE = TILE_ENTITY_TYPES
                        .builder(WUTBlocks.SOLARADV, SolarPanelBlockEntity.Advanced::new).build();
        public static final TileEntityTypeRegistryObject<SolarPanelBlockEntity.Ultra> SOLARULTRA_BE = TILE_ENTITY_TYPES
                        .builder(WUTBlocks.SOLARULTRA, SolarPanelBlockEntity.Ultra::new).build();

        public static final TileEntityTypeRegistryObject<CaseBlockEntity> RACK_CASE = TILE_ENTITY_TYPES
                        .mekBuilder(WUTBlocks.RACK_CASE, (pos, state) -> new CaseBlockEntity(pos, state)).build();
        public static final TileEntityTypeRegistryObject<TerminalBlockEntity> RACK_TERMINAL = TILE_ENTITY_TYPES
                        .mekBuilder(WUTBlocks.RACK_TERMINAL, (pos, state) -> new TerminalBlockEntity(pos, state))
                        .build();
        public static final TileEntityTypeRegistryObject<ControllerItemBlockEntity> RACKITEM_CONTROLLER = TILE_ENTITY_TYPES
                        .mekBuilder(WUTBlocks.RACKITEM_CONTROLLER,
                                        (pos, state) -> new ControllerItemBlockEntity(pos, state))
                        .build();
        public static final TileEntityTypeRegistryObject<ControllerFluidBlockEntity> RACKFLUID_CONTROLLER = TILE_ENTITY_TYPES
                        .mekBuilder(WUTBlocks.RACKFLUID_CONTROLLER,
                                        (pos, state) -> new ControllerFluidBlockEntity(pos, state))
                        .build();
        public static final TileEntityTypeRegistryObject<ControllerEnergyBlockEntity> RACKENERGY_CONTROLLER = TILE_ENTITY_TYPES
                        .mekBuilder(WUTBlocks.RACKENERGY_CONTROLLER,
                                        (pos, state) -> new ControllerEnergyBlockEntity(pos, state))
                        .build();

        private static final List<BlockEntity> DUMMY_BE_LIST = new ArrayList<>();

        public static Stream<BlockEntity> streamBlockEntities() {
                if (DUMMY_BE_LIST.isEmpty()) {
                        DUMMY_BE_LIST.addAll(TILE_ENTITY_TYPES.getEntries().stream()
                                        .flatMap(holder -> holder.get().getValidBlocks().stream()
                                                        .findFirst()
                                                        .stream()
                                                        .map(b -> holder.get().create(BlockPos.ZERO,
                                                                        b.defaultBlockState())))
                                        .toList());
                }
                return DUMMY_BE_LIST.stream();
        }
}
