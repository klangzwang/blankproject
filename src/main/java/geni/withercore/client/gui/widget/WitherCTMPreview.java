package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import geni.withercore.client.gui.animation.GuiAnimationHelper;
import geni.withercore.common.util.AthenaCtmHelper;
import geni.withercore.common.util.AthenaCtmHelper.FaceBasis;
import geni.withercore.common.util.AthenaCtmHelper.ShapePreset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Interactive 3D Athena Connected Texture (CTM) preview widget for Minecraft NeoForge GUI screens.
 * Features:
 * - Free 3D mouse rotation (yaw/pitch) with double-click reset
 * - Responsive viewport with depth buffer and scissor clipping
 * - WitherSlider controls for Width (X), Height (Y), and Depth (Z)
 * - Quick dimension preset buttons (1×1×1, 3×3×1, 5×1×1, 1×5×1, 3×3×3, 5×5×1)
 * - TestGeometry shape preset buttons (Box, Frame, L-Wall, Cross, Bunker, Pillars)
 * - Real-time Athena 5-tile quadrant connectivity evaluation with obscured-crease culling
 */
public class WitherCTMPreview extends AbstractWidget {

    public static final float DEFAULT_YAW = -35.0f;
    public static final float DEFAULT_PITCH = 25.0f;
    public static final float DEFAULT_ZOOM = 1.0f;

    // BlockState & Voxel Matrix
    private BlockState blockState;
    private int sizeX = 3;
    private int sizeY = 3;
    private int sizeZ = 1;
    private ShapePreset currentShape = ShapePreset.HOLLOW_FRAME;
    private boolean[][][] voxels;
    private int activeBlockCount = 0;
    private boolean respectObscuredCulling = true;
    private boolean showGridFloor = true;

    // 3D Camera State
    private float yaw = DEFAULT_YAW;
    private float pitch = DEFAULT_PITCH;
    private float zoom = DEFAULT_ZOOM;
    private boolean isDraggingViewport = false;
    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private long lastClickTime = 0;

    // Viewport & Sidebar Geometry
    private int vpX, vpY, vpW, vpH;
    private int sbX, sbY, sbW, sbH;

    // Embedded Sub-Widgets
    private WitherSlider sliderX;
    private WitherSlider sliderY;
    private WitherSlider sliderZ;

    private final List<WitherButton> dimPresetButtons = new ArrayList<>();
    private final List<WitherButton> shapeButtons = new ArrayList<>();
    private WitherButton resetCamBtn;
    private WitherButton toggleGridBtn;
    private WitherButton toggleCullBtn;

    public WitherCTMPreview(int x, int y, int width, int height) {
        this(x, y, width, height, Blocks.IRON_BLOCK.defaultBlockState());
    }

    public WitherCTMPreview(int x, int y, int width, int height, BlockState initialBlockState) {
        super(x, y, width, height, Component.literal("Athena CTM Preview"));
        this.blockState = initialBlockState != null ? initialBlockState : Blocks.IRON_BLOCK.defaultBlockState();
        rebuildVoxelMatrix();
        initSubWidgets();
        layoutSubWidgets();
    }

    /**
     * Updates the BlockState dynamically (e.g. from an inventory slot, cutter input, or machine recipe).
     */
    public void setBlockState(BlockState state) {
        this.blockState = state != null ? state : Blocks.IRON_BLOCK.defaultBlockState();
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    /**
     * Updates the preview from an ItemStack.
     */
    public void setItemStack(ItemStack stack) {
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
            setBlockState(blockItem.getBlock().defaultBlockState());
        }
    }

    public void setDimensions(int x, int y, int z) {
        this.sizeX = Math.max(1, Math.min(7, x));
        this.sizeY = Math.max(1, Math.min(7, y));
        this.sizeZ = Math.max(1, Math.min(5, z));
        rebuildVoxelMatrix();
    }

    public void setShapePreset(ShapePreset preset) {
        this.currentShape = preset;
        for (WitherButton btn : shapeButtons) {
            btn.setSelected(btn.getMessage().getString().equalsIgnoreCase(preset.getLabel()));
        }
        rebuildVoxelMatrix();
    }

    public void resetCamera() {
        this.yaw = DEFAULT_YAW;
        this.pitch = DEFAULT_PITCH;
        this.zoom = DEFAULT_ZOOM;
    }

