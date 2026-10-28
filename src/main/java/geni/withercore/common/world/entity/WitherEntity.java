package geni.withercore.common.world.entity;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

public abstract class WitherEntity extends PathfinderMob implements IEntityWithComplexSpawn, GeoEntity {

    protected WitherEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            clientTick();
        } else {
            serverTick();
        }
    }

    public void serverTick() {
    }

    public void clientTick() {
    }

    /*
     * 
     * CAPABILITIES
     * 
     */
    public boolean hasItemCapability() {
        return false;
    }

    public boolean hasFluidCapability() {
        return false;
    }

    public boolean hasEnergyCapability() {
        return false;
    }

    @Nullable
    public IItemHandler getItemHandler(@Nullable Direction side) {
        return null;
    }

    @Nullable
    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        return null;
    }

    @Nullable
    public IEnergyStorage getEnergyHandler(@Nullable Direction side) {
        return null;
    }

    @Nullable
    public final IItemHandler getItemHandler() {
        return getItemHandler(null);
    }

    @Nullable
    public final IFluidHandler getFluidHandler() {
        return getFluidHandler(null);
    }

    @Nullable
    public final IEnergyStorage getEnergyHandler() {
        return getEnergyHandler(null);
    }
}
