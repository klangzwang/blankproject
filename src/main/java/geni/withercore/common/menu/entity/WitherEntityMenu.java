package geni.withercore.common.menu.entity;

import geni.withercore.common.menu.WitherAbstractMenu;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class WitherEntityMenu<T extends PathfinderMob> extends WitherAbstractMenu {

    @Nullable
    private final T mobInstance;

    protected WitherEntityMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, @Nullable T mobInstance) {
        super(menuType, containerId, playerInventory);
        this.mobInstance = mobInstance;
    }

    @Nullable
    public T getMobInstance() {
        return mobInstance;
    }
}
