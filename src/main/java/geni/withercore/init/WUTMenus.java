package geni.withercore.init;

import geni.witherutils.WitherUtils;
import geni.witherutils.common.menu.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WUTMenus {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, WitherUtils.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<DroneMenu>> DRONE_DELIVER =
            MENUS.register("drone", () -> IMenuTypeExtension.create(DroneMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<CardMenu>> BLOCKCARD = MENUS.register("blockcard",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new CardMenu(windowId, inv, inv.player)));

    public static final DeferredHolder<MenuType<?>, MenuType<CutterMenu>> CUTTER = MENUS.register("cutter",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new CutterMenu(windowId, inv, data, ContainerLevelAccess.NULL)));

    public static final DeferredHolder<MenuType<?>, MenuType<DeviceProgMenu>> DEVICE_PROG = MENUS.register("device_prog",
            () -> IMenuTypeExtension.create((windowId, inv, data) -> new DeviceProgMenu(windowId, inv, data)));
}
