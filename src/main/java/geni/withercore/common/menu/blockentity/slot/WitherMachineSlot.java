package geni.withercore.common.menu.blockentity.slot;

import geni.withercore.common.inventory.blockentity.item.MachineInventory;
import geni.withercore.common.inventory.blockentity.item.SingleSlotAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class WitherMachineSlot extends SlotItemHandler {

    @Nullable
    private ResourceLocation foregroundSprite;

    private final boolean highlightable;
    private boolean active = true;
    private boolean locked = false;

    @Nullable
    private Supplier<Boolean> activeCondition;
    @Nullable
    private Predicate<ItemStack> itemFilter;
    @Nullable
    private net.minecraft.network.chat.Component lockedReason;
    
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
        setForeground(foregroundSprite);
    }
    
    public WitherMachineSlot(IItemHandler itemHandler, SingleSlotAccess access, int xPosition, int yPosition)
    {
        super(itemHandler, access.getIndex(), xPosition, yPosition);
        this.highlightable = true;
    }

    @Nullable
    public MachineInventory getMachineInventory()
    {
        return super.getItemHandler() instanceof MachineInventory mi ? mi : null;
    }

    @Override
    public boolean isActive()
    {
        return active && (activeCondition == null || activeCondition.get());
    }

    public WitherMachineSlot setActive(boolean active)
    {
        this.active = active;
        return this;
    }

    public WitherMachineSlot setActiveCondition(@Nullable Supplier<Boolean> condition)
    {
        this.activeCondition = condition;
        return this;
    }

    public boolean isLocked()
    {
        return locked;
    }

    public WitherMachineSlot setLocked(boolean locked)
    {
        this.locked = locked;
        return this;
    }

    @Nullable
    public net.minecraft.network.chat.Component getLockedReason()
    {
        return lockedReason;
    }

    public WitherMachineSlot setLockedReason(@Nullable net.minecraft.network.chat.Component reason)
    {
        this.lockedReason = reason;
        return this;
    }

    @Nullable
    public Predicate<ItemStack> getFilter()
    {
        return itemFilter;
    }

    public WitherMachineSlot setFilter(@Nullable Predicate<ItemStack> filter)
    {
        this.itemFilter = filter;
        return this;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        if (locked || !isActive())
        {
            return false;
        }
        if (itemFilter != null && !itemFilter.test(stack))
        {
            return false;
        }
        MachineInventory mi = getMachineInventory();
        if (mi != null && !mi.getLayout().guiCanInsert(this.getSlotIndex()))
        {
            return false;
        }
        return super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn)
    {
        if (locked || !isActive())
        {
            return false;
        }
        MachineInventory mi = getMachineInventory();
        if (mi != null && !mi.getLayout().guiCanExtract(this.getSlotIndex()))
        {
            return false;
        }
        return super.mayPickup(playerIn);
    }

    public boolean canQuickInsertStack()
    {
        if (locked || !isActive())
        {
            return false;
        }
        MachineInventory mi = getMachineInventory();
        return mi == null || mi.getLayout().guiCanInsert(getSlotIndex());
    }

    public WitherMachineSlot setForeground(ResourceLocation sprite)
    {
        foregroundSprite = sprite;
        return this;
    }

    @Nullable
    public ResourceLocation getForeground()
    {
        return foregroundSprite;
    }
    
    @Override
    public boolean isHighlightable()
    {
        return this.highlightable;
    }
}
