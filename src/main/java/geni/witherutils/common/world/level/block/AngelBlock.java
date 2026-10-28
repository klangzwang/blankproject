package geni.witherutils.common.world.level.block;

import geni.withercore.block.WitherAbstractBlock;
import geni.withercore.init.WUTParticles;
import geni.withercore.network.packet.TeleportPacket;
import geni.withercore.network.payload.ClientPacketSender;
import geni.withercore.util.ParticleUtil;
import geni.witherutils.client.render.effect.particle.MoltenParticleData;
import geni.witherutils.client.render.effect.particle.UpdraftParticleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

public class AngelBlock extends WitherAbstractBlock implements Fallable {

	private static boolean lastJumping;

	public AngelBlock(BlockBehaviour.Properties props) {
		super(props);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		List<ItemStack> dropsOriginal = new ArrayList<>();
		dropsOriginal.add(new ItemStack(this));
		return dropsOriginal;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (!world.getBlockState(pos.below()).isAir()) {
			return InteractionResult.PASS;
		}
		else if(pos.getY() >= world.getMinBuildHeight())
		{
			FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(world, pos, state);
			this.falling(fallingblockentity);
		}
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		super.stepOn(level, pos, state, entity);
		if (!level.isClientSide && entity instanceof Player && level.getBlockState(pos.below()).isAir()) {
			level.scheduleTick(pos, this, 50);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if(level.getBlockState(pos.below()).isAir() && pos.getY() >= level.getMinBuildHeight()) {
			AABB hitbox = new AABB(pos).expandTowards(0, 1, 0);
			boolean playerOnTop = !level.getEntitiesOfClass(Player.class, hitbox).isEmpty();
			if (playerOnTop) {
				FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(level, pos, state);
				this.falling(fallingblockentity);
			}
		}
	}

	protected void falling(FallingBlockEntity entity) {
	}

	@Override
	public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
		if (!level.isClientSide) {
			AABB hitbox = new AABB(pos).inflate(0.0D);
			List<Player> hitPlayers = level.getEntitiesOfClass(Player.class, hitbox);
			if (!hitPlayers.isEmpty()) {
				for (Player p : hitPlayers) {
					p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 1200, 0));
				}
				level.destroyBlock(pos, false);
			}

			if(level instanceof ServerLevel serverLevel) {
				double dirMultiplier = 0.5D;
				double dirYMultiplier = -0.5D;
				serverLevel.sendParticles(WUTParticles.SOULFRAGSOFT.get(), pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 20, dirMultiplier, dirYMultiplier, -dirMultiplier, 0.05D);
				serverLevel.sendParticles(WUTParticles.SOULFRAGSOFT.get(), pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 20, -dirMultiplier, dirYMultiplier, dirMultiplier, 0.05D);
			}
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		int dropDistance = -1;
		for (int i = 1; i <= 6; i++) {
			if (!level.getBlockState(pos.below(i)).isAir()) {
				dropDistance = i;
				break;
			}
		}
		if (dropDistance >= 3) {
			if (random.nextInt(2) == 0) {
				BlockPos particlePos = pos.below(dropDistance).above();

				double pX = particlePos.getX() + random.nextDouble();
				double pY = particlePos.getY() + 0.1D;
				double pZ = particlePos.getZ() + random.nextDouble();

				/*ParticleUtil.spawnMoltenEmber(level, pX, pY, pZ, MoltenParticleData.HOT_INGOT);*/
				ParticleUtil.spawnUpdraft(level, pX, pY, pZ, UpdraftParticleData.THERMAL);
			}
		}

		if(random.nextInt(16) == 0 && level.getBlockState(pos.below()).isAir()) {
			double d0 = (double) pos.getX() + random.nextDouble();
			double d1 = (double) pos.getY() - 0.05D;
			double d2 = (double) pos.getZ() + random.nextDouble();

			level.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, state), d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}
	}

	public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
		return -16777216;
	}

	public static void handleInput(ClientPacketSender sender) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null || player.isSpectator() || !player.isAlive() || player.input == null)
			return;

		boolean jumping = player.input.jumping;
		if (lastJumping != jumping) {
			lastJumping = jumping;
			if (jumping) {
				tryTeleport(player, Direction.UP, sender);
			}
		}
	}

	private static void tryTeleport(LocalPlayer player, Direction facing, ClientPacketSender sender) {
		Level world = player.level();
		BlockPos playerPos = player.blockPosition();

		for (int i = 1; i <= 6; i++) {
			BlockPos targetPos = playerPos.above(i);
			var toAngel = TeleportPacket.getAngel(world.getBlockState(targetPos));

			if (toAngel != null && TeleportPacket.isValidPos(world, targetPos)) {
				sender.sendToServer(new TeleportPacket(playerPos, targetPos));
				break;
			}
		}
	}
}
