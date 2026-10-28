package geni.withercore.entity;

import geni.withercore.helper.DroneKinematicsHelper;
import geni.witherutils.common.world.item.BatteryItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.items.ItemStackHandler;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import geni.withercore.sound.DroneRotorSoundInstance;
import geni.withercore.sound.DroneFarSoundInstance;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Abstract base class for autonomous quadcopter entities.
 * Implements PathfinderMob, FlyingAnimal, IEntityWithComplexSpawn, and GeckoLib GeoEntity.
 * Includes a 1-slot battery inventory with synchronized battery stack & energy state.
 */
public abstract class AbstractDroneEntity extends PathfinderMob implements FlyingAnimal, IEntityWithComplexSpawn, GeoEntity {

    @OnlyIn(Dist.CLIENT)
    private DroneRotorSoundInstance rotorSoundInstance;
    @OnlyIn(Dist.CLIENT)
    private DroneFarSoundInstance farSoundInstance;

    /**
     * Sound event played when the player is close to the drone.
     * Must be a mono OGG file for proper 3D positioning.
     */
    protected abstract SoundEvent getNearRotorSound();

    /**
     * Sound event played when the player is far from the drone.
     * Must be a mono OGG file for proper 3D positioning.
     */
    protected abstract SoundEvent getFarRotorSound();

    /**
     * Max distance (in blocks) at which the rotor sound is audible.
     * Default 48 blocks — override per drone type if needed.
     */
    protected float getMaxRotorSoundDistance() {
        return 48.0F;
    }

    public static final int STATE_OFF = 0;
    public static final int STATE_STARTUP_CHECK = 1;
    public static final int STATE_TAKEOFF = 2;
    public static final int STATE_HOVERING = 3;