    public void rebuildVoxelMatrix() {
        this.voxels = AthenaCtmHelper.buildVoxelMatrix(this.sizeX, this.sizeY, this.sizeZ, this.currentShape);
        int count = 0;
        for (int y = 0; y < sizeY; y++) {
            for (int z = 0; z < sizeZ; z++) {
                for (int x = 0; x < sizeX; x++) {
                    if (voxels[x][y][z]) count++;
                }
            }
        }
        this.activeBlockCount = count;
    }

    private void initSubWidgets() {
        // 1. Dimensions Sliders
        sliderX = new WitherSlider(0, 0, 80, 14, Component.literal("X:"), 1.0, 7.0, 1.0,
                () -> (double) sizeX, v -> { sizeX = v.intValue(); rebuildVoxelMatrix(); })
                .setValueFormatter(v -> Component.literal(String.valueOf(v.intValue())));

        sliderY = new WitherSlider(0, 0, 80, 14, Component.literal("Y:"), 1.0, 7.0, 1.0,
                () -> (double) sizeY, v -> { sizeY = v.intValue(); rebuildVoxelMatrix(); })
                .setValueFormatter(v -> Component.literal(String.valueOf(v.intValue())));

        sliderZ = new WitherSlider(0, 0, 80, 14, Component.literal("Z:"), 1.0, 5.0, 1.0,
                () -> (double) sizeZ, v -> { sizeZ = v.intValue(); rebuildVoxelMatrix(); })
                .setValueFormatter(v -> Component.literal(String.valueOf(v.intValue())));

        // 2. Quick Dimension Presets
        addDimPreset("1×1", 1, 1, 1);
        addDimPreset("3×3", 3, 3, 1);
        addDimPreset("5×1", 5, 1, 1);
        addDimPreset("1×5", 1, 5, 1);
        addDimPreset("3×3×3", 3, 3, 3);
        addDimPreset("5×5", 5, 5, 1);

        // 3. Shape Presets
        for (ShapePreset preset : ShapePreset.values()) {
            WitherButton btn = new WitherButton(0, 0, 32, 13, Component.literal(preset.getLabel()), WitherButton.Variant.SECONDARY, b -> {
                setShapePreset(preset);
            });
            btn.setSelected(preset == this.currentShape);
            shapeButtons.add(btn);
        }

        // 4. Viewport Utility Buttons
        resetCamBtn = new WitherButton(0, 0, 38, 12, Component.literal("Reset"), WitherButton.Variant.GHOST, b -> resetCamera());
        toggleGridBtn = new WitherButton(0, 0, 34, 12, Component.literal("Grid"), WitherButton.Variant.GHOST, b -> {
            this.showGridFloor = !this.showGridFloor;
            b.setSelected(this.showGridFloor);
        });
        toggleGridBtn.setSelected(this.showGridFloor);

        toggleCullBtn = new WitherButton(0, 0, 34, 12, Component.literal("Cull"), WitherButton.Variant.GHOST, b -> {
            this.respectObscuredCulling = !this.respectObscuredCulling;
            b.setSelected(this.respectObscuredCulling);
        });
        toggleCullBtn.setSelected(this.respectObscuredCulling);
    }

    private void addDimPreset(String label, int x, int y, int z) {
        WitherButton btn = new WitherButton(0, 0, 32, 13, Component.literal(label), WitherButton.Variant.SECONDARY, b -> {
            setDimensions(x, y, z);
            for (WitherButton pBtn : dimPresetButtons) {
                pBtn.setSelected(pBtn == b);
            }
        });
        if (x == sizeX && y == sizeY && z == sizeZ) {
            btn.setSelected(true);
        }
        dimPresetButtons.add(btn);
    }

