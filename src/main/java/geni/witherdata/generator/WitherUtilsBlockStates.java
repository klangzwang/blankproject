package geni.witherdata.generator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import geni.withercore.common.registration.impl.BlockRegistryObject;
import geni.withercore.common.world.level.block.state.MultiBlockState;
import geni.withercore.init.WUTBlocks;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.level.block.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Map;
import java.util.function.Supplier;

public class WitherUtilsBlockStates extends BlockStateProvider {

    public WitherUtilsBlockStates(PackOutput output, ExistingFileHelper fileHelper)
    {
        super(output, WitherUtils.MODID, fileHelper);
    }

    /*
     *
     * FLUIDS
     *
     */
    private void registerFluids()
    {
        simpleBlock(WUTBlocks.COLDSLUSH.get(), models().getBuilder("block/fluid/coldslush")
                .texture("particle", modLoc("block/fluid/coldslush_still"))
                .texture("particle2", modLoc("block/fluid/coldslush_flow"))
                .renderType("translucent"));
    }

    /*
     *
     * STEELS
     *
     */
    private void registerSteels()
    {
        // BEAM
        ModelFile steelBeam = models().getExistingFile(modLoc("block/pole/steel_beam"));
        ModelFile steelWall = models().getExistingFile(modLoc("block/pole/steel_wall"));
        ModelFile steelConn = models().getExistingFile(modLoc("block/pole/steel_conn"));
        ModelFile steelConnB = models().getExistingFile(modLoc("block/pole/steel_connb"));

        MultiPartBlockStateBuilder beamBuilder = getMultipartBuilder(WUTBlocks.STEEL_BEAM.get());

        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            beamBuilder.part()
                    .modelFile(steelBeam)
                    .rotationY(dir == Direction.SOUTH ? 180 : dir == Direction.WEST ? 270 : dir == Direction.EAST ? 90 : 0)
                    .addModel()
                    .condition(SteelBeamBlock.FACING, dir)
                    .end();

            beamBuilder.part()
                    .modelFile(steelWall)
                    .rotationY(dir == Direction.SOUTH ? 180 : dir == Direction.WEST ? 270 : dir == Direction.EAST ? 90 : 0)
                    .addModel()
                    .condition(SteelBeamBlock.FACING, dir)
                    .condition(SteelBeamBlock.WALL, true)
                    .end();
        }

        beamBuilder.part()
                .modelFile(steelConnB)
                .rotationY(0)
                .addModel()
                .condition(SteelBeamBlock.NORTHCONN, true)
                .end();

        beamBuilder.part()
                .modelFile(steelConnB)
                .rotationY(90)
                .addModel()
                .condition(SteelBeamBlock.EASTCONN, true)
                .end();

        beamBuilder.part()
                .modelFile(steelConnB)
                .rotationY(180)
                .addModel()
                .condition(SteelBeamBlock.SOUTHCONN, true)
                .end();

        beamBuilder.part()
                .modelFile(steelConnB)
                .rotationY(270)
                .addModel()
                .condition(SteelBeamBlock.WESTCONN, true)
                .end();

        simpleBlockItem(WUTBlocks.STEEL_BEAM.get(), steelBeam);

        // FOOT
        ModelFile steelPole = models().getExistingFile(modLoc("block/pole/steel_pole"));
        ModelFile steelFoot = models().getExistingFile(modLoc("block/pole/steel_foot"));

        MultiPartBlockStateBuilder poleBuilder = getMultipartBuilder(WUTBlocks.STEEL_POLE.get());

