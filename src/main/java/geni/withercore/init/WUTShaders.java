package geni.withercore.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import geni.witherutils.WitherUtils;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import javax.annotation.Nullable;
import java.io.IOException;

@EventBusSubscriber(modid = WitherUtils.MODID, value = Dist.CLIENT)
public class WUTShaders {

    @Nullable
    private static ShaderInstance powerCableShader;

    @Nullable
    private static ShaderInstance roundedRectShader;

    @SubscribeEvent
    public static void register(RegisterShadersEvent event) {
        try {
            event.registerShader(
                    new ShaderInstance(
                            event.getResourceProvider(),
                            ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "rounded_rect"),
                            DefaultVertexFormat.POSITION_TEX_COLOR
                    ),
                    shader -> roundedRectShader = shader
            );
            event.registerShader(
                    new ShaderInstance(
                            event.getResourceProvider(),
                            ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "power_cable"),
                            DefaultVertexFormat.BLOCK
                    ),
                    shader -> powerCableShader = shader
            );
        } catch (IOException e) {
            WitherUtils.LOGGER.error("Failed to register WitherUtils shaders", e);
        }
    }

    @Nullable
    public static ShaderInstance getPowerCableShader() {
        return powerCableShader;
    }

    @Nullable
    public static ShaderInstance getRoundedRectShader() {
        return roundedRectShader;
    }

    public static boolean isRoundedRectShaderAvailable() {
        return roundedRectShader != null;
    }
}
