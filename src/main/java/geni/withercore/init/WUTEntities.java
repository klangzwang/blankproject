package geni.withercore.init;

import java.util.function.Supplier;

import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.entity.bolt.CursedLightningBolt;
import geni.witherutils.common.world.entity.cursed.creeper.CursedCreeper;
import geni.witherutils.common.world.entity.cursed.dryhead.CursedDryHead;
import geni.witherutils.common.world.entity.cursed.skeleton.CursedSkeleton;
import geni.witherutils.common.world.entity.cursed.spider.CursedSpider;
import geni.witherutils.common.world.entity.cursed.zombie.CursedZombie;
import geni.witherutils.common.world.entity.drone.DroneDeliverEntity;
import geni.witherutils.common.world.entity.naked.ChickenNaked;
import geni.witherutils.common.world.entity.portal.Portal;
import geni.witherutils.common.world.entity.projectile.ThrownSteelShield;
import geni.witherutils.common.world.entity.soulorb.SoulOrb;
import geni.witherutils.common.world.entity.soulorb.SoulOrbProjectile;
import geni.witherutils.common.world.entity.worm.Worm;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WUTEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, WitherUtils.MODID);

    public static final Supplier<EntityType<Worm>> WORM = register("worm", WUTEntities::worm);
    public static final Supplier<EntityType<SoulOrb>> SOULORB = register("soulorb", WUTEntities::soulorb);
    public static final Supplier<EntityType<SoulOrbProjectile>> SOULORBPRO = register("soulorb_projectile", WUTEntities::soulorbpro);
    public static final Supplier<EntityType<CursedLightningBolt>> CURSEDBOLT = register("cursed_bolt", WUTEntities::cursedbolt);
    public static final Supplier<EntityType<CursedCreeper>> CURSEDCREEPER = register("cursedcreeper", WUTEntities::cursedcreeper);
    public static final Supplier<EntityType<CursedDryHead>> CURSEDDRYHEAD = register("curseddryhead", WUTEntities::curseddryhead);
    public static final Supplier<EntityType<CursedSkeleton>> CURSEDSKELETON = register("cursedskeleton", WUTEntities::cursedskeleton);
    public static final Supplier<EntityType<CursedSpider>> CURSEDSPIDER = register("cursedspider", WUTEntities::cursedspider);
    public static final Supplier<EntityType<CursedZombie>> CURSEDZOMBIE = register("cursedzombie", WUTEntities::cursedzombie);
    public static final Supplier<EntityType<ChickenNaked>> CHICKENNAKED = register("chickennaked", WUTEntities::chickennaked);
    public static final Supplier<EntityType<Portal>> PORTAL = register("portal", WUTEntities::portal);
    public static final Supplier<EntityType<DroneDeliverEntity>> DRONE_DELIVER = register("drone", WUTEntities::drone);
    public static final Supplier<EntityType<ThrownSteelShield>> THROWN_STEEL_SHIELD = register("thrown_steel_shield", WUTEntities::thrownSteelShield);

    private static <E extends Entity> Supplier<EntityType<E>> register(final String name, final Supplier<EntityType.Builder<E>> sup)
    {
        return ENTITY_TYPES.register(name, () -> sup.get().build(name));
    }

    private static EntityType.Builder<CursedLightningBolt> cursedbolt()
    {
        return EntityType.Builder.of(CursedLightningBolt::new, MobCategory.MISC)
                .noSave()
                .sized(0.0F, 0.0F)
                .clientTrackingRange(16)
                .updateInterval(Integer.MAX_VALUE);
    }

    private static EntityType.Builder<CursedZombie> cursedzombie()
    {
        return EntityType.Builder.of(CursedZombie::new, MobCategory.MONSTER)
                .sized(0.6F, 1.95F)
                .setTrackingRange(64)
                .setUpdateInterval(3)
                .fireImmune()
                .setShouldReceiveVelocityUpdates(true);
    }
    private static EntityType.Builder<CursedCreeper> cursedcreeper()
    {
        return EntityType.Builder.of(CursedCreeper::new, MobCategory.MONSTER)
                .sized(0.6F, 1.7F)
                .setTrackingRange(64)
                .setUpdateInterval(3)
                .fireImmune()
                .setShouldReceiveVelocityUpdates(true);
    }
    private static EntityType.Builder<CursedSkeleton> cursedskeleton()
    {
        return EntityType.Builder.of(CursedSkeleton::new, MobCategory.MONSTER)
                .sized(0.6F, 1.95F)
                .setTrackingRange(64)
                .setUpdateInterval(3)
                .fireImmune()
                .setShouldReceiveVelocityUpdates(true);
    }
    private static EntityType.Builder<CursedSpider> cursedspider()
    {
        return EntityType.Builder.of(CursedSpider::new, MobCategory.MONSTER)
                .sized(0.6F, 1.95F)
                .setTrackingRange(64)
                .setUpdateInterval(3)
                .fireImmune()
                .setShouldReceiveVelocityUpdates(true);
    }
    private static EntityType.Builder<CursedDryHead> curseddryhead()
    {
        return EntityType.Builder.of(CursedDryHead::new, MobCategory.MONSTER)
                .sized(0.6F, 1.8F);
    }
    private static EntityType.Builder<ChickenNaked> chickennaked()
    {
        return EntityType.Builder.of(ChickenNaked::new, MobCategory.CREATURE)
                .sized(0.4F, 0.7F)
                .setTrackingRange(80)
                .setUpdateInterval(1);
    }
    private static EntityType.Builder<Portal> portal()
    {
        return EntityType.Builder.<Portal>of(Portal::new, MobCategory.MISC)
                .sized(2F, 3F)
                .setTrackingRange(5)
                .setUpdateInterval(20);
    }
    private static EntityType.Builder<Worm> worm()
    {
        return EntityType.Builder.of(Worm::new, MobCategory.MISC)
                .sized(0.6f, 1.8f)
                .setTrackingRange(64)
                .setUpdateInterval(1)
                .setShouldReceiveVelocityUpdates(true);
    }
    private static EntityType.Builder<SoulOrb> soulorb()
    {
        return EntityType.Builder.<SoulOrb>of(SoulOrb::new, MobCategory.MISC)
                .sized(0.6F, 1.8F)
                .setTrackingRange(10)
                .fireImmune();
    }
    private static EntityType.Builder<SoulOrbProjectile> soulorbpro()
    {
        return EntityType.Builder.<SoulOrbProjectile>of(SoulOrbProjectile::new, MobCategory.MISC)
                .sized(.6F, .6F)
                .setTrackingRange(128)
                .setUpdateInterval(1)
                .setShouldReceiveVelocityUpdates(true);
    }

    private static EntityType.Builder<DroneDeliverEntity> drone()
    {
        return EntityType.Builder.<DroneDeliverEntity>of(DroneDeliverEntity::new, MobCategory.CREATURE)
                .sized(0.7F, 0.35F)
                .eyeHeight(0.22F)
                .clientTrackingRange(10)
                .updateInterval(1);
    }

    private static EntityType.Builder<ThrownSteelShield> thrownSteelShield()
    {
        return EntityType.Builder.<ThrownSteelShield>of(ThrownSteelShield::new, MobCategory.MISC)
                .sized(0.8F, 0.25F)
                .setTrackingRange(64)
                .setUpdateInterval(1)
                .setShouldReceiveVelocityUpdates(true);
    }

    @EventBusSubscriber(modid = WitherUtils.MODID)
    public static class Listener
    {
        @SubscribeEvent
        public static void registerGlobalAttributes(EntityAttributeCreationEvent event)
        {
            event.put(DRONE_DELIVER.get(), DroneDeliverEntity.createAttributes().build());
            event.put(SOULORB.get(), SoulOrb.createAttributes().build());
            event.put(CURSEDCREEPER.get(), CursedCreeper.createAttributes().build());
            event.put(CURSEDDRYHEAD.get(), CursedZombie.createAttributes().build());
            event.put(CURSEDSKELETON.get(), CursedSkeleton.createAttributes().build());
            event.put(CURSEDSPIDER.get(), CursedSpider.createAttributes().build());
            event.put(CURSEDZOMBIE.get(), CursedZombie.createAttributes().build());
            event.put(CHICKENNAKED.get(), ChickenNaked.createAttributes().build());
        }

        @SubscribeEvent
        public static void init(RegisterSpawnPlacementsEvent event)
        {
        }
    }
}

