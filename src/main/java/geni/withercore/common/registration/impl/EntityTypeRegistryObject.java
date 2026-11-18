package geni.withercore.common.registration.impl;

import geni.witherapi.providers.IEntityTypeProvider;
import geni.withercore.common.registration.WUTDeferredHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class EntityTypeRegistryObject<ENTITY extends Entity>
        extends WUTDeferredHolder<EntityType<?>, EntityType<ENTITY>> implements IEntityTypeProvider {

    public EntityTypeRegistryObject(ResourceKey<EntityType<?>> key) {
        super(key);
    }

    @NotNull
    @Override
    public EntityType<ENTITY> getEntityType() {
        return value();
    }

    @NotNull
    @Override
    public ResourceLocation getRegistryName() {
        return getId();
    }
}


