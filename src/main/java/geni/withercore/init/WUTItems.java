package geni.withercore.init;

import geni.withercore.item.WitherItem;
import geni.withercore.item.WitherUtilsIconItem;
import geni.withercore.registration.impl.ItemDeferredRegister;
import geni.withercore.registration.impl.ItemRegistryObject;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.item.*;
import geni.witherutils.common.world.item.device.*;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.function.Supplier;

public class WUTItems
{
    public static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(WitherUtils.MODID);

    public static final ItemRegistryObject<WitherUtilsIconItem> ICON = ITEMS.registerItem("icon", WitherUtilsIconItem::new);
    public static final ItemRegistryObject<SteelHammerItem> HAMMER = ITEMS.registerItem("hammer", SteelHammerItem::new);
    public static final ItemRegistryObject<CutterItem> CUTTER = ITEMS.registerItem("cutter", CutterItem::new);
    public static final ItemRegistryObject<WrenchItem> WRENCH = ITEMS.registerItem("wrench", WrenchItem::new);
    public static final ItemRegistryObject<DroneItem> DRONE_DELIVER = ITEMS.registerItem("drone", DroneItem::new);
    public static final ItemRegistryObject<GPSToolItem> GPS_TOOL = ITEMS.registerItem("gps_tool", GPSToolItem::new);
    public static final ItemRegistryObject<BatteryItem> BATTERY = ITEMS.registerItem("battery", BatteryItem::new);

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

    public static final ItemRegistryObject<CardItem> CARD = ITEMS.registerItem("card", CardItem::new);
    public static final ItemRegistryObject<WitherItem> ANCHOR = ITEMS.registerItem("anchor", CardItem::new);

    public static final ItemRegistryObject<Item> FEATHER = register("feather");

    public static final ItemRegistryObject<HotIngotItem> HOTINGOT = ITEMS.registerItem("hotingot", HotIngotItem::new);

    /*
     *
     * DEVICEPROGS
     *
     */
    public static final ItemRegistryObject<VacuumProgItem> PROG_VACUUM = ITEMS.registerItem("prog_vacuum", VacuumProgItem::new);
    public static final ItemRegistryObject<MobHoverProgItem> PROG_MOB_HOVER = ITEMS.registerItem("prog_mob_hover", MobHoverProgItem::new);
    public static final ItemRegistryObject<MobSlowLorisProgItem> PROG_MOB_SLOW_LORIS = ITEMS.registerItem("prog_mob_slow_loris", MobSlowLorisProgItem::new);
    public static final ItemRegistryObject<MobSolarProgItem> PROG_MOB_SOLAR = ITEMS.registerItem("prog_mob_solar", MobSolarProgItem::new);
    public static final ItemRegistryObject<MobTypeProgItem> PROG_MOB_TYPE = ITEMS.registerItem("prog_mob_type", MobTypeProgItem::new);
    public static final ItemRegistryObject<MobLeashProgItem> PROG_LEASH = ITEMS.registerItem("prog_leash", MobLeashProgItem::new);
    public static final ItemRegistryObject<FanProgItem> PROG_FAN = ITEMS.registerItem("prog_fan", FanProgItem::new);
    public static final ItemRegistryObject<HeliumProgItem> PROG_HELIUM = ITEMS.registerItem("prog_helium", HeliumProgItem::new);
    public static final ItemRegistryObject<DeviceTamingProgItem> PROG_TAMING = ITEMS.registerItem("prog_taming", DeviceTamingProgItem::new);
    public static final ItemRegistryObject<DeviceObserverProgItem> PROG_OBSERVER = ITEMS.registerItem("prog_observer", DeviceObserverProgItem::new);
    public static final ItemRegistryObject<DeviceReportProgItem> PROG_REPORT = ITEMS.registerItem("prog_report", DeviceReportProgItem::new);

    /*
     *
     * SHIELD
     *
     */
    public static final ItemRegistryObject<WitherItem> SHIELDBASIC = ITEMS.registerItem("shield_basic", WitherItem::new);
    public static final ItemRegistryObject<ShieldSteelItem> SHIELDHERO = ITEMS.registerItem("shield_hero", ShieldSteelItem.Hero::new);
    public static final ItemRegistryObject<ShieldSteelItem> SHIELDROTTEN = ITEMS.registerItem("shield_rotten", ShieldSteelItem.Rotten::new);
    public static final ItemRegistryObject<ShieldSteelItem> SHIELDENERGY = ITEMS.registerItem("shield_energy", ShieldSteelItem.Energy::new);

    /*
     *
     * EGGS
     *
     */
    public static final ItemRegistryObject<Item> EGG_CURSEDZOMBIE = register("egg_cursedzombie", () -> new DeferredSpawnEggItem(WUTEntities.CURSEDZOMBIE, 0x3F8439, 0x9D612A, new Item.Properties()));
    public static final ItemRegistryObject<Item> EGG_CURSEDCREEPER = register("egg_cursedcreeper", () -> new DeferredSpawnEggItem(WUTEntities.CURSEDCREEPER, 0x51857A, 0x7F6258, new Item.Properties()));
    public static final ItemRegistryObject<Item> EGG_CURSEDSKELETON = register("egg_cursedskeleton", () -> new DeferredSpawnEggItem(WUTEntities.CURSEDSKELETON, 0x7CE2E5, 0x564645, new Item.Properties()));
    public static final ItemRegistryObject<Item> EGG_CURSEDSPIDER = register("egg_cursedspider", () -> new DeferredSpawnEggItem(WUTEntities.CURSEDSPIDER, 0x9F8439, 0x364645, new Item.Properties()));
    public static final ItemRegistryObject<Item> EGG_CURSEDDRYHEAD = register("egg_curseddryhead", () -> new DeferredSpawnEggItem(WUTEntities.CURSEDDRYHEAD, 0x121212, 0x464646, new Item.Properties()));

    /*
     *
     * BUCKETS
     *
     */
    public static final ItemRegistryObject<BucketItem> BLUELIMBO_BUCKET = registerBucket("bluelimbo_bucket", WUTFluids.BLUELIMBO);
    public static final ItemRegistryObject<BucketItem> COLDSLUSH_BUCKET = registerBucket("coldslush_bucket", WUTFluids.COLDSLUSH);
    public static final ItemRegistryObject<BucketItem> EXPERIENCE_BUCKET = registerBucket("experience_bucket", WUTFluids.EXPERIENCE);
    public static final ItemRegistryObject<BucketItem> FERTILIZER_BUCKET = registerBucket("fertilizer_bucket", WUTFluids.FERTILIZER);
    public static final ItemRegistryObject<BucketItem> PORTIUM_BUCKET = registerBucket("portium_bucket", WUTFluids.PORTIUM);
    public static final ItemRegistryObject<BucketItem> REDRESIN_BUCKET = registerBucket("redresin_bucket", WUTFluids.REDRESIN);
    public static final ItemRegistryObject<BucketItem> SOULFUL_BUCKET = registerBucket("soulful_bucket", WUTFluids.SOULFUL);
    public static final ItemRegistryObject<BucketItem> WITHERWATER_BUCKET = registerBucket("witherwater_bucket", WUTFluids.WITHERWATER);

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
