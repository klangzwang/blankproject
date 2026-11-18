package geni.withercore.common.menu.item;

import geni.withercore.common.world.item.WitherItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class WitherContainerItemMenu<T extends WitherItem> extends WitherItemMenu<T> {

    protected WitherContainerItemMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, @Nullable T itemInstance, ItemStack itemStack) {
        super(menuType, containerId, inventory, itemInstance, itemStack);
    }

    public IItemHandler getItemInventory() {
        IItemHandler handler = itemStack.getCapability(Capabilities.ItemHandler.ITEM);
        if (handler == null) {
            throw new IllegalStateException("Item does not have an item handler capability.");
        }
        return handler;
    }

    @Override
    public boolean stillValid(Player player) {
        return !itemStack.isEmpty() && (player.getMainHandItem() == itemStack || player.getOffhandItem() == itemStack || player.getInventory().contains(itemStack));
    }
}
