package geni.witherdata.generator;

import codechicken.lib.datagen.ItemModelProvider;
import geni.withercore.common.registration.impl.BlockRegistryObject;
import geni.withercore.common.registration.impl.ItemRegistryObject;
import geni.withercore.init.WUTBlocks;
import geni.withercore.init.WUTItems;
import geni.witherutils.WitherUtils;
import geni.witherutils.client.item.render.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class WitherUtilsItemModels extends ItemModelProvider {

    public WitherUtilsItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), WitherUtils.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        withExistingParent("shield_sphere", mcLoc("item/generated"))
                .customLoader(ObjModelBuilder::begin)
                .modelLocation(modLoc("models/wither/shield/shield_sphere.obj"))
                .automaticCulling(true)
                .flipV(true)
                .end()
                .texture("particle", modLoc("block/model/shield/shield_sphere"));

        blockItem(WUTBlocks.ANVIL, modLoc("block/anvil/anvil"));
        blockItem(WUTBlocks.WITHERSTEEL_BLOCK, modLoc("block/" + WUTBlocks.WITHERSTEEL_BLOCK.getId().getPath()));

        dummyBlock(WUTBlocks.SOLARBASIC);
        dummyBlock(WUTBlocks.SOLARADV);
        dummyBlock(WUTBlocks.SOLARULTRA);
        dummyBlock(WUTBlocks.SOLARCASE);

        dummyItem(WUTItems.ICON);
        dummyItem(WUTItems.WITHERSTEEL_CASE);
        dummyItem(WUTItems.WITHERSTEEL_GEAR);
        dummyItem(WUTItems.WITHERSTEEL_ROD);

        simpleItem(WUTItems.WITHERSTEEL_INGOT, modLoc("item/withersteel_ingot"));
        simpleItem(WUTItems.WITHERSTEEL_NUGGET, modLoc("item/withersteel_nugget"));
        simpleItem(WUTItems.SOULBANK_CASE, modLoc("item/soulbank/soulbank"));

        withExistingParent(WUTItems.SOULBANK_BASIC.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", "witherutils:item/soulbank/soulbank")
                .texture("layer1", "witherutils:item/soulbank/soulbank_basic");
        withExistingParent(WUTItems.SOULBANK_ADVANCED.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", "witherutils:item/soulbank/soulbank")
                .texture("layer1", "witherutils:item/soulbank/soulbank_adv");
        withExistingParent(WUTItems.SOULBANK_ULTRA.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", "witherutils:item/soulbank/soulbank")
                .texture("layer1", "witherutils:item/soulbank/soulbank_ultra");

        withExistingParent(WUTBlocks.CREATIVE_GENERATOR.getName(), modLoc("block/generator/creative/creative_generator"));

        withExistingParent(WUTBlocks.RACK_CASE.getId().getPath(), modLoc("block/rack/casing/case_none"));
        withExistingParent(WUTBlocks.RACK_TERMINAL.getId().getPath(), modLoc("block/rack/terminal/terminal"));
        withExistingParent(WUTBlocks.RACKITEM_CONTROLLER.getId().getPath(), modLoc("block/rack/controller/controller_on"));
        withExistingParent(WUTBlocks.RACKFLUID_CONTROLLER.getId().getPath(), modLoc("block/rack/controller/controller_on"));
        withExistingParent(WUTBlocks.RACKENERGY_CONTROLLER.getId().getPath(), modLoc("block/rack/controller/controller_on"));

        clazz(WUTItems.DRONESTICK.get(), RenderModularStick.class);
        clazz(WUTItems.DRONE.get(), RenderModularDrone.class);
        clazz(WUTItems.GUARD_DRONE.get(), RenderModularDrone.class);
        clazz(WUTItems.ICON.get(), RenderModularIcon.class);
        clazz(WUTItems.WITHERSTEEL_CASE.get(), RenderModularCase.class);
        clazz(WUTItems.WITHERSTEEL_GEAR.get(), RenderModularGear.class);
        clazz(WUTItems.WITHERSTEEL_ROD.get(), RenderModularRod.class);
        clazz(WUTItems.HAMMER.get(), RenderModularHammer.class);
        clazz(WUTItems.CUTTER.get(), RenderModularCutter.class);
        clazz(WUTBlocks.SOLARCASE.get(), RenderModularSolarPanel.SOLAR_CASE.class);
        clazz(WUTBlocks.SOLARBASIC.get(), RenderModularSolarPanel.SOLAR_BASIC.class);
        clazz(WUTBlocks.SOLARADV.get(), RenderModularSolarPanel.SOLAR_ADV.class);
        clazz(WUTBlocks.SOLARULTRA.get(), RenderModularSolarPanel.SOLAR_ULTRA.class);
        clazz(WUTBlocks.STEEL_POLE.get(), RenderModularPole.class);

        ModelFile wrenchNormal = withExistingParent(WUTItems.WRENCH.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/wrench"));

        ModelFile wrenchUsing = withExistingParent(WUTItems.WRENCH.getId().getPath() + "_using", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/wrench_using"));

        getBuilder(WUTItems.WRENCH.getId().getPath())
                .override().predicate(WitherUtils.loc("using"), 0.0f).model(wrenchNormal).end()
                .override().predicate(WitherUtils.loc("using"), 1.0f).model(wrenchUsing).end();

        singleTexture("coldslush_bucket", mcLoc("item/generated"), "layer0", modLoc("item/fluid/coldslush_bucket"));
        blockItem(WUTBlocks.COLDSLUSH, ResourceLocation.fromNamespaceAndPath(WUTBlocks.COLDSLUSH.getId().getNamespace(),
                "block/fluid/" + WUTBlocks.COLDSLUSH.getId().getPath()));

        // Block-Items
        withExistingParent("hydro_reservoir", modLoc("block/hydro_reservoir"));
        withExistingParent("hydro_turbine", modLoc("block/hydro_turbine"));

        // Penstock Pipe Hand-Werkzeug (Standard generated item mit Rohr-Icon)
        singleTexture("penstock_pipe",
                mcLoc("item/generated"),
                "layer0",
                modLoc("item/anchor"));
    }

    protected void simpleItem(ItemRegistryObject<? extends Item> item) {
        simpleItem(item, "item");
    }

    @SuppressWarnings("ConstantConditions")
    protected void simpleItem(ItemRegistryObject<? extends Item> item, String textureFolder) {
        ResourceLocation reg = item.getId();
        simpleItem(item, ResourceLocation.fromNamespaceAndPath(reg.getNamespace(), textureFolder + "/" + reg.getPath()));
    }

    @SuppressWarnings("ConstantConditions")
    protected void simpleItem(ItemRegistryObject<? extends Item> item, ResourceLocation texture) {
        ResourceLocation reg = item.getId();
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture);
    }

    @SuppressWarnings("ConstantConditions")
    protected void multiLayerItem(ItemRegistryObject<? extends Item> item, ResourceLocation texture, ResourceLocation overlay) {
        ResourceLocation reg = item.getId();
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture)
                .texture("layer1", overlay);
    }

    protected void multiLayerItem(Item item, ResourceLocation texture, ResourceLocation overlay) {
        ResourceLocation reg = BuiltInRegistries.ITEM.getKey(item);
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture)
                .texture("layer1", overlay);
    }

    protected void blockItem(BlockRegistryObject<? extends Block, ? extends BlockItem> block) {
        if (block == null) return;
        ResourceLocation reg = block.getId();
        blockItem(block, ResourceLocation.fromNamespaceAndPath(reg.getNamespace(), "block/" + reg.getPath()));
    }

    protected void blockItem(BlockRegistryObject<? extends Block, ? extends BlockItem> block, ResourceLocation blockModel) {
        if (block == null) return;
        ResourceLocation reg = block.getId();
        getBuilder(reg.getPath()).parent(new ModelFile.UncheckedModelFile(blockModel));
    }

    protected void dummyBlock(BlockRegistryObject<? extends Block, ? extends BlockItem> block) {
        getBuilder(block.getId().getPath()).parent(new ModelFile.UncheckedModelFile("builtin/generated"));
    }

    protected void dummyItem(ItemRegistryObject<? extends Item> item) {
        getBuilder(item.getId().getPath()).parent(new ModelFile.UncheckedModelFile("builtin/generated"));
    }

    /*@SuppressWarnings("unused")
	private void simpleItem(DeferredItem<? extends Item> item)
    {
        simpleItem(item, "item");
    }

    private void simpleItem(DeferredItem<? extends Item> item, String textureFolder)
    {
        ResourceLocation reg = item.getId();
        simpleItem(item, ResourceLocation.fromNamespaceAndPath(reg.getNamespace(), textureFolder + "/" + reg.getPath()));
    }

    private void simpleItem(DeferredItem<? extends Item> item, ResourceLocation texture)
    {
        ResourceLocation reg = item.getId();
        getBuilder(reg.getPath())
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", texture);
    }*/

    public void registerBuiltInEntity(ItemRegistryObject<?> item, BlockRegistryObject<?, ?> block)
    {
		if(item != null)
		{
	        getBuilder(item.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"));
		}
		if(block != null)
		{
	        getBuilder(block.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"));
		}
    }

    @Override
    public String getName() {
        return "WitherUtils Item Models";
    }
}
