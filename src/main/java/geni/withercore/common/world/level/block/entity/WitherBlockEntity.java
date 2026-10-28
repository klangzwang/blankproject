package geni.withercore.common.world.level.block.entity;

import geni.witherapi.block.entity.UseOnly;
import geni.withercore.common.network.dataslot.ClientboundDataSlotChange;
import geni.withercore.common.network.dataslot.NetworkDataSlot;
import geni.withercore.common.network.dataslot.ServerboundCDataSlotUpdate;
import geni.withercore.common.registration.impl.TileEntityTypeRegistryObject;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WitherBlockEntity extends BlockEntity implements GeoAnimatable {
	
    public static final String DATA = "Data";
    public static final String INDEX = "Index";
    private final List<NetworkDataSlot<?>> dataSlots = new ArrayList<>();
    private final List<Runnable> afterDataSync = new ArrayList<>();
    private boolean isChangedDeferred = true;

    private final Map<BlockCapability<?, ?>, EnumMap<Direction, BlockCapabilityCache<?, ?>>> selfCapabilities = new HashMap<>();
    private final Map<BlockCapability<?, ?>, EnumMap<Direction, BlockCapabilityCache<?, ?>>> neighbourCapabilities = new HashMap<>();

	protected int powerLevel = 0;

    public WitherBlockEntity(TileEntityTypeRegistryObject<?> pType, BlockPos pPos, BlockState pBlockState)
    {
		super(pType.value(), pPos, pBlockState);
	}
    
    public static void tick(Level level, BlockPos pos, BlockState state, WitherBlockEntity blockEntity)
    {
        if (level.isClientSide)
        {
            blockEntity.clientTick();
        }
        else
        {
            blockEntity.serverTick();
        }
    }
    
    public void serverTick()
    {
        if (level != null && !level.isClientSide)
        {
            sync();
            level.blockEntityChanged(worldPosition);
        }
    }
    public void clientTick()
    {
    }

    public void endTick()
    {
        if (this.level == null)
        {
            return;
        }
        if (isChangedDeferred)
        {
            isChangedDeferred = false;
            setChanged(level, getBlockPos(), getBlockState());
        }
    }
    
    @Override
    public void setChanged()
    {
        this.isChangedDeferred = true;
    }

    protected void updateNeighbours() {
        if (level != null) {
            level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        CompoundTag data = super.getUpdateTag(registries);

        ListTag dataList = new ListTag();
        for (int i = 0; i < dataSlots.size(); i++) {
            var slot = dataSlots.get(i);
            var nbt = slot.save(registries, true);

            CompoundTag slotTag = new CompoundTag();
            slotTag.putInt(INDEX, i);
            slotTag.put(DATA, nbt);

            dataList.add(slotTag);
        }

        data.put(DATA, dataList);

        saveAdditionalSynced(data, registries);

        return data;
    }
    
    @Override
    public void handleUpdateTag(CompoundTag syncData, HolderLookup.Provider lookupProvider)
    {
        super.handleUpdateTag(syncData, lookupProvider);

        if (syncData.contains(DATA, Tag.TAG_LIST))
        {
            ListTag dataList = syncData.getList(DATA, Tag.TAG_COMPOUND);

            for (Tag dataEntry : dataList)
            {
                if (dataEntry instanceof CompoundTag slotData)
                {
                    int slotIdx = slotData.getInt(INDEX);
                    dataSlots.get(slotIdx).parse(lookupProvider, Objects.requireNonNull(slotData.get(DATA)));
                }
            }

            for (Runnable task : afterDataSync)
            {
                task.run();
            }
        }
    }

    @SuppressWarnings("deprecation")
	private byte @Nullable [] createBufferSlotUpdate()
    {
        List<Integer> needsUpdate = new ArrayList<>();
        for (int i = 0; i < dataSlots.size(); i++) {
            if (dataSlots.get(i).doesNeedUpdate()) {
                needsUpdate.add(i);
            }
        }

        if (needsUpdate.isEmpty()) {
            return null;
        }

        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), level.registryAccess());
        buf.writeInt(needsUpdate.size());
        needsUpdate.forEach(i -> {
            buf.writeInt(i);
            dataSlots.get(i).write(buf);
        });
        return buf.array();
    }

    public <T extends NetworkDataSlot<?>> T addDataSlot(T slot) {
        dataSlots.add(slot);
        return slot;
    }

    public void addAfterSyncRunnable(Runnable runnable) {
        afterDataSync.add(runnable);
    }

	@UseOnly(LogicalSide.CLIENT)
    public <T> void clientUpdateSlot(@Nullable NetworkDataSlot<T> slot, T value) {
        if (slot == null) {
            return;
        }

        if (dataSlots.contains(slot)) {
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), level.registryAccess());
            buf.writeInt(dataSlots.indexOf(slot));
            slot.write(buf, value);
            PacketDistributor.sendToServer(new ClientboundDataSlotChange(getBlockPos(), buf.array()));
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_NEIGHBORS);
        }
    }

    @UseOnly(LogicalSide.SERVER)
    public void sync() {
        var syncData = createBufferSlotUpdate();
        if (syncData != null && level instanceof ServerLevel serverLevel) {
            setChanged();
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(getBlockPos()),
                    new ServerboundCDataSlotUpdate(getBlockPos(), syncData));
        }
    }

    @UseOnly(LogicalSide.CLIENT)
    public void clientHandleBufferSync(RegistryFriendlyByteBuf buf) {
        for (int amount = buf.readInt(); amount > 0; amount--) {
            int index = buf.readInt();
            dataSlots.get(index).read(buf);
        }

        for (Runnable task : afterDataSync) {
            task.run();
        }
    }

    @UseOnly(LogicalSide.SERVER)
    public void serverHandleBufferChange(RegistryFriendlyByteBuf buf) {
        int index;
        try {
            index = buf.readInt();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid buffer was passed over the network to the server.");
        }

        dataSlots.get(index).read(buf);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveAdditionalSynced(tag, registries);
    }

    protected void saveAdditionalSynced(CompoundTag tag, HolderLookup.Provider registries) {
    }

    @SuppressWarnings("unchecked")
	@Nullable
    protected <T> T getSelfCapability(BlockCapability<T, Direction> capability, Direction side) {
        if (level == null) {
            return null;
        }

        if (!selfCapabilities.containsKey(capability)) {
            selfCapabilities.put(capability, new EnumMap<>(Direction.class));

            for (Direction direction : Direction.values()) {
                populateSelfCachesFor(direction, capability);
            }
        }

        if (!selfCapabilities.get(capability).containsKey(side)) {
            return null;
        }

        return (T) selfCapabilities.get(capability).get(side).getCapability();
    }

    private void populateSelfCachesFor(Direction direction, BlockCapability<?, Direction> capability) {
        if (level instanceof ServerLevel serverLevel) {
            selfCapabilities.get(capability)
                    .put(direction, BlockCapabilityCache.create(capability, serverLevel, getBlockPos(), direction));
        }
    }

    @SuppressWarnings("unchecked")
	@Nullable
    protected <T> T getNeighbouringCapability(BlockCapability<T, Direction> capability, Direction side) {
        if (level == null) {
            return null;
        }

        if (!neighbourCapabilities.containsKey(capability)) {

            neighbourCapabilities.put(capability, new EnumMap<>(Direction.class));

            for (Direction direction : Direction.values()) {
                populateNeighbourCachesFor(direction, capability);
            }
        }

        if (!neighbourCapabilities.get(capability).containsKey(side)) {
            return null;
        }

        return (T) neighbourCapabilities.get(capability).get(side).getCapability();
    }

    private void populateNeighbourCachesFor(Direction direction, BlockCapability<?, Direction> capability) {
        if (level instanceof ServerLevel serverLevel) {
            BlockPos neighbourPos = getBlockPos().relative(direction);
            neighbourCapabilities.get(capability)
                    .put(direction, BlockCapabilityCache.create(capability, serverLevel, neighbourPos,
                            direction.getOpposite()));
        }
    }

    /*
     * 
     * CAP TRANSFER RATE
     * 
     */
    public Map<BlockCapability<?, ?>, Integer> getCapabilityTransferMap()
    {
    	Map<BlockCapability<?, ?>, Integer> allCapsMap = new HashMap<>();
        BlockPos currentPos = this.getBlockPos();

        for (Direction direction : Direction.values())
        {
            BlockPos neighborPos = currentPos.relative(direction);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            
            if (neighbor != null)
            {
                if(level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, direction.getOpposite()) != null)
                {
                	Optional<IFluidHandler> fluidOptHandler = Optional.of(level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, direction.getOpposite()));
                	if(fluidOptHandler.isPresent())
                	{
                		FluidStack availableFluid = fluidOptHandler.get().drain(Integer.MAX_VALUE, FluidAction.SIMULATE);
                		if (!availableFluid.isEmpty())
                		{
 
                			allCapsMap.put(Capabilities.FluidHandler.BLOCK, availableFluid.getAmount());
                		}
                	}
                }
                
                if(level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, direction.getOpposite()) != null)
                {
                	Optional<IEnergyStorage> energyOptHandler = Optional.of(level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, direction.getOpposite()));
                	if(energyOptHandler.isPresent())
                	{
                		int availablePower = energyOptHandler.get().extractEnergy(Integer.MAX_VALUE, true);
                		if (availablePower > 0)
                		{
                			allCapsMap.put(Capabilities.EnergyStorage.BLOCK, energyOptHandler.get().receiveEnergy(Integer.MAX_VALUE, true));
                		}
                	}
                }
            }
        }

        return allCapsMap;
    }
    
    public int getNeighbourFluidTransfer()
    {
        AtomicInteger totalTransfer = new AtomicInteger();
        if(getCapabilityTransferMap() != null)
        {
            for (var capValue : getCapabilityTransferMap().entrySet())
            {
            	if(capValue.getKey() == Capabilities.FluidHandler.BLOCK)
            	{
            		totalTransfer.addAndGet(capValue.getValue());
            	}
            }
        }
        return totalTransfer.get();
    }
    
    public int getNeighbourEnergyTransfer()
    {
        AtomicInteger totalTransfer = new AtomicInteger();
        if(getCapabilityTransferMap() != null)
        {
            for (var capValue : getCapabilityTransferMap().entrySet())
            {
            	if(capValue.getKey() == Capabilities.EnergyStorage.BLOCK)
            	{
            		totalTransfer.addAndGet(capValue.getValue());
            	}
            }
        }
        return totalTransfer.get();
    }

    // --
    
    public Direction getCurrentFacing()
    {
        if(this.getBlockState().hasProperty(BlockStateProperties.FACING))
        {
            return this.getBlockState().getValue(BlockStateProperties.FACING);
        }
        if(this.getBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING))
        {
            return this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
        return Direction.SOUTH;
    }
    
    public Direction getSideFacingBlock()
    {
        return getCurrentFacing().getOpposite();
    }
    public Direction getSideFacingPlayer()
    {
        return getCurrentFacing();
    }
	
    /**
     * 
     * BLOCK
     * 
     */
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {}
    public void onBlockPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {}
    public void onAdded(Level world, BlockState state, BlockState oldState, boolean isMoving) {}
	public void onNeighborBlockChange(BlockPos fromPos) {}

	public void popRecipeExperience(ServerPlayer serverplayer, float count) {}

    public boolean stillValid(Player pPlayer)
    {
        if (this.level == null)
        {
            return false;
        }
        if (this.level.getBlockEntity(this.worldPosition) != this)
        {
            return false;
        }
        return pPlayer.canInteractWithBlock(this.worldPosition, 1.5);
    }
  
    /*
     * 
     * PlayerAABB
     * 
     */
    public boolean canSearchPlayer()
    {
        return false;
    }
    
	public LivingEntity getEntity()
	{
		return null;
	}
	
	public Player getPlayer(int range)
	{
		if(canSearchPlayer())
		{
			AABB ofSize = AABB.ofSize(Vec3.atCenterOf(worldPosition), range, range, range);
	        List<Player> playerList = level.getEntitiesOfClass(Player.class, ofSize);
	        return playerList.isEmpty() ? null : playerList.getFirst();			
		}
		else
			return null;
	}
	
    /*
     * 
     * CAPABILITY
     * 
     */
    public boolean hasItemCapability()
    {
        return false;
    }
    public boolean hasFluidCapability()
    {
        return false;
    }
    public boolean hasEnergyCapability()
    {
        return false;
    }

    public IItemHandler getPlayerItemHandler()
    {
        return null;
    }
    
    public IItemHandler getItemHandler(@Nullable Direction dir)
    {
        return null;
    }
    public IFluidHandler getFluidHandler(@Nullable Direction dir)
    {
        return null;
    }
    public IEnergyStorage getEnergyHandler(@Nullable Direction dir)
    {
        return null;
    }
    
    public IItemHandler getItemHandler()
    {
        return null;
    }
    public IFluidHandler getFluidHandler()
    {
        return null;
    }
    public IEnergyStorage getEnergyHandler()
    {
        return null;
    }
    
    
	/*
	 * 
	 * REDSTONE
	 * 
	 */
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) { return -1; }
    
    public void checkRedstone(Level world, BlockPos pos)
    {
        int powered = world.getBestNeighborSignal(pos);
        setPowerInput(powered);
    }

    public void setPowerInput(int powered)
    {
        if (powerLevel != powered)
        {
            powerLevel = powered;
            setChanged();
        }
    }

    /*
     *
     * DIRTY STUFF BOY
     *
     */
    public void markDirtyClient() {
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(getBlockPos());
            level.sendBlockUpdated(getBlockPos(), state, state, 3);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }
}
