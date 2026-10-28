package geni.withercore.common.menu.entity.slot;

import geni.withercore.common.inventory.blockentity.item.MachineInventory;
import geni.withercore.common.inventory.blockentity.item.MachineInventoryLayout;
import geni.withercore.common.inventory.blockentity.item.SingleSlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class WitherMachineSlotGhost extends WitherMachineSlot {

    public WitherMachineSlotGhost(MachineInventory itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);

        MachineInventoryLayout layout = itemHandler.getLayout();
        if (layout.canInsert(index) || layout.canExtract(index))
        {
            throw new RuntimeException("Ghost slot can be externally modified!!");
        }
        if (!layout.guiCanInsert(index))
        {
            throw new RuntimeException("Ghost slot cannot be modified by the player!");
        }
    }

    public WitherMachineSlotGhost(MachineInventory itemHandler, SingleSlotAccess access, int xPosition, int yPosition)
    {
        this(itemHandler, access.getIndex(), xPosition, yPosition);
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int amount)
    {
        if (!stack.isEmpty() && mayPlace(stack))
        {
            ItemStack ghost = stack.copy();
            ghost.setCount(Math.min(ghost.getCount(), this.getMaxStackSize()));
            set(ghost);
        }
        return stack;
    }

    @Override
    public ItemStack remove(int amount)
    {
        set(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean mayPickup(Player playerIn)
    {
        return true;
    }
}
