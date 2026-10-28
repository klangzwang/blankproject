package test.withertest.developer;

import geni.withercore.common.world.level.block.entity.WitherFuncBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import test.withertest.WitherTest;

import javax.annotation.Nullable;

public class DeveloperBlockEntity extends WitherFuncBlockEntity {

    public DeveloperBlockEntity(BlockPos pos, BlockState state) {
        super(WitherTest.DEVELOPER_TE, pos, state);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if(!readFuncStuff())
            return;

        if(level.getGameTime() % 10 == 0) {
            if(level.getBlockState(getTargetPos()).getBlock() != Blocks.AIR) {
                System.out.println(scanTargetCapabilities());
                System.out.println(scanTargetItems());
                System.out.println(scanTargetEnergy());
                System.out.println(scanTargetFluids());
                System.out.println(scanTargetVoxelShape());
                System.out.println(scanTargetBlockState());
                System.out.println(scanTargetRedstone());
                System.out.println(scanTargetBlockEntity());
                System.out.println(scanTargetEntities());
            }
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();

        if(!readFuncStuff())
            return;

        if(level.getGameTime() % 25 == 0) {
            if(level.getBlockState(getTargetPos()).getBlock() != Blocks.AIR) {
                System.out.println(scanTargetBakedModel());
                System.out.println(scanTargetModelInfo());
            }
        }
    }

    @Override
    public boolean canOpenMenu()
    {
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player)
    {
        return new DeveloperMenu(containerId, playerInventory, this);
    }
}
