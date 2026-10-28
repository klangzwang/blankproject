package geni.witherutils.common.world.entity.drone;

import geni.witherapi.tags.WitherTags;
import geni.withercore.entity.AbstractDroneEntity;
import geni.withercore.helper.DroneKinematicsHelper;
import geni.withercore.init.WUTEntities;
import geni.withercore.init.WUTItems;
import geni.withercore.init.WUTSounds;
import geni.witherutils.common.menu.DroneMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Concrete Quadcopter DroneDeliverEntity implementation with 1-Slot Battery GUI,
 * manual right-click power toggle, automatic battery depletion fall, and modular AI Goals.
 *
 * Lifecycle:
 * 1. Placed via DroneItem on the ground in STATE_OFF (lights off, rotors stopped).
 * 2. Shift + Right-Click (without wrench) or Right-Click when no charged battery is installed
 *    opens the 1-Slot Battery GUI (DroneMenu / DroneScreen). The slot locks when the drone is on.
 * 3. Right-Click on the ground when a charged battery (> 0 FE) is installed starts the drone
 *    (STATE_STARTUP_CHECK -> STATE_TAKEOFF -> STATE_HOVERING).
 * 4. Right-Click in the air OR when the battery reaches 0 FE immediately powers down the drone
 *    (STATE_OFF, LightsEnabled = false, NoGravity = false) so it falls to the ground.
 * 5. Shift + Right-Click with a Wrench dismantles the drone and drops both the DroneItem and any installed Battery.
 */
public class DroneDeliverEntity extends AbstractDroneEntity implements MenuProvider, FlyingAnimal {

    @OnlyIn(Dist.CLIENT)
    private boolean crossfadeSoundPlaying = false;

    /*private static final RawAnimation STATE_OFF_ANIM = RawAnimation.begin().thenPlay("animation.wasp.folded");
    private static final RawAnimation STATE_STARTUP_ANIM = RawAnimation.begin().thenPlay("animation.wasp.unfolding");
    private static final RawAnimation STATE_TAKEOFF_ANIM = RawAnimation.begin().thenPlay("animation.wasp.weapons");*/

    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(DroneDeliverEntity.class, EntityDataSerializers.BYTE);

    private static final MutableComponent DEF_DRONE_NAME = Component.translatable("entity.witherutils.drone");

    private float rollAmount;
    private float rollAmountO;

    @Nullable
    BlockPos platformPos;
    @Nullable
    BlockPos targetPos;
    /*DroneDeliverEntity.DroneGoToPlatformGoal goToPlatformGoal;*/

    public Component ownerName = DEF_DRONE_NAME;
    private UUID ownerUUID;

    public DroneDeliverEntity(EntityType<? extends DroneDeliverEntity> type, Level world) {
        super(type, world);
        /*this.lookControl = new DroneDeliverEntity.DroneLookControl(this);*/
        /*moveControl = new DroneMovementController(this);
        goalSelector.addGoal(1, chargeAI = new DroneGoToChargingStation(this));*/
    }

    protected DroneDeliverEntity(EntityType<? extends DroneDeliverEntity> type, Level world, Player player) {
        this(type, world);
        if (player != null) {
            ownerUUID = player.getGameProfile().getId();
            ownerName = player.getName();
        } else {
            ownerUUID = getUUID();
            ownerName = DEF_DRONE_NAME;
        }
    }

    public DroneDeliverEntity(Level world, Player player) {
        this(WUTEntities.DRONE_DELIVER.get(), world, player);
    }

    @Override
    protected SoundEvent getNearRotorSound() {
        return WUTSounds.DRONENEAR.get();
    }

    @Override
    protected SoundEvent getFarRotorSound() {
        return WUTSounds.DRONEFAR.get();
    }

