package geni.withercore.blockentity;

import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import geni.withercore.io.item.MachineInventory;
import geni.withercore.io.item.MachineInventoryLayout;
import geni.withercore.io.item.SingleSlotAccess;
import geni.withercore.network.dataslot.NetworkDataSlot;
import geni.withercore.registration.impl.TileEntityTypeRegistryObject;
import geni.withercore.fakeplayer.WUTFakePlayer;
import geni.witherutils.WitherUtils;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.EventHooks;

public abstract class WitherMachineFakeBlockEntity extends WitherMachineBlockEntity {

	public UUID placedByUUID;
	protected int direction = 0;
	protected int tickSpeed = 20;
	protected int operationTicks = -1;
	protected WUTFakePlayer usefulFakePlayer;
	protected final Map<ChunkPos, Boolean> chunkTestCache = new Object2BooleanOpenHashMap<>();

	public static final SingleSlotAccess INPUT = new SingleSlotAccess();

	public WitherMachineFakeBlockEntity(TileEntityTypeRegistryObject<?> type, BlockPos worldPosition, BlockState blockState)
	{
		super(type, worldPosition, blockState);
		addDataSlot(NetworkDataSlot.ITEM_STACK.create(() -> INPUT.getItemStack(this), f -> INPUT.setStackInSlot(this, f)));
	}

	@Override
	public MachineInventoryLayout getInventoryLayout()
	{
		return MachineInventoryLayout.builder().inputSlot().slotAccess(INPUT).setStackLimit(1).build();
	}

	@Override
	protected MachineInventory createMachineInventory(MachineInventoryLayout layout)
	{
		return new MachineInventory(layout)
		{
			protected void onContentsChanged(int slot)
			{
				super.onContentsChanged(slot);
				if (level == null)
					return;
				if (slot == 0)
				{
					doFakePlayerStuff();
				}
				setChanged();
			}

			@Override
			public int getSlotLimit(int slot)
			{
				return 1;
			}
		};
	}

	@Override
	public ItemInteractionResult onBlockEntityUsed(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	private void doFakePlayerStuff()
	{
		ItemStack stackIn = INPUT.getItemStack(getInventory());
		if(stackIn.isEmpty())
		{
		}
		else
		{
		}
	}





	@Override
	public void serverTick()
	{
		super.serverTick();

		if (operationTicks <= 0)
			operationTicks = tickSpeed;
		operationTicks--;

		clearProtectionCache();
	}

	@Override
	public void clientTick()
	{
		super.clientTick();

		setLitProperty(!INPUT.getItemStack(getInventory()).isEmpty());
	}

	public int getTickSpeed() {
		return tickSpeed;
	}

	public void setTickSpeed(int newTickSpeed) {
		this.tickSpeed = newTickSpeed;
		if (operationTicks > tickSpeed)
			operationTicks = tickSpeed;
		markDirtyClient();
	}

	public void clearProtectionCache() {
		chunkTestCache.clear();
	}







	public boolean canRun() {
		return operationTicks == 0;
	}





	public int getDirection() {
		return direction;
	}

	public Direction getDirectionValue() {
		return Direction.values()[direction];
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	protected GameProfile getPlacedByProfile() {
		if (placedByUUID == null)
			return WitherUtils.gameProfile;
		return new GameProfile(placedByUUID, "[WitherFakePlayer]");
	}






	protected boolean canPlaceAt(Level level, BlockPos blockPos, FakePlayer fakePlayer) {
		ChunkPos chunkPos = new ChunkPos(blockPos);
		if (chunkTestCache.containsKey(chunkPos))
			return chunkTestCache.get(chunkPos);
		boolean canPlace = !EventHooks.onBlockPlace(fakePlayer, BlockSnapshot.create(level.dimension(), level, blockPos.below()), Direction.UP);
		chunkTestCache.put(chunkPos, canPlace);
		return canPlace;
	}

	protected boolean canBreakAt(Level level, BlockPos blockPos, FakePlayer fakePlayer) {
		return true;
	}

	protected boolean canBreakAndPlaceAt(Level level, BlockPos blockPos, FakePlayer fakePlayer) {
		ChunkPos chunkPos = new ChunkPos(blockPos);
		if (chunkTestCache.containsKey(chunkPos))
			return chunkTestCache.get(chunkPos);
		boolean canBreak = canBreakAt(level, blockPos, fakePlayer) && canPlaceAt(level, blockPos, fakePlayer);
		chunkTestCache.put(chunkPos, canBreak);
		return canBreak;
	}

	protected FakePlayer getFakePlayer(ServerLevel level) {
		return FakePlayerFactory.get(level, getPlacedByProfile());
	}

	protected FakePlayer getFakePlayer(ServerLevel level, UUID uuid) {
		GameProfile gameProfile = new GameProfile(uuid, "[WitherFakePlayer]");
		return FakePlayerFactory.get(level, gameProfile);
	}

	protected WUTFakePlayer getUsefulFakePlayer(ServerLevel level) {
		if (usefulFakePlayer == null)
			usefulFakePlayer = WUTFakePlayer.createPlayer(level, getPlacedByProfile());
		return usefulFakePlayer;
	}

	public void setFakePlayerData(ItemStack itemStack, FakePlayer fakePlayer, BlockPos blockPos, Direction direction) {
		fakePlayer.setPos(blockPos.below().relative(direction).getX() + 0.5, blockPos.below().relative(direction).getY(), blockPos.below().relative(direction).getZ() + 0.5);
		float xRot = direction == Direction.DOWN ? 90 : direction == Direction.UP ? -90 : 0;
		fakePlayer.setXRot(xRot);
		fakePlayer.setYRot(direction.toYRot());
		fakePlayer.setYHeadRot(direction.toYRot());
		fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
	}

	public void setPlacedBy(UUID placedBy) {
		this.placedByUUID = placedBy;
	}





	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider)
	{
		this.getInventory().deserializeNBT(lookupProvider, tag.getCompound("Items"));
		if (tag.contains("direction"))
			direction = tag.getInt("direction");
		if (tag.contains("tickspeed"))
			tickSpeed = tag.getInt("tickspeed");
		if (tag.contains("placedBy"))
			placedByUUID = tag.getUUID("placedBy");
		super.loadAdditional(tag, lookupProvider);
	}
	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider)
	{
		super.saveAdditional(tag, lookupProvider);
		tag.put("Items", getInventory().serializeNBT(lookupProvider));
		tag.putInt("tickspeed", tickSpeed);
		if (placedByUUID != null)
			tag.putUUID("placedBy", placedByUUID);
		tag.putInt("direction", direction);
	}
}

