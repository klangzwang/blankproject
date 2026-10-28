package geni.witherutils.common.world.level.block.entity;

import geni.witherapi.block.DronePlatformBlockType;
import geni.withercore.blockentity.WitherMachineBlockEntity;
import geni.withercore.init.WUTBlockEntities;
import geni.withercore.network.dataslot.NetworkDataSlot;
import geni.witherutils.common.world.level.block.DronePlatformBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DronePlatformBlockEntity extends WitherMachineBlockEntity implements MenuProvider {

	private DronePlatformBlockEntity master = null;
	private boolean isMaster;
	
	private BlockPos hookPosition = BlockPos.ZERO;

	public DronePlatformBlockEntity(BlockPos pos, BlockState state)
	{
		super(WUTBlockEntities.DRONEPLATFORM, pos, state);
        addDataSlot(NetworkDataSlot.BOOL.create(this::isMaster, p -> isMaster = p));
	}

	@Override
	public void serverTick()
	{
		super.serverTick();

		if(master == null)
			getMasterBe();
		else
			isMaster();
	}
	
	@Override
	public void clientTick()
	{
		super.clientTick();
		
		if(master == null)
			getMasterBe();
	}

	/*@Override
	public boolean canOpenMenu()
	{
		return true;
	}

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity)
    {
        return new FisherContainer(this, playerInventory, i);
    }*/

	public boolean isMaster()
	{
		this.isMaster = this.getBlockState().getValue(DronePlatformBlock.BLOCK_TYPE) == DronePlatformBlockType.MASTER;
		return this.isMaster;
	}
	
	public DronePlatformBlockEntity getMasterBe()
	{
		if(master != null)
			return master;

		master = getMaster();
		return master;
	}
	
	public DronePlatformBlockEntity getMaster()
	{
		if(getBlockState().getValue(DronePlatformBlock.BLOCK_TYPE) == DronePlatformBlockType.MASTER)
			return this;

		BlockPos offset = getBlockState().getValue(DronePlatformBlock.BLOCK_TYPE).getOffsetToMaster();
	    if (offset == null)
	    	return null;

		BlockPos masterPos = getBlockPos().offset(offset.getX(), offset.getY(), offset.getZ());
	    if (!level.isLoaded(masterPos))
	    	return null;
		
	    BlockEntity res = level.getBlockEntity(masterPos);
	    if (res instanceof DronePlatformBlockEntity)
	    {
	    	return (DronePlatformBlockEntity) res;
	    }
	    
		return null;
	}
}
