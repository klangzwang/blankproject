package geni.withercore.common.menu.item;

import geni.withercore.common.menu.WitherAbstractMenu;
import geni.withercore.common.world.item.WitherItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class WitherItemMenu<T extends WitherItem> extends WitherAbstractMenu {

    @Nullable
    private final T itemInstance;
    public final ItemStack itemStack;

    protected WitherItemMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, @Nullable T itemInstance, ItemStack itemStack) {
        super(menuType, containerId, playerInventory);
        this.itemInstance = itemInstance;
        this.itemStack = itemStack;
    }

    @Nullable
    public T getItemInstance() {
        return itemInstance;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