    private void layoutSubWidgets() {
        int x0 = getX();
        int y0 = getY();
        int totalW = getWidth();
        int totalH = getHeight();

        // Viewport on the left, Sidebar on the right
        int sidebarTargetWidth = Math.min(108, Math.max(90, totalW / 3 + 10));
        this.sbW = sidebarTargetWidth;
        this.vpW = totalW - sbW - 9;
        this.vpH = totalH - 6;

        this.vpX = x0 + 3;
        this.vpY = y0 + 3;

        this.sbX = x0 + vpW + 6;
        this.sbY = y0 + 3;
        this.sbH = vpH;

        // Sliders layout
        int curY = sbY + 12;
        sliderX.setPosition(sbX + 2, curY);
        curY += 21;
        sliderY.setPosition(sbX + 2, curY);
        curY += 21;
        sliderZ.setPosition(sbX + 2, curY);
        curY += 26;

        // Dimension presets (2 rows of 3 buttons)
        curY += 10; // After "Presets" header
        int btnW = (sbW - 8) / 3;
        for (int i = 0; i < dimPresetButtons.size(); i++) {
            int col = i % 3;
            int row = i / 3;
            dimPresetButtons.get(i).setPosition(sbX + 2 + col * (btnW + 2), curY + row * 15);
            dimPresetButtons.get(i).setWidth(btnW);
        }
        curY += 34;

        // Shape presets (2 rows of 3 buttons)
        curY += 10; // After "Shape" header
        for (int i = 0; i < shapeButtons.size(); i++) {
            int col = i % 3;
            int row = i / 3;
            shapeButtons.get(i).setPosition(sbX + 2 + col * (btnW + 2), curY + row * 15);
            shapeButtons.get(i).setWidth(btnW);
        }

        // Viewport bottom toolbar
        resetCamBtn.setPosition(vpX + 4, vpY + vpH - 15);
        toggleGridBtn.setPosition(vpX + 44, vpY + vpH - 15);
        toggleCullBtn.setPosition(vpX + 80, vpY + vpH - 15);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        layoutSubWidgets();
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        layoutSubWidgets();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        layoutSubWidgets();
    }

    @Override
    public void modularRender(GuiGraphics gg, GuiRender render, float partialTicks, int mouseX, int mouseY) {
        int x0 = getX();
        int y0 = getY();
        int w = getWidth();
        int h = getHeight();

        // 1. Outer Container Panel Background - Industrial Dark Slate
        gg.fill(x0, y0, x0 + w, y0 + h, 0xFF121417);
        // Bevel highlights
        gg.fill(x0 + 1, y0 + 1, x0 + w - 1, y0 + 2, 0xFF353C47);
        gg.fill(x0 + 1, y0 + 1, x0 + 2, y0 + h - 1, 0xFF353C47);
        // Bevel shadows
        gg.fill(x0 + 1, y0 + h - 2, x0 + w - 1, y0 + h - 1, 0xFF0B0D0F);
        gg.fill(x0 + w - 2, y0 + 1, x0 + w - 1, y0 + h - 1, 0xFF0B0D0F);
        // Body
        gg.fill(x0 + 2, y0 + 2, x0 + w - 2, y0 + h - 2, 0xFF181B20);

        // 2. Viewport Well Background
        gg.fill(vpX, vpY, vpX + vpW, vpY + vpH, 0xFF0A0C0E);
        gg.renderOutline(vpX, vpY, vpW, vpH, 0xFF282E37);

        // Subtle accent line above viewport
        gg.fill(vpX + 2, vpY + 1, vpX + 38, vpY + 2, 0xFFD49B24);

        // 3. Render 3D CTM Mesh inside Viewport with Scissor and Depth Test
        gg.enableScissor(vpX + 1, vpY + 1, vpX + vpW - 1, vpY + vpH - 1);
        render3DScene(gg);
        gg.disableScissor();

        // 4. Viewport Overlays & Telemetry (HUD)
        renderViewportHud(gg, render);

        // 5. Sidebar Separator & Headers
        gg.fill(sbX - 3, sbY + 2, sbX - 2, sbY + sbH - 2, 0xFF2B313B);

        Font font = render.font();
        gg.drawString(font, "DIMENSIONS", sbX + 2, sbY + 3, 0xFFD49B24, false);
        sliderX.render(gg, mouseX, mouseY, partialTicks);
        sliderY.render(gg, mouseX, mouseY, partialTicks);
        sliderZ.render(gg, mouseX, mouseY, partialTicks);

        gg.drawString(font, "PRESETS", sbX + 2, sbY + 80, 0xFF94A3B8, false);
        for (WitherButton btn : dimPresetButtons) {
            btn.render(gg, mouseX, mouseY, partialTicks);
        }

        gg.drawString(font, "SHAPE", sbX + 2, sbY + 124, 0xFF94A3B8, false);
        for (WitherButton btn : shapeButtons) {
            btn.render(gg, mouseX, mouseY, partialTicks);
        }

        // Viewport Toolbar Buttons
        resetCamBtn.render(gg, mouseX, mouseY, partialTicks);
        toggleGridBtn.render(gg, mouseX, mouseY, partialTicks);
        toggleCullBtn.render(gg, mouseX, mouseY, partialTicks);
    }

