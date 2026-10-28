package geni.withercore.item;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public final class StackList extends ArrayList<ItemStack> {
	
    private StackList() {}

    public static StackList of(ItemStack... stacks)
    {
        StackList newList = new StackList();
        Collections.addAll(newList, stacks);
        return newList;
    }

    public static StackList from(Container inventory)
    {
        StackList newList = new StackList();
        for (int i = 0; i < inventory.getContainerSize(); ++i)
        {
            newList.add(inventory.getItem(i));
        }
        return newList;
    }

    public static StackList from(CraftingInput input)
    {
        StackList newList = new StackList();
        for (int i = 0; i < input.size(); ++i)
        {
            newList.add(input.getItem(i));
        }
        return newList;
    }

    public ItemStack firstOfType(Class<?> itemClass)
    {
        return firstMatch(itemClassMatcher(itemClass));
    }

    public ItemStack firstMatch(Predicate<ItemStack> predicate)
    {
        return stream().filter(predicate).findFirst().orElse(ItemStack.EMPTY);
    }

    public ItemStack uniqueOfType(Class<?> itemClass)
    {
        return uniqueMatch(itemClassMatcher(itemClass));
    }

    public ItemStack uniqueMatch(Predicate<ItemStack> predicate) 
    {
        return stream().filter(predicate).collect(Collectors.collectingAndThen(Collectors.toList(),
                list -> list.size() == 1 ? list.get(0) : ItemStack.EMPTY));
    }

    public Collection<ItemStack> allOfType(Class<?> itemClass)
    {
        return allMatches(itemClassMatcher(itemClass));
    }

    public Collection<ItemStack> allMatches(Predicate<ItemStack> predicate)
    {
        return stream().filter(predicate).collect(Collectors.toList());
    }

    public int countOfType(Class<?> itemClass)
    {
        return countOfMatches(itemClassMatcher(itemClass));
    }

    public int countOfMatches(Predicate<ItemStack> predicate)
    {
        return (int) stream().filter(predicate).count();
    }

    private static Predicate<ItemStack> itemClassMatcher(Class<?> itemClass)
    {
        return stack -> itemClass.isInstance(stack.getItem());
    }

    @Override
    public boolean add(ItemStack itemStack)
    {
        return !itemStack.isEmpty() && super.add(itemStack);
    }

    @Override
    public boolean addAll(Collection<? extends ItemStack> c)
    {
        boolean added = false;
        for (ItemStack stack : c)
        {
            if (!stack.isEmpty())
            {
                added |= super.add(stack);
            }
        }
        return added;
    }

    @Override
    public boolean addAll(int index, Collection<? extends ItemStack> c) 
    {
        boolean added = false;
        for (ItemStack stack : c)
        {
            if (!stack.isEmpty())
            {
                super.add(index, stack);
                added = true;
            }
        }
        return added;
    }

    @Override
    public void add(int index, ItemStack element)
    {
        if (!element.isEmpty())
        {
            super.add(index, element);
        }
    }
}
