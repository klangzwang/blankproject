package geni.witherutils.common.world.level.block;

import geni.withercore.item.block.ItemBlockWither;
import geni.withercore.util.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class AngelBlockItem extends ItemBlockWither {

	public AngelBlockItem(Block blockIn, Properties builder)
	{
		super(blockIn, builder);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack itemstack = player.getItemInHand(hand);
		if (level.isClientSide)
			return InteractionResultHolder.success(itemstack);

		int x = (int) Math.floor(player.getX() + 3 * player.getLookAngle().x);
		int y = (int) Math.floor(1.5 + player.getY() + 3 * player.getLookAngle().y);
		int z = (int) Math.floor(player.getZ() + 3 * player.getLookAngle().z);

		BlockPos pos = new BlockPos(x, y, z);

		if (level.getBlockState(pos).canBeReplaced())
		{
			level.setBlock(pos, getBlock().defaultBlockState(), 3);
	        if (!player.isCreative())
	        	itemstack.shrink(1);
	        SoundUtil.playSoundFromServer((ServerPlayer) player, SoundEvents.BONE_BLOCK_PLACE, 1.0f, 1.0f);
	        return InteractionResultHolder.consume(itemstack);
		}
		return super.use(level, player, hand);
	}
}