    private void renderViewportHud(GuiGraphics gg, GuiRender render) {
        Font font = render.font();

        // Block Title / State Name (top-left)
        String blockName = blockState != null ? blockState.getBlock().getName().getString() : "Unknown Block";
        if (font.width(blockName) > vpW - 14) {
            blockName = font.plainSubstrByWidth(blockName, vpW - 20) + "...";
        }
        gg.drawString(font, blockName, vpX + 6, vpY + 5, 0xFFFFFFFF, true);

        // Dimension & Voxel Count Tag (top-right)
        String dimTag = sizeX + "×" + sizeY + "×" + sizeZ + " (" + activeBlockCount + " blk)";
        int tagW = font.width(dimTag);
        gg.drawString(font, dimTag, vpX + vpW - tagW - 6, vpY + 5, 0xFF38BDF8, true);

        // Shape preset label (below block name)
        gg.drawString(font, "Shape: " + currentShape.getDescription(), vpX + 6, vpY + 16, 0xFF64748B, false);

        // Interaction hint (visible when hovered)
        if (isInsideViewport(render.mc().mouseHandler.xpos(), render.mc().mouseHandler.ypos())) {
            gg.drawCenteredString(font, "Drag mouse to rotate", vpX + vpW / 2, vpY + vpH - 26, 0x99A1A1AA);
        }
    }

    private void render3DScene(GuiGraphics gg) {
        RenderSystem.enableDepthTest();

        PoseStack pose = gg.pose();
        pose.pushPose();

        // 1. Center in viewport
        float centerX = vpX + vpW / 2.0f;
        float centerY = vpY + vpH / 2.0f;
        pose.translate(centerX, centerY, 150.0f);

        // 2. Dynamic scale calculation to fit structure within viewport bounds
        int maxDim = Math.max(sizeX, Math.max(sizeY, sizeZ));
        float baseScale = Math.min(vpW, vpH) / (float) (maxDim * 1.55f + 0.4f);
        float scale = baseScale * zoom;
        pose.scale(scale, -scale, scale);

        // 3. User camera rotation
        pose.mulPose(Axis.XP.rotationDegrees(pitch));
        pose.mulPose(Axis.YP.rotationDegrees(yaw));

        // 4. Center voxel formation origin at (0, 0, 0)
        pose.translate(-sizeX / 2.0f, -sizeY / 2.0f, -sizeZ / 2.0f);

        // 5. Optional Floor Grid
        if (showGridFloor) {
            renderGridFloor(pose, gg);
        }

        // 6. Lighting setup & CTM Voxel Rendering
        Lighting.setupFor3DItems();
        renderCtmBlockFaces(pose);
        Lighting.setupForFlatItems();

        pose.popPose();
        RenderSystem.disableDepthTest();
    }

    private void renderGridFloor(PoseStack pose, GuiGraphics gg) {
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.lines());
        Matrix4f mat = pose.last().pose();

        float yBase = -0.01f;
        int gridPad = 1;
        float minX = -gridPad;
        float maxX = sizeX + gridPad;
        float minZ = -gridPad;
        float maxZ = sizeZ + gridPad;

        int gridR = 40;
        int gridG = 48;
        int gridB = 60;
        int gridA = 120;

        // X-parallel lines
        for (int z = -gridPad; z <= sizeZ + gridPad; z++) {
            lineConsumer.addVertex(mat, minX, yBase, z).setColor(gridR, gridG, gridB, gridA).setNormal(pose.last(), 0, 1, 0);
            lineConsumer.addVertex(mat, maxX, yBase, z).setColor(gridR, gridG, gridB, gridA).setNormal(pose.last(), 0, 1, 0);
        }
        // Z-parallel lines
        for (int x = -gridPad; x <= sizeX + gridPad; x++) {
            lineConsumer.addVertex(mat, x, yBase, minZ).setColor(gridR, gridG, gridB, gridA).setNormal(pose.last(), 0, 1, 0);
            lineConsumer.addVertex(mat, x, yBase, maxZ).setColor(gridR, gridG, gridB, gridA).setNormal(pose.last(), 0, 1, 0);
        }

