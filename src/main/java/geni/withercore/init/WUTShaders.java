package geni.withercore.init;

import geni.witherutils.WitherUtils;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import javax.annotation.Nullable;
import java.io.IOException;

public class WUTShaders {

    @Nullable
    private static ShaderInstance roundedRectShader;

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
        } catch (IOException e) {
            WitherUtils.LOGGER.error("Failed to register rounded_rect shader", e);
        }
    }

    @Nullable
    public static ShaderInstance getRoundedRectShader() {
        return roundedRectShader;
    }

    public static boolean isRoundedRectShaderAvailable() {
        return roundedRectShader != null;
    }
}
