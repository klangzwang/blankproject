package geni.withercore.common.menu.blockentity.slot;

import geni.withercore.common.inventory.blockentity.item.MachineInventory;
import geni.withercore.common.inventory.blockentity.item.SingleSlotAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.function.Predicate;

/**
 * Ein spezialisierter Slot mit Filterregeln (Item, Tag oder benutzerdefiniertes Prädikat).
 */
public class WitherFilterSlot extends WitherMachineSlot {

    public WitherFilterSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Predicate<ItemStack> filter)
    {
        super(itemHandler, index, xPosition, yPosition);
        setFilter(filter);
    }

    public WitherFilterSlot(MachineInventory itemHandler, SingleSlotAccess access, int xPosition, int yPosition, Predicate<ItemStack> filter)
    {
        super(itemHandler, access, xPosition, yPosition);
        setFilter(filter);
    }

    public static WitherFilterSlot ofItem(IItemHandler itemHandler, int index, int x, int y, Item allowedItem)
    {
        return new WitherFilterSlot(itemHandler, index, x, y, stack -> stack.is(allowedItem));
    }

    public static WitherFilterSlot ofTag(IItemHandler itemHandler, int index, int x, int y, TagKey<Item> tag)
    {
        return new WitherFilterSlot(itemHandler, index, x, y, stack -> stack.is(tag));
    }
}

