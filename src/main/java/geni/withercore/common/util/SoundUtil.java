package geni.withercore.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class SoundUtil {
    
    /*
     * 
     * TYPES
     * 
     */
	public static void playSoundDistrib(LevelAccessor levelacc, BlockPos pos, SoundEvent soundIn, float volume, float pitch)
	{
		playSoundDistrib(levelacc, pos, soundIn, volume, pitch, false, false);
	}
	public static void playSoundDistrib(LevelAccessor levelacc, BlockPos pos, SoundEvent soundIn, float volume, float pitch, boolean randomvolume, boolean randompitch)
	{
		playSoundDistrib(levelacc, pos, soundIn, volume, pitch, randompitch, randompitch, 0);
	}
    @SuppressWarnings("resource")
	public static void playSoundDistrib(LevelAccessor levelacc, BlockPos pos, SoundEvent soundIn, float volume, float pitch, boolean randomvolume, boolean randompitch, int distanceTo)
    {
		if (levelacc instanceof Level level)
		{
	    	float vol = randomvolume ? volume * ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.8F) : volume;
	    	float pit = randompitch ? pitch * ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.8F) : pitch;

	    	if(distanceTo > 0)
	    	{
	        	float distance = (float) Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
	            float filledDistance = distance / (float) distanceTo;
	        	float fader = Math.min(filledDistance, 1.0f);
	        	if(distanceTo <= 30)
	        	{
	        		vol -= fader;
	        	}
	    	}

	    	if(!level.isClientSide)
	    	{
		        level.playSound(null, pos, soundIn, SoundSource.BLOCKS, vol, pit);
//	        	if(level instanceof ServerLevel serverLevel)
//	        	{
//	                for (ServerPlayer sp : serverLevel.players())
//	                {
//	                	sp.connection.send(new ClientboundSoundPacket(Holder.direct(soundIn), SoundSource.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), pit, vol, 0));
//	                }
//	        	}
	    	}
	    	else
	    	{
		        level.playLocalSound(pos, soundIn, SoundSource.BLOCKS, vol, pit, false);
	    	}
		}
    }

    public static void playSound(Level world, BlockPos pos, SoundEvent soundIn)
    {
        world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), soundIn, SoundSource.BLOCKS, 0.5F, 0.5F, false);
    }
    public static void playSound(Level world, BlockPos pos, SoundEvent soundIn, float volume)
    {
        world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), soundIn, SoundSource.BLOCKS, volume, 0.5F, false);
    }
    public static void playSound(Entity entityIn, SoundEvent soundIn)
    {
        playSound(entityIn, soundIn, 1.0F, 1.0F);
    }
    public static void playSound(Entity entityIn, SoundEvent soundIn, float volume)
    {
        playSound(entityIn, soundIn, volume, 1.0F);
    }
    public static void playSound(Entity entityIn, SoundEvent soundIn, float volume, float pitch)
    {
        if (entityIn != null && entityIn.level().isClientSide)
        {
            entityIn.playSound(soundIn, volume, pitch);
        }
    }
    public static void playServerSound(Level level, BlockPos pos, SoundEvent soundIn, float volume, float pitch)
    {
    	if(level.isClientSide)
    		return;
    	if(level instanceof ServerLevel serverLevel)
    	{
            for (ServerPlayer sp : serverLevel.players())
            {
                playSoundFromServer(sp, pos, soundIn, volume, pitch);
            }
    	}
    }
    public static void playSoundFromServer(ServerPlayer entityIn, BlockPos pos, SoundEvent soundIn, float p, float v)
    {
        if (soundIn == null || entityIn == null)
        {
            return;
        }
        entityIn.connection.send(new ClientboundSoundPacket(Holder.direct(soundIn), SoundSource.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), p, v, 0));
    }
    public static void playSoundFromServer(ServerPlayer entityIn, SoundEvent soundIn, float p, float v)
    {
        if (soundIn == null || entityIn == null)
        {
            return;
        }
        entityIn.connection.send(new ClientboundSoundPacket(Holder.direct(soundIn), SoundSource.BLOCKS, entityIn.xOld, entityIn.yOld, entityIn.zOld, p, v, 0));
    }
    public static void playSoundFromServer(ServerLevel world, BlockPos pos, SoundEvent soundIn)
    {
        for (ServerPlayer sp : world.players())
        {
            playSoundFromServer(sp, pos, soundIn, 1F, 1F);
        }
    }
    public static void playSoundFromServerById(ServerLevel world, BlockPos pos, String sid)
    {
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.withDefaultNamespace(sid));
        if (sound != null)
        {
            for (ServerPlayer sp : world.players())
            {
                playSoundFromServer(sp, pos, sound, 1F, 1F);
            }
        }
    }
    public static void playSoundById(Player player, String sid)
    {
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.withDefaultNamespace(sid));
        if (sound != null && player.level().isClientSide)
        {
            SoundUtil.playSound(player, sound);
        }
    }

    public static void playDistanceSound(Minecraft mc, Level level, BlockPos pos, SoundEvent nearSoundIn, int distanceTo)
    {
    	float distance = (float) mc.gameRenderer.getMainCamera().getPosition().distanceToSqr(pos.getX(), pos.getY(), pos.getZ());
        float filledDistance = distance / (float) distanceTo;
    	float fader = Math.min(filledDistance, 1.0f);
    	if(distanceTo <= 30)
    	{
    		level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), nearSoundIn, SoundSource.BLOCKS, 0.8f - fader, 1F, false);
    	}
    }
    
    public static void playSlotSound(Level level, BlockPos pos, SoundEvent soundIn, float volume, float pitch)
    {
    	if(level.isClientSide)
    		return;
    	if(level instanceof ServerLevel serverLevel)
    	{
            for (ServerPlayer sp : serverLevel.players())
            {
                playSoundFromServer(sp, pos, soundIn, volume, pitch);
            }
    	}
    }
    
    public static void playDistanceEntitySound(Entity entity, Minecraft mc, Level level, Vec3 pos, SoundEvent nearSoundIn, int distanceTo)
    {
    	float distance = (float) mc.gameRenderer.getMainCamera().getPosition().distanceToSqr(pos.x, pos.y, pos.z);
        float filledDistance = distance / (float) distanceTo;
    	float fader = Math.min(filledDistance, 1.0f);
    	if(distanceTo <= 30)
    	{
    		level.playSound(entity, new BlockPos((int) pos.x, (int) pos.y, (int) pos.z), nearSoundIn, SoundSource.HOSTILE, 0.8f - fader, 1F);
    	}
    }

    public static void playImpactSound(Entity entity, float damage) {
        if (entity == null || !entity.level().isClientSide) return;

        SoundEvent sound;
        float volume;
        float pitch;

        if (damage < 5.0f) {
            // Leichter Treffer: Hohe Pitch, leiser Anvil- / Shield-Pling
            sound = SoundEvents.SHIELD_BLOCK;
            volume = 0.6f + (damage * 0.08f);
            pitch = 1.2f - (damage * 0.05f);
        } else if (damage < 15.0f) {
            // Mittlerer Treffer: Anvil Land mit Standard-Pitch
            sound = SoundEvents.ANVIL_LAND;
            volume = 0.8f;
            pitch = 1.0f;
        } else {
            // Schwerer Treffer: Tiefer, wuchtiger Anvil-Einschlag + hoher Volume
            sound = SoundEvents.ANVIL_LAND;
            volume = 1.2f;
            pitch = 0.6f; // Tiefer Pitch wirkt massiver
        }

        playSound(entity, sound, volume, pitch);
    }
}