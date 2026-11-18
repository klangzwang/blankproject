package geni.withercore.common.menu.blockentity.slot;

import geni.withercore.common.inventory.blockentity.item.MachineInventory;
import geni.withercore.common.inventory.blockentity.item.MachineInventoryLayout;
import geni.withercore.common.inventory.blockentity.item.SingleSlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WitherMachineSlotGhost extends WitherMachineSlot {

    private List<ItemStack> previewStacks = new ArrayList<>();
    private int previewIntervalTicks = 30;

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

    public WitherMachineSlotGhost(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
    }

    public WitherMachineSlotGhost(MachineInventory itemHandler, SingleSlotAccess access, int xPosition, int yPosition)
    {
        this(itemHandler, access.getIndex(), xPosition, yPosition);
    }

    public WitherMachineSlotGhost(IItemHandler itemHandler, SingleSlotAccess access, int xPosition, int yPosition)
    {
        this(itemHandler, access.getIndex(), xPosition, yPosition);
    }

    public WitherMachineSlotGhost setPreviewStacks(List<ItemStack> stacks)
    {
        this.previewStacks = new ArrayList<>(stacks);
        return this;
    }

    public WitherMachineSlotGhost addPreviewStack(ItemStack stack)
    {
        this.previewStacks.add(stack);
        return this;
    }

    public List<ItemStack> getPreviewStacks()
    {
        return Collections.unmodifiableList(previewStacks);
    }

    public WitherMachineSlotGhost setPreviewIntervalTicks(int ticks)
    {
        this.previewIntervalTicks = Math.max(1, ticks);
        return this;
    }

    public ItemStack getActivePreviewStack(long gameTime)
    {
        if (hasItem())
        {
            return getItem();
        }
        if (previewStacks.isEmpty())
        {
            return ItemStack.EMPTY;
        }
        int index = (int) ((gameTime / previewIntervalTicks) % previewStacks.size());
        return previewStacks.get(index);
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
