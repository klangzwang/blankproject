package geni.withercore.init;

import geni.witherutils.WitherUtils;
import geni.witherutils.common.menu.CutterMenu;
import geni.witherutils.common.menu.ScreenMenu;
import geni.witherutils.common.menu.SlotControllerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WUTMenus {

        public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU,
                        WitherUtils.MODID);

        public static final DeferredHolder<MenuType<?>, MenuType<CutterMenu>> CUTTER = MENUS.register("cutter",
                        () -> IMenuTypeExtension.create((windowId, inv, data) -> new CutterMenu(windowId, inv, data,
                                        ContainerLevelAccess.NULL)));

        public static final DeferredHolder<MenuType<?>, MenuType<ScreenMenu>> SCREEN = MENUS.register("screen",
                () -> IMenuTypeExtension.create((windowId, inv, data) -> new ScreenMenu(windowId, inv, data)));

        public static final DeferredHolder<MenuType<?>, MenuType<SlotControllerMenu>> SLOT_CONTROLLER = MENUS.register(
                "slot_controller",
                () -> IMenuTypeExtension.create((windowId, inv, data) -> new SlotControllerMenu(windowId, inv, data)));
}
