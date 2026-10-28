package geni.withercore.blockentity;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import geni.witherapi.ISerializableTanks;
import geni.withercore.io.item.MachineInventory;
import geni.withercore.io.item.MachineInventoryLayout;
import geni.withercore.registration.impl.TileEntityTypeRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class WitherMachineBlockEntity extends WitherBlockEntity implements Nameable, MenuProvider {

    @Nullable
	protected MachineInventory inventory;
    
    private Component customName = null;
    
    public WitherMachineBlockEntity(TileEntityTypeRegistryObject<?> type, BlockPos worldPosition, BlockState blockState)
    {
        super(type, worldPosition, blockState);

        MachineInventoryLayout slotLayout = getInventoryLayout();
        
        if (slotLayout != null)
            inventory = createMachineInventory(slotLayout);
        else
            inventory = null;
    }

    @Nonnull
    public Level nonNullLevel() {
        // use in methods where we know the level is not null, e.g. tickers, renderers...
        return Objects.requireNonNull(super.getLevel());
    }

    public ItemInteractionResult onBlockEntityUsed(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
    }
    
    public boolean canOpenMenu()
    {
        return false;
    }
    
    public boolean canAct()
    {
        if (this.level == null)
        {
            return false;
        }
        return true;
    }

    public boolean canActSlow()
    {
        if (this.level == null)
        {
            return false;
        }
        return canAct() && this.level.getGameTime() % 5 == 0;
    }

//    private void moveItems(Direction side)
//    {
//        IItemHandler selfHandler = getSelfCapability(Capabilities.ItemHandler.BLOCK, side);
//        IItemHandler otherHandler = getNeighbouringCapability(Capabilities.ItemHandler.BLOCK, side);
//        if (selfHandler == null || otherHandler == null)
//        {
//            return;
//        }
//        moveItems(selfHandler, otherHandler);
//    }
//
//    private static void moveItems(IItemHandler from, IItemHandler to)
//    {
//        for (int i = 0; i < from.getSlots(); i++)
//        {
//            ItemStack extracted = from.extractItem(i, 1, true);
//            if (!extracted.isEmpty())
//            {
//                for (int j = 0; j < to.getSlots(); j++)
//                {
//                    ItemStack inserted = to.insertItem(j, extracted, false);
//                    if (inserted.isEmpty())
//                    {
//                        from.extractItem(i, 1, false);
//                        return;
//                    }
//                }
//            }
//        }
//    }
    
//    protected void getFluidsFromDirection()
//    {
//        for (Direction direction : Direction.values())
//        {
//        	if(hasFluidCapability() && getFluidHandler(direction) != null)
//        	{
//        		if (getFluidHandler(null).getFluidInTank(0).getAmount() > 0 && tank.getFluidInTank(0).getAmount() < tank.getTankCapacity(0))
//        		{
//                    int drain = getFluidHandler(direction).drain(new FluidStack(getFluidHandler(direction).getFluidInTank(0).getFluid(), 100), FluidAction.SIMULATE).getAmount();
//                    if (drain > 0)
//                    {
//                        int filled = tank.fill(new FluidStack(getFluidHandler(direction).getFluidInTank(0).getFluid(), 100), FluidAction.EXECUTE);
//                        getFluidHandler(direction).drain(new FluidStack(getFluidHandler(direction).getFluidInTank(0).getFluid(), filled), FluidAction.EXECUTE);
//                    }
//        		}
//        	}
//        }
//    }
    
    /*
     * 
     * ITEMINVENTORY
     * 
     */
    @Override
    public boolean hasItemCapability()
    {
        return true;
    }
    
    public final IItemHandler getItemHandler()
    {
        return getItemHandler(null);
    }
    
    @Nullable
    public MachineInventoryLayout getInventoryLayout()
    {
        return null;
    }

    @Nullable
    public MachineInventory getInventory()
    {
        return inventory;
    }

    protected final MachineInventory getInventoryNN()
    {
        return Objects.requireNonNull(inventory);
    }

    protected MachineInventory createMachineInventory(MachineInventoryLayout layout)
    {
        return new MachineInventory(layout)
        {
            @Override
            protected void onContentsChanged(int slot)
            {
                onInventoryContentsChanged(slot);
                setChanged();
            }
        };
    }
    
    protected void onInventoryContentsChanged(int slot) {};
    
	public final boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack)
	{
		if (i < 0 || i >= getInventoryLayout().getSlotCount())
		{
			return false;
		}
		return isMachineItemValidForSlot(i, itemstack);
	}

    public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) { return false; };
    
	public int getSizeInventory()
	{
		return getInventoryLayout().getSlotCount();
	}

	public int getInventoryStackLimit(int slot)
	{
		return getInventoryStackLimit();
	}

	public int getInventoryStackLimit()
	{
		return 64;
	}

	public @Nonnull ItemStack getStackInSlot(int slot)
	{
		if (slot < 0 || slot >= inventory.getSlots())
		{
			return ItemStack.EMPTY;
		}
		final ItemStack itemStack = inventory.getStackInSlot(slot);
		return itemStack == null ? ItemStack.EMPTY : itemStack;
	}

	public void setInventorySlotContents(int slot, @Nonnull ItemStack contents)
	{
		inventory.setStackInSlot(slot, contents.copy());
		if (inventory.getStackInSlot(slot).getCount() > getInventoryStackLimit(slot))
		{
			inventory.getStackInSlot(slot).setCount(getInventoryStackLimit(slot));
			contents.shrink(getInventoryStackLimit(slot));
		}
		setChanged();
	}
    
    /*
     * 
     * FLUIDTANK
     * 
     */
    @Override
    public boolean hasFluidCapability()
    {
        return true;
    }
    
    public final IFluidHandler getFluidHandler()
    {
        return getFluidHandler(null);
    }
    
    protected void onTankContentsChanged() {}
    
    /*
     * 
     * LITREDSTONE
     * 
     */
    public void setLitProperty(boolean lit)
    {
        BlockState st = this.getBlockState();
        if(!st.hasProperty(BlockStateProperties.LIT))
            return;
        boolean previous = st.getValue(BlockStateProperties.LIT);
        if(previous != lit)
            this.level.setBlockAndUpdate(worldPosition, st.setValue(BlockStateProperties.LIT, lit));
    }
    
    public boolean isLit()
    {
    	return this.getBlockState().getValue(BlockStateProperties.LIT);
    }
    
    public boolean isPowered()
    {
        return this.getLevel().hasNeighborSignal(this.getBlockPos());
    }

    /*
     * 
     * TAGS
     * 
     */
    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider)
    {
        super.saveAdditional(pTag, lookupProvider);

        if (this.inventory != null)
        {
            pTag.put("Items", inventory.serializeNBT(lookupProvider));
        }
        if (this instanceof ISerializableTanks st)
        {
        	pTag.put("Tanks", st.serializeTanks(lookupProvider));
        }
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider)
    {
        super.loadAdditional(pTag, lookupProvider);
        
        if (this.inventory != null)
        {
            inventory.deserializeNBT(lookupProvider, pTag.getCompound("Items"));
        }
        if (this instanceof ISerializableTanks st)
        {
            st.deserializeTanks(lookupProvider, pTag.getCompound("Tanks"));
        }
    }
    
    @Override
    protected void applyImplicitComponents(DataComponentInput components)
    {
        super.applyImplicitComponents(components);
        if (this.inventory != null)
        {
            this.inventory.copyFromItem(components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
        }
        if (this instanceof ISerializableTanks st)
        {
            st.getSerializableTanks().forEach((comp, tank) ->
                    tank.loadFromContent(components.getOrDefault(comp, SimpleFluidContent.EMPTY)));
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        super.collectImplicitComponents(components);
        if (this.inventory != null)
        {
            components.set(DataComponents.CONTAINER, this.inventory.toItemContents());
        }
        if (this instanceof ISerializableTanks st)
        {
            st.getSerializableTanks().forEach((comp, tank) -> {
                if (!tank.isEmpty())
                {
                	components.set(comp, tank.getContent());
                }
            });
        }
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        super.removeComponentsFromTag(tag);
        tag.remove("Items");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player)
    {
        return null;
    }
    
    private String getBlockTranslationKey()
    {
        String key = BuiltInRegistries.BLOCK_ENTITY_TYPE.getResourceKey(getType())
                .map(rk -> rk.location().getPath())
                .orElse("unknown");
        return "block.witherutils." + key;
    }
    
    @Override
    public Component getName()
    {
        return customName == null ? Component.translatable(getBlockTranslationKey()) : customName;
    }
    
	@Override
	public Component getDisplayName()
	{
		return getName();
	}
	
    protected BlockCapabilityCache<IItemHandler,Direction> createItemHandlerCache(Direction dir) {
        return getLevel() instanceof ServerLevel serverLevel ?
                BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, serverLevel, getBlockPos().relative(dir), dir.getOpposite(), () -> !isRemoved(), () -> {}) :
                null;
    }

    protected BlockCapabilityCache<IFluidHandler,Direction> createFluidHandlerCache(Direction dir) {
        return getLevel() instanceof ServerLevel serverLevel ?
                BlockCapabilityCache.create(Capabilities.FluidHandler.BLOCK, serverLevel, getBlockPos().relative(dir), dir.getOpposite(), () -> !isRemoved(), () -> {}) :
                null;
    }
}
