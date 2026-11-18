package geni.withercore.common.menu.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class WitherContainerEntityMenu<T extends PathfinderMob> extends WitherEntityMenu<T> {

    protected WitherContainerEntityMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, @Nullable T mobInstance) {
        super(menuType, containerId, inventory, mobInstance);
    }

    protected static <T extends PathfinderMob> T getPathfinderMob(Inventory playerInv, FriendlyByteBuf extraData)
    {
        return (T) playerInv.player.level().getEntity(extraData.readInt());
    }
}