package geni.withercore.init;

import geni.withercore.common.registration.impl.ItemDeferredRegister;
import geni.withercore.common.registration.impl.ItemRegistryObject;
import geni.withercore.common.world.item.WitherItem;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.item.*;
import geni.witherutils.common.world.item.pipe.PipeBuildToolItem;
import geni.witherutils.common.world.item.pipe.PipePumpItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public class WUTItems
{
    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(WitherUtils.MODID);

    public static final ItemRegistryObject<Item> ICON = ITEMS.registerItem("icon", Item::new);
    public static final ItemRegistryObject<SteelHammerItem> HAMMER = ITEMS.registerItem("hammer", SteelHammerItem::new);
    public static final ItemRegistryObject<CutterItem> CUTTER = ITEMS.registerItem("cutter", CutterItem::new);
    public static final ItemRegistryObject<WrenchItem> WRENCH = ITEMS.registerItem("wrench", WrenchItem::new);

    public static final ItemRegistryObject<SplineBuildToolItem> SPLINE_BUILD_TOOL = ITEMS.registerItem("spline_build_tool", SplineBuildToolItem::new);
    public static final ItemRegistryObject<PenstockPipeItem> PENSTOCKPIPE = ITEMS.registerItem("penstock_pipe", PenstockPipeItem::new);
    public static final ItemRegistryObject<PowerCableItem> POWER_CABLE = ITEMS.registerItem("power_cable", PowerCableItem::new);
    public static final ItemRegistryObject<RemoteItem> REMOTE = ITEMS.registerItem("remote", RemoteItem::new);
    public static final ItemRegistryObject<PipeBuildToolItem> PIPE_BUILD_TOOL = ITEMS.registerItem("pipe_build_tool", PipeBuildToolItem::new);
    public static final ItemRegistryObject<PipePumpItem> PIPE_PUMP = ITEMS.registerItem("pipe_pump", PipePumpItem::new);

    public static final ItemRegistryObject<WitherItem> WITHERSTEEL_CASE = ITEMS.registerItem("withersteel_case", WitherItem::new);
    public static final ItemRegistryObject<WitherItem> WITHERSTEEL_INGOT = ITEMS.registerItem("withersteel_ingot", WitherItem::new);
    public static final ItemRegistryObject<WitherItem> WITHERSTEEL_NUGGET = ITEMS.registerItem("withersteel_nugget", WitherItem::new);
    public static final ItemRegistryObject<WitherItem> WITHERSTEEL_PLATE = ITEMS.registerItem("withersteel_plate", WitherItem::new);
    public static final ItemRegistryObject<WitherItem> WITHERSTEEL_GEAR = ITEMS.registerItem("withersteel_gear", WitherItem::new);
    public static final ItemRegistryObject<WitherItem> WITHERSTEEL_ROD = ITEMS.registerItem("withersteel_rod", WitherItem::new);

    public static final ItemRegistryObject<Item> SOULBANK_CASE = ITEMS.registerItem("soulbank_case", WitherItem::new);
    public static final ItemRegistryObject<SoulBankItem> SOULBANK_BASIC = ITEMS.registerItem("soulbank_basic", SoulBankItem.Basic::new);
    public static final ItemRegistryObject<SoulBankItem> SOULBANK_ADVANCED = ITEMS.registerItem("soulbank_adv", SoulBankItem.Advanced::new);
    public static final ItemRegistryObject<SoulBankItem> SOULBANK_ULTRA = ITEMS.registerItem("soulbank_ultra", SoulBankItem.Ultra::new);

    /*
     *
     * BUCKETS
     *
     */
    public static final ItemRegistryObject<BucketItem> COLDSLUSH_BUCKET = registerBucket("coldslush_bucket", WUTFluids.COLDSLUSH);

    /*
     *
     * REGISTERING
     *
     */
    public static Item.Properties defaultProps()
    {
        return new Item.Properties();
    }
    public static Item.Properties filledBucketProps()
    {
        return defaultProps().stacksTo(1).craftRemainder(Items.BUCKET);
    }
    private static ItemRegistryObject<Item> register(final String name)
    {
        return register(name, () -> new Item(WUTItems.defaultProps()));
    }
    private static <T extends Item> ItemRegistryObject<T> register(final String name, final Supplier<T> sup)
    {
        return ITEMS.register(name, sup);
    }
    private static ItemRegistryObject<BucketItem> registerBucket(String name, Supplier<? extends Fluid> sup)
    {
        return register(name, () -> new FluidBucketItem(sup.get()));
    }
}
