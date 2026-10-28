package geni.withercore.render.blockentity;

import codechicken.lib.vec.Matrix4;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import geni.withercore.blockentity.WitherBlockEntity;
import geni.withercore.helper.ColorHelper;
import geni.withercore.helper.RenderHelper;
import geni.withercore.helper.TickHelper;
import geni.withercore.util.FacingUtil;
import geni.witherutils.client.entity.render.AbstractEntityRenderer;
import geni.witherutils.common.world.level.block.entity.CreativeGeneratorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class AbstractBlockEntityRenderer<T extends WitherBlockEntity> implements BlockEntityRenderer<T> {

    protected ItemRenderer itemRenderer;
    protected BlockRenderDispatcher blockRenderer;

    protected final BlockEntityRendererProvider.Context context;
    
	protected AbstractBlockEntityRenderer(BlockEntityRendererProvider.Context context)
	{
        this.context = context;

        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
	}

    @Override
    public final void render(T te, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource buffer, int packedLight, int overlayLight)
    {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (te.getLevel() != null)
        {
            if (player != null)
            {
                /*matrix.pushPose();
                matrix.translate(0.5, 1.5, 0.5);
                matrix.scale(1f, -1f, -1f);*/
                render(te, TickHelper.getPartialTick(), matrix, buffer, mc, player.clientLevel, player, packedLight, overlayLight);
                /*matrix.popPose();*/
            }
        }
    }

    public abstract void render(T te, float partialTick, PoseStack matrix, MultiBufferSource buffer, Minecraft mc, ClientLevel level, LocalPlayer player, int light, int overlayLight);

    /*public void renderPreview(PoseStack matrixStack, MultiBufferSource buffer, int light, Vec3 minCoord, Vec3 maxCoord, float lineThickness, Vector4f rgba)
    {
        matrixStack.pushPose();

        // 1. Transparenten Würfel rendern
        renderCube(Blocks.GRAY_CONCRETE, matrixStack, buffer, minCoord, maxCoord, light);

        // 2. Outlines (Kanten) rendern
        RenderSystem.lineWidth(Math.max(lineThickness + 2.5F, (float) Minecraft.getInstance().getWindow().getWidth() / 1920.0F * lineThickness + 2.5F));

        VertexConsumer lineBuffer = buffer.getBuffer(RenderType.lines());

        // AABB exakt passend zu den Bounding-Grenzen definieren
        AABB thebox = new AABB(minCoord.x, minCoord.y, minCoord.z, maxCoord.x + 1.0D, maxCoord.y + 1.0D, maxCoord.z + 1.0D);
        LevelRenderer.renderLineBox(matrixStack, lineBuffer, thebox, rgba.x(), rgba.y(), rgba.z(), rgba.w());

        matrixStack.popPose();
    }

    public void renderCube(Block block, PoseStack matrixStack, MultiBufferSource bufferIn, Vec3 minCoord, Vec3 maxCoord, int combinedLight)
    {
        // Leichtes Offset zur Vermeidung von Z-Fighting mit Blöcken
        float offset = 0.002F;

        float xMin = (float) (minCoord.x - offset);
        float yMin = (float) (minCoord.y - offset);
        float zMin = (float) (minCoord.z - offset);

        float xMax = (float) (maxCoord.x + 1.0F + offset);
        float yMax = (float) (maxCoord.y + 1.0F + offset);
        float zMax = (float) (maxCoord.z + 1.0F + offset);

        float alpha = 0.3F;
        float red = 1.0F;
        float green = 1.0F;
        float blue = 1.0F;

        VertexConsumer buffer = bufferIn.getBuffer(RenderType.translucent());
        TextureAtlasSprite sprite = RenderHelper.getBlockTexture(BuiltInRegistries.BLOCK.getKey(block));

        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV0();
        float vMax = sprite.getV1();

        // Oben (UP)
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMax, uMax, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMin, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMin, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMax, uMin, vMax, red, green, blue, alpha, combinedLight);

        // Unten (DOWN)
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMin, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMax, uMax, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMax, uMin, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMin, uMin, vMin, red, green, blue, alpha, combinedLight);

        // Nord (NORTH)
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMin, uMin, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMin, uMax, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMin, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMin, uMin, vMin, red, green, blue, alpha, combinedLight);

        // Süd (SOUTH)
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMax, uMin, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMax, uMax, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMax, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMax, uMin, vMin, red, green, blue, alpha, combinedLight);

        // Ost (EAST)
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMax, uMin, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMin, uMax, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMin, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMax, uMin, vMin, red, green, blue, alpha, combinedLight);

        // West (WEST)
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMin, uMin, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMax, uMax, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMax, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMin, uMin, vMin, red, green, blue, alpha, combinedLight);
    }

    private void addVertexWithUV(VertexConsumer buffer, PoseStack matrixStack, float x, float y, float z, float u, float v, float red, float green, float blue, float alpha, int combinedLight)
    {
        buffer.addVertex(matrixStack.last().pose(), x, y, z)
                .setColor(red, green, blue, alpha)
                .setUv(u, v)
                .setUv2(combinedLight, 240)
                .setNormal(0, 1, 0);
    }*/

    public void renderPreview(PoseStack matrixStack, MultiBufferSource buffer, int light, Vec3 minCoord, Vec3 maxCoord, float lineThickness, Vector4f rgba)
    {
        matrixStack.pushPose();

        RenderSystem.lineWidth(Math.max(lineThickness + 2.5F, (float) Minecraft.getInstance().getWindow().getWidth() / 1920.0F * lineThickness + 2.5F));

        VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.lines());
        matrixStack.translate(-0.0005D, -0.0005D, -0.0005D);
        matrixStack.scale(0.999F, 0.999F, 0.999F);

        AABB thebox = new AABB(-minCoord.x, 0, -minCoord.z, maxCoord.x +1, maxCoord.y, maxCoord.z +1);
        LevelRenderer.renderLineBox(matrixStack, ivertexbuilder, thebox, rgba.x, rgba.y, rgba.z, rgba.w);

        matrixStack.popPose();
    }

    public void renderCube(Block block, PoseStack matrixStack, MultiBufferSource bufferIn, Vec3 minCoord, Vec3 maxCoord, int combinedLight)
    {
        float xMin, xMax, yMin, yMax, zMin, zMax = 0;

        xMin = (float) (-minCoord.x + 0.015625F);
        yMin = 0.015625F;
        zMin = (float) (-minCoord.z + 0.015625F);

        xMax = (float) (maxCoord.x + 1.984375F);
        yMax = (float) (maxCoord.y + 0.984375F);
        zMax = (float) (maxCoord.z + 1.984375F);

        float alpha = 0.25F;
        float red = (0xFFFFFFFF >> 16 & 0xFF) / 255.0F;
        float green = (0xFFFFFFFF >> 8 & 0xFF) / 255.0F;
        float blue = (0xFFFFFFFF & 0xFF) / 255.0F;

        VertexConsumer buffer = ItemRenderer.getCompassFoilBuffer(bufferIn, RenderType.translucent(), matrixStack.last());
        TextureAtlasSprite textureAtlasSprite = RenderHelper.getBlockTexture(BuiltInRegistries.BLOCK.getKey(block));

        float uMin = textureAtlasSprite.getU0();
        float uMax = textureAtlasSprite.getU1();
        float vMin = textureAtlasSprite.getV0();
        float vMax = textureAtlasSprite.getV1();

        float vHeight = vMax - vMin;

        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMax, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMin, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMin, uMin, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMax, uMax, vMax, red, green, blue, alpha, combinedLight);

        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMin, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMin, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMin, uMin, vMin + (vHeight * yMax), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMin, uMax, vMin + (vHeight * yMax), red, green, blue, alpha, combinedLight);

        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMax, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMax, uMin, vMin + (vHeight * yMax), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMax, uMax, vMin + (vHeight * yMax), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMax, uMax, vMin, red, green, blue, alpha, combinedLight);

        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMin, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMin, uMin, vMin + (vHeight * yMax), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMax, zMax, uMax, vMin + (vHeight * yMax), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMax, uMax, vMin, red, green, blue, alpha, combinedLight);

        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMax, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMax, uMin, vMin + (vHeight * yMax), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMax, zMin, uMax, vMin + (vHeight * yMax), red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMin, uMax, vMin, red, green, blue, alpha, combinedLight);

        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMin, uMax, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMax, yMin, zMax, uMin, vMin, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMax, uMin, vMax, red, green, blue, alpha, combinedLight);
        addVertexWithUV(buffer, matrixStack, xMin, yMin, zMin, uMax, vMax, red, green, blue, alpha, combinedLight);
    }

    private void addVertexWithUV(VertexConsumer buffer, PoseStack matrixStack, float x, float y, float z, float u, float v, float red, float green, float blue, float alpha, int combinedLight)
    {
        buffer.addVertex(matrixStack.last().pose(), x, y, z)
                .setColor(red, green, blue, alpha)
                .setUv(u, v)
                .setUv2(combinedLight, 240)
                .setNormal(0, 1, 0);
    }

    //
    // MODEL3D TESTING
    //
    /**
     * Get a fluid texture when a stack does not exist.
     *
     * @param fluid the fluid to get
     * @param type  Still or Flowing
     *
     * @return the sprite, or missing sprite if not found
     */
    public static TextureAtlasSprite getBaseFluidTexture(@NotNull Fluid fluid, @NotNull FluidTextureType type) {
        IClientFluidTypeExtensions properties = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation spriteLocation;
        if (type == FluidTextureType.STILL) {
            spriteLocation = properties.getStillTexture();
        } else {
            spriteLocation = properties.getFlowingTexture();
        }
        return getSprite(spriteLocation);
    }

    public static TextureAtlasSprite getFluidTexture(@NotNull FluidStack fluidStack, @NotNull FluidTextureType type) {
        IClientFluidTypeExtensions properties = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation spriteLocation;
        if (type == FluidTextureType.STILL) {
            spriteLocation = properties.getStillTexture(fluidStack);
        } else {
            spriteLocation = properties.getFlowingTexture(fluidStack);
        }
        return getSprite(spriteLocation);
    }

    public static TextureAtlasSprite getSprite(ResourceLocation spriteLocation) {
        if (spriteLocation == null) {
            spriteLocation = MissingTextureAtlasSprite.getLocation();
        }
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(spriteLocation);
    }

    public enum FluidTextureType {
        STILL,
        FLOWING
    }
    public static class Model3D {

        public float minX, minY, minZ;
        public float maxX, maxY, maxZ;

        private final TextureAtlasSprite[] textures = new TextureAtlasSprite[6];
        private final boolean[] renderSides = {true, true, true, true, true, true};

        public Model3D setSideRender(Predicate<Direction> shouldRender) {
            for (Direction direction : FacingUtil.DIRECTIONS) {
                setSideRender(direction, shouldRender.test(direction));
            }
            return this;
        }

        public Model3D setSideRender(Direction side, boolean value) {
            renderSides[side.ordinal()] = value;
            return this;
        }

        public Model3D copy() {
            Model3D copy = new Model3D();
            System.arraycopy(textures, 0, copy.textures, 0, textures.length);
            System.arraycopy(renderSides, 0, copy.renderSides, 0, renderSides.length);
            return copy.bounds(minX, minY, minZ, maxX, maxY, maxZ);
        }

        @Nullable
        public TextureAtlasSprite getSpriteToRender(Direction side) {
            int ordinal = side.ordinal();
            return renderSides[ordinal] ? textures[ordinal] : null;
        }

        public Model3D shrink(float amount) {
            return grow(-amount);
        }

        public Model3D grow(float amount) {
            return bounds(minX - amount, minY - amount, minZ - amount, maxX + amount, maxY + amount, maxZ + amount);
        }

        public Model3D xBounds(float min, float max) {
            this.minX = min;
            this.maxX = max;
            return this;
        }

        public Model3D yBounds(float min, float max) {
            this.minY = min;
            this.maxY = max;
            return this;
        }

        public Model3D zBounds(float min, float max) {
            this.minZ = min;
            this.maxZ = max;
            return this;
        }

        public Model3D bounds(float min, float max) {
            return bounds(min, min, min, max, max, max);
        }

        public Model3D bounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
            return xBounds(minX, maxX)
                    .yBounds(minY, maxY)
                    .zBounds(minZ, maxZ);
        }

        public Model3D prepSingleFaceModelSize(Direction face) {
            bounds(0, 1);
            return switch (face) {
                case DOWN -> yBounds(-0.01F, -0.001F);
                case UP -> yBounds(1.001F, 1.01F);
                case NORTH -> zBounds(-0.01F, -0.001F);
                case SOUTH -> zBounds(1.001F, 1.01F);
                case WEST -> xBounds(-0.01F, -0.001F);
                case EAST -> xBounds(1.001F, 1.01F);
            };
        }

        public Model3D prepFlowing(@NotNull FluidStack fluid) {
            TextureAtlasSprite still = getFluidTexture(fluid, FluidTextureType.STILL);
            TextureAtlasSprite flowing = getFluidTexture(fluid, FluidTextureType.FLOWING);
            return setTextures(still, still, flowing, flowing, flowing, flowing);
        }

        public Model3D setTexture(Direction side, @Nullable TextureAtlasSprite sprite) {
            textures[side.ordinal()] = sprite;
            return this;
        }

        public Model3D setTexture(TextureAtlasSprite tex) {
            Arrays.fill(textures, tex);
            return this;
        }

        public Model3D setTextures(TextureAtlasSprite down, TextureAtlasSprite up, TextureAtlasSprite north, TextureAtlasSprite south, TextureAtlasSprite west,
                                   TextureAtlasSprite east) {
            textures[0] = down;
            textures[1] = up;
            textures[2] = north;
            textures[3] = south;
            textures[4] = west;
            textures[5] = east;
            return this;
        }

        public interface ModelBoundsSetter {

            Model3D set(float min, float max);
        }
    }

    public static class LazyModel implements Supplier<Model3D> {

        private final Supplier<Model3D> supplier;
        @Nullable
        private Model3D model;

        public LazyModel(Supplier<Model3D> supplier) {
            this.supplier = supplier;
        }

        public void reset() {
            model = null;
        }

        @Override
        public Model3D get() {
            if (model == null) {
                model = supplier.get();
            }
            return model;
        }
    }
}
