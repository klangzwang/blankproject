package geni.witherutils.client;

import geni.withercore.init.WUTBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class ClientCursedEarthHandler {

    private static final ResourceLocation DESATURATE_SHADER = ResourceLocation.withDefaultNamespace("shaders/post/desaturate.json");
    private static boolean shaderActive = false;
    private static boolean onCursedEarth = false;

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null)
        {
            if (shaderActive)
            {
                mc.gameRenderer.shutdownEffect();
                shaderActive = false;
                onCursedEarth = false;
            }
            return;
        }

        BlockState stateOn = mc.player.getBlockStateOn();
        BlockState stateFeet = mc.level.getBlockState(mc.player.blockPosition());
        BlockState stateBelow = mc.level.getBlockState(mc.player.blockPosition().below());

        boolean standingOnCursed = stateOn.is(WUTBlocks.WITHEREARTH.get())
                || stateFeet.is(WUTBlocks.WITHEREARTH.get())
                || stateBelow.is(WUTBlocks.WITHEREARTH.get());

        onCursedEarth = standingOnCursed;

        if (standingOnCursed)
        {
            // Activate monochrome / desaturated nightmare shader
            if (!shaderActive || mc.gameRenderer.currentEffect() == null)
            {
                mc.gameRenderer.loadEffect(DESATURATE_SHADER);
                shaderActive = true;
            }
        }
        else
        {
            // Turn off shader effect immediately when stepping off cursed earth
            if (shaderActive)
            {
                mc.gameRenderer.shutdownEffect();
                shaderActive = false;
            }
        }
    }

    @SubscribeEvent
    public void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event)
    {
        if (!onCursedEarth)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        // Eerie camera wobble/sway that makes steering difficult
        float time = (mc.player.tickCount + (float) event.getPartialTick()) * 0.08F;
        float yawSway = (float) Math.sin(time) * 1.8F;
        float pitchSway = (float) Math.cos(time * 0.7F) * 1.3F;
        float rollSway = (float) Math.sin(time * 0.5F) * 2.2F;

        event.setYaw(event.getYaw() + yawSway);
        event.setPitch(event.getPitch() + pitchSway);
        event.setRoll(event.getRoll() + rollSway);
    }
}
