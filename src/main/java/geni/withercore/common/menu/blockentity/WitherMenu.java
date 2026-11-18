package geni.withercore.common.menu.blockentity;

import geni.withercore.common.menu.WitherAbstractMenu;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class WitherMenu<T extends BlockEntity> extends WitherAbstractMenu {

    @Nullable
    private final T blockEntity;

    @SuppressWarnings("unused")
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

    protected WitherMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory, @Nullable T blockEntity) {
        super(menuType, containerId, playerInventory);
        this.blockEntity = blockEntity;
    }

    @Nullable
    public T getBlockEntity() {
        return blockEntity;
    }
}
