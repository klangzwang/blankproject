package geni.withercore.client.shader;

import codechicken.lib.render.shader.CCUniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import geni.witherutils.WitherUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class BaseShader extends WitherBaseShader<BaseShader> {

    private CCUniform uv1OverrideUniform;
    private CCUniform uv2OverrideUniform;

    private CCUniform baseColorUniform;

    public BaseShader(String path, VertexFormat format) {
        super(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, path), format);
    }

    public BaseShader(ResourceLocation location, VertexFormat format) {
        super(location, format);
    }

    public final CCUniform getUv1OverrideUniform() { return Objects.requireNonNull(uv1OverrideUniform, missingUniformMessage("UV1Override")); }
    public final boolean hasUv1OverrideUniform() { return uv1OverrideUniform != null; }
    public final CCUniform getUv2OverrideUniform() { return Objects.requireNonNull(uv2OverrideUniform, missingUniformMessage("UV2Override")); }
    public final boolean hasUv2OverrideUniform() { return uv2OverrideUniform != null; }
    public final CCUniform getBaseColorUniform() { return Objects.requireNonNull(baseColorUniform, missingUniformMessage("BaseColor")); }
    public final boolean hasBaseColorUniform() { return baseColorUniform != null; }

    @Override
    protected void onShaderLoaded() {
        super.onShaderLoaded();
        uv1OverrideUniform = shaderInstance.getUniform("UV1Override");
        uv2OverrideUniform = shaderInstance.getUniform("UV2Override");
        baseColorUniform = shaderInstance.getUniform("BaseColor");
    }
}