        for (Direction dir : Direction.values())
        {
            poleBuilder.part()
                    .modelFile(steelPole)
                    .rotationX(180)
                    .addModel()
                    .condition(SteelPoleBlock.FACINGDOWN, true)
                    .end();
            poleBuilder.part()
                    .modelFile(steelPole)
                    .addModel()
                    .condition(SteelPoleBlock.FACINGDOWN, false)
                    .end();

            poleBuilder.part()
                    .modelFile(steelFoot)
                    .rotationX(180)
                    .addModel()
                    .condition(SteelPoleBlock.FOOT, true)
                    .condition(SteelPoleBlock.FACINGDOWN, true)
                    .end();
            poleBuilder.part()
                    .modelFile(steelFoot)
                    .addModel()
                    .condition(SteelPoleBlock.FOOT, true)
                    .condition(SteelPoleBlock.FACINGDOWN, false)
                    .end();

            poleBuilder.part()
                    .modelFile(steelConn)
                    .rotationY(0)
                    .addModel()
                    .condition(SteelPoleBlock.NORTHCONN, true)
                    .end();

            poleBuilder.part()
                    .modelFile(steelConn)
                    .rotationY(90)
                    .addModel()
                    .condition(SteelPoleBlock.EASTCONN, true)
                    .end();

            poleBuilder.part()
                    .modelFile(steelConn)
                    .rotationY(180)
                    .addModel()
                    .condition(SteelPoleBlock.SOUTHCONN, true)
                    .end();

            poleBuilder.part()
                    .modelFile(steelConn)
                    .rotationY(270)
                    .addModel()
                    .condition(SteelPoleBlock.WESTCONN, true)
                    .end();
        }
    }

    /*
     *
     * RACK
     *
     */
    public ModelFile getMBStateModel(MultiBlockState mbstate)
    {
        if(mbstate == MultiBlockState.NONE)
        {
            return models().getExistingFile(modLoc("block/rack/casing/case_none"));
        }
        else if(mbstate == MultiBlockState.XMAX_ZMAX_YEDGE ||
                mbstate == MultiBlockState.XMAX_ZMIN_YEDGE ||
                mbstate == MultiBlockState.XMIN_ZMAX_YEDGE ||
                mbstate == MultiBlockState.XMIN_ZMIN_YEDGE)
        {
            return models().getExistingFile(modLoc("block/rack/casing/case_corner"));
        }
        else
        {
            return models().getExistingFile(modLoc("block/rack/casing/case_null"));
        }
    }

    public int getMBStateYRotation(MultiBlockState mbstate)
    {
        if(mbstate == MultiBlockState.XMIN_ZMAX_YEDGE)
        {
            return -90;
        }
        else if(mbstate == MultiBlockState.XMAX_ZMIN_YEDGE)
        {
            return 90;
        }
        else if(mbstate == MultiBlockState.XMAX_ZMAX_YEDGE)
        {
            return 180;
        }
        else
        {
            return 0;
        }
    }

    protected void registerRack()
    {
        getVariantBuilder(WUTBlocks.RACKENERGY_CONTROLLER.get()).forAllStates(state -> {

            Direction dir = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
            boolean lit = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT);

            return ConfiguredModel.builder()
                    .modelFile(lit ? models().getExistingFile(modLoc("block/rack/controller/controller_on")) : models().getExistingFile(modLoc("block/rack/controller/controller")))
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        getVariantBuilder(WUTBlocks.RACKITEM_CONTROLLER.get()).forAllStates(state -> {

            Direction dir = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
            boolean lit = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT);

            return ConfiguredModel.builder()
                    .modelFile(lit ? models().getExistingFile(modLoc("block/rack/controller/controller_on")) : models().getExistingFile(modLoc("block/rack/controller/controller")))
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        getVariantBuilder(WUTBlocks.RACKFLUID_CONTROLLER.get()).forAllStates(state -> {

            Direction dir = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
            boolean lit = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT);

            return ConfiguredModel.builder()
                    .modelFile(lit ? models().getExistingFile(modLoc("block/rack/controller/controller_on")) : models().getExistingFile(modLoc("block/rack/controller/controller")))
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        getVariantBuilder(WUTBlocks.RACK_TERMINAL.get()).forAllStates(state -> {

            Direction dir = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING);
            boolean formed = state.getValue(geni.withercore.common.world.level.block.state.BlockStateProperties.FORMED);

            return ConfiguredModel.builder()
                    .modelFile(formed ? models().getExistingFile(modLoc("block/rack/terminal/terminal_formed")) : models().getExistingFile(modLoc("block/rack/terminal/terminal")))
                    .rotationX(dir == Direction.DOWN ? 270 : dir == Direction.UP ? 90 : 0)
                    .rotationY(dir == Direction.SOUTH ? 180 : dir == Direction.EAST ? 90 : dir == Direction.WEST ? 270 : 0)
                    .build();
        });

        getVariantBuilder(WUTBlocks.RACK_CASE.get()).forAllStates(state -> {

            MultiBlockState mbstate = state.getValue(geni.withercore.common.world.level.block.state.BlockStateProperties.MBSTATE);

            return ConfiguredModel.builder()
                    .modelFile(getMBStateModel(mbstate))
                    .rotationY(getMBStateYRotation(mbstate))
                    .build();
        });
    }

    private void registerSensors()
    {
        getVariantBuilder(WUTBlocks.FLOORSENSOR.get()).forAllStates(state -> {

            Direction dir = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
            boolean lit = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT);
            boolean covered = state.getValue(FloorSensorBlock.COVERED);

            return ConfiguredModel.builder()
                    .modelFile(lit || covered ? models().getExistingFile(modLoc("block/sensor/floor/floorsensor")) : models().getExistingFile(modLoc("block/sensor/floor/floorsensor")))
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });

        getVariantBuilder(WUTBlocks.WALL_SENSOR.get()).forAllStates(state -> {

            Direction dir = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
            boolean lit = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT);

            return ConfiguredModel.builder()
                    .modelFile(lit ? models().getExistingFile(modLoc("block/sensor/wall/wallsensor_on")) : models().getExistingFile(modLoc("block/sensor/wall/wallsensor")))
                    .rotationY(dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360)
                    .build();
        });
    }

    @Override
    protected void registerStatesAndModels()
    {
        registerCtmMap(WUTBlocks.CTM_CONCRETE);
        registerCtmMap(WUTBlocks.CTM_METAL);
        registerCtmMap(WUTBlocks.CTM_STONE);
        registerCtmMap(WUTBlocks.CTM_WOOD);

        registerBlock(WUTBlocks.WITHERSTEEL_BLOCK);
        registerModelBlock(WUTBlocks.ANGEL, "angel");

        registerRack();
        registerSensors();
        registerSteels();
        registerFluids();

        // Hydro Reservoir BlockState mit horizontaler Ausrichtung
        horizontalBlock(WUTBlocks.HYDRO_RESERVOIR.get(),
                models().cube(
                        "hydro_reservoir",
                        modLoc("block/shared/full00"),
                        modLoc("block/shared/full00"),
                        modLoc("block/shared/full00"),
                        modLoc("block/shared/full00"),
                        modLoc("block/shared/full00"),
                        modLoc("block/shared/full00")
                )
        );

        // Hydro Turbine: Verwendet GeckoLib EntityBlock-Dummy-Modell für Partikel und Platzierung
        ModelFile turbineModel = models().getBuilder("block/hydro_turbine")
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("particle", modLoc("block/shared/full00"));

        getVariantBuilder(WUTBlocks.HYDRO_TURBINE.get())
                .forAllStates(state -> {
                    int yRot = ((int) state.getValue(HydroTurbineBlock.FACING).toYRot() + 180) % 360;
                    return net.neoforged.neoforge.client.model.generators.ConfiguredModel.builder()
                            .modelFile(turbineModel)
                            .rotationY(yRot)
                            .build();
                });

        getVariantBuilder(WUTBlocks.CREATIVE_GENERATOR.get()).forAllStates(state -> {

            boolean lit = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT);

            return ConfiguredModel.builder()
                    .modelFile(lit ? models().getExistingFile(modLoc("block/generator/creative/creative_generator_on")) :
                            models().getExistingFile(modLoc("block/generator/creative/creative_generator")))
                    .build();
        });

        getVariantBuilder(WUTBlocks.ANVIL.get()).forAllStates(state -> {
            return ConfiguredModel.builder()
                    .modelFile(state.getValue(AnvilBlock.LIT) ? models().getExistingFile(modLoc("block/anvil/anvil")) : models().getExistingFile(modLoc("block/anvil/anvil")))
                    .rotationY((((int) state.getValue(AnvilBlock.FACING).toYRot()) + 180) % 360)
                    .build();
        });
    }

    /*
     *
     * SORTOF
     *
     */
    private void registerCtmMap(Map<String, ? extends BlockRegistryObject<? extends Block, ?>> map) {
        for (BlockRegistryObject<? extends Block, ?> blockObj : map.values()) {
            Block block = blockObj.get();
            ctmBlock(block);
        }
    }

    public void ctmConnBlock(Block block) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        String name = blockId.getPath();
        String modId = blockId.getNamespace();

        // Pfade für CTM Modell & Texturen
        String ctmModelPath = "block/ctm/" + name;
        String fullCtmModelLocation = modId + ":" + ctmModelPath;
        String textureBase = modId + ":block/ctm/" + name + "/";

        // 1. CTM Hülle als Modell erzeugen
        models().withExistingParent(ctmModelPath, mcLoc("block/cube_all"))
                .texture("all", textureBase + "particle");

        // 2. Item-Modell erzeugen
        itemModels().withExistingParent(name, modLoc(ctmModelPath));

        // 3. BlockState JSON (Athena CTM Loader + Multipart Kombination)
        registeredBlocks.put(block, () -> {
            JsonObject root = new JsonObject();

            // Athena CTM Konfiguration
            root.addProperty("athena:loader", "athena:ctm");

            JsonObject ctmTextures = new JsonObject();
            ctmTextures.addProperty("center", textureBase + "center");
            ctmTextures.addProperty("empty", textureBase + "empty");
            ctmTextures.addProperty("horizontal", textureBase + "horizontal");
            ctmTextures.addProperty("vertical", textureBase + "vertical");
            ctmTextures.addProperty("particle", textureBase + "particle");
            root.add("ctm_textures", ctmTextures);

            // Multipart Liste für CTM-Hülle, ConnBase & ConnConnect
            JsonArray multipart = new JsonArray();

            // Part 1: CTM Außenhülle (immer aktiv)
            JsonObject ctmPart = new JsonObject();
            JsonObject ctmApply = new JsonObject();
            ctmApply.addProperty("model", fullCtmModelLocation);
            ctmPart.add("apply", ctmApply);
            multipart.add(ctmPart);

            // Part 2: ConnBase Basis-Modell im Inneren (immer aktiv)
            JsonObject basePart = new JsonObject();
            JsonObject baseApply = new JsonObject();
            baseApply.addProperty("model", modId + ":block/ctm/ctmconn/connbase");
            basePart.add("apply", baseApply);
            multipart.add(basePart);

            // Part 3: ConnConnect Teile in allen 6 Richtungen (gesteuert durch Booleans)
            for (Direction dir : Direction.values()) {
                int xRot = 0;
                int yRot = 0;

                switch (dir) {
                    case NORTH -> { xRot = 0; yRot = 0; }
                    case EAST  -> { xRot = 0; yRot = 90; }
                    case SOUTH -> { xRot = 0; yRot = 180; }
                    case WEST  -> { xRot = 0; yRot = 270; }
                    case UP    -> { xRot = 270; yRot = 0; }
                    case DOWN  -> { xRot = 90; yRot = 0; }
                }

                JsonObject connPart = new JsonObject();

                // "when" Bedingung: richtung = true
                JsonObject when = new JsonObject();
                when.addProperty(dir.getName(), "true");
                connPart.add("when", when);

                // "apply" Modell & Rotationen
                JsonObject apply = new JsonObject();
                apply.addProperty("model", modId + ":block/ctm/ctmconn/connconnect");
                if (xRot != 0) apply.addProperty("x", xRot);
                if (yRot != 0) apply.addProperty("y", yRot);
                connPart.add("apply", apply);

                multipart.add(connPart);
            }

            root.add("multipart", multipart);

            return root;
        });
    }

    public void ctmBlock(Block block) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        String fullName = blockId.getPath(); // z.B. "stone_ctm00" oder "concrete_ctm01"
        String modId = blockId.getNamespace();

        // Material und Nummer aus dem Namen extrahieren (z.B. "stone" und "ctm00")
        // Da der Name nach dem Schema "material_ctmXX" aufgebaut ist:
        String[] parts = fullName.split("_");
        String material = parts[0]; // z.B. "stone", "concrete", "wood"
        String textureName = parts[1]; // z.B. "ctm00"

        // Der relative Pfad für das Modell (bleibt flach unter block/ctm/ oder wo du es haben willst)
        String modelRelativePath = "block/ctm/" + fullName;
        String fullModelLocation = modId + ":" + modelRelativePath;

        // WICHTIG: Hier wird der Material-Unterordner in den Texturpfad eingebaut!
        // Ergebnis: modid:block/ctm/stone/ctm00/center etc.
        String textureBase = modId + ":block/ctm/" + material + "/" + textureName + "/";

        // 1. Basis-Modell erzeugen
        models().withExistingParent(modelRelativePath, mcLoc("block/cube_all"))
                .texture("all", textureBase + "particle");

        // 2. Item-Modell erzeugen
        itemModels().withExistingParent(fullName, modLoc(modelRelativePath));

        // 3. BlockState JSON mit Athena CTM Loader
        registeredBlocks.put(block, () -> {
            JsonObject root = new JsonObject();

            JsonObject variants = new JsonObject();
            JsonObject variantObj = new JsonObject();
            variantObj.addProperty("model", fullModelLocation);
            variants.add("", variantObj);
            root.add("variants", variants);

            root.addProperty("athena:loader", "athena:ctm");

            JsonObject ctmTextures = new JsonObject();
            ctmTextures.addProperty("center", textureBase + "center");
            ctmTextures.addProperty("empty", textureBase + "empty");
            ctmTextures.addProperty("horizontal", textureBase + "horizontal");
            ctmTextures.addProperty("vertical", textureBase + "vertical");
            ctmTextures.addProperty("particle", textureBase + "particle");

            root.add("ctm_textures", ctmTextures);

            return root;
        });
    }

    public void giantCtmBlock(Block block, int width, int height) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        String name = blockId.getPath();
        String modId = blockId.getNamespace();

        String modelRelativePath = "block/ctm/" + name;
        String textureBase = modId + ":block/ctm/" + name + "/";

        // 1. Block-Modell und Item-Modell wie gewohnt registrieren
        models().withExistingParent(modelRelativePath, mcLoc("block/cube_all"))
                .texture("all", textureBase + name);

        itemModels().withExistingParent(name, modLoc(modelRelativePath));

        // 2. BlockState JSON entsprechend des Beispiels erstellen
        registeredBlocks.put(block, () -> {
            JsonObject root = new JsonObject();

            // Standard Variant Model auf air setzen (wie im Beispiel)
            JsonObject variants = new JsonObject();
            JsonObject variantObj = new JsonObject();
            variantObj.addProperty("model", "minecraft:block/air");
            variants.add("", variantObj);
            root.add("variants", variants);

            // Athena Giant Loader
            root.addProperty("athena:loader", "athena:giant");

            // Durchnummerierte Texturen generieren (1 bis width * height)
            JsonObject ctmTextures = new JsonObject();
            int totalIndex = 1;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    ctmTextures.addProperty(String.valueOf(totalIndex), textureBase + (totalIndex - 1));
                    totalIndex++;
                }
            }
            ctmTextures.addProperty("particle", textureBase + name);
            root.add("ctm_textures", ctmTextures);

            // Höhe und Breite im JSON speichern
            root.addProperty("height", height);
            root.addProperty("width", width);

            return root;
        });
    }

    public void ctmBlock(Supplier<? extends Block> blockSupplier) {
        ctmBlock(blockSupplier);
    }

    private void registerSimpleBlock(BlockRegistryObject<? extends Block, ? extends BlockItem> block)
    {
        String location = "block/";
        location = location + BuiltInRegistries.BLOCK.getKey(block.get()).getPath();

        ModelFile model = models().getBuilder(location)
                .parent(models().getExistingFile(mcLoc("block/cube_all")))
                .texture("all", modLoc(location));
        super.simpleBlock(block.get(), model);
    }

    private void registerBlock(BlockRegistryObject<? extends Block, ? extends BlockItem> block)
    {
        registerBlock(block, "", "solid");
    }

    private void registerBlock(BlockRegistryObject<? extends Block, ? extends BlockItem> block, String dir, String renderType)
    {
        String location = "block/";
        if(!dir.isEmpty()) {
            location = location + dir + "/";
        }
        location = location + BuiltInRegistries.BLOCK.getKey(block.get()).getPath();

        ModelFile model = models().getBuilder(location)
                .parent(models().getExistingFile(mcLoc("block/cube_all")))
                .texture("all", modLoc(location))
                .renderType(renderType);
        super.simpleBlock(block.get(), model);
    }

    private void registerModelBlock(BlockRegistryObject<? extends Block, ? extends BlockItem> block)
    {
        registerModelBlock(block, "");
    }

    private void registerModelBlock(BlockRegistryObject<? extends Block, ? extends BlockItem> block, String dir)
    {
        String location = "wither/block/";
        if(!dir.isEmpty()) {
            location = location + dir + "/";
        }
        location = location + BuiltInRegistries.BLOCK.getKey(block.get()).getPath();

        ModelFile model = models().getExistingFile(modLoc(location));
        super.simpleBlockWithItem(block.get(), model);
    }

