package geni.withercore.blockentity;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import geni.witherapi.energy.EnergyIOMode;
import geni.witherapi.soulbank.SoulBankData;
import geni.witherapi.soulbank.SoulBankScalable;
import geni.withercore.io.energy.IWitherEnergyStorage;
import geni.withercore.io.energy.ImmutableMachineEnergyStorage;
import geni.withercore.io.energy.WitherEnergyStorage;
import geni.withercore.io.item.MachineInstallable;
import geni.withercore.io.item.MachineInventory;
import geni.withercore.io.item.MachineInventoryLayout;
import geni.withercore.network.dataslot.NetworkDataSlot;
import geni.withercore.registration.impl.TileEntityTypeRegistryObject;
import geni.withercore.sync.EnergySyncData;
import geni.withercore.init.WUTComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public abstract class WitherMachineEnergyBlockEntity extends WitherMachineBlockEntity implements MachineInstallable {

    protected final WitherEnergyStorage energyStorage;
    protected IWitherEnergyStorage clientEnergyStorage = ImmutableMachineEnergyStorage.EMPTY;
    
    private SoulBankData cachedSoulBankData = SoulBankData.NONE;
    private boolean soulbankCacheDirty;
    
    protected final @Nonnull SoulBankScalable capacity, usageRate, received;

    public WitherMachineEnergyBlockEntity(EnergyIOMode energyIOMode, SoulBankScalable capacity, SoulBankScalable usageRate, SoulBankScalable received, TileEntityTypeRegistryObject<?> type, BlockPos worldPosition, BlockState blockState)
    {
        super(type, worldPosition, blockState);
        
        this.capacity = capacity;
        this.usageRate = usageRate;
        this.received = received;
        
        this.energyStorage = createEnergyStorage(energyIOMode,
                capacity.scaleI(this::getSoulBankData),
                usageRate.scaleI(this::getSoulBankData),
                received.scaleI(this::getSoulBankData));
        
        soulbankCacheDirty = true;
        addDataSlot(createEnergyDataSlot());
    }

    public NetworkDataSlot<?> createEnergyDataSlot()
    {
        return EnergySyncData.DATA_SLOT_TYPE.create(() -> EnergySyncData.from((IWitherEnergyStorage) getEnergyHandler(null)), v -> clientEnergyStorage = v.toImmutableStorage());
    }
	
    protected WitherEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, Supplier<Integer> capacity, Supplier<Integer> usageRate, Supplier<Integer> received)
    {
        return new WitherEnergyStorage(energyIOMode, capacity, usageRate, received)
        {
            @Override
            protected void onContentsChanged()
            {
                setChanged();
            }
        };
    }
    
    @Override
    public boolean hasEnergyCapability()
    {
        return true;
    }
    
    public final boolean hasEnergy()
    {
        if (requiresSoulBank() && !isSoulBankInstalled())
        {
            return false;
        }
        return energyStorage.getEnergyStored() > 0;
    }
    
    @Override
    public IEnergyStorage getEnergyHandler(@Nullable Direction dir)
    {
        if (level != null && level.isClientSide())
        {
            return clientEnergyStorage;
        }
        return energyStorage;
    }
    
    protected boolean moveEnergy(Direction facing, int energyAmount)
    {
        return moveEnergy(facing, worldPosition.relative(facing), energyAmount);
    }

    protected boolean moveEnergy(Direction facing, BlockPos targetPosition, int energyAmount)
    {
        if (this.level.isClientSide)
        {
            return false;
        }
        IEnergyStorage fromHandler = getSelfCapability(Capabilities.EnergyStorage.BLOCK, facing);
        if (fromHandler == null || fromHandler.getEnergyStored() == 0)
        {
            return false;
        }
        
        if (facing == null)
        {
            facing = Direction.UP;
        }
        
        Direction facingMe = facing.getOpposite();
        BlockEntity be = level.getBlockEntity(targetPosition);
        if (be == null)
        {
            return false;
        }
        
        IEnergyStorage toHandler = getNeighbouringCapability(Capabilities.EnergyStorage.BLOCK, facingMe);

        if (toHandler == null)
        {
            return false;
        }
        
        if (fromHandler != null && toHandler != null && fromHandler.canExtract() && toHandler.canReceive())
        {
            int drain = fromHandler.extractEnergy(received.scaleI(this::getSoulBankData).get(), true);
            if (drain > 0)
            {
                int filled = toHandler.receiveEnergy(drain, false);
                fromHandler.extractEnergy(filled, false);
                return filled > 0;
            }
        }
        return false;
    }

    /*
     * 
     * SOULBANK
     * 
     */
    public final boolean requiresSoulBank()
    {
        MachineInventoryLayout layout = getInventoryLayout();
        if (layout == null)
        {
            return false;
        }
        return layout.supportsSoulBank();
    }

    public final int getSoulBankSlot()
    {
        MachineInventoryLayout layout = getInventoryLayout();
        if (layout == null)
        {
            return -1;
        }
        return layout.getSoulBankSlot();
    }

    public boolean isSoulBankInstalled()
    {
        if (level != null && level.isClientSide)
        {
            return !getSoulBankItem().isEmpty();
        }

        if (soulbankCacheDirty)
        {
            cacheSoulBankData();
        }
        return !cachedSoulBankData.equals(SoulBankData.NONE);
    }

    public ItemStack getSoulBankItem()
    {
        MachineInventory inventory = getInventory();
        MachineInventoryLayout layout = getInventoryLayout();
        if (inventory == null || layout == null)
        {
            return ItemStack.EMPTY;
        }
        return inventory.getStackInSlot(layout.getSoulBankSlot());
    }

    public SoulBankData getSoulBankData()
    {
        if (soulbankCacheDirty)
        {
            cacheSoulBankData();
        }
        return cachedSoulBankData;
    }

    @Override
    protected void onInventoryContentsChanged(int slot)
    {
        MachineInventoryLayout inventoryLayout = getInventoryLayout();
        if (inventoryLayout != null && inventoryLayout.getSoulBankSlot() == slot)
        {
            if (requiresSoulBank())
            {
                soulbankCacheDirty = true;
            }
        }
        super.onInventoryContentsChanged(slot);
    }

    private void cacheSoulBankData()
    {
        if (level == null)
        {
            return;
        }
        soulbankCacheDirty = false;
        MachineInventoryLayout layout = getInventoryLayout();
        if (requiresSoulBank() && layout != null)
        {
            cachedSoulBankData = getSoulBankItem().getOrDefault(WUTComponents.SOULBANK_DATA, SoulBankData.NONE);
        }
        else
        {
            cachedSoulBankData = SoulBankData.NONE;
        }
    }

    @Override
    public boolean canAct() {
        return super.canAct() && (!requiresSoulBank() || isSoulBankInstalled());
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
        var energyStorage = getEnergyHandler(null);
        if (energyStorage instanceof WitherEnergyStorage storage)
        {
            pTag.put("EnergyStored", storage.serializeNBT(lookupProvider));
        }
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider)
    {
        super.loadAdditional(pTag, lookupProvider);
        var energyStorage = getEnergyHandler(null);
        if (energyStorage instanceof WitherEnergyStorage storage && pTag.contains("EnergyStored"))
        {
            storage.deserializeNBT(lookupProvider, pTag.getCompound("EnergyStored"));
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components)
    {
        super.applyImplicitComponents(components);
        energyStorage.setEnergyStored(components.getOrDefault(WUTComponents.ENERGY, 0));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        super.collectImplicitComponents(components);
        components.set(WUTComponents.ENERGY, energyStorage.getEnergyStored());
    }
    
    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        super.removeComponentsFromTag(tag);
        tag.remove("EnergyStored");
    }
}
