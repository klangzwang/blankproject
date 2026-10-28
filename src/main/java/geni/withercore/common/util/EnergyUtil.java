package geni.withercore.common.util;

import com.google.common.primitives.Ints;
import geni.withercore.init.WUTCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public final class EnergyUtil {
	
	public static final String ENERGY_KEY = "energy";
	
    private EnergyUtil()
    {
    }

    public static boolean hasEnergy(Level level, BlockPos pos, Direction side)
    {
        return level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side) != null;
    }

    public static long pushEnergy(Level level, BlockPos pos, Direction side, long howMuch)
    {
        var handler = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
        return handler != null ? handler.receiveEnergy(Ints.saturatedCast(howMuch), false) : 0;
    }

	@Nullable
	public static IEnergyStorage getEnergy(ItemStack stack)
	{
		if (stack == null) return null;
		return stack.getCapability(Capabilities.EnergyStorage.ITEM);
	}
	@Nullable
	public static boolean hasEnergyHandler(ItemStack stack)
	{
		IEnergyStorage cap = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		return cap != null;
	}

    public static int getMaxEnergyStored(ItemStack stack) {
        return WUTCapabilities.getItemEnergyHandler(stack).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }
    public static int getEnergyStored(ItemStack stack) {
        return WUTCapabilities.getItemEnergyHandler(stack).map(IEnergyStorage::getEnergyStored).orElse(0);
    }
    public static boolean hasEnergy(ItemStack stack, int amount) {
        return WUTCapabilities.getItemEnergyHandler(stack).map(storage -> storage.getEnergyStored() >= amount).orElse(false);
    }
    public static void setFull(ItemStack stack) {
    	WUTCapabilities.getItemEnergyHandler(stack).ifPresent(energyStorage -> energyStorage.receiveEnergy(energyStorage.getMaxEnergyStored(), false));
    }
    public static void setEmpty(ItemStack stack) {
    	WUTCapabilities.getItemEnergyHandler(stack).ifPresent(energyStorage -> energyStorage.extractEnergy(energyStorage.getEnergyStored(), false));
    }

    public static void set(ItemStack stack, int energy)
    {
    	WUTCapabilities.getItemEnergyHandler(stack).ifPresent(energyStorage -> {
            int delta = energy - energyStorage.getEnergyStored();
            if (delta < 0)
            {
                energyStorage.extractEnergy(-delta, false);
            }
            else
            {
                energyStorage.receiveEnergy(delta, false);
            }
        });
    }

    public static int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate) {
        return WUTCapabilities.getItemEnergyHandler(stack).map(energyStorage -> energyStorage.receiveEnergy(maxReceive, simulate)).orElse(0);
    }
    public static int extractEnergy(ItemStack stack, int maxExtract, boolean simulate) {
        return WUTCapabilities.getItemEnergyHandler(stack).map(energyStorage -> energyStorage.extractEnergy(maxExtract, simulate)).orElse(0);
    }
    public static long calculateSunEnergy(Level level, BlockPos pos)
    {
    	long baseRatio = calculateLightRatio(level, pos);
        return calculateLocalLightRatio(level, pos, baseRatio);
    }
    static boolean isSolarPowered(@Nonnull Level level, BlockPos pos)
    {
    	return level.canSeeSky(pos.above());
    }
	static long calculateLocalLightRatio(@Nonnull Level level, BlockPos pos, long baseRatio)
	{
		long ratio = isSolarPowered(level, pos) ? baseRatio : 0;
		return ratio;
	}
	public static long calculateLightRatio(@Nonnull Level level, BlockPos pos)
	{
		long lightValue = level.getBrightness(LightLayer.SKY, pos) - level.getSkyDarken();
		float sunAngle = level.getSunAngle(1.0F);
		if(sunAngle < (float) Math.PI)
		{
			sunAngle += (0.0F - sunAngle) * 0.2F;
		}
		else
		{
			sunAngle += (((float) Math.PI * 2F) - sunAngle) * 0.2F;
		}
		lightValue = Math.round(lightValue * Mth.cos(sunAngle));
		lightValue = Mth.clamp(lightValue, 0, 15);

		return lightValue / 4 * 2;
	}
    public static boolean isEnergyTE(BlockEntity te, @Nullable Direction side)
    {
        if (te == null)
        {
            return false;
        }
        return te.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, te.getBlockPos(), side) != null;
    }
    /*public static int receiveEnergy(BlockEntity tileEntity, Direction from, int maxReceive)
    {
        if (tileEntity != null)
        {
            return WUTCapabilities.getBlockEnergyHandler(tileEntity, from).map(handler ->
                    handler.receiveEnergy(unsignedClampToInt(maxReceive), false)).orElse(0);
        }
        return 0;
    }*/
    /*public static int unsignedClampToInt(long l)
    {
        return l > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)l;
    }
    public static void handleSendingEnergy(Level world, BlockPos pos, int storedPower, float sendPerTick, WitherEnergyStorage storage)
    {
        for (Direction facing : FacingUtil.FACES_AROUND_X)
        {
            BlockPos p = pos.relative(facing);
            BlockEntity te = world.getBlockEntity(p);
            Direction opposite = facing.getOpposite();
            if (EnergyUtil.isEnergyTE(te, opposite))
            {
                int rfToGive = (int) Math.min(sendPerTick, storedPower);
                int received = EnergyUtil.receiveEnergy(te, opposite, rfToGive);
                storage.consumeEnergy(received, false);
                storedPower -= received;
                if (storedPower <= 0)
                {
                    break;
                }
            }
        }
    }*/
    
//	public static class ItemEnergyStorage implements IEnergyStorage
//	{
//		private final ItemStack stack;
//		private final ToIntFunction<ItemStack> getCapacity;
//
//		public ItemEnergyStorage(ItemStack item, ToIntFunction<ItemStack> getCapacity)
//		{
//			this.stack = item;
//			this.getCapacity = getCapacity;
//		}
//
//		@Override
//		public int receiveEnergy(int maxReceive, boolean simulate)
//		{
//			int stored = getEnergyStored();
//			int accepted = Math.min(maxReceive, getMaxEnergyStored()-stored);
//			if(!simulate)
//			{
//				stored += accepted;
//				ItemNBTHelper.putInt(stack, ENERGY_KEY, stored);
//			}
//			return accepted;
//		}
//
//		@Override
//		public int extractEnergy(int maxExtract, boolean simulate)
//		{
//			int stored = getEnergyStored();
//			int extracted = Math.min(maxExtract, stored);
//			if(!simulate)
//			{
//				stored -= extracted;
//				ItemNBTHelper.putInt(stack, ENERGY_KEY, stored);
//			}
//			return extracted;
//		}
//
//		@Override
//		public int getEnergyStored()
//		{
//			return ItemNBTHelper.getInt(stack, ENERGY_KEY);
//		}
//
//		@Override
//		public int getMaxEnergyStored()
//		{
//			return getCapacity.applyAsInt(stack);
//		}
//
//		@Override
//		public boolean canExtract()
//		{
//			return true;
//		}
//
//		@Override
//		public boolean canReceive()
//		{
//			return true;
//		}
//	}
}