        bufferSource.endBatch(RenderType.lines());
    }

    private void renderCtmBlockFaces(PoseStack pose) {
        if (blockState == null || voxels == null) return;

        Minecraft mc = Minecraft.getInstance();
        BakedModel model = mc.getBlockRenderer().getBlockModel(blockState);

        // Verwende das erste verfügbare Sprite oder die Blocktextur direkt statt nur das Partikel-Icon
        TextureAtlasSprite sprite = model.getParticleIcon();
        if (sprite == null) {
            sprite = mc.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS)
                    .getSprite(net.minecraft.client.resources.model.ModelBakery.MISSING_MODEL_LOCATION);
        }

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.solid());
        Matrix4f mat = pose.last().pose();

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        // Da deine Texturen 32x32 sind und du CTM Full (keinen 5er-Streifen) nutzt:
        // Wir erzwingen hier die Logik für Full-CTM (z.B. ein 2x2 oder 4x4 Sub-Raster auf der 32x32 Textur)
        boolean isFullCtm = true;

        // Beispiel für ein CTM-Full Raster (z.B. 2x2 Unterteilung pro Textur oder Full-Sheet)
        float tileDeltaU = (u1 - u0) / 2.0f; // Bei einem 2x2 Full-Raster
        float tileDeltaV = (v1 - v0) / 2.0f;

        // Iteration durch alle aktiven Voxel...
        for (int y = 0; y < sizeY; y++) {
            for (int z = 0; z < sizeZ; z++) {
                for (int x = 0; x < sizeX; x++) {
                    if (!voxels[x][y][z]) continue;

                    for (FaceBasis face : AthenaCtmHelper.FACES) {
                        int adjX = x + face.nx;
                        int adjY = y + face.ny;
                        int adjZ = z + face.nz;
                        if (adjX >= 0 && adjX < sizeX && adjY >= 0 && adjY < sizeY && adjZ >= 0 && adjZ < sizeZ) {
                            if (voxels[adjX][adjY][adjZ]) {
                                continue; // Nachbar verdeckt Fläche -> cullen
                            }
                        }

                        renderExposedFaceFullCtm(mat, pose, consumer, x, y, z, face, u0, v0, tileDeltaU, tileDeltaV);
                    }
                }
            }
        }

        bufferSource.endBatch(RenderType.solid());
    }

    private void renderExposedFaceFullCtm(Matrix4f mat, PoseStack pose, VertexConsumer consumer,
                                          int x, int y, int z, FaceBasis face,
                                          float baseU0, float baseV0, float tileDeltaU, float tileDeltaV) {
        float cx = x + 0.5f + face.nx * 0.5f;
        float cy = y + 0.5f + face.ny * 0.5f;
        float cz = z + 0.5f + face.nz * 0.5f;

        float hrX = face.rx * 0.25f;
        float hrY = face.ry * 0.25f;
        float hrZ = face.rz * 0.25f;

        float huX = face.ux * 0.25f;
        float huY = face.uy * 0.25f;
        float huZ = face.uz * 0.25f;

        float shade = switch (face.direction) {
            case UP -> 1.0f;
            case DOWN -> 0.5f;
            case NORTH, SOUTH -> 0.8f;
            case EAST, WEST -> 0.65f;
        };
        int rgb = (int) (255 * shade);

        // Nachbarschaftsabfrage für Full-CTM Quadranten (TL, TR, BL, BR)
        boolean hTL = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, -face.rx, -face.ry, -face.rz, face.nx, face.ny, face.nz, respectObscuredCulling);
        boolean vTL = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, face.ux, face.uy, face.uz, face.nx, face.ny, face.nz, respectObscuredCulling);
        int roleTL = AthenaCtmHelper.resolveQuadrantRole(hTL, vTL, false);

        // Hier werden die UVs für die 4 Quadranten basierend auf der 32x32 Full-CTM Textur zugewiesen
        // (Beispielhafte Indizierung 0 bis 3 im 2x2 Raster der 32x32 Textur)
        renderQuadrantQuadFull(mat, pose, consumer, cx - hrX, cy - hrY, cz - hrZ, hrX, hrY, hrZ, huX, huY, huZ,
                roleTL, baseU0, baseV0, tileDeltaU, tileDeltaV, rgb, face);
    }

    private void renderQuadrantQuadFull(Matrix4f mat, PoseStack pose, VertexConsumer consumer,
                                        float qcx, float qcy, float qcz,
                                        float hx, float hy, float hz,
                                        float vx, float vy, float vz,
                                        int role, float baseU0, float baseV0,
                                        float tileDeltaU, float tileDeltaV, int rgb, FaceBasis face) {

        // Da es sich um eine 32x32 Full-CTM Textur handelt, unterteilen wir die UVs
        // basierend auf der ermittelten Quadranten-Rolle (role) in ein 2x2 Raster (oder entsprechendes CTM-Mapping).

        float subU0 = (role % 2) * tileDeltaU;
        float subU1 = subU0 + tileDeltaU;
        float subV0 = (role / 2) * tileDeltaV;
        float subV1 = subV0 + tileDeltaV;

        float finalU0 = baseU0 + subU0;
        float finalU1 = baseU0 + subU1;
        float finalV0 = baseV0 + subV0;
        float finalV1 = baseV0 + subV1;

        int r = rgb;
        int g = rgb;
        int b = rgb;

        // 4 Vertices des Quadranten-Quads
        float p1x = qcx - hx - vx;
        float p1y = qcy - hy - vy;
        float p1z = qcz - hz - vz;

        float p2x = qcx + hx - vx;
        float p2y = qcy + hy - vy;
        float p2z = qcz + hz - vz;

        float p3x = qcx + hx + vx;
        float p3y = qcy + hy + vy;
        float p3z = qcz + hz + vz;

        float p4x = qcx - hx + vx;
        float p4y = qcy - hy + vy;
        float p4z = qcz - hz + vz;

        int light = 0xF000F0;

        consumer.addVertex(mat, p1x, p1y, p1z).setColor(r, g, b, 255).setUv(finalU0, finalV1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(), face.nx, face.ny, face.nz);
        consumer.addVertex(mat, p2x, p2y, p2z).setColor(r, g, b, 255).setUv(finalU1, finalV1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(), face.nx, face.ny, face.nz);
        consumer.addVertex(mat, p3x, p3y, p3z).setColor(r, g, b, 255).setUv(finalU1, finalV0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(), face.nx, face.ny, face.nz);
        consumer.addVertex(mat, p4x, p4y, p4z).setColor(r, g, b, 255).setUv(finalU0, finalV0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(), face.nx, face.ny, face.nz);
    }

    private void renderExposedFace(Matrix4f mat, PoseStack pose, VertexConsumer consumer,
                                   int x, int y, int z, FaceBasis face,
                                   float u0, float v0, float tileDeltaU, float tileDeltaV, boolean isStrip) {
        // Face center
        float cx = x + 0.5f + face.nx * 0.5f;
        float cy = y + 0.5f + face.ny * 0.5f;
        float cz = z + 0.5f + face.nz * 0.5f;

        // Tangent vectors scaled to half-block for quadrant positioning
        float hrX = face.rx * 0.25f;
        float hrY = face.ry * 0.25f;
        float hrZ = face.rz * 0.25f;

        float huX = face.ux * 0.25f;
        float huY = face.uy * 0.25f;
        float huZ = face.uz * 0.25f;

        // Normal shading factors (diffuse simulation)
        float shade = switch (face.direction) {
            case UP -> 1.0f;
            case DOWN -> 0.5f;
            case NORTH, SOUTH -> 0.8f;
            case EAST, WEST -> 0.65f;
        };
        int rgb = (int) (255 * shade);

        // 4 Quadrants: Top-Left (TL), Top-Right (TR), Bottom-Left (BL), Bottom-Right (BR)
        // 1. Top-Left Quadrant
        boolean hTL = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, -face.rx, -face.ry, -face.rz, face.nx, face.ny, face.nz, respectObscuredCulling);
        boolean vTL = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, face.ux, face.uy, face.uz, face.nx, face.ny, face.nz, respectObscuredCulling);
        boolean dTL = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, -face.rx + face.ux, -face.ry + face.uy, -face.rz + face.uz, face.nx, face.ny, face.nz, respectObscuredCulling);
        int roleTL = AthenaCtmHelper.resolveQuadrantRole(hTL, vTL, dTL);
        renderQuadrantQuad(mat, pose, consumer, cx - hrX, cy - hrY, cz - hrZ, hrX, hrY, hrZ, huX, huY, huZ,
                roleTL, 0.0f, 0.5f, 0.0f, 0.5f, u0, v0, tileDeltaU, tileDeltaV, isStrip, rgb, face);

        // 2. Top-Right Quadrant
        boolean hTR = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, face.rx, face.ry, face.rz, face.nx, face.ny, face.nz, respectObscuredCulling);
        boolean vTR = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, face.ux, face.uy, face.uz, face.nx, face.ny, face.nz, respectObscuredCulling);
        boolean dTR = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, face.rx + face.ux, face.ry + face.uy, face.rz + face.uz, face.nx, face.ny, face.nz, respectObscuredCulling);
        int roleTR = AthenaCtmHelper.resolveQuadrantRole(hTR, vTR, dTR);
        renderQuadrantQuad(mat, pose, consumer, cx + hrX, cy + hrY, cz + hrZ, hrX, hrY, hrZ, huX, huY, huZ,
                roleTR, 0.5f, 1.0f, 0.0f, 0.5f, u0, v0, tileDeltaU, tileDeltaV, isStrip, rgb, face);

        // 3. Bottom-Left Quadrant
        boolean hBL = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, -face.rx, -face.ry, -face.rz, face.nx, face.ny, face.nz, respectObscuredCulling);
        boolean vBL = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, -face.ux, -face.uy, -face.uz, face.nx, face.ny, face.nz, respectObscuredCulling);
        boolean dBL = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, -face.rx - face.ux, -face.ry - face.uy, -face.rz - face.uz, face.nx, face.ny, face.nz, respectObscuredCulling);
        int roleBL = AthenaCtmHelper.resolveQuadrantRole(hBL, vBL, dBL);
        renderQuadrantQuad(mat, pose, consumer, cx - hrX, cy - hrY, cz - hrZ, hrX, hrY, hrZ, -huX, -huY, -huZ,
                roleBL, 0.0f, 0.5f, 0.5f, 1.0f, u0, v0, tileDeltaU, tileDeltaV, isStrip, rgb, face);

        // 4. Bottom-Right Quadrant
        boolean hBR = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, face.rx, face.ry, face.rz, face.nx, face.ny, face.nz, respectObscuredCulling);
        boolean vBR = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, -face.ux, -face.uy, -face.uz, face.nx, face.ny, face.nz, respectObscuredCulling);
        boolean dBR = AthenaCtmHelper.isConnected(voxels, sizeX, sizeY, sizeZ, x, y, z, face.rx - face.ux, face.ry - face.uy, face.rz - face.uz, face.nx, face.ny, face.nz, respectObscuredCulling);
        int roleBR = AthenaCtmHelper.resolveQuadrantRole(hBR, vBR, dBR);
        renderQuadrantQuad(mat, pose, consumer, cx + hrX, cy + hrY, cz + hrZ, hrX, hrY, hrZ, -huX, -huY, -huZ,
                roleBR, 0.5f, 1.0f, 0.5f, 1.0f, u0, v0, tileDeltaU, tileDeltaV, isStrip, rgb, face);
    }

    private void renderQuadrantQuad(Matrix4f mat, PoseStack pose, VertexConsumer consumer,
                                    float qcx, float qcy, float qcz,
                                    float hx, float hy, float hz,
                                    float vx, float vy, float vz,
                                    int role, float subU0, float subU1, float subV0, float subV1,
                                    float baseU0, float baseV0, float tileDeltaU, float tileDeltaV,
                                    boolean isStrip, int rgb, FaceBasis face) {
        // Calculate texture UV for this quadrant
        float roleOffsetU = isStrip ? (role * tileDeltaU) : 0.0f;
        float finalU0 = baseU0 + roleOffsetU + subU0 * tileDeltaU;
        float finalU1 = baseU0 + roleOffsetU + subU1 * tileDeltaU;
        float finalV0 = baseV0 + subV0 * tileDeltaV;
        float finalV1 = baseV0 + subV1 * tileDeltaV;

        // Visual subtle tinting for role identification on standard single textures
        int r = rgb;
        int g = rgb;
        int b = rgb;
        if (!isStrip && role != AthenaCtmHelper.ROLE_EMPTY) {
            // Emphasize edges and outer corners
            if (role == AthenaCtmHelper.ROLE_PARTICLE) {
                r = (int) (r * 0.90f);
                g = (int) (g * 0.90f);
            } else if (role == AthenaCtmHelper.ROLE_CENTER) {
                b = (int) (b * 0.85f);
            }
        }

        // 4 Vertices of the quadrant quad
        // v1: Bottom-Left
        float p1x = qcx - hx - vx;
        float p1y = qcy - hy - vy;
        float p1z = qcz - hz - vz;

        // v2: Bottom-Right
        float p2x = qcx + hx - vx;
        float p2y = qcy + hy - vy;
        float p2z = qcz + hz - vz;

        // v3: Top-Right
        float p3x = qcx + hx + vx;
        float p3y = qcy + hy + vy;
        float p3z = qcz + hz + vz;

        // v4: Top-Left
        float p4x = qcx - hx + vx;
        float p4y = qcy - hy + vy;
        float p4z = qcz - hz + vz;

        int light = 0xF000F0;

        consumer.addVertex(mat, p1x, p1y, p1z).setColor(r, g, b, 255).setUv(finalU0, finalV1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(), face.nx, face.ny, face.nz);
        consumer.addVertex(mat, p2x, p2y, p2z).setColor(r, g, b, 255).setUv(finalU1, finalV1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(), face.nx, face.ny, face.nz);
        consumer.addVertex(mat, p3x, p3y, p3z).setColor(r, g, b, 255).setUv(finalU1, finalV0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(), face.nx, face.ny, face.nz);
        consumer.addVertex(mat, p4x, p4y, p4z).setColor(r, g, b, 255).setUv(finalU0, finalV0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose.last(), face.nx, face.ny, face.nz);
    }

    private boolean isInsideViewport(double mouseX, double mouseY) {
        return mouseX >= vpX && mouseX < vpX + vpW && mouseY >= vpY && mouseY < vpY + vpH;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible) return false;

        // 1. Check sidebar sliders
        if (sliderX.mouseClicked(mouseX, mouseY, button)) return true;
        if (sliderY.mouseClicked(mouseX, mouseY, button)) return true;
        if (sliderZ.mouseClicked(mouseX, mouseY, button)) return true;

        // 2. Check preset and shape buttons
        for (WitherButton btn : dimPresetButtons) {
            if (btn.mouseClicked(mouseX, mouseY, button)) return true;
        }
        for (WitherButton btn : shapeButtons) {
            if (btn.mouseClicked(mouseX, mouseY, button)) return true;
        }

        // 3. Check viewport toolbar buttons
        if (resetCamBtn.mouseClicked(mouseX, mouseY, button)) return true;
        if (toggleGridBtn.mouseClicked(mouseX, mouseY, button)) return true;
        if (toggleCullBtn.mouseClicked(mouseX, mouseY, button)) return true;

        // 4. Viewport Mouse Interaction
        if (isInsideViewport(mouseX, mouseY)) {
            if (button == 0) { // Left-click: start rotation drag or double-click reset
                long now = System.currentTimeMillis();
                if (now - lastClickTime < 280) {
                    resetCamera();
                }
                lastClickTime = now;
                isDraggingViewport = true;
                lastMouseX = mouseX;
                lastMouseY = mouseY;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!this.active || !this.visible) return false;

        if (sliderX.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        if (sliderY.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        if (sliderZ.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;

        if (isDraggingViewport && button == 0) {
            double deltaX = mouseX - lastMouseX;
            double deltaY = mouseY - lastMouseY;
            lastMouseX = mouseX;
            lastMouseY = mouseY;

            // Horizontal rotation (natural feel)
            this.yaw += (float) (deltaX * 0.85);

            // Vertical pitch (clamped to prevent awkward camera flip)
            this.pitch = GuiAnimationHelper.clamp(this.pitch + (float) (deltaY * 0.85), -65.0f, 65.0f);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isDraggingViewport = false;
        sliderX.mouseReleased(mouseX, mouseY, button);
        sliderY.mouseReleased(mouseX, mouseY, button);
        sliderZ.mouseReleased(mouseX, mouseY, button);
        for (WitherButton btn : dimPresetButtons) {
            btn.mouseReleased(mouseX, mouseY, button);
        }
        for (WitherButton btn : shapeButtons) {
            btn.mouseReleased(mouseX, mouseY, button);
        }
        resetCamBtn.mouseReleased(mouseX, mouseY, button);
        toggleGridBtn.mouseReleased(mouseX, mouseY, button);
        toggleCullBtn.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isInsideViewport(mouseX, mouseY)) {
            this.zoom = GuiAnimationHelper.clamp(this.zoom + (float) (scrollY * 0.12), 0.4f, 3.0f);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
