package geni.withercore.helper;

import java.time.Duration;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.Blaze3D;

import geni.witherutils.WitherUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = WitherUtils.MODID)
public final class TickHelper {

    private static Minecraft minecraft;
	
	private static long serverTickCount = 0;
	private static long clientTickCount = 0;
	private static long clientPausedTickCount = 0;

    public static int elapsedTicks = 0;
	
    public static Optional<MinecraftServer> getMinecraftServer()
    {
        return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer());
    }
	
	@SubscribeEvent
	public static void onTick(@Nonnull ServerTickEvent.Post evt)
	{
		if (evt.hasTime())
		{
			++serverTickCount;
		}
	}

	@SubscribeEvent
	public static void onTick(@Nonnull ClientTickEvent.Post evt)
	{
		if(minecraft == null)
			minecraft = Minecraft.getInstance();

		elapsedTicks++;
		
		if (minecraft.isPaused())
		{
			++clientPausedTickCount;
		}
		else
		{
			++clientTickCount;
		}
	}
	public static long getServerTickCount()
	{
		return serverTickCount;
	}
	public static long getClientTickCount()
	{
		return clientTickCount;
	}
	public static long getClientPausedTickCount()
	{
		return clientPausedTickCount;
	}
	public static float getPartialTick()
	{
		if(minecraft.level.isClientSide)
			return minecraft.getTimer().getGameTimeDeltaPartialTick(true);
		return 0;
	}
	
    public static double getCurrentSystemTick()
    {
        return System.nanoTime() / 1E6 / 50d;
    }

    /**
     * Returns the current time (in ticks) that the {@link org.lwjgl.glfw.GLFW GLFW} instance has been running. This is
     * effectively a permanent timer that counts up since the game was launched.
     */
    public static double getCurrentTick()
    {
        return Blaze3D.getTime() * 20d;
    }
    
    public static int toTicks(final Duration duration)
    {
        return (int) (duration.getSeconds() * 20);
    }
    
    public static String getTotalRealTime(long ticks)
    {
        int days = (int) (ticks / 1728000);
        int hours = (int) Math.floor(ticks / 24000.0) % 24;
        int minutes = (int) Math.floor(ticks / 1200) % 60;
        int seconds = (int) Math.floor(ticks / 20) % 60;
        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        }
        return String.format("%ds", seconds);
    }

    @SuppressWarnings("unused")
	public static String getTotalGameTime(long ticks)
    {
        int days = (int) (ticks / 24000);
        int hours = (int) ((Math.floor(ticks / 1000.0) + 7) % 24);
        int minutes = (int) Math.floor((ticks % 1000) / 1000.0 * 60);
        return String.format("%02d:%02d", hours, minutes);
    }

    public static String getGameTime(long ticks)
    {
        int hours = (int) ((Math.floor(ticks / 1000.0) + 7) % 24);
        int minutes = (int) Math.floor((ticks % 1000) / 1000.0 * 60);
        return String.format("%02d:%02d", hours, minutes);
    }

    public static long getRealTimeToTicks(int hours, int minutes, int seconds)
    {
        return hours * 72000L + minutes * 1200L + seconds * 20L;
    }
    
	public static long timeToTick(long time)
	{
		return time * 20 / 1000;
	}

	public static long tickToTime(long ticks)
	{
		return ticks * 1000 / 20;
	}
	
	public long elapsedTime()
	{
		return System.currentTimeMillis() - clientTickCount;
	}
	
	@Override
	public String toString()
	{
		int elapsed = (int) (elapsedTime() / 1000);
		int minutes = (elapsed % 3600) / 60;
		int seconds = elapsed % 60;

		return String.format("%02d:%02d ago", minutes, seconds);
	}
}
