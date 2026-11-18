package geni.withercore.init;

import geni.withercore.common.registration.impl.EntityTypeDeferredRegister;
import geni.withercore.common.registration.impl.EntityTypeRegistryObject;
import geni.witherutils.WitherUtils;
import geni.witherutils.common.world.entity.bolt.CursedLightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class WUTEntities {

    public static final EntityTypeDeferredRegister ENTITY_TYPES = new EntityTypeDeferredRegister(WitherUtils.MODID);

    public static final EntityTypeRegistryObject<CursedLightningBolt> CURSEDBOLT = ENTITY_TYPES.registerBuilder("cursed_bolt", WUTEntities::cursedbolt);

    private static EntityType.Builder<CursedLightningBolt> cursedbolt()
    {
        return EntityType.Builder.of(CursedLightningBolt::new, MobCategory.MISC)
                .noSave()
                .sized(0.0F, 0.0F)
                .clientTrackingRange(16)
                .updateInterval(Integer.MAX_VALUE);
    }
}