/*    public void createModularConnected(Block block, String baseName, ResourceLocation floorTexture, ResourceLocation borderTexture) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);

        // 1. BODENPLATTE (basierend auf catwalk_north_east_south_west.json)
        BlockModelBuilder floorModel = models().withExistingParent(baseName + "_floor", "block/block")
                .texture("0", floorTexture)
                .texture("particle", floorTexture)
                .element()
                .from(0, 0.5f, 0).to(16, 1.5f, 16)
                .face(Direction.UP).uvs(0, 0, 16, 16).rotation(270).texture("#0").cullface(Direction.UP).end()
                .face(Direction.DOWN).uvs(0, 0, 16, 16).rotation(90).texture("#0").end()
                .end();

        // Bodenplatte ist in jedem Zustand sichtbar
        builder.part().modelFile(floorModel).addModel().end();

        // 2. RAND-MODELL (basierend auf catwalk_bordered.json, Nord-Segment)
        BlockModelBuilder borderModel = models().withExistingParent(baseName + "_border", "block/block")
                .texture("3", borderTexture)
                .texture("particle", borderTexture)
                .element()
                .from(2, 0, 0).to(14, 2, 2)
                .face(Direction.NORTH).uvs(3f, 0f, 13f, 2f).texture("#3").end()
                .face(Direction.SOUTH).uvs(2.5f, 0f, 13.5f, 2f).texture("#3").end()
                .face(Direction.UP).uvs(2.5f, 0f, 13.5f, 2f).texture("#3").end()
                .face(Direction.DOWN).uvs(2.5f, 0f, 13.5f, 2f).texture("#3").end()
                .end();

        // Ränder generieren (0, 90, 180, 270 Grad rotiert)
        builder.part().modelFile(borderModel).rotationY(0).addModel().condition(CatwalkBlock.NORTH, false).end();
        builder.part().modelFile(borderModel).rotationY(90).addModel().condition(CatwalkBlock.EAST, false).end();
        builder.part().modelFile(borderModel).rotationY(180).addModel().condition(CatwalkBlock.SOUTH, false).end();
        builder.part().modelFile(borderModel).rotationY(270).addModel().condition(CatwalkBlock.WEST, false).end();

        // 3. ECK-MODELL (basierend auf catwalk_north_corner.json, Nord-West Ecke)
        BlockModelBuilder cornerModel = models().withExistingParent(baseName + "_corner", "block/block")
                .texture("3", borderTexture)
                .texture("particle", borderTexture)
                .element()
                .from(0, 0, 0).to(2, 2, 2)
                .face(Direction.NORTH).uvs(0, 0, 2, 2).texture("#3").cullface(Direction.NORTH).end()
                .face(Direction.EAST).uvs(0, 0, 2, 2).texture("#3").end()
                .face(Direction.SOUTH).uvs(0, 0, 2, 2).texture("#3").end()
                .face(Direction.WEST).uvs(0, 0, 2, 2).texture("#3").cullface(Direction.WEST).end()
                .face(Direction.UP).uvs(0, 0, 2, 2).texture("#3").end()
                .face(Direction.DOWN).uvs(0, 0, 2, 2).texture("#3").cullface(Direction.DOWN).end()
                .end();

        // Ecken generieren (Nutzt Hilfsmethode zur Vermeidung von Modellüberlappungen)
        addCorner(builder, cornerModel, 0, CatwalkBlock.NORTH, CatwalkBlock.WEST, CatwalkBlock.CORNER_NORTH_WEST);
        addCorner(builder, cornerModel, 90, CatwalkBlock.EAST, CatwalkBlock.NORTH, CatwalkBlock.CORNER_NORTH_EAST);
        addCorner(builder, cornerModel, 180, CatwalkBlock.SOUTH, CatwalkBlock.EAST, CatwalkBlock.CORNER_SOUTH_EAST);
        addCorner(builder, cornerModel, 270, CatwalkBlock.WEST, CatwalkBlock.SOUTH, CatwalkBlock.CORNER_SOUTH_WEST);
    }

    *//**
     * Fügt ein Eckmodell hinzu. Um Z-Fighting zu vermeiden, werden alle 4 möglichen
     * Kombinationen abgedeckt, in denen die Ecke gerendert werden MUSS.
     * Die einzige Kombination, die hier fehlt, ist (side1=true && side2=true && innerCorner=false),
     * was bedeutet, dass der Weg frei ist und keine Ecke gezeichnet werden darf.
     *//*
    private void addCorner(MultiPartBlockStateBuilder builder, BlockModelBuilder cornerModel, int rotationY,
                           BooleanProperty side1, BooleanProperty side2, BooleanProperty innerCorner) {
        // Fall 1: Äußere Ecke (Kein Anschluss an beiden Seiten)
        builder.part().modelFile(cornerModel).rotationY(rotationY).addModel()
                .condition(side1, false).condition(side2, false).end();

        // Fall 2: Abschluss für Seite 2 (Anschluss nur an Seite 1)
        builder.part().modelFile(cornerModel).rotationY(rotationY).addModel()
                .condition(side1, true).condition(side2, false).end();

        // Fall 3: Abschluss für Seite 1 (Anschluss nur an Seite 2)
        builder.part().modelFile(cornerModel).rotationY(rotationY).addModel()
                .condition(side1, false).condition(side2, true).end();

        // Fall 4: Innere Ecke (Anschluss an beiden Seiten, aber kein diagonaler Nachbar)
        builder.part().modelFile(cornerModel).rotationY(rotationY).addModel()
                .condition(side1, true).condition(side2, true).condition(innerCorner, true).end();
    }*/

    @Override
    public String getName()
    {
        return "WitherUtils Blockstates";
    }
}

    /*private void registerFloorSensor()
    {
        getVariantBuilder(WUTBlocks.FLOOR_SENSOR.get()).forAllStates(state -> {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            boolean lit = state.getValue(BlockStateProperties.LIT);
            boolean covered = state.getValue(FloorSensorBlock.COVERED);

            String modelPath = covered ? "block/floor_sensor_covered" : (lit ? "block/floor_sensor_on" : "block/floor_sensor");

            return ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(modLoc(modelPath)))
                    .rotationY((((int) dir.toYRot()) + 180) % 360)
                    .build();
        });
        simpleBlockItem(WUTBlocks.FLOOR_SENSOR.get(), models().getExistingFile(modLoc("block/floor_sensor")));
    }

    private void registerGreenhouse()
    {
        ModelFile baseModel = models().getExistingFile(modLoc("block/greenhouse/greenhouse_base"));
        ModelFile rodModel = models().getExistingFile(modLoc("block/greenhouse/greenhouse_rod"));

        getMultipartBuilder(WUTBlocks.GREENHOUSE.get())
                .part().modelFile(baseModel).addModel().end()
                .part().modelFile(rodModel).addModel().condition(GreenhouseBlock.CORNER_ROD, true).end();

        simpleBlockItem(WUTBlocks.GREENHOUSE.get(), baseModel);
    }

    private void registerMetalDoor()
    {
        getVariantBuilder(WUTBlocks.METAL_DOOR.get()).forAllStates(state -> {
            Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            boolean open = state.getValue(BlockStateProperties.LIT);
            String suffix = open ? "_open" : "";

            return ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(modLoc("block/metal_door/metal_door" + suffix)))
                    .rotationY((((int) dir.toYRot()) + 180) % 360)
                    .build();
        });
        simpleBlockItem(WUTBlocks.METAL_DOOR.get(), models().getExistingFile(modLoc("block/metal_door/metal_door")));
    }

    private void registerRailings()
    {
        ModelFile postModel = models().getExistingFile(modLoc("block/railing/railing_post"));
        ModelFile sideModel = models().getExistingFile(modLoc("block/railing/railing_side"));

        getMultipartBuilder(WUTBlocks.STEELRAILING.get())
                .part().modelFile(postModel).addModel().end()
                .part().modelFile(sideModel).addModel().condition(RailingBlock.NORTH, true).end()
                .part().modelFile(sideModel).rotationY(90).addModel().condition(RailingBlock.EAST, true).end()
                .part().modelFile(sideModel).rotationY(180).addModel().condition(RailingBlock.SOUTH, true).end()
                .part().modelFile(sideModel).rotationY(270).addModel().condition(RailingBlock.WEST, true).end();

        simpleBlockItem(WUTBlocks.STEELRAILING.get(), postModel);
    }

    private void registerSlicedConcrete()
    {
        ModelFile faceModel = models().getExistingFile(modLoc("block/sliced_concrete"));

        getMultipartBuilder(WUTBlocks.SLICEDCONCRETEBLACK.get())
                .part().modelFile(faceModel).rotationX(90).addModel().condition(BlockStateProperties.UP, true).end()
                .part().modelFile(faceModel).rotationX(270).addModel().condition(BlockStateProperties.DOWN, true).end()
                .part().modelFile(faceModel).addModel().condition(BlockStateProperties.NORTH, true).end()
                .part().modelFile(faceModel).rotationY(90).addModel().condition(BlockStateProperties.EAST, true).end()
                .part().modelFile(faceModel).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, true).end()
                .part().modelFile(faceModel).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).end();

        simpleBlockItem(WUTBlocks.SLICEDCONCRETEBLACK.get(), faceModel);

        getMultipartBuilder(WUTBlocks.SLICEDCONCRETEGRAY.get())
                .part().modelFile(faceModel).rotationX(90).addModel().condition(BlockStateProperties.UP, true).end()
                .part().modelFile(faceModel).rotationX(270).addModel().condition(BlockStateProperties.DOWN, true).end()
                .part().modelFile(faceModel).addModel().condition(BlockStateProperties.NORTH, true).end()
                .part().modelFile(faceModel).rotationY(90).addModel().condition(BlockStateProperties.EAST, true).end()
                .part().modelFile(faceModel).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, true).end()
                .part().modelFile(faceModel).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).end();

        simpleBlockItem(WUTBlocks.SLICEDCONCRETEGRAY.get(), faceModel);

        getMultipartBuilder(WUTBlocks.SLICEDCONCRETEWHITE.get())
                .part().modelFile(faceModel).rotationX(90).addModel().condition(BlockStateProperties.UP, true).end()
                .part().modelFile(faceModel).rotationX(270).addModel().condition(BlockStateProperties.DOWN, true).end()
                .part().modelFile(faceModel).addModel().condition(BlockStateProperties.NORTH, true).end()
                .part().modelFile(faceModel).rotationY(90).addModel().condition(BlockStateProperties.EAST, true).end()
                .part().modelFile(faceModel).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, true).end()
                .part().modelFile(faceModel).rotationY(270).addModel().condition(BlockStateProperties.WEST, true).end();

        simpleBlockItem(WUTBlocks.SLICEDCONCRETEWHITE.get(), faceModel);
    }

    private void registerSteelPole()
    {
        getVariantBuilder(WUTBlocks.STEELPOLE.get()).forAllStates(state -> {
            Direction dir = state.getValue(BlockStateProperties.FACING);

            int xRot = dir == Direction.DOWN ? 180 : dir == Direction.UP ? 0 : 90;
            int yRot = dir.getAxis().isVertical() ? 0 : (((int) dir.toYRot()) + 180) % 360;

            return ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(modLoc("block/steel_pole")))
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        });
        simpleBlockItem(WUTBlocks.STEELPOLE.get(), models().getExistingFile(modLoc("block/steel_pole")));
    }

    private void registerTankReservoir()
    {
        ModelFile baseModel = models().getExistingFile(modLoc("block/tank_reservoir/tank_base"));

        getVariantBuilder(WUTBlocks.RESERVOIR.get()).forAllStates(state -> {
            return ConfiguredModel.builder()
                    .modelFile(baseModel)
                    .build();
        });
        simpleBlockItem(WUTBlocks.RESERVOIR.get(), baseModel);
    }*/