    @Override
    protected float getMaxRotorSoundDistance() {
        return 48.0F;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLAGS_ID, (byte)0);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    protected void registerGoals() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.FLYING_SPEED, 0.4D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 4.0D);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.hasPlatform()) {
            compound.put("platform_pos", NbtUtils.writeBlockPos(this.getPlatformPos()));
        }
        if (this.hasTargetPos()) {
            compound.put("target_pos", NbtUtils.writeBlockPos(this.getTargetPos()));
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.platformPos = NbtUtils.readBlockPos(compound, "platform_pos").orElse(null);
        this.targetPos = NbtUtils.readBlockPos(compound, "target_pos").orElse(null);
        super.readAdditionalSaveData(compound);
    }

    void pathfindRandomlyTowards(BlockPos pos) {
        Vec3 vec3 = Vec3.atBottomCenterOf(pos);
        int i = 0;
        BlockPos blockpos = this.blockPosition();
        int j = (int)vec3.y - blockpos.getY();
        if (j > 2) {
            i = 4;
        } else if (j < -2) {
            i = -4;
        }

        int k = 6;
        int l = 8;
        int i1 = blockpos.distManhattan(pos);
        if (i1 < 15) {
            k = i1 / 2;
            l = i1 / 2;
        }

        Vec3 vec31 = AirRandomPos.getPosTowards(this, k, l, i, vec3, (float) (Math.PI / 10));
        if (vec31 != null) {
            this.navigation.setMaxVisitedNodesMultiplier(0.5F);
            this.navigation.moveTo(vec31.x, vec31.y, vec31.z, 1.0);
        }
    }

    boolean wantsToLandOnPlatform() {
        if (this.getTarget() == null) {
            boolean flag = this.level().isRaining() || this.level().isNight();
            return flag;
        } else {
            return false;
        }
    }

    public float getRollAmount(float partialTick) {
        return Mth.lerp(partialTick, this.rollAmountO, this.rollAmount);
    }

    private void updateRollAmount() {
        /*this.rollAmountO = this.rollAmount;
        if (this.isRolling()) {
            this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
        } else {
            this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
        }*/
    }

    @VisibleForDebug
    public boolean hasPlatform() {
        return this.platformPos != null;
    }

    @Nullable
    @VisibleForDebug
    public BlockPos getPlatformPos() {
        return this.platformPos;
    }

    @VisibleForDebug
    public GoalSelector getGoalSelector() {
        return this.goalSelector;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            /*boolean flag = this.getTarget() != null && this.getTarget().distanceToSqr(this) < 4.0;
            this.setRolling(flag);
            if (this.tickCount % 20 == 0 && !this.isPlatformValid()) {
                this.platformPos = null;
            }*/
        }
    }

    boolean isPlatformValid() {
        if (!this.hasPlatform()) {
            return false;
        } else if (this.isTooFarAway(this.platformPos)) {
            return false;
        } else {
            BlockEntity blockentity = this.level().getBlockEntity(this.platformPos);
            return blockentity instanceof BeehiveBlockEntity;
        }
    }

    private boolean isRolling() {
        return this.getFlag(2);
    }

    private void setRolling(boolean isRolling) {
        this.setFlag(2, isRolling);
    }

    boolean isTooFarAway(BlockPos pos) {
        return !this.closerThan(pos, 32);
    }

    private void setFlag(int flagId, boolean value) {
        if (value) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) | flagId));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) & ~flagId));
        }
    }

    private boolean getFlag(int flagId) {
        return (this.entityData.get(DATA_FLAGS_ID) & flagId) != 0;
    }

    @Override
    protected PathNavigation createNavigation(Level p_level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_level) {
            @Override
            public boolean isStableDestination(BlockPos p_27947_) {
                return !this.level.getBlockState(p_27947_.below()).isAir();
            }

            @Override
            public void tick() {
                super.tick();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(false);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    boolean closerThan(BlockPos pos, int distance) {
        return pos.closerThan(this.blockPosition(), (double)distance);
    }

    public void setPlatformPos(BlockPos platformPos) {
        this.platformPos = platformPos;
    }

    @VisibleForDebug
    public boolean hasTargetPos() {
        return this.targetPos != null;
    }

    @Nullable
    @VisibleForDebug
    public BlockPos getTargetPos() {
        return this.targetPos;
    }

    public void setTargetPos(BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    /**
     * Initializes the drone right after placement from DroneItem#useOn.
     * The drone remains powered off (STATE_OFF) on the ground until a charged battery is inserted
     * and the player right-clicks it.
     */
    public void initializeOnPlacement(BlockPos placementPos, float playerYaw) {
        this.setHomePos(placementPos);
        this.setYRot(playerYaw);
        this.yRotO = playerYaw;
        this.yBodyRot = playerYaw;
        this.yBodyRotO = playerYaw;
        this.setYHeadRot(playerYaw);

        this.setLightsEnabled(false);
        this.setDroneState(STATE_OFF);
        this.setRotorSpeed(0.0F);
        this.setNoGravity(false);
    }

    /**
     * Powers on the drone from the ground once a charged battery is verified.
     */
    public void powerOnFromGround() {
        this.setHomePos(this.blockPosition());
        this.setLightsEnabled(true);
        this.setDroneState(STATE_STARTUP_CHECK);
        this.setRotorSpeed(2.5F);
        this.setNoGravity(false);

        if (!this.level().isClientSide()) {
            this.level().playSound(
                    null,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    WUTSounds.DRONEBEEP.get(),
                    SoundSource.NEUTRAL,
                    0.9F,
                    1.15F
            );
        }
    }

    /**
     * Immediately shuts down the drone (either from right-clicking in mid-air or battery depletion)
     * so gravity pulls it back to the ground.
     */
    public void powerDownAndFall() {
        this.setLightsEnabled(false);
        this.setDroneState(STATE_OFF);
        this.setNoGravity(false);
        this.getNavigation().stop();

        if (this.level().isClientSide()) {
            this.crossfadeSoundPlaying = false;
        } else {
            this.level().playSound(
                    null,
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    WUTSounds.DRONEBEEP.get(),
                    SoundSource.NEUTRAL,
                    0.85F,
                    0.85F
            );
        }
    }

    private void handleRedstoneEmission() {
/*        if (level().isEmptyBlock(blockPosition())) {
            for (Direction d : FacingUtil.VALUES) {
                if (getEmittingRedstone(d) > 0) {
                    level().setBlockAndUpdate(blockPosition(), WUTBlocks.DRONE_REDSTONE_EMITTER.get().defaultBlockState());
                    BlockEntityUtil.getBlockEntityAt(level(), blockPosition(), DroneRedstoneEmitterBlockEntity.class)
                            .ifPresent(be -> be.setOwner(this));
                }
                break;
            }
        }*/
    }

    private void onFirstTick() {
        if (!level().isClientSide) {
        }
    }

    @Override
    public void tick() {
        if (tickCount == 1) {
            onFirstTick();
        }

        if (!this.level().isClientSide()) {
            tickServerStateMachine();
        } else {
            tickClientVisualEffects();
        }

        Level level = level();

        if (!level.isClientSide) {
        }

        super.tick();

        if (!level.isClientSide && isAlive()) {
            handleRedstoneEmission();
        }
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
    }

    public boolean shouldLoadChunk(ChunkPos cp) {
        return true; // Math.abs(cp.x - chunkPosition().x) + Math.abs(cp.z - chunkPosition().z) < getUpgrades(ModUpgrades.CHUNKLOADER.get());
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);

        if (level() instanceof ServerLevel serverLevel) {
        }

        /*if (shouldDropAsItem()) {
            ItemStack stack = new ItemStack(getDroneItem());
            writeToItemStack(stack);
            spawnAtLocation(stack, 0);
            if (!level().isClientSide) {
                reportDroneDeath(getOwner(), damageSource);
            }
        }*/

        setCustomName(Component.empty());
    }

    private void tickServerStateMachine() {
        int state = getDroneState();
        int ticks = getStateTicks() + 1;
        setStateTicks(ticks);

        BlockPos home = getHomePos();
        if (home.equals(BlockPos.ZERO)) {
            home = this.blockPosition();
            setHomePos(home);
        }

        // If powered on, drain battery energy according to current flight phase; shut down & fall if depleted
        if (state != STATE_OFF) {
            int energyCost = switch (state) {
                case STATE_STARTUP_CHECK -> DroneKinematicsHelper.ENERGY_COST_STARTUP_PER_TICK;
                case STATE_TAKEOFF -> DroneKinematicsHelper.ENERGY_COST_TAKEOFF_PER_TICK;
                default -> DroneKinematicsHelper.ENERGY_COST_HOVER_PER_TICK;
            };

            if (!consumeBatteryEnergy(energyCost)) {
                powerDownAndFall();
                return;
            }
        }

        switch (state) {
            case STATE_OFF -> {
                this.setNoGravity(false);
                if (isLightsEnabled()) {
                    setLightsEnabled(false);
                }
                // Spool down rotors smoothly while falling or resting on the ground
                setRotorSpeed(Mth.lerp(0.15F, getRotorSpeed(), 0.0F));
                if (getRotorSpeed() < 0.05F) {
                    setRotorSpeed(0.0F);
                }
                // Damp horizontal slide once resting on the ground
                if (this.onGround()) {
                    Vec3 vel = this.getDeltaMovement();
                    this.setDeltaMovement(vel.x * 0.6D, vel.y, vel.z * 0.6D);
                }
            }

            case STATE_STARTUP_CHECK -> {
                if (!isLightsEnabled()) {
                    setLightsEnabled(true);
                }
                this.setNoGravity(false);

                float progress = Mth.clamp((float) ticks / DroneKinematicsHelper.STARTUP_DURATION_TICKS, 0.0F, 1.0F);
                float currentRpmSpeed = Mth.lerp(progress, 3.0F, DroneKinematicsHelper.MAX_HOVER_ROTOR_SPEED);
                setRotorSpeed(currentRpmSpeed);

                if (ticks == 20) {
                    this.level().playSound(
                            null,
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            WUTSounds.DRONESTART.get(),
                            SoundSource.NEUTRAL,
                            0.75F,
                            1.0F
                    );
                }

                if (ticks >= DroneKinematicsHelper.STARTUP_DURATION_TICKS) {
                    setDroneState(STATE_TAKEOFF);
                    this.setNoGravity(true);
                }
            }

            case STATE_TAKEOFF -> {
                this.setNoGravity(true);
                setRotorSpeed(DroneKinematicsHelper.MAX_CLIMB_ROTOR_SPEED);

                float climbProgress = Mth.clamp((float) ticks / DroneKinematicsHelper.TAKEOFF_DURATION_TICKS, 0.0F, 1.0F);
                double desiredY = home.getY() + DroneKinematicsHelper.calculateTakeoffHeightOffset(climbProgress);

                Vec3 currentVel = this.getDeltaMovement();
                double nextVy = DroneKinematicsHelper.computeVerticalSpringVelocity(
                        this.getY(),
                        desiredY,
                        currentVel.y,
                        6.5D,
                        2.4D
                );

                double targetX = home.getX() + 0.5D;
                double targetZ = home.getZ() + 0.5D;
                double vx = (targetX - this.getX()) * 0.12D;
                double vz = (targetZ - this.getZ()) * 0.12D;

                this.setDeltaMovement(vx, nextVy, vz);

                if (ticks >= DroneKinematicsHelper.TAKEOFF_DURATION_TICKS + 120) {
                    setDroneState(STATE_HOVERING);
                }
            }

            case STATE_HOVERING -> {
                // Handled by DroneHoverAndOrbitGoal (or higher-priority AI Goals registered in goalSelector)
                this.setNoGravity(true);
                if (getRotorSpeed() < DroneKinematicsHelper.MAX_HOVER_ROTOR_SPEED) {
                    setRotorSpeed(DroneKinematicsHelper.MAX_HOVER_ROTOR_SPEED);
                }
            }
        }
    }

    private void tickClientVisualEffects() {
        int state = getDroneState();

        /*if (this.level().isClientSide()) {
            if (state != STATE_OFF) {
                DroneSoundManager.play(this, WUTSounds.DRONEIDLE.get());
            } else {
                DroneSoundManager.stop(this);
            }
        }

        if (this.level().isClientSide()) {
            if (state != STATE_OFF) {
                // Falls der Sound noch nicht läuft, jetzt einmalig starten
                if (!this.crossfadeSoundPlaying) {
                    DroneSoundManager.play(this, WUTSounds.DRONENEAR.get());
                    DroneSoundManager.playDroneCrossfadeSounds(this);
                    this.crossfadeSoundPlaying = true;
                }
            } else {
                // Drohne ist ausgeschaltet -> Flag zurücksetzen (Sound stoppt von selbst via DistanceCrossfadeSound)
                this.crossfadeSoundPlaying = false;
            }
        }*/

        if (state == STATE_OFF) {
            return;
        }

        // Rotoren-Partikel
        if (this.getRotorSpeed() > 10.0F && this.tickCount % 3 == 0) {
            /*this.level().addParticle(
                    ParticleTypes.CLOUD,
                    this.getX() + (this.random.nextDouble() - 0.5D) * 0.5D,
                    this.getY() - 0.15D,
                    this.getZ() + (this.random.nextDouble() - 0.5D) * 0.5D,
                    0.0D,
                    -0.06D,
                    0.0D
            );*/
        }
    }

    // =========================================================================
    // PLAYER RIGHT-CLICK INTERACTIONS (GUI, POWER TOGGLE, WRENCH DISMANTLE)
    // =========================================================================
    /*@Override*/
    public UUID getOwnerUUID() {
        if (ownerUUID == null) {
            /*Log.warning("Drone with owner '{}' has no UUID! Substituting the Drone's UUID ({}).", ownerName, getUUID());
            Log.warning("If you use any protection mods, the drone might not be able to operate in protected areas.");*/
            ownerUUID = getUUID();
        }
        return ownerUUID;
    }

    /*@Override*/
    public Player getOwner() {
        MinecraftServer server = level().getServer();
        return server != null ? server.getPlayerList().getPlayer(ownerUUID) : null;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {

        /*if (!getOwnerUUID().equals(player.getUUID())) return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(hand);
        Level level = level();

        if (stack.getItem() == WUTItems.GPS_TOOL.get()) {
            if (!level.isClientSide) {
                return GPSToolItem.getGPSLocation(player.getUUID(), stack).map(gpsPos -> {
                    getNavigation().moveTo(gpsPos.getX(), gpsPos.getY(), gpsPos.getZ(), 0.1D);
                    return InteractionResult.SUCCESS;
                }).orElse(InteractionResult.PASS);
            }
            return InteractionResult.SUCCESS;
        }*/

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        ItemStack heldStack = player.getItemInHand(hand);

        // 1. Shift + Right-Click with Wrench -> Dismantle Drone
        if (player.isSecondaryUseActive() && isWrench(heldStack)) {
            if (!this.level().isClientSide()) {
                dismantleWithWrench(player, heldStack);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        // 2. If Drone is currently powered on / airborne:
        //    - Shift + Right-Click opens the GUI (showing the locked battery slot)
        //    - Normal Right-Click with hand shuts the drone down so it falls to the ground
        if (this.isPoweredOn()) {
            if (player.isSecondaryUseActive()) {
                if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(this, buf -> buf.writeInt(this.getId()));
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide());
            }

            if (!this.level().isClientSide()) {
                powerDownAndFall();
                player.displayClientMessage(
                        Component.translatable("message.witherutils.drone.powered_off").withStyle(ChatFormatting.RED),
                        true
                );
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        // 3. Drone is OFF on the ground:
        //    - Shift + Right-Click (or Right-Click while holding a Battery, or when no charged battery is in slot)
        //      opens the 1-Slot Battery GUI so the player can insert/remove the battery.
        //    - Normal Right-Click when a charged battery (> 0 FE) is in the slot starts the drone!
        if (player.isSecondaryUseActive() || !this.hasBatteryPower() || DroneMenu.isBatteryItem(heldStack)) {
            if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
                if (!this.hasBattery() && !player.isSecondaryUseActive() && !DroneMenu.isBatteryItem(heldStack)) {
                    player.displayClientMessage(
                            Component.translatable("message.witherutils.drone.no_battery").withStyle(ChatFormatting.YELLOW),
                            true
                    );
                } else if (this.hasBattery() && !this.hasBatteryPower() && !player.isSecondaryUseActive()) {
                    player.displayClientMessage(
                            Component.translatable("message.witherutils.drone.battery_empty").withStyle(ChatFormatting.YELLOW),
                            true
                    );
                }
                serverPlayer.openMenu(this, buf -> buf.writeInt(this.getId()));
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        // 4. Drone is OFF on the ground and has a charged battery -> Power On & Takeoff!
        if (!this.level().isClientSide()) {
            powerOnFromGround();
            player.displayClientMessage(
                    Component.translatable("message.witherutils.drone.powered_on").withStyle(ChatFormatting.AQUA),
                    true
            );
        }

        return InteractionResult.PASS;
    }

    @Override
    public Component getDisplayName() {
        return super.getDisplayName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DroneMenu(containerId, playerInventory, this);
    }

    private boolean isWrench(ItemStack stack) {
        return stack.is(WitherTags.Items.WRENCHES) || stack.is(WitherTags.Items.WITHER_WRENCHES);
    }

    private void dismantleWithWrench(Player player, ItemStack wrenchStack) {
        Level level = this.level();

        level.playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.IRON_TRAPDOOR_CLOSE,
                SoundSource.PLAYERS,
                0.85F,
                1.35F
        );

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    this.getX(),
                    this.getY() + 0.25D,
                    this.getZ(),
                    12,
                    0.2D,
                    0.15D,
                    0.2D,
                    0.05D
            );
        }

        // Drop installed battery if present
        ItemStack installedBattery = this.getBatteryInventory().getStackInSlot(0);
        if (!installedBattery.isEmpty()) {
            this.spawnAtLocation(installedBattery.copy(), 0.15F);
            this.setBatteryStack(ItemStack.EMPTY);
        }

        // Drop DroneItem for collection
        ItemStack droneDrop = createItemWithComponents();
        this.spawnAtLocation(droneDrop, 0.15F);

        this.discard();
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        ItemStack installedBattery = this.getBatteryInventory().getStackInSlot(0);
        if (!installedBattery.isEmpty()) {
            this.spawnAtLocation(installedBattery.copy(), 0.1F);
            this.setBatteryStack(ItemStack.EMPTY);
        }
        this.spawnAtLocation(createItemWithComponents(), 0.1F);
    }

    private ItemStack createItemWithComponents() {
        ItemStack droneDrop = new ItemStack(WUTItems.DRONE_DELIVER.get());
        if (this.hasCustomName()) {
            droneDrop.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, this.getCustomName());
        }
        if (this.hasPlatform()) {
            droneDrop.set(geni.withercore.init.WUTComponents.DRONE_DOCK_POS.get(), this.getPlatformPos());
        }
        if (this.hasTargetPos()) {
            droneDrop.set(geni.withercore.init.WUTComponents.DRONE_TARGET_POS.get(), this.getTargetPos());
        }
        return droneDrop;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    // =========================================================================
    // GECKOLIB ANIMATION CONTROLLERS
    // =========================================================================

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "hover_controller", 5, state -> {
            if (this.getDroneState() == STATE_STARTUP_CHECK) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("animation.wasp.unfolding"));
            }
            if (this.getDroneState() == STATE_TAKEOFF) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("animation.wasp.weapons"));
            }
            return software.bernie.geckolib.animation.PlayState.STOP;
        }));
    }
}