/*
private static final UUID ID = UUID.randomUUID();
protected GameProfile gameProfile;
protected WeakReference<WUTFakePlayer> fakePlayer;
protected boolean unrotatedYaw = false;

protected int fakeStamina = 0;

private final static ItemStack genericDigger = new ItemStack(Items.DIAMOND_PICKAXE, 1);
private final static ItemStack genericRod = new ItemStack(Items.FISHING_ROD, 1);

public WitherMachineFakeBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState)
{
	super(type, worldPosition, blockState);
	addDataSlot(new IntegerDataSlot(this::getFakeStamina, p -> fakeStamina = p, SyncMode.WORLD));
}

protected WitherMachineFakeBlockEntity setUnrotatedYaw(boolean unRot)
{
	unrotatedYaw = unRot;
	return this;
}

@Nullable
public FoodData getFoodData()
{
	if(fakePlayer == null)
		return null;
	return fakePlayer.get().getFoodData();
}

public WeakReference<WUTFakePlayer> initFakePlayer(ServerLevel ws, String blockName, BlockEntity machineBe)
{
	final String name = "fp." + blockName;
	final GameProfile profile = new GameProfile(ID, name);
	WeakReference<WUTFakePlayer> fakePlayer = new WeakReference<>(WUTFakePlayer.get(ws).get());
	if (fakePlayer == null || fakePlayer.get() == null)
	{
		fakePlayer = null;
		gameProfile = null;
		return null;
	}
	fakePlayer.get().setOnGround(true);
	fakePlayer.get().connection = new ServerGamePacketListenerImpl(ws.getServer(), new Connection(PacketFlow.SERVERBOUND), fakePlayer.get())
	{
		@Override
		public void send(Packet<?> packetIn) {}
	};
	fakePlayer.get().setSilent(false);
	gameProfile = profile;

	fakePlayer.get().setPosition(machineBe.getBlockPos().getX(), machineBe.getBlockPos().getY(), machineBe.getBlockPos().getZ());

	if(!unrotatedYaw)
		fakePlayer.get().setYRot(getYawFromFacing(getCurrentFacing()));

	return fakePlayer;
}

public float getYawFromFacing(Direction currentFacing)
{
	switch (currentFacing)
	{
		case DOWN:
		case UP:
		case SOUTH:
		default:
			return 0;
		case EAST:
			return 270F;
		case NORTH:
			return 180F;
		case WEST:
			return 90F;
	}
}

public void setPlayer(Player player)
{
	this.gameProfile = player.getGameProfile();
	this.setChanged();
}

public void tryEquipItem(ItemStack item, InteractionHand hand)
{
	fakePlayer.get().setItemInHand(hand, item);
}

public static InteractionResult playerAttackBreakBlock(WeakReference<WUTFakePlayer> fakePlayer, Level world, BlockPos targetPos, InteractionHand hand, Direction facing) throws Exception
{
	if (fakePlayer == null)
		return InteractionResult.FAIL;
	try
	{
		fakePlayer.get().gameMode.handleBlockBreakAction(targetPos, ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, facing, world.getMaxBuildHeight(), 0);
		return InteractionResult.SUCCESS;
	}
	catch (Exception e)
	{
		return InteractionResult.FAIL;
	}
}

public boolean placeBlock(Level world, BlockPos pos, ItemStack stack)
{
	fakePlayer.get().setItemInHand(InteractionHand.MAIN_HAND, stack);
	return ForgeHooks.onPlaceItemIntoWorld(new UseOnContext(fakePlayer.get(), InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(0, 0, 0), Direction.DOWN, pos, false))) == InteractionResult.SUCCESS;
}

public static InteractionResult interactUseOnBlock(WeakReference<WUTFakePlayer> fakePlayer, Level world, BlockPos targetPos, InteractionHand hand, Direction facing) throws Exception
{
	if (fakePlayer == null)
		return InteractionResult.FAIL;
	Direction placementOn = (facing == null) ? fakePlayer.get().getMotionDirection() : facing;
	BlockHitResult blockraytraceresult = new BlockHitResult(fakePlayer.get().getLookAngle(), placementOn, targetPos, true);
	ItemStack itemInHand = fakePlayer.get().getItemInHand(hand);
	InteractionResult result = fakePlayer.get().gameMode.useItemOn(fakePlayer.get(), world, itemInHand, hand, blockraytraceresult);
	return result;
}

public void fakePlayerDumpInventory(WeakReference<WUTFakePlayer> fakePlayer, MultiSlotAccess outputs, boolean onGround)
{
	ArrayList<ItemStack> toDrop = new ArrayList<ItemStack>();

	for (int i = outputs.get(0).getIndex(); i < fakePlayer.get().getInventory().items.size(); i++)
	{
		ItemStack fpItem = fakePlayer.get().getInventory().items.get(i);

		if (fpItem.isEmpty())
		{
			continue;
		}

		if (fpItem == fakePlayer.get().getMainHandItem())
		{
			continue;
		}

		for (int j = 0; j < outputs.getAccesses().size(); j++)
		{
			fpItem = outputs.get(j).insertItem(getInventory(), fpItem, false);
		}

		if (onGround)
		{
			toDrop.add(fpItem);
		}

		else
		{
			fakePlayer.get().getInventory().items.set(i, fpItem);
		}

	}
	if (onGround)
	{
		ItemStackUtil.drop(this.level, this.worldPosition.above(), toDrop);
	}
}

@Override
public void serverTick()
{
	if(fakePlayer != null)
		this.fakePlayer.get().getCooldowns().tick();

	super.serverTick();
}

public abstract void resetFakeStamina();

public int getFakeStamina()
{
	return fakeStamina;
}

public void attackTargetEntityWithCurrentItem(@Nonnull Entity targetEntity)
{
	fakePlayer.get().setOnGround(true);
	faceEntity(targetEntity);
}
public void faceEntity(Entity entityIn)
{
	double d0 = entityIn.getX() - fakePlayer.get().position().x;
	double d2 = entityIn.getZ() - fakePlayer.get().position().z;
	double d1;

	if (entityIn instanceof LivingEntity)
	{
		LivingEntity entitylivingbase = (LivingEntity) entityIn;
		d1 = entitylivingbase.position().y + entitylivingbase.getEyeHeight() - (fakePlayer.get().position().y + fakePlayer.get().getEyeHeight());
	}
	else
		d1 = (entityIn.getBoundingBox().minY + entityIn.getBoundingBox().maxY) / 2.0D - (fakePlayer.get().position().y + fakePlayer.get().getEyeHeight());

	double d3 = Math.sqrt(d0 * d0 + d2 * d2);
	fakePlayer.get().xRotO = Mth.wrapDegrees((float) (-(Math.atan2(d1, d3) * (180D / Math.PI))));
	fakePlayer.get().yRotO = Mth.wrapDegrees((float) (Math.atan2(d2, d0) * (180D / Math.PI)) - 90.0F);
}*/
