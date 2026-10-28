package test.withertest;

import geni.withercore.common.registration.impl.*;
import geni.withercore.init.WUTItems;
import geni.witherutils.WitherUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import test.withertest.developer.*;

public class WitherTest {

    public static final String MODID = "withertest";

    public static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(WitherUtils.MODID);
    public static final TileEntityTypeDeferredRegister TILE_ENTITY_TYPES = new TileEntityTypeDeferredRegister(WitherUtils.MODID);
    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(WitherUtils.MODID);
    public static final DeferredRegister<CreativeModeTab> TAB_TYPES = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WitherUtils.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, WitherUtils.MODID);

	public static void onModConstruction(IEventBus modBus)
    {
        modBus.addListener(WitherTest::registerScreens);
    }

    public static void registerScreens(RegisterMenuScreensEvent event)
    {
        event.register(DEVELOPER_MENU.get(), DeveloperScreen::new);
    }

    public static final BlockRegistryObject<DeveloperBlock, BlockItem> DEVELOPER = BLOCKS.register("developer", DeveloperBlock::new);
    public static final BlockRegistryObject<DeveloperMultiBlock, BlockItem> DEVELOPER_MB = BLOCKS.register("developermb", DeveloperMultiBlock::new);
    public static final TileEntityTypeRegistryObject<DeveloperBlockEntity> DEVELOPER_TE = TILE_ENTITY_TYPES.mekBuilder(DEVELOPER, DeveloperBlockEntity::new).build();
    public static final DeferredHolder<MenuType<?>, MenuType<DeveloperMenu>> DEVELOPER_MENU = MENUS.register("developer", () -> IMenuTypeExtension.create(DeveloperMenu::new));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TABWU_TEST = TAB_TYPES
            .register("tabwu_test", () -> {
                CreativeModeTab tab = CreativeModeTab.builder()
                        .backgroundTexture(CreativeModeTab.createTextureLocation("wither_blue"))
                        .icon(() -> new ItemStack(WUTItems.ICON.get()))
                        .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                        .title(Component.translatable("itemGroup.witherutils.test"))
                        .displayItems((features, output) -> {

                            output.accept(new ItemStack(DEVELOPER.get()));

                        })
                        .build();
                return tab;
            });

    public static void init(IEventBus bus) {
        TAB_TYPES.register(bus);
    }

    public WitherTest(IEventBus modEventBus) {

        TAB_TYPES.register(modEventBus);
        WitherTest.BLOCKS.register(modEventBus);
        WitherTest.TILE_ENTITY_TYPES.register(modEventBus);
        WitherTest.ITEMS.register(modEventBus);
        WitherTest.MENUS.register(modEventBus);

        modEventBus.addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }
}
