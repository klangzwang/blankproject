package geni.withercore.init;

import geni.withercore.common.world.entity.AbstractDroneEntity;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.entity.bolt.CursedLightningBolt;
import geni.witherutils.common.world.entity.drone.DroneEntity;
import geni.witherutils.common.world.entity.drone.GuardDroneEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WUTEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, WitherUtils.MODID);

    public static final Supplier<EntityType<CursedLightningBolt>> CURSEDBOLT = register("cursed_bolt", WUTEntities::cursedbolt);
    public static final Supplier<EntityType<DroneEntity>> DRONE = register("drone", WUTEntities::drone);
    public static final Supplier<EntityType<GuardDroneEntity>> GUARD_DRONE = register("guard_drone", WUTEntities::guardDrone);

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

    private static EntityType.Builder<DroneEntity> drone() {
        return EntityType.Builder.<DroneEntity>of(DroneEntity::new, MobCategory.CREATURE)
                .sized(0.7f, 0.35f)
                .setTrackingRange(32)
                .setUpdateInterval(3)
                .setShouldReceiveVelocityUpdates(true);
    }

    private static EntityType.Builder<GuardDroneEntity> guardDrone() {
        return EntityType.Builder.<GuardDroneEntity>of(GuardDroneEntity::new, MobCategory.CREATURE)
                .sized(0.7f, 0.35f)
                .setTrackingRange(32)
                .setUpdateInterval(3)
                .setShouldReceiveVelocityUpdates(true);
    }

    @EventBusSubscriber(modid = WitherUtils.MODID)
    public static class Listener
    {
        @SubscribeEvent
        public static void registerGlobalAttributes(EntityAttributeCreationEvent event)
        {
            event.put(WUTEntities.DRONE.get(), DroneEntity.prepareAttributes().build());
            event.put(WUTEntities.GUARD_DRONE.get(), DroneEntity.prepareAttributes().build());
        }

        @SubscribeEvent
        public static void init(RegisterSpawnPlacementsEvent event)
        {
        }
    }
}

