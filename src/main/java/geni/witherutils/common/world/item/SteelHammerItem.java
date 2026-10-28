package geni.witherutils.common.world.item;

import geni.withercore.init.WUTSounds;
import geni.withercore.item.WitherItem;
import geni.withercore.render.tooltip.Tooltips;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SteelHammerItem extends WitherItem {

	public SteelHammerItem(Item.Properties props)
	{
		super(props);
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity)
	{
		return 100;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack)
	{
		return UseAnim.SPEAR;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		if (player.getXRot() < 0.0F)
		{
			player.startUsingItem(hand);
			level.playSound(null, player.blockPosition(), SoundEvents.BREEZE_IDLE_AIR, SoundSource.PLAYERS, 1.0F, 0.8F);
			return InteractionResultHolder.consume(player.getItemInHand(hand));
		}
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft)
	{
		if (!(entity instanceof Player player)) return;

		int usedTicks = getUseDuration(stack, entity) - timeLeft;

		if (usedTicks >= 20)
		{
			float chargeRatio = (float) usedTicks / getUseDuration(stack, entity);

			float pitch = player.getXRot();
			if (pitch < 0.0F)
			{
				Vec3 lookVec = player.getLookAngle();

				double forwardForce = (1.5D + (chargeRatio * 1.5D));
				double upwardForce = (0.8D + (chargeRatio * 1.0D));

				Vec3 jumpVelocity = new Vec3(
						lookVec.x * forwardForce,
						Math.abs(lookVec.y) * upwardForce,
						lookVec.z * forwardForce
				);

				player.setDeltaMovement(jumpVelocity);
				player.hasImpulse = true;

				level.playSound(null, player.blockPosition(), SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 1.2F, 0.9F);

				// NBT-Daten für Flug, Charge und Höhenberechnung speichern
				player.getPersistentData().putBoolean("WUT_HammerSlam", true);
				player.getPersistentData().putFloat("WUT_SlamCharge", chargeRatio);
				player.getPersistentData().putDouble("WUT_SlamStartY", player.getY());
				player.getPersistentData().putDouble("WUT_SlamMaxY", player.getY());

				player.getCooldowns().addCooldown(this, 60);
			}
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity)
	{
		if(entity.isAlive() && entity instanceof Mob mob)
		{
			mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 90, 2));
			if(!mob.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
				mob.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
		}
		player.level().playSound(null, entity.blockPosition(), WUTSounds.HAMMERHIT.get(), SoundSource.PLAYERS, 1.0F, 0.8F + player.level().getRandom().nextFloat() - 0.6F);
		return super.onLeftClickEntity(stack, player, entity);
	}

	public Recipe<?> findMatchingRecipe(Level world, ItemStack dropMe)
	{
		Collection<RecipeHolder<?>> list = world.getServer().getRecipeManager().getRecipes();
		for(RecipeHolder<?> recipe : list)
		{
			if(recipe.value().getType() == RecipeType.CRAFTING)
			{
				if(recipeMatches(world, dropMe, recipe.value()))
				{
					return recipe.value();
				}
			}
		}
		return null;
	}

	public boolean recipeMatches(Level level, ItemStack stack, Recipe<?> recipe)
	{
		if(stack.isEmpty() || recipe == null || recipe.getResultItem(level.registryAccess()).isEmpty() || recipe.getResultItem(level.registryAccess()).getCount() > stack.getCount())
		{
			return false;
		}
		return stack.getItem() == recipe.getResultItem(level.registryAccess()).getItem() && ItemStack.matches(stack, recipe.getResultItem(level.registryAccess()));
	}

	public boolean uncraftRecipe(Level level, BlockPos pos, Recipe<?> match)
	{
		List<ItemStack> result = match.getIngredients().stream().flatMap(ingredient -> Arrays.stream(ingredient.getItems()).filter(stack -> !stack.hasCraftingRemainingItem()).findAny().map(Stream::of).orElseGet(Stream::empty)).collect(Collectors.toList());
		return !result.isEmpty();
	}

	public void hammerSmash(Level level, Player player, BlockPos pos, Recipe<?> match)
	{
		List<ItemStack> result = match.getIngredients().stream().flatMap(ingredient -> Arrays.stream(ingredient.getItems()).filter(stack -> !stack.hasCraftingRemainingItem()).findAny().map(Stream::of).orElseGet(Stream::empty)).collect(Collectors.toList());
		for(ItemStack r : result)
		{
			ItemStack itemstack = r.copy();
			double d0 = level.random.nextFloat() * 0.5F + 0.25D;
			double d1 = level.random.nextFloat() * 0.5F + 0.25D;
			double d2 = level.random.nextFloat() * 0.5F + 0.25D;
			ItemEntity entityitem = new ItemEntity(level, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, itemstack);
			entityitem.setDefaultPickUpDelay();
			entityitem.hurt(level.damageSources().inFire(), -100);
			entityitem.setRemainingFireTicks(10);
			level.destroyBlock(pos, false);
			level.addFreshEntity(entityitem);
			player.getCooldowns().addCooldown(this, 12);
			player.getMainHandItem().setDamageValue(player.getMainHandItem().getDamageValue() + 5);
			if(player.getMainHandItem().getDamageValue() >= player.getMainHandItem().getMaxDamage())
			{
				player.getMainHandItem().shrink(1);
			}
		}
	}
}