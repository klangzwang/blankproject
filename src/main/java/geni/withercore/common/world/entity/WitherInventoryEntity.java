package geni.withercore.common.world.entity;

import geni.withercore.common.inventory.entity.energy.EntityEnergyStorage;
import geni.withercore.common.inventory.entity.fluid.EntityFluidTank;
import geni.withercore.common.inventory.entity.item.EntityInventory;
import geni.withercore.common.inventory.entity.item.EntityInventoryLayout;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class WitherInventoryEntity extends WitherEntity implements MenuProvider {

    @Nullable
    protected EntityInventory inventory;
    @Nullable
    protected EntityFluidTank fluidTank;
    @Nullable
    protected EntityEnergyStorage energyStorage;

    protected WitherInventoryEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);

        EntityInventoryLayout layout = getInventoryLayout();
        if (layout != null) {
            this.inventory = createEntityInventory(layout);
        }
    }

    @Nullable
    public EntityInventoryLayout getInventoryLayout() {
        return null;
    }

    protected EntityInventory createEntityInventory(EntityInventoryLayout layout) {
        return new EntityInventory(this, layout);
    }

    protected EntityFluidTank createFluidTank(int capacity) {
        return new EntityFluidTank(this, capacity);
    }

    protected EntityEnergyStorage createEnergyStorage(int capacity, int maxTransfer) {
        return new EntityEnergyStorage(this, capacity, maxTransfer);
    }

    @Nullable
    public EntityInventory getInventory() {
        return inventory;
    }

    @Nullable
    public EntityFluidTank getFluidTank() {
        return fluidTank;
    }

    @Nullable
    public EntityEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    /*
     * 
     * CAPABILITIES
     * 
     */
    @Override
    public boolean hasItemCapability() {
        return this.inventory != null;
    }

    @Override
    public boolean hasFluidCapability() {
        return this.fluidTank != null;
    }

    @Override
    public boolean hasEnergyCapability() {
        return this.energyStorage != null;
    }

    @Nullable
    @Override
    public IItemHandler getItemHandler(@Nullable Direction side) {
        return this.inventory != null ? this.inventory.getForSide(side) : null;
    }

    @Nullable
    @Override
    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        return this.fluidTank;
    }

    @Nullable
    @Override
    public IEnergyStorage getEnergyHandler(@Nullable Direction side) {
        return this.energyStorage != null ? this.energyStorage.getForSide(side) : null;
    }

    /*
     * 
     * SAVE / LOAD
     * 
     */
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        HolderLookup.Provider provider = registryAccess();
        if (this.inventory != null) {
            compound.put("Items", this.inventory.serializeNBT(provider));
        }
        if (this.fluidTank != null) {
            compound.put("Tanks", this.fluidTank.writeToNBT(provider));
        }
        if (this.energyStorage != null) {
            compound.put("Energy", this.energyStorage.writeToNBT(provider));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        HolderLookup.Provider provider = registryAccess();
        if (this.inventory != null && compound.contains("Items")) {
            this.inventory.deserializeNBT(provider, compound.getCompound("Items"));
        }
        if (this.fluidTank != null && compound.contains("Tanks")) {
            this.fluidTank.readFromNBT(provider, compound.getCompound("Tanks"));
        }
        if (this.energyStorage != null && compound.contains("Energy")) {
            this.energyStorage.readFromNBT(provider, compound.getCompound("Energy"));
        }
    }

    /*
     * 
     * DEATH / DROPS
     * 
     */
    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        if (this.inventory != null) {
            this.inventory.dropAll(this);
        }
    }

    /*
     * 
     * INTERACTION / MENU
     * 
     */
    public boolean canOpenMenu(Player player) {
        return isAlive() && hasItemCapability();
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (canOpenMenu(player)) {
            if (!level().isClientSide && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(this, buf -> buf.writeVarInt(getId()));
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null;
    }
}
