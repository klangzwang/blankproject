package geni.withercore.common.menu.entity.slot;

import geni.withercore.common.inventory.blockentity.item.MachineInventory;
import geni.withercore.common.inventory.blockentity.item.SingleSlotAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class WitherMachineSlot extends SlotItemHandler {

    @Nullable
    private ResourceLocation foregroundSprite;

    private final boolean highlightable;
    
    public WitherMachineSlot(MachineInventory itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
        this.highlightable = true;
    }

    public WitherMachineSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
        this.highlightable = true;
    }
    
    public WitherMachineSlot(MachineInventory itemHandler, SingleSlotAccess access, int xPosition, int yPosition)
    {
        super(itemHandler, access.getIndex(), xPosition, yPosition);
        this.highlightable = true;
    }
    
    public WitherMachineSlot(MachineInventory itemHandler, SingleSlotAccess access, int xPosition, int yPosition, boolean highlightable)
    {
        super(itemHandler, access.getIndex(), xPosition, yPosition);
        this.highlightable = highlightable;
    }
    
    public WitherMachineSlot(MachineInventory itemHandler, SingleSlotAccess access, int xPosition, int yPosition, boolean highlightable, ResourceLocation foregroundSprite)
    {
        super(itemHandler, access.getIndex(), xPosition, yPosition);
        this.highlightable = highlightable;
        setForeground(this.foregroundSprite);
    }
    
    public WitherMachineSlot(IItemHandler itemHandler, SingleSlotAccess access, int xPosition, int yPosition)
    {
        super(itemHandler, access.getIndex(), xPosition, yPosition);
        this.highlightable = true;
    }
    
    @Override
    public MachineInventory getItemHandler()
    {
        return (MachineInventory) super.getItemHandler();
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return getItemHandler().getLayout().guiCanInsert(this.getSlotIndex()) && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn)
    {
        return getItemHandler().getLayout().guiCanExtract(this.getSlotIndex()) && super.mayPickup(playerIn);
    }

    public boolean canQuickInsertStack()
    {
        return getItemHandler().getLayout().guiCanInsert(getSlotIndex());
    }

    public WitherMachineSlot setForeground(ResourceLocation sprite)
    {
        foregroundSprite = sprite;
        return this;
    }
    
    @Override
    public boolean isHighlightable()
    {
    	return this.highlightable;
    }
}
