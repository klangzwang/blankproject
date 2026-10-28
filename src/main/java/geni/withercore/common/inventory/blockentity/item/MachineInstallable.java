package geni.withercore.common.inventory.blockentity.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public interface MachineInstallable {

    InteractionResult tryItemInstall(ItemStack stack, UseOnContext context);
}

