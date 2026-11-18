package geni.withercore.common.registration.impl;

import geni.withercore.common.registration.WUTDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

public class EntityTypeDeferredRegister extends WUTDeferredRegister<EntityType<?>> {

    public EntityTypeDeferredRegister(String modid) {
        super(Registries.ENTITY_TYPE, modid, EntityTypeRegistryObject::new);
    }

    @SuppressWarnings("unchecked")
    public <ENTITY extends Entity> EntityTypeRegistryObject<ENTITY> registerEntityType(String name, Supplier<? extends EntityType<ENTITY>> sup) {
        return (EntityTypeRegistryObject<ENTITY>) super.register(name, sup);
    }

    public <ENTITY extends Entity> EntityTypeRegistryObject<ENTITY> registerBuilder(String name, Supplier<EntityType.Builder<ENTITY>> sup) {
        return registerEntityType(name, () -> sup.get().build(name));
    }
}


