package geni.withercore.common.menu.blockentity;

import geni.withercore.common.inventory.blockentity.item.MachineInventory;
import geni.withercore.common.world.level.block.entity.WitherMachineBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class WitherMachineMenu<T extends WitherMachineBlockEntity> extends WitherMenu<T> {

    protected WitherMachineMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, @Nullable T blockEntity)
    {
        super(pMenuType, pContainerId, inventory, blockEntity);
    }

    public MachineInventory getMachineInventory()
    {
        if (getBlockEntity() == null)
        {
            throw new IllegalStateException("BlockEntity is null");
        }
        if (getBlockEntity().getInventory() == null)
        {
            throw new IllegalStateException("Machine does not have an inventory.");
        }
        return getMachineInventoryInternal();
    }

    private MachineInventory getMachineInventoryInternal()
    {
        return getBlockEntity().getInventory();
    }
    
    @Override
    public boolean stillValid(Player pPlayer)
    {
        return getBlockEntity() != null && Container.stillValidBlockEntity(getBlockEntity(), pPlayer);
    }

    @SuppressWarnings("unchecked")
    protected static <T extends BlockEntity> T getBlockEntity(Inventory playerInv, FriendlyByteBuf extraData)
    {
        return (T) playerInv.player.level().getBlockEntity(extraData.readBlockPos());
    }
}
