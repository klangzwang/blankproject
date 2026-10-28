package geni.withercore.common.helper;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import geni.witherutils.WitherUtils;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AtlasTextureHelper {

    private static final Map<ResourceLocation, Function<ResourceLocation, TextureAtlasSprite>> ATLAS_CACHE = new HashMap<>();

    public static TextureAtlasSprite SOULORB;
    public static TextureAtlasSprite WHITEOVERLAY;
    public static TextureAtlasSprite BAKED_EMISSIVE;

    public static ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType()
    {
        @SuppressWarnings("deprecation")
        @Override
        public BufferBuilder begin(Tesselator pTesselator, TextureManager pTextureManager)
        {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            return pTesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public String toString()
        {
            return "TERRAIN_SHEET_TRANSLUCENT";
        }
    };

    public static void init(IEventBus modBus)
    {
        modBus.addListener(AtlasTextureHelper::textureStitch);
    }

    @SuppressWarnings("deprecation")
    private static void textureStitch(TextureAtlasStitchedEvent event)
    {
        TextureAtlas atlas = event.getAtlas();
        if (atlas.location().equals(TextureAtlas.LOCATION_PARTICLES))
        {
            ATLAS_CACHE.clear();
            SOULORB = atlas.getSprite(WitherUtils.loc("soulorb"));
        }
        if (atlas.location().equals(TextureAtlas.LOCATION_BLOCKS))
        {
            ATLAS_CACHE.clear();
            WHITEOVERLAY = atlas.getSprite(WitherUtils.loc("overlay_white"));
            BAKED_EMISSIVE = atlas.getSprite(WitherUtils.loc("block/emissive/blue"));
        }
    }
}
