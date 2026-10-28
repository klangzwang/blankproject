package geni.witherdata.generator;

import codechicken.lib.datagen.ItemModelProvider;
import geni.withercore.init.WUTBlocks;
import geni.withercore.init.WUTItems;
import geni.withercore.registration.impl.BlockRegistryObject;
import geni.withercore.registration.impl.ItemRegistryObject;
import geni.witherutils.WitherUtils;
import geni.witherutils.client.render.item.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
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
                /*.overrideMaterialLibrary()*/
                .flipV(true)
                .end()
                .texture("particle", modLoc("block/model/shield/shield_sphere"));

        registerLightBlock();

        simpleItem(WUTItems.GPS_TOOL, modLoc("item/anchor"));
        simpleItem(WUTItems.DRONE_DELIVER, modLoc("item/anchor"));

        blockItem(WUTBlocks.ANVIL, modLoc("block/anvil/anvil"));
        blockItem(WUTBlocks.WITHERSTEEL_BLOCK, modLoc("block/" + WUTBlocks.WITHERSTEEL_BLOCK.getId().getPath()));
        blockItem(WUTBlocks.WITHEREARTH, modLoc("block/wither_earth"));
        blockItem(WUTBlocks.DEVICE, modLoc("block/device"));

        simpleItem(WUTItems.PROG_VACUUM, modLoc("item/card_unbound"));
        simpleItem(WUTItems.PROG_MOB_HOVER, modLoc("item/card_unbound"));
        simpleItem(WUTItems.PROG_MOB_SLOW_LORIS, modLoc("item/card_unbound"));
        simpleItem(WUTItems.PROG_MOB_SOLAR, modLoc("item/card_unbound"));
        simpleItem(WUTItems.PROG_MOB_TYPE, modLoc("item/card_unbound"));
        simpleItem(WUTItems.PROG_LEASH, modLoc("item/card_unbound"));
        simpleItem(WUTItems.PROG_FAN, modLoc("item/card_unbound"));
        simpleItem(WUTItems.PROG_HELIUM, modLoc("item/card_unbound"));
        simpleItem(WUTItems.PROG_TAMING, modLoc("item/card_unbound"));
        simpleItem(WUTItems.PROG_OBSERVER, modLoc("item/card_unbound"));
        simpleItem(WUTItems.PROG_REPORT, modLoc("item/card_unbound"));

        dummyBlock(WUTBlocks.SOLARBASIC);
        dummyBlock(WUTBlocks.SOLARADV);
        dummyBlock(WUTBlocks.SOLARULTRA);
        dummyBlock(WUTBlocks.SOLARCASE);

        /*getBuilder(WUTItems.HAMMER.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity"));*/

        dummyItem(WUTItems.ICON);
        dummyItem(WUTItems.WITHERSTEEL_CASE);
        dummyItem(WUTItems.WITHERSTEEL_GEAR);
        dummyItem(WUTItems.WITHERSTEEL_ROD);

        simpleItem(WUTItems.WITHERSTEEL_INGOT, modLoc("item/withersteel_ingot"));
        simpleItem(WUTItems.WITHERSTEEL_NUGGET, modLoc("item/withersteel_nugget"));
        simpleItem(WUTItems.SOULBANK_CASE, modLoc("item/soulbank/soulbank"));
        simpleItem(WUTItems.ANCHOR, modLoc("item/anchor"));

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
        withExistingParent(WUTBlocks.DRONEPLATFORM.getName(), modLoc("block/droneplatform/droneplatform_master"));
        withExistingParent(WUTBlocks.DRONEPLATFORM_IO.getName(), modLoc("block/droneplatform/droneplatform_io_single"));

        withExistingParent(WUTBlocks.RACK_CASE.getId().getPath(), modLoc("block/rack/casing/case_none"));
        withExistingParent(WUTBlocks.RACK_TERMINAL.getId().getPath(), modLoc("block/rack/terminal/terminal"));
        withExistingParent(WUTBlocks.RACKITEM_CONTROLLER.getId().getPath(), modLoc("block/rack/controller/controller_on"));
        withExistingParent(WUTBlocks.RACKFLUID_CONTROLLER.getId().getPath(), modLoc("block/rack/controller/controller_on"));
        withExistingParent(WUTBlocks.RACKENERGY_CONTROLLER.getId().getPath(), modLoc("block/rack/controller/controller_on"));

        withExistingParent(WUTBlocks.VANISH_BLOCK.getId().getPath(), modLoc("block/vanish_block"));
        withExistingParent(WUTBlocks.LINES.getId().getPath(), modLoc("block/lines"));

        /*withExistingParent(WUTBlocks.STEEL_POLE.getName(), mcLoc("block/block"))
                .customLoader(CompositeModelBuilder::begin)
                .child("part_1", nested()
                        .parent(getExistingFile(modLoc("block/pole/steel_foot"))))
                .child("part_2", nested()
                        .parent(getExistingFile(modLoc("block/pole/steel_pillar"))))
                .end();*/

        /*withExistingParent(WUTBlocks.STEEL_CATWALK.getName(), mcLoc("block/block"))
                .customLoader(CompositeModelBuilder::begin)
                .child("part_1", nested()
                        .parent(getExistingFile(modLoc("block/catwalk/catwalk_north_east_south_west"))))
                .child("part_2", nested()
                        .parent(getExistingFile(modLoc("block/catwalk/catwalk_bordered"))))
                .end();*/

        clazz(WUTItems.ICON.get(), RenderModularIcon.class);
        clazz(WUTItems.WITHERSTEEL_CASE.get(), RenderModularCase.class);
        clazz(WUTItems.WITHERSTEEL_GEAR.get(), RenderModularGear.class);
        clazz(WUTItems.WITHERSTEEL_ROD.get(), RenderModularRod.class);
        clazz(WUTItems.HAMMER.get(), RenderModularHammer.class);
        clazz(WUTItems.CUTTER.get(), RenderModularCutter.class);
        clazz(WUTItems.HOTINGOT.get(), RenderModularIngot.class);
        clazz(WUTBlocks.SOLARCASE.get(), RenderModularSolarPanel.SOLAR_CASE.class);
        clazz(WUTBlocks.SOLARBASIC.get(), RenderModularSolarPanel.SOLAR_BASIC.class);
        clazz(WUTBlocks.SOLARADV.get(), RenderModularSolarPanel.SOLAR_ADV.class);
        clazz(WUTBlocks.SOLARULTRA.get(), RenderModularSolarPanel.SOLAR_ULTRA.class);
        clazz(WUTBlocks.STEEL_POLE.get(), RenderModularPole.class);

        singleTexture("bluelimbo_bucket", mcLoc("item/generated"), "layer0", modLoc("item/fluid/bluelimbo_bucket"));
        singleTexture("coldslush_bucket", mcLoc("item/generated"), "layer0", modLoc("item/fluid/coldslush_bucket"));
        singleTexture("experience_bucket", mcLoc("item/generated"), "layer0", modLoc("item/fluid/experience_bucket"));
        singleTexture("fertilizer_bucket", mcLoc("item/generated"), "layer0", modLoc("item/fluid/fertilizer_bucket"));
        singleTexture("portium_bucket", mcLoc("item/generated"), "layer0", modLoc("item/fluid/portium_bucket"));
        singleTexture("redresin_bucket", mcLoc("item/generated"), "layer0", modLoc("item/fluid/redresin_bucket"));
        singleTexture("soulful_bucket", mcLoc("item/generated"), "layer0", modLoc("item/fluid/soulful_bucket"));
        singleTexture("witherwater_bucket", mcLoc("item/generated"), "layer0", modLoc("item/fluid/witherwater_bucket"));

        singleTexture(WUTItems.FEATHER.getId().getPath(), mcLoc("item/generated"), "layer0", modLoc("item/blackfeather"));

        withExistingParent(WUTItems.EGG_CURSEDZOMBIE.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(WUTItems.EGG_CURSEDCREEPER.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(WUTItems.EGG_CURSEDSKELETON.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(WUTItems.EGG_CURSEDSPIDER.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(WUTItems.EGG_CURSEDDRYHEAD.getId().getPath(), mcLoc("item/template_spawn_egg"));

        dummyBlock(WUTBlocks.BLUELIMBO);
        dummyBlock(WUTBlocks.COLDSLUSH);
        dummyBlock(WUTBlocks.EXPERIENCE);
        dummyBlock(WUTBlocks.FERTILIZER);
        dummyBlock(WUTBlocks.PORTIUM);
        dummyBlock(WUTBlocks.REDRESIN);
        dummyBlock(WUTBlocks.SOULFUL);
        dummyBlock(WUTBlocks.WITHERWATER);

        /*ItemModelBuilder card = withExistingParent(WUTItems.CARD.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", "witherutils:item/card_unbound");
        ModelFile cardBound = withExistingParent(WUTItems.CARD.getId().getPath() + "_bound", mcLoc("item/generated"))
                .texture("layer0", "witherutils:item/card_bound");
        card
                .override()
                .predicate(WitherUtils.loc("bound"), 1.0f)
                .model(cardBound)
                .end();*/

        //
        ModelFile card = withExistingParent(WUTItems.CARD.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/card_unbound"));

        ModelFile cardBound = withExistingParent(WUTItems.CARD.getId().getPath() + "_bound", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/card_bound"));

        getBuilder(WUTItems.WRENCH.getId().getPath())
                .override().predicate(WitherUtils.loc("bound"), 0.0f).model(card).end()
                .override().predicate(WitherUtils.loc("bound"), 1.0f).model(cardBound).end();

        //
        ModelFile wrenchNormal = withExistingParent(WUTItems.WRENCH.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/wrench"));

        ModelFile wrenchUsing = withExistingParent(WUTItems.WRENCH.getId().getPath() + "_using", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/wrench_using"));

        getBuilder(WUTItems.WRENCH.getId().getPath())
                .override().predicate(WitherUtils.loc("using"), 0.0f).model(wrenchNormal).end()
                .override().predicate(WitherUtils.loc("using"), 1.0f).model(wrenchUsing).end();

        //
        ModelFile batteryNormal = withExistingParent(WUTItems.BATTERY.getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/battery/battery"))
                .texture("layer1", modLoc("item/battery/battery_blink"));

        ModelFile battery25 = withExistingParent(WUTItems.BATTERY.getId().getPath() + "_25", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/battery/battery"))
                .texture("layer1", modLoc("item/battery/battery25"));

        ModelFile battery50 = withExistingParent(WUTItems.BATTERY.getId().getPath() + "_50", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/battery/battery"))
                .texture("layer1", modLoc("item/battery/battery50"));

        ModelFile battery75 = withExistingParent(WUTItems.BATTERY.getId().getPath() + "_75", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/battery/battery"))
                .texture("layer1", modLoc("item/battery/battery75"));

        ModelFile battery100 = withExistingParent(WUTItems.BATTERY.getId().getPath() + "_100", mcLoc("item/generated"))
                .texture("layer0", modLoc("item/battery/battery"))
                .texture("layer1", modLoc("item/battery/battery100"));

        getBuilder(WUTItems.BATTERY.getId().getPath())
                .override().predicate(WitherUtils.loc("charge"), 0.00f).model(batteryNormal).end()
                .override().predicate(WitherUtils.loc("charge"), 0.25f).model(battery25).end()
                .override().predicate(WitherUtils.loc("charge"), 0.50f).model(battery50).end()
                .override().predicate(WitherUtils.loc("charge"), 0.75f).model(battery75).end()
                .override().predicate(WitherUtils.loc("charge"), 1.00f).model(battery100).end();
    }

    private void registerLightBlock() {

        ModelFile model;
        String path = WUTBlocks.LIGHT.getRegistryName().getPath();
        model = new ModelFile.UncheckedModelFile(modLoc("block/light"));

        getBuilder(path).parent(model)
                .transforms().transform(ItemDisplayContext.GUI)
                .rotation(30, -135, 180)
                .translation(0,-6,0)
                .scale(0.625f, 0.625f, 0.625f)
                .end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                .rotation(-45,0,90)
                .translation(-7.5f, 6.5f, 4.5f)
                .scale(1f,1f,1f)
                .end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(-45,0,90)
                .translation(-7.5f, 6.5f, 4.5f)
                .scale(1f,1f,1f)
                .end()
                .transform(ItemDisplayContext.FIXED)
                .rotation(0,-180,0)
                .scale(1f,1f,1f)
                .translation(0f, 7.5f, -8f)
                .end()
                .transform(ItemDisplayContext.HEAD)
                .scale(1f,1f,1f)
                .translation(0f,14f,1.5f)
                .end();
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
