package test.withertest.developer;

import geni.withercore.common.menu.entity.WitherMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import test.withertest.WitherTest;

public class DeveloperMenu extends WitherMachineMenu<DeveloperBlockEntity> {

    public DeveloperMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, extraData));
    }

    public DeveloperMenu(int containerId, Inventory playerInventory, @Nullable DeveloperBlockEntity blockEntity) {
        super(WitherTest.DEVELOPER_MENU.get(), containerId, playerInventory, blockEntity);
    }
}