    protected static final EntityDataAccessor<Integer> DATA_DRONE_STATE =
            SynchedEntityData.defineId(AbstractDroneEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> DATA_LIGHTS_ENABLED =
            SynchedEntityData.defineId(AbstractDroneEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> DATA_STATE_TICKS =
            SynchedEntityData.defineId(AbstractDroneEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> DATA_ROTOR_SPEED =
            SynchedEntityData.defineId(AbstractDroneEntity.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<BlockPos> DATA_HOME_POS =
            SynchedEntityData.defineId(AbstractDroneEntity.class, EntityDataSerializers.BLOCK_POS);
    protected static final EntityDataAccessor<ItemStack> DATA_BATTERY_STACK =
            SynchedEntityData.defineId(AbstractDroneEntity.class, EntityDataSerializers.ITEM_STACK);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 1-Slot Battery Inventory. Automatically syncs with DATA_BATTERY_STACK for client-side rendering
     * of the underside battery_pack bone, status lamp color, and GUI energy bar.
     */
    protected final ItemStackHandler batteryInventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof BatteryItem;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (!AbstractDroneEntity.this.level().isClientSide()) {
                AbstractDroneEntity.this.entityData.set(DATA_BATTERY_STACK, getStackInSlot(0).copy());
            }
        }
    };

    // Client/Server kinematic state for smooth partialTick interpolation
    protected Vec3 prevVelocity = Vec3.ZERO;
    public float prevBodyPitch;
    public float bodyPitch;
    public float prevBodyRoll;
    public float bodyRoll;
    public float prevRotorAngle;
    public float rotorAngle;
    public float prevHoverBobOffset;
    public float hoverBobOffset;

    protected AbstractDroneEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.xpReward = 0;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DRONE_STATE, STATE_OFF);
        builder.define(DATA_LIGHTS_ENABLED, false);
        builder.define(DATA_STATE_TICKS, 0);
        builder.define(DATA_ROTOR_SPEED, 0.0F);
        builder.define(DATA_HOME_POS, BlockPos.ZERO);
        builder.define(DATA_BATTERY_STACK, ItemStack.EMPTY);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    // =========================================================================
    // GETTERS & SETTERS FOR SYNCHED ENTITY DATA
    // =========================================================================

    public int getDroneState() {
        return this.entityData.get(DATA_DRONE_STATE);
    }

    public void setDroneState(int state) {
        this.entityData.set(DATA_DRONE_STATE, state);
        this.entityData.set(DATA_STATE_TICKS, 0);
    }

    public boolean isLightsEnabled() {
        return this.entityData.get(DATA_LIGHTS_ENABLED);
    }

    public void setLightsEnabled(boolean enabled) {
        this.entityData.set(DATA_LIGHTS_ENABLED, enabled);
    }

    public int getStateTicks() {
        return this.entityData.get(DATA_STATE_TICKS);
    }

    public void setStateTicks(int ticks) {
        this.entityData.set(DATA_STATE_TICKS, ticks);
    }

    public float getRotorSpeed() {
        return this.entityData.get(DATA_ROTOR_SPEED);
    }

    public void setRotorSpeed(float speed) {
        this.entityData.set(DATA_ROTOR_SPEED, speed);
    }

    public BlockPos getHomePos() {
        return this.entityData.get(DATA_HOME_POS);
    }

    public void setHomePos(BlockPos pos) {
        this.entityData.set(DATA_HOME_POS, pos.immutable());
    }

    public ItemStackHandler getBatteryInventory() {
        return this.batteryInventory;
    }

    public ItemStack getBatteryStack() {
        if (!this.level().isClientSide()) {
            return this.batteryInventory.getStackInSlot(0);
        }
        return this.entityData.get(DATA_BATTERY_STACK);
    }

    public void setBatteryStack(ItemStack stack) {
        this.batteryInventory.setStackInSlot(0, stack);
        this.entityData.set(DATA_BATTERY_STACK, stack.copy());
    }

    public boolean hasBattery() {
        return !getBatteryStack().isEmpty();
    }

    public int getBatteryEnergy() {
        return BatteryItem.getStoredEnergy(getBatteryStack());
    }

    public boolean hasBatteryPower() {
        return hasBattery() && getBatteryEnergy() > 0;
    }

    /**
     * Extracts FE energy from the installed battery on the server and syncs the updated ItemStack.
     *
     * @param amount FE to consume this tick
     * @return true if the drone still has power, false if battery is missing or depleted
     */
    public boolean consumeBatteryEnergy(int amount) {
        if (this.level().isClientSide()) {
            return hasBatteryPower();
        }
        ItemStack stack = this.batteryInventory.getStackInSlot(0);
        if (stack.isEmpty() || !(stack.getItem() instanceof BatteryItem)) {
            return false;
        }
        int current = BatteryItem.getStoredEnergy(stack);
        if (current <= 0) {
            return false;
        }
        int nextEnergy = Math.max(0, current - amount);
        BatteryItem.setStoredEnergy(stack, nextEnergy);
        this.batteryInventory.setStackInSlot(0, stack);
        this.entityData.set(DATA_BATTERY_STACK, stack.copy());
        return nextEnergy > 0;
    }

    public boolean isPoweredOn() {
        return getDroneState() != STATE_OFF;
    }

    // =========================================================================
    // FLYING ANIMAL & AERODYNAMIC OVERRIDES
    // =========================================================================

    @Override
    public boolean isFlying() {
        int state = getDroneState();
        return state == STATE_TAKEOFF || state == STATE_HOVERING || !this.onGround();
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        // Drones never accumulate fall damage
    }

    @Override
    public boolean isNoGravity() {
        int state = getDroneState();
        return state == STATE_TAKEOFF || state == STATE_HOVERING || super.isNoGravity();
    }

    // =========================================================================
    // KINEMATICS UPDATE LOOP (COMMON TICK)
    // =========================================================================

    @Override
    public void tick() {
        this.prevBodyPitch = this.bodyPitch;
        this.prevBodyRoll = this.bodyRoll;
        this.prevRotorAngle = this.rotorAngle;
        this.prevHoverBobOffset = this.hoverBobOffset;

        super.tick();

        // Advance rotor angle based on synchronized rotor angular speed
        float currentRotorSpeed = getRotorSpeed();
        this.rotorAngle = (this.rotorAngle + currentRotorSpeed) % 360.0F;

        // Calculate acceleration vector and aerodynamic Pitch/Roll banking
        Vec3 currentVel = this.getDeltaMovement();
        Vec3 acceleration = currentVel.subtract(this.prevVelocity);
        this.prevVelocity = currentVel;

        float[] aeroAngles = DroneKinematicsHelper.calculateAerodynamicBanking(
                currentVel,
                acceleration,
                this.getYRot()
        );

        float targetPitch = aeroAngles[0];
        float targetRoll = aeroAngles[1];

        if (this.isFlying()) {
            // Layer organic sinusoidal hover-bobbing & micro-turbulence when airborne
            this.hoverBobOffset = (float) DroneKinematicsHelper.calculateHoverBobOffset(this.tickCount, this.getId());
            targetPitch += DroneKinematicsHelper.calculateHoverTurbulencePitch(this.tickCount, this.getId());
            targetRoll += DroneKinematicsHelper.calculateHoverTurbulenceRoll(this.tickCount, this.getId());
        } else {
            this.hoverBobOffset = Mth.lerp(0.25F, this.hoverBobOffset, 0.0F);
        }

        // Smoothly damp body pitch and roll
        this.bodyPitch = Mth.lerp(0.22F, this.bodyPitch, targetPitch);
        this.bodyRoll = Mth.lerp(0.22F, this.bodyRoll, targetRoll);

        if (this.level().isClientSide()) {
            this.tickRotorSoundClient();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickRotorSoundClient() {
        boolean shouldPlay = isPoweredOn() && hasBatteryPower();

        if (shouldPlay && this.rotorSoundInstance == null) {
            // Start both instances
            this.rotorSoundInstance = new DroneRotorSoundInstance(
                    this, getNearRotorSound(), getFarRotorSound(), getMaxRotorSoundDistance());
            this.farSoundInstance = new DroneFarSoundInstance(
                    getFarRotorSound(), this.rotorSoundInstance);

            Minecraft.getInstance().getSoundManager().play(this.rotorSoundInstance);
            Minecraft.getInstance().getSoundManager().play(this.farSoundInstance);
        } else if (!shouldPlay && this.rotorSoundInstance != null) {
            // Stop and clean up
            this.rotorSoundInstance.stop();
            if (this.farSoundInstance != null) {
                this.farSoundInstance.stop();
            }
            this.rotorSoundInstance = null;
            this.farSoundInstance = null;
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (this.level().isClientSide() && this.rotorSoundInstance != null) {
            this.rotorSoundInstance.stop();
            if (this.farSoundInstance != null) {
                this.farSoundInstance.stop();
            }
        }
        super.remove(reason);
    }

    public float getLerpedBodyPitch(float partialTick) {
        return Mth.lerp(partialTick, this.prevBodyPitch, this.bodyPitch);
    }

    public float getLerpedBodyRoll(float partialTick) {
        return Mth.lerp(partialTick, this.prevBodyRoll, this.bodyRoll);
    }

    public float getLerpedRotorAngle(float partialTick) {
        return this.prevRotorAngle + (this.getRotorSpeed() * partialTick);
    }

    public float getLerpedHoverBobOffset(float partialTick) {
        return Mth.lerp(partialTick, this.prevHoverBobOffset, this.hoverBobOffset);
    }

    // =========================================================================
    // NBT PERSISTENCE & IENTITYWITHCOMPLEXSPAWN
    // =========================================================================

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("DroneState", getDroneState());
        compound.putBoolean("LightsEnabled", isLightsEnabled());
        compound.putInt("StateTicks", getStateTicks());
        compound.putFloat("RotorSpeed", getRotorSpeed());
        BlockPos home = getHomePos();
        compound.putInt("HomeX", home.getX());
        compound.putInt("HomeY", home.getY());
        compound.putInt("HomeZ", home.getZ());
        compound.put("BatteryInventory", this.batteryInventory.serializeNBT(this.registryAccess()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("DroneState")) {
            setDroneState(compound.getInt("DroneState"));
        }
        setLightsEnabled(compound.getBoolean("LightsEnabled"));
        setStateTicks(compound.getInt("StateTicks"));
        setRotorSpeed(compound.getFloat("RotorSpeed"));
        if (compound.contains("HomeX")) {
            setHomePos(new BlockPos(
                    compound.getInt("HomeX"),
                    compound.getInt("HomeY"),
                    compound.getInt("HomeZ")
            ));
        }
        if (compound.contains("BatteryInventory")) {
            this.batteryInventory.deserializeNBT(this.registryAccess(), compound.getCompound("BatteryInventory"));
            this.entityData.set(DATA_BATTERY_STACK, this.batteryInventory.getStackInSlot(0).copy());
        }
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(getDroneState());
        buffer.writeBoolean(isLightsEnabled());
        buffer.writeInt(getStateTicks());
        buffer.writeFloat(getRotorSpeed());
        buffer.writeBlockPos(getHomePos());
        buffer.writeFloat(this.getYRot());
        buffer.writeFloat(this.yHeadRot);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, getBatteryStack());
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        setDroneState(additionalData.readInt());
        setLightsEnabled(additionalData.readBoolean());
        setStateTicks(additionalData.readInt());
        setRotorSpeed(additionalData.readFloat());
        setHomePos(additionalData.readBlockPos());
        float yaw = additionalData.readFloat();
        float headYaw = additionalData.readFloat();
        this.setYRot(yaw);
        this.yRotO = yaw;
        this.yBodyRot = yaw;
        this.yBodyRotO = yaw;
        this.setYHeadRot(headYaw);
        this.yHeadRotO = headYaw;
        ItemStack syncedBattery = ItemStack.OPTIONAL_STREAM_CODEC.decode(additionalData);
        this.entityData.set(DATA_BATTERY_STACK, syncedBattery);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
