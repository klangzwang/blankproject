package geni.withercore.common.world.level.block.entity;

import geni.witherapi.block.entity.UseOnly;
import geni.withercore.common.registration.impl.TileEntityTypeRegistryObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * WitherFuncBlockEntity extends {@link WitherBlockEntity} with comprehensive scanning and diagnostic capabilities.
 * It provides detailed scan results for blocks positioned in facing direction or arbitrary offsets.
 */
public class WitherFuncBlockEntity extends WitherMachineBlockEntity {

    public WitherFuncBlockEntity(TileEntityTypeRegistryObject<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public boolean readFuncStuff() {
        return false;
    }

    /* =========================================================================
     *  TARGET COORDINATE & DIRECTION UTILITIES
     * ========================================================================= */

    /**
     * @return the adjacent BlockPos directly in front of this block's current facing direction.
     */
    public BlockPos getTargetPos() {
        return getBlockPos().relative(getCurrentFacing().getOpposite());
    }

    /**
     * @param distance Distance forward in blocks.
     * @return the BlockPos offset by distance in the facing direction.
     */
    public BlockPos getTargetPos(int distance) {
        return getBlockPos().relative(getCurrentFacing().getOpposite(), distance);
    }

    /**
     * @return the side of the targeted block that faces back towards this block entity.
     */
    public Direction getTargetSide() {
        return getCurrentFacing().getOpposite();
    }

    /* =========================================================================
     *  1.) CAPABILITY SCANNING
     * ========================================================================= */

    public record CapabilityScanResult(
        BlockPos pos,
        @Nullable Direction side,
        boolean hasItemHandler,
        boolean hasEnergyStorage,
        boolean hasFluidHandler,
        boolean hasBlockEntity,
        @Nullable BlockEntity blockEntity,
        List<ResourceLocation> foundCapabilities
    ) {
        public static CapabilityScanResult empty(BlockPos pos, @Nullable Direction side) {
            return new CapabilityScanResult(pos, side, false, false, false, false, null, List.of());
        }

        public boolean hasAny() {
            return hasItemHandler || hasEnergyStorage || hasFluidHandler;
        }

        public int count() {
            int c = 0;
            if (hasItemHandler) c++;
            if (hasEnergyStorage) c++;
            if (hasFluidHandler) c++;
            return c;
        }
    }

    public CapabilityScanResult scanTargetCapabilities() {
        return scanCapabilities(getTargetPos(), getTargetSide());
    }

    public CapabilityScanResult scanCapabilities(Direction direction) {
        return scanCapabilities(getBlockPos().relative(direction), direction.getOpposite());
    }

    public CapabilityScanResult scanCapabilities(BlockPos pos, @Nullable Direction side) {
        if (this.level == null) {
            return CapabilityScanResult.empty(pos, side);
        }

        boolean hasItem = this.level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side) != null;
        boolean hasEnergy = this.level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side) != null;
        boolean hasFluid = this.level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side) != null;

        BlockEntity be = this.level.getBlockEntity(pos);
        boolean hasBE = be != null;

        List<ResourceLocation> list = new ArrayList<>();
        if (hasItem) list.add(ResourceLocation.parse("neoforge:item_handler"));
        if (hasEnergy) list.add(ResourceLocation.parse("neoforge:energy_storage"));
        if (hasFluid) list.add(ResourceLocation.parse("neoforge:fluid_handler"));

        return new CapabilityScanResult(pos, side, hasItem, hasEnergy, hasFluid, hasBE, be, Collections.unmodifiableList(list));
    }

    public <T> Optional<T> scanCapability(BlockCapability<T, Direction> capability) {
        return scanCapability(capability, getTargetPos(), getTargetSide());
    }

    public <T> Optional<T> scanCapability(BlockCapability<T, Direction> capability, Direction direction) {
        return scanCapability(capability, getBlockPos().relative(direction), direction.getOpposite());
    }

    public <T> Optional<T> scanCapability(BlockCapability<T, Direction> capability, BlockPos pos, @Nullable Direction side) {
        if (this.level == null) return Optional.empty();
        return Optional.ofNullable(this.level.getCapability(capability, pos, side));
    }

    /* =========================================================================
     *  2.) ITEM HANDLER & SLOT SCANNING
     * ========================================================================= */

    public record ItemSlotInfo(
        int slot,
        ItemStack stack,
        int count,
        int maxStackSize,
        int slotLimit,
        boolean isEmpty,
        ResourceLocation itemId,
        Component itemName
    ) {
        public static ItemSlotInfo of(int slot, IItemHandler handler) {
            ItemStack s = handler.getStackInSlot(slot);
            int limit = handler.getSlotLimit(slot);
            return new ItemSlotInfo(
                slot,
                s.copy(),
                s.getCount(),
                s.getMaxStackSize(),
                limit,
                s.isEmpty(),
                BuiltInRegistries.ITEM.getKey(s.getItem()),
                s.getHoverName()
            );
        }

        public boolean hasSpace() {
            return count < Math.min(maxStackSize, slotLimit);
        }

        public int getFreeSpace() {
            return Math.max(0, Math.min(maxStackSize, slotLimit) - count);
        }

        public double getFillRatio() {
            int maxCap = Math.min(maxStackSize, slotLimit);
            return maxCap > 0 ? (double) count / (double) maxCap : 0.0;
        }
    }

    public record ItemScanResult(
        BlockPos pos,
        @Nullable Direction side,
        boolean hasCapability,
        @Nullable IItemHandler handler,
        int totalSlots,
        int filledSlots,
        int emptySlots,
        int totalItemCount,
        List<ItemSlotInfo> slots,
        List<ItemStack> nonEmpties
    ) {
        public static ItemScanResult empty(BlockPos pos, @Nullable Direction side) {
            return new ItemScanResult(pos, side, false, null, 0, 0, 0, 0, List.of(), List.of());
        }

        public boolean isEmpty() {
            return filledSlots == 0;
        }

        public boolean isFull() {
            return hasCapability && emptySlots == 0 && slots.stream().noneMatch(ItemSlotInfo::hasSpace);
        }

        public boolean containsItem(Item item) {
            return nonEmpties.stream().anyMatch(s -> s.is(item));
        }

        public int countItem(Item item) {
            return nonEmpties.stream().filter(s -> s.is(item)).mapToInt(ItemStack::getCount).sum();
        }

        public Optional<ItemSlotInfo> findFirst(Item item) {
            return slots.stream().filter(slot -> slot.stack().is(item)).findFirst();
        }
    }

    public ItemScanResult scanTargetItems() {
        return scanItems(getTargetPos(), getTargetSide());
    }

    public ItemScanResult scanItems(Direction direction) {
        return scanItems(getBlockPos().relative(direction), direction.getOpposite());
    }

    public ItemScanResult scanItems(BlockPos pos, @Nullable Direction side) {
        if (this.level == null) {
            return ItemScanResult.empty(pos, side);
        }
        IItemHandler handler = this.level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side);
        if (handler == null) {
            return ItemScanResult.empty(pos, side);
        }

        int totalSlots = handler.getSlots();
        int filled = 0;
        int empty = 0;
        int totalCount = 0;
        List<ItemSlotInfo> slotInfos = new ArrayList<>(totalSlots);
        List<ItemStack> nonEmpties = new ArrayList<>();

        for (int i = 0; i < totalSlots; i++) {
            ItemSlotInfo info = ItemSlotInfo.of(i, handler);
            slotInfos.add(info);
            if (info.isEmpty()) {
                empty++;
            } else {
                filled++;
                totalCount += info.count();
                nonEmpties.add(info.stack());
            }
        }

        return new ItemScanResult(
            pos,
            side,
            true,
            handler,
            totalSlots,
            filled,
            empty,
            totalCount,
            Collections.unmodifiableList(slotInfos),
            Collections.unmodifiableList(nonEmpties)
        );
    }

    /* =========================================================================
     *  3.) ENERGY STORAGE SCANNING
     * ========================================================================= */

    public record EnergyScanResult(
        BlockPos pos,
        @Nullable Direction side,
        boolean hasCapability,
        @Nullable IEnergyStorage handler,
        int energyStored,
        int maxEnergyStored,
        int maxReceiveRate,
        int maxExtractRate,
        boolean canReceive,
        boolean canExtract,
        int neededEnergy,
        double fillRatio
    ) {
        public static EnergyScanResult empty(BlockPos pos, @Nullable Direction side) {
            return new EnergyScanResult(pos, side, false, null, 0, 0, 0, 0, false, false, 0, 0.0);
        }

        public boolean isFull() {
            return maxEnergyStored > 0 && energyStored >= maxEnergyStored;
        }

        public boolean isEmpty() {
            return energyStored <= 0;
        }

        public int getPercentage() {
            return (int) (fillRatio * 100);
        }
    }

    public EnergyScanResult scanTargetEnergy() {
        return scanEnergy(getTargetPos(), getTargetSide());
    }

    public EnergyScanResult scanEnergy(Direction direction) {
        return scanEnergy(getBlockPos().relative(direction), direction.getOpposite());
    }

    public EnergyScanResult scanEnergy(BlockPos pos, @Nullable Direction side) {
        if (this.level == null) {
            return EnergyScanResult.empty(pos, side);
        }
        IEnergyStorage handler = this.level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
        if (handler == null) {
            return EnergyScanResult.empty(pos, side);
        }

        int stored = handler.getEnergyStored();
        int max = handler.getMaxEnergyStored();
        int maxReceive = handler.receiveEnergy(Integer.MAX_VALUE, true);
        int maxExtract = handler.extractEnergy(Integer.MAX_VALUE, true);
        boolean canReceive = handler.canReceive();
        boolean canExtract = handler.canExtract();
        int needed = Math.max(0, max - stored);
        double ratio = max > 0 ? (double) stored / (double) max : 0.0;

        return new EnergyScanResult(
            pos,
            side,
            true,
            handler,
            stored,
            max,
            maxReceive,
            maxExtract,
            canReceive,
            canExtract,
            needed,
            ratio
        );
    }

    /* =========================================================================
     *  4.) FLUID HANDLER & TANK SCANNING
     * ========================================================================= */

    public record FluidTankInfo(
        int tankIndex,
        FluidStack fluidStack,
        Fluid fluid,
        ResourceLocation fluidId,
        Component fluidName,
        int amount,
        int capacity,
        int spaceRemaining,
        double fillRatio,
        boolean isEmpty,
        boolean isFull
    ) {
        public static FluidTankInfo of(int index, IFluidHandler handler) {
            FluidStack stack = handler.getFluidInTank(index);
            int cap = handler.getTankCapacity(index);
            int amt = stack.getAmount();
            Fluid fluid = stack.getFluid();
            ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluid);
            Component name = stack.getHoverName();
            int space = Math.max(0, cap - amt);
            double ratio = cap > 0 ? (double) amt / (double) cap : 0.0;
            return new FluidTankInfo(
                index,
                stack.copy(),
                fluid,
                id,
                name,
                amt,
                cap,
                space,
                ratio,
                stack.isEmpty(),
                cap > 0 && amt >= cap
            );
        }

        public int getPercentage() {
            return (int) (fillRatio * 100);
        }
    }

    public record FluidScanResult(
        BlockPos pos,
        @Nullable Direction side,
        boolean hasCapability,
        @Nullable IFluidHandler handler,
        int tankCount,
        int totalFluidAmount,
        int totalCapacity,
        int simulatedDrainAmount,
        List<FluidTankInfo> tanks,
        List<FluidStack> nonEmpties
    ) {
        public static FluidScanResult empty(BlockPos pos, @Nullable Direction side) {
            return new FluidScanResult(pos, side, false, null, 0, 0, 0, 0, List.of(), List.of());
        }

        public boolean isEmpty() {
            return totalFluidAmount <= 0;
        }

        public boolean isFull() {
            return hasCapability && totalCapacity > 0 && totalFluidAmount >= totalCapacity;
        }

        public double getFillRatio() {
            return totalCapacity > 0 ? (double) totalFluidAmount / (double) totalCapacity : 0.0;
        }

        public boolean containsFluid(Fluid fluid) {
            return nonEmpties.stream().anyMatch(f -> f.getFluid() == fluid);
        }
    }

    public FluidScanResult scanTargetFluids() {
        return scanFluids(getTargetPos(), getTargetSide());
    }

    public FluidScanResult scanFluids(Direction direction) {
        return scanFluids(getBlockPos().relative(direction), direction.getOpposite());
    }

    public FluidScanResult scanFluids(BlockPos pos, @Nullable Direction side) {
        if (this.level == null) {
            return FluidScanResult.empty(pos, side);
        }
        IFluidHandler handler = this.level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side);
        if (handler == null) {
            return FluidScanResult.empty(pos, side);
        }

        int tankCount = handler.getTanks();
        int totalAmount = 0;
        int totalCap = 0;
        List<FluidTankInfo> tankInfos = new ArrayList<>(tankCount);
        List<FluidStack> nonEmpties = new ArrayList<>();

        for (int i = 0; i < tankCount; i++) {
            FluidTankInfo info = FluidTankInfo.of(i, handler);
            tankInfos.add(info);
            totalAmount += info.amount();
            totalCap += info.capacity();
            if (!info.isEmpty()) {
                nonEmpties.add(info.fluidStack());
            }
        }

        FluidStack simDrain = handler.drain(Integer.MAX_VALUE, FluidAction.SIMULATE);
        int maxDrain = simDrain.isEmpty() ? 0 : simDrain.getAmount();

        return new FluidScanResult(
            pos,
            side,
            true,
            handler,
            tankCount,
            totalAmount,
            totalCap,
            maxDrain,
            Collections.unmodifiableList(tankInfos),
            Collections.unmodifiableList(nonEmpties)
        );
    }

    /* =========================================================================
     *  5.) VOXEL SHAPE SCANNING
     * ========================================================================= */

    public record VoxelShapeScanResult(
        BlockPos pos,
        VoxelShape outlineShape,
        VoxelShape collisionShape,
        VoxelShape visualShape,
        VoxelShape interactionShape,
        VoxelShape blockSupportShape,
        AABB outlineBounds,
        AABB collisionBounds,
        List<AABB> outlineBoxes,
        List<AABB> collisionBoxes,
        boolean isFullCube,
        boolean isEmpty,
        double volume
    ) {
        public static VoxelShapeScanResult of(Level level, BlockPos pos, BlockState state) {
            CollisionContext ctx = CollisionContext.empty();
            VoxelShape outline = state.getShape(level, pos, ctx);
            VoxelShape collision = state.getCollisionShape(level, pos, ctx);
            VoxelShape visual = state.getVisualShape(level, pos, ctx);
            VoxelShape interaction = state.getInteractionShape(level, pos);
            VoxelShape support = state.getBlockSupportShape(level, pos);

            AABB outlineBounds = outline.isEmpty() ? new AABB(0, 0, 0, 0, 0, 0) : outline.bounds();
            AABB collBounds = collision.isEmpty() ? new AABB(0, 0, 0, 0, 0, 0) : collision.bounds();
            List<AABB> outlineBoxes = outline.toAabbs();
            List<AABB> collBoxes = collision.toAabbs();
            boolean fullCube = state.isCollisionShapeFullBlock(level, pos);
            boolean empty = outline.isEmpty();

            double vol = 0.0;
            for (AABB box : outlineBoxes) {
                vol += box.getXsize() * box.getYsize() * box.getZsize();
            }

            return new VoxelShapeScanResult(
                pos,
                outline,
                collision,
                visual,
                interaction,
                support,
                outlineBounds,
                collBounds,
                Collections.unmodifiableList(outlineBoxes),
                Collections.unmodifiableList(collBoxes),
                fullCube,
                empty,
                vol
            );
        }

        public double height() {
            return outlineBounds.getYsize();
        }

        public double width() {
            return outlineBounds.getXsize();
        }

        public double depth() {
            return outlineBounds.getZsize();
        }
    }

    public VoxelShapeScanResult scanTargetVoxelShape() {
        return scanVoxelShape(getTargetPos());
    }

    public VoxelShapeScanResult scanVoxelShape(Direction direction) {
        return scanVoxelShape(getBlockPos().relative(direction));
    }

    public VoxelShapeScanResult scanVoxelShape(BlockPos pos) {
        if (this.level == null) {
            return VoxelShapeScanResult.of(null, pos, Blocks.AIR.defaultBlockState());
        }
        BlockState state = this.level.getBlockState(pos);
        return VoxelShapeScanResult.of(this.level, pos, state);
    }

    /* =========================================================================
     *  6.) BAKED MODEL SCANNING (CLIENT SIDE)
     * ========================================================================= */

    @OnlyIn(Dist.CLIENT)
    public record ModelScanResult(
        BlockPos pos,
        BlockState state,
        @Nullable BakedModel model,
        boolean usesBlockLight,
        boolean isGui3d,
        boolean isCustomRenderer,
        boolean useAmbientOcclusion
    ) {
        public static ModelScanResult empty(BlockPos pos, BlockState state) {
            return new ModelScanResult(pos, state, null, false, false, false, false);
        }

        public boolean hasModel() {
            return model != null;
        }
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    @UseOnly(LogicalSide.CLIENT)
    public BakedModel scanTargetBakedModel() {
        return scanBakedModel(getTargetPos());
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    @UseOnly(LogicalSide.CLIENT)
    public BakedModel scanBakedModel(Direction direction) {
        return scanBakedModel(getBlockPos().relative(direction));
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    @UseOnly(LogicalSide.CLIENT)
    public BakedModel scanBakedModel(BlockPos pos) {
        if (this.level == null) return null;
        BlockState targetState = this.level.getBlockState(pos);
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(targetState);
    }

    @OnlyIn(Dist.CLIENT)
    @UseOnly(LogicalSide.CLIENT)
    public ModelScanResult scanTargetModelInfo() {
        return scanModelInfo(getTargetPos());
    }

    @OnlyIn(Dist.CLIENT)
    @UseOnly(LogicalSide.CLIENT)
    public ModelScanResult scanModelInfo(BlockPos pos) {
        if (this.level == null) {
            return ModelScanResult.empty(pos, Blocks.AIR.defaultBlockState());
        }
        BlockState state = this.level.getBlockState(pos);
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(state);
        if (model == null) {
            return ModelScanResult.empty(pos, state);
        }
        return new ModelScanResult(
            pos,
            state,
            model,
            model.usesBlockLight(),
            model.isGui3d(),
            model.isCustomRenderer(),
            model.useAmbientOcclusion()
        );
    }

    /* =========================================================================
     *  7.) BLOCKSTATE & PROPERTIES SCANNING
     * ========================================================================= */

    public record PropertyInfo(
        String name,
        String valueString,
        String valueType,
        List<String> allowedValues
    ) {
        public static <T extends Comparable<T>> PropertyInfo of(Property<T> property, Comparable<?> value) {
            List<String> allowed = property.getPossibleValues().stream().map(property::getName).toList();
            @SuppressWarnings("unchecked")
            String valStr = property.getName((T) value);
            return new PropertyInfo(
                property.getName(),
                valStr,
                property.getValueClass().getSimpleName(),
                Collections.unmodifiableList(allowed)
            );
        }
    }

    public record BlockStateScanResult(
        BlockPos pos,
        BlockState state,
        Block block,
        ResourceLocation blockId,
        Component displayName,
        Map<Property<?>, Comparable<?>> properties,
        List<PropertyInfo> propertyList,
        List<ResourceLocation> tags,
        FluidState fluidState,
        float destroySpeed,
        int lightEmission,
        int lightBlock,
        boolean isAir,
        boolean isSolid,
        boolean hasBlockEntity,
        boolean isSignalSource,
        boolean canOcclude
    ) {
        public boolean hasTag(ResourceLocation tagLocation) {
            return tags.contains(tagLocation);
        }

        public boolean hasProperty(String propertyName) {
            return propertyList.stream().anyMatch(p -> p.name().equals(propertyName));
        }

        public Optional<String> getPropertyValue(String propertyName) {
            return propertyList.stream()
                .filter(p -> p.name().equals(propertyName))
                .map(PropertyInfo::valueString)
                .findFirst();
        }
    }

    public BlockStateScanResult scanTargetBlockState() {
        return scanBlockState(getTargetPos());
    }

    public BlockStateScanResult scanBlockState(Direction direction) {
        return scanBlockState(getBlockPos().relative(direction));
    }

    public BlockStateScanResult scanBlockState(BlockPos pos) {
        if (this.level == null) {
            BlockState air = Blocks.AIR.defaultBlockState();
            return new BlockStateScanResult(
                pos, air, air.getBlock(), BuiltInRegistries.BLOCK.getKey(air.getBlock()),
                air.getBlock().getName(), Map.of(), List.of(), List.of(),
                air.getFluidState(), 0f, 0, 0, true, false, false, false, false
            );
        }

        BlockState state = this.level.getBlockState(pos);
        Block block = state.getBlock();
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        Component name = block.getName();
        Map<Property<?>, Comparable<?>> values = state.getValues();

        List<PropertyInfo> propInfos = new ArrayList<>(values.size());
        for (var entry : values.entrySet()) {
            propInfos.add(PropertyInfo.of(entry.getKey(), entry.getValue()));
        }

        List<ResourceLocation> tags = state.getTags().map(tagKey -> tagKey.location()).toList();
        FluidState fluidState = state.getFluidState();
        float destroySpeed = state.getDestroySpeed(this.level, pos);
        int lightEmission = state.getLightEmission(this.level, pos);
        int lightBlock = state.getLightBlock(this.level, pos);
        boolean isAir = state.isAir();
        boolean isSolid = state.isSolid();
        boolean hasBE = state.hasBlockEntity();
        boolean isSignalSource = state.isSignalSource();
        boolean canOcclude = state.canOcclude();

        return new BlockStateScanResult(
            pos,
            state,
            block,
            blockId,
            name,
            values,
            Collections.unmodifiableList(propInfos),
            Collections.unmodifiableList(tags),
            fluidState,
            destroySpeed,
            lightEmission,
            lightBlock,
            isAir,
            isSolid,
            hasBE,
            isSignalSource,
            canOcclude
        );
    }

    /* =========================================================================
     *  8.) ADDITIONAL HELPFUL DIAGNOSTIC SCANS
     * ========================================================================= */

    /**
     * Redstone & Power signals from the scanned block.
     */
    public record RedstoneScanResult(
        BlockPos pos,
        @Nullable Direction side,
        int directSignal,
        int weakSignal,
        int bestNeighborSignal,
        int analogOutputSignal,
        boolean hasAnalogOutputSignal,
        boolean isSignalSource,
        boolean isPowered
    ) {}

    public RedstoneScanResult scanTargetRedstone() {
        return scanRedstone(getTargetPos(), getTargetSide());
    }

    public RedstoneScanResult scanRedstone(BlockPos pos, @Nullable Direction side) {
        if (this.level == null) {
            return new RedstoneScanResult(pos, side, 0, 0, 0, 0, false, false, false);
        }
        BlockState state = this.level.getBlockState(pos);
        Direction querySide = side != null ? side : Direction.UP;
        int direct = state.getDirectSignal(this.level, pos, querySide);
        int weak = state.getSignal(this.level, pos, querySide);
        int best = this.level.getBestNeighborSignal(pos);
        boolean hasAnalog = state.hasAnalogOutputSignal();
        int analog = hasAnalog ? state.getAnalogOutputSignal(this.level, pos) : 0;
        boolean source = state.isSignalSource();
        boolean powered = this.level.hasNeighborSignal(pos);

        return new RedstoneScanResult(pos, side, direct, weak, best, analog, hasAnalog, source, powered);
    }

    /**
     * Neighbor BlockEntity identification.
     */
    public record BlockEntityScanResult(
        BlockPos pos,
        boolean exists,
        @Nullable BlockEntity blockEntity,
        @Nullable BlockEntityType<?> type,
        @Nullable ResourceLocation typeId,
        @Nullable Component customName,
        boolean isWitherBlockEntity
    ) {}

    public BlockEntityScanResult scanTargetBlockEntity() {
        return scanBlockEntity(getTargetPos());
    }

    public BlockEntityScanResult scanBlockEntity(BlockPos pos) {
        if (this.level == null) {
            return new BlockEntityScanResult(pos, false, null, null, null, null, false);
        }
        BlockEntity be = this.level.getBlockEntity(pos);
        if (be == null) {
            return new BlockEntityScanResult(pos, false, null, null, null, null, false);
        }
        BlockEntityType<?> type = be.getType();
        ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
        Component name = (be instanceof Nameable nameable && nameable.hasCustomName()) ? nameable.getCustomName() : null;
        boolean isWBE = be instanceof WitherBlockEntity;

        return new BlockEntityScanResult(pos, true, be, type, id, name, isWBE);
    }

    /**
     * Scans entities present in the target block space or in front of this block.
     */
    public record EntityScanResult(
        BlockPos targetPos,
        AABB searchBox,
        int totalEntities,
        List<Entity> allEntities,
        List<LivingEntity> livingEntities,
        List<ItemEntity> itemEntities,
        List<Player> players
    ) {
        public boolean hasEntities() {
            return totalEntities > 0;
        }

        public boolean hasLiving() {
            return !livingEntities.isEmpty();
        }

        public boolean hasPlayers() {
            return !players.isEmpty();
        }
    }

    public EntityScanResult scanTargetEntities() {
        return scanEntities(new AABB(getTargetPos()));
    }

    public EntityScanResult scanEntities(AABB box) {
        if (this.level == null) {
            return new EntityScanResult(getTargetPos(), box, 0, List.of(), List.of(), List.of(), List.of());
        }

        List<Entity> all = this.level.getEntities((Entity) null, box, Predicate.not(Entity::isSpectator));
        List<LivingEntity> living = new ArrayList<>();
        List<ItemEntity> items = new ArrayList<>();
        List<Player> players = new ArrayList<>();

        for (Entity e : all) {
            if (e instanceof Player p) {
                players.add(p);
                living.add(p);
            } else if (e instanceof LivingEntity l) {
                living.add(l);
            } else if (e instanceof ItemEntity item) {
                items.add(item);
            }
        }

        return new EntityScanResult(
            getTargetPos(),
            box,
            all.size(),
            Collections.unmodifiableList(all),
            Collections.unmodifiableList(living),
            Collections.unmodifiableList(items),
            Collections.unmodifiableList(players)
        );
    }

    /**
     * Raycast scanning along the facing axis up to a maximum distance.
     */
    public record RaycastScanResult(
        BlockPos startPos,
        BlockPos hitPos,
        Direction direction,
        int distance,
        BlockState hitState,
        boolean hitSolid
    ) {}

    public RaycastScanResult scanRaycast(int maxDistance, Predicate<BlockState> stopPredicate) {
        BlockPos current = getBlockPos();
        Direction facing = getCurrentFacing();

        for (int dist = 1; dist <= maxDistance; dist++) {
            BlockPos checkPos = current.relative(facing, dist);
            if (this.level == null) {
                return new RaycastScanResult(current, checkPos, facing, dist, Blocks.AIR.defaultBlockState(), false);
            }
            BlockState state = this.level.getBlockState(checkPos);
            if (stopPredicate.test(state)) {
                return new RaycastScanResult(current, checkPos, facing, dist, state, state.isSolid());
            }
        }

        BlockPos maxPos = current.relative(facing, maxDistance);
        BlockState finalState = this.level != null ? this.level.getBlockState(maxPos) : Blocks.AIR.defaultBlockState();
        return new RaycastScanResult(current, maxPos, facing, maxDistance, finalState, finalState.isSolid());
    }

    /**
     * Comprehensive master scan combining all diagnostics in one single call.
     */
    public record ComprehensiveScanResult(
        BlockPos pos,
        Direction facing,
        BlockStateScanResult blockState,
        VoxelShapeScanResult voxelShape,
        CapabilityScanResult capabilities,
        ItemScanResult items,
        EnergyScanResult energy,
        FluidScanResult fluids,
        RedstoneScanResult redstone,
        BlockEntityScanResult blockEntity,
        EntityScanResult entities
    ) {}

    public ComprehensiveScanResult scanTargetAll() {
        BlockPos targetPos = getTargetPos();
        Direction targetSide = getTargetSide();

        return new ComprehensiveScanResult(
            targetPos,
            getCurrentFacing(),
            scanBlockState(targetPos),
            scanVoxelShape(targetPos),
            scanCapabilities(targetPos, targetSide),
            scanItems(targetPos, targetSide),
            scanEnergy(targetPos, targetSide),
            scanFluids(targetPos, targetSide),
            scanRedstone(targetPos, targetSide),
            scanBlockEntity(targetPos),
            scanTargetEntities()
        );
    }
}
