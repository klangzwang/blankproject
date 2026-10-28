package geni.witherdata.generator.template;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.*;

public class BlockStateTemplateHelper {

    private final BlockStateProvider provider;

    public BlockStateTemplateHelper(BlockStateProvider provider) {
        this.provider = provider;
    }

    // =========================================================================
    // TEMPLATE 1: Voller 3D Rahmen (Dynamisch NxNxN)
    // =========================================================================
    public void generateCubeFrame(Block block, ModelFile center, ModelFile edgeHoriz, ModelFile edgeVert, ModelFile corner) {
        MultiPartBlockStateBuilder builder = provider.getMultipartBuilder(block);
        
        // Center
        builder.part().modelFile(center).addModel().end();

        // Kanten (Horizontale & Vertikale) - Helferfunktion siehe unten
        addEdge(builder, edgeHoriz, Direction.DOWN, Direction.NORTH, 0, 0);
        addEdge(builder, edgeHoriz, Direction.DOWN, Direction.EAST, 0, 90);
        addEdge(builder, edgeHoriz, Direction.DOWN, Direction.SOUTH, 0, 180);
        addEdge(builder, edgeHoriz, Direction.DOWN, Direction.WEST, 0, 270);
        addEdge(builder, edgeHoriz, Direction.UP, Direction.SOUTH, 180, 0);
        addEdge(builder, edgeHoriz, Direction.UP, Direction.WEST, 180, 90);
        addEdge(builder, edgeHoriz, Direction.UP, Direction.NORTH, 180, 180);
        addEdge(builder, edgeHoriz, Direction.UP, Direction.EAST, 180, 270);

        addEdge(builder, edgeVert, Direction.NORTH, Direction.WEST, 0, 0);
        addEdge(builder, edgeVert, Direction.NORTH, Direction.EAST, 0, 90);
        addEdge(builder, edgeVert, Direction.SOUTH, Direction.EAST, 0, 180);
        addEdge(builder, edgeVert, Direction.SOUTH, Direction.WEST, 0, 270);

        // Ecken
        addCorner(builder, corner, Direction.DOWN, Direction.NORTH, Direction.WEST, 0, 0);
        addCorner(builder, corner, Direction.DOWN, Direction.NORTH, Direction.EAST, 0, 90);
        addCorner(builder, corner, Direction.DOWN, Direction.SOUTH, Direction.EAST, 0, 180);
        addCorner(builder, corner, Direction.DOWN, Direction.SOUTH, Direction.WEST, 0, 270);
        addCorner(builder, corner, Direction.UP, Direction.SOUTH, Direction.WEST, 180, 0);
        addCorner(builder, corner, Direction.UP, Direction.SOUTH, Direction.EAST, 180, 90);
        addCorner(builder, corner, Direction.UP, Direction.NORTH, Direction.EAST, 180, 180);
        addCorner(builder, corner, Direction.UP, Direction.NORTH, Direction.WEST, 180, 270);
    }

    // =========================================================================
    // TEMPLATE 2: 2D Horizontaler Rahmen (Böden / Plattformen)
    // Verbindet sich nur nach Nord, Ost, Süd, West. (Kein Oben/Unten)
    // =========================================================================
    public void generateHorizontalPlane(Block block, ModelFile center, ModelFile edge, ModelFile corner) {
        MultiPartBlockStateBuilder builder = provider.getMultipartBuilder(block);
        
        builder.part().modelFile(center).addModel().end();

        // 4 Kanten
        builder.part().modelFile(edge).rotationY(0).addModel().condition(BlockStateProperties.NORTH, false).end();
        builder.part().modelFile(edge).rotationY(90).addModel().condition(BlockStateProperties.EAST, false).end();
        builder.part().modelFile(edge).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, false).end();
        builder.part().modelFile(edge).rotationY(270).addModel().condition(BlockStateProperties.WEST, false).end();

        // 4 Ecken
        builder.part().modelFile(corner).rotationY(0).addModel().condition(BlockStateProperties.NORTH, false).condition(BlockStateProperties.WEST, false).end();
        builder.part().modelFile(corner).rotationY(90).addModel().condition(BlockStateProperties.NORTH, false).condition(BlockStateProperties.EAST, false).end();
        builder.part().modelFile(corner).rotationY(180).addModel().condition(BlockStateProperties.SOUTH, false).condition(BlockStateProperties.EAST, false).end();
        builder.part().modelFile(corner).rotationY(270).addModel().condition(BlockStateProperties.SOUTH, false).condition(BlockStateProperties.WEST, false).end();
    }

    // =========================================================================
    // TEMPLATE 3: 2D Vertikaler Rahmen (KORRIGIERT OHNE Z-ROTATION)
    // =========================================================================
    public void generateVerticalPlane(Block block, Direction.Axis axis, ModelFile center, ModelFile edgeHoriz, ModelFile edgeVert, ModelFile corner) {
        MultiPartBlockStateBuilder builder = provider.getMultipartBuilder(block);
        builder.part().modelFile(center).addModel().end();

        Direction left = (axis == Direction.Axis.X) ? Direction.WEST : Direction.SOUTH;
        Direction right = (axis == Direction.Axis.X) ? Direction.EAST : Direction.NORTH;
        int rotY = (axis == Direction.Axis.X) ? 0 : 90; // Dreht die gesamte Wand für die Z-Achse
        int rotYFlipped = (rotY + 180) % 360; // Spiegelt die linke Seite auf die rechte Seite

        // 4 Kanten (Oben, Unten, Links, Rechts)
        // Oben
        builder.part().modelFile(edgeHoriz).rotationY(rotY).addModel().condition(BlockStateProperties.UP, false).end();
        // Unten (X=180 spiegelt Oben nach Unten)
        builder.part().modelFile(edgeHoriz).rotationX(180).rotationY(rotY).addModel().condition(BlockStateProperties.DOWN, false).end();
        // Links
        builder.part().modelFile(edgeVert).rotationY(rotY).addModel().condition(getDirectionProperty(left), false).end();
        // Rechts (Y=180 spiegelt Links nach Rechts)
        builder.part().modelFile(edgeVert).rotationY(rotYFlipped).addModel().condition(getDirectionProperty(right), false).end();

        // 4 Ecken
        // Oben Links
        builder.part().modelFile(corner).rotationY(rotY).addModel()
                .condition(BlockStateProperties.UP, false).condition(getDirectionProperty(left), false).end();
        // Oben Rechts
        builder.part().modelFile(corner).rotationY(rotYFlipped).addModel()
                .condition(BlockStateProperties.UP, false).condition(getDirectionProperty(right), false).end();
        // Unten Links
        builder.part().modelFile(corner).rotationX(180).rotationY(rotY).addModel()
                .condition(BlockStateProperties.DOWN, false).condition(getDirectionProperty(left), false).end();
        // Unten Rechts
        builder.part().modelFile(corner).rotationX(180).rotationY(rotYFlipped).addModel()
                .condition(BlockStateProperties.DOWN, false).condition(getDirectionProperty(right), false).end();
    }

    // =========================================================================
    // INTERNE HELFER FÜR MULTIPART
    // =========================================================================
    private BooleanProperty getDirectionProperty(Direction direction) {
        return switch (direction) {
            case NORTH -> BlockStateProperties.NORTH; case EAST -> BlockStateProperties.EAST; case SOUTH -> BlockStateProperties.SOUTH;
            case WEST -> BlockStateProperties.WEST; case UP -> BlockStateProperties.UP; case DOWN -> BlockStateProperties.DOWN;
        };
    }

    private void addEdge(MultiPartBlockStateBuilder builder, ModelFile model, Direction d1, Direction d2, int rotX, int rotY) {
        builder.part().modelFile(model).rotationX(rotX).rotationY(rotY).addModel()
                .condition(getDirectionProperty(d1), false) // Fallback oder eigene Property
                .condition(getDirectionProperty(d2), false)
                .end();
    }

    private void addCorner(MultiPartBlockStateBuilder builder, ModelFile model, Direction d1, Direction d2, Direction d3, int rotX, int rotY) {
        builder.part().modelFile(model).rotationX(rotX).rotationY(rotY).addModel()
                .condition(getDirectionProperty(d1), false)
                .condition(getDirectionProperty(d2), false)
                .condition(getDirectionProperty(d3), false)
                .end();
    }

    // =========================================================================
    // TEMPLATE 4: Fester 2x2x2 Multiblock 
    // Nutzt 3 Booleans im Block: UP (oben/unten), NORTH (nord/süd), EAST (ost/west)
    // Braucht nur 1 Model (Ecke Unten-Nord-Ost), den Rest macht die Rotation!
    // =========================================================================
    public void generateFixed2x2x2(Block block, ModelFile cornerModel) {
        VariantBlockStateBuilder builder = provider.getVariantBuilder(block);

        // Geht alle 8 Positionen des 2x2x2 Würfels durch
        for (boolean isUp : new boolean[]{false, true}) {
            for (boolean isNorth : new boolean[]{false, true}) {
                for (boolean isEast : new boolean[]{false, true}) {
                    
                    int rotX = isUp ? 180 : 0; // Wenn es oben ist, kippen wir das Modell auf den Kopf
                    int rotY = 0;

                    // Y-Rotation basierend auf der Ecke (Nord-Ost = 0, Süd-Ost = 90, Süd-West = 180, Nord-West = 270)
                    if (isNorth && isEast) rotY = 0;
                    else if (!isNorth && isEast) rotY = 90;
                    else if (!isNorth && !isEast) rotY = 180;
                    else if (isNorth && !isEast) rotY = 270;

                    builder.partialState()
                        .with(BlockStateProperties.UP, isUp)
                        .with(BlockStateProperties.NORTH, isNorth)
                        .with(BlockStateProperties.EAST, isEast)
                        .addModels(new ConfiguredModel(cornerModel, rotX, rotY, false));
                }
            }
        }
    }

/*    // =========================================================================
    // CODE-GENERIERTE TEMPLATES (Keine externen JSONs nötig)
    // Übergeben generierte Modelle an die bestehenden Methoden
    // =========================================================================

    *//**
     * Erstellt einen kompletten 3D-Rahmen (NxNxN) aus einer einzigen Textur.
     *//*
    public void generateCubeFrame(Block block, ResourceLocation texture) {
        String name = block.builtInRegistryHolder().key().location().getPath();

        ModelFile center = provider.models().withExistingParent(name + "_center", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(2, 2, 2).to(14, 14, 14).face(f -> f.uvs(2, 2, 14, 14).texture("#all")).end();

        ModelFile edgeHoriz = provider.models().withExistingParent(name + "_edge_horiz", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(2, 0, 0).to(14, 2, 2).face(f -> f.uvs(2, 0, 14, 2).texture("#all")).end();

        ModelFile edgeVert = provider.models().withExistingParent(name + "_edge_vert", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(0, 2, 0).to(2, 14, 2).face(f -> f.uvs(0, 2, 2, 14).texture("#all")).end();

        ModelFile corner = provider.models().withExistingParent(name + "_corner", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(0, 0, 0).to(2, 2, 2).face(f -> f.uvs(0, 0, 2, 2).texture("#all")).end();

        // Aufruf der bestehenden Multipart-Funktion
        generateCubeFrame(block, center, edgeHoriz, edgeVert, corner);
    }

    *//**
     * Erstellt einen flachen, horizontalen 2D-Rahmen (z.B. für Laufstege / Böden).
     *//*
    public void generateHorizontalPlane(Block block, ResourceLocation texture) {
        String name = block.builtInRegistryHolder().key().location().getPath();

        // 2 Pixel dicke Bodenplatte
        ModelFile center = provider.models().withExistingParent(name + "_center", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(2, 0, 2).to(14, 2, 14).face(f -> f.uvs(2, 2, 14, 14).texture("#all")).end();

        ModelFile edge = provider.models().withExistingParent(name + "_edge", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(2, 0, 0).to(14, 2, 2).face(f -> f.uvs(2, 0, 14, 2).texture("#all")).end();

        ModelFile corner = provider.models().withExistingParent(name + "_corner", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(0, 0, 0).to(2, 2, 2).face(f -> f.uvs(0, 0, 2, 2).texture("#all")).end();

        generateHorizontalPlane(block, center, edge, corner);
    }

    *//**
     * Erstellt einen vertikalen Rahmen (z.B. für dicke Glasscheiben oder Portale).
     *//*
    public void generateVerticalPlane(Block block, Direction.Axis axis, ResourceLocation texture) {
        String name = block.builtInRegistryHolder().key().location().getPath() + "_" + axis.getName();

        // Zentriert auf der Z-Achse (von X=7 bis X=9, also 2 Pixel dick)
        ModelFile center = provider.models().withExistingParent(name + "_center", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(7, 2, 2).to(9, 14, 14).face(f -> f.uvs(2, 2, 14, 14).texture("#all")).end();

        // Obere Kante
        ModelFile edge = provider.models().withExistingParent(name + "_edge", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(7, 14, 2).to(9, 16, 14).face(f -> f.uvs(2, 0, 14, 2).texture("#all")).end();

        // Obere linke Ecke
        ModelFile corner = provider.models().withExistingParent(name + "_corner", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(7, 14, 0).to(9, 16, 2).face(f -> f.uvs(0, 0, 2, 2).texture("#all")).end();

        generateVerticalPlane(block, axis, center, edge, corner);
    }

    *//**
     * Erstellt die Geometrie für einen festen 2x2x2 Multiblock automatisch.
     *//*
    public void generateFixed2x2x2(Block block, ResourceLocation texture) {
        String name = block.builtInRegistryHolder().key().location().getPath();

        // Baut einen Standard 16x16x16 Cube, der dann durch die Hauptfunktion in die 8 Ecken gedreht wird
        ModelFile corner = provider.models().withExistingParent(name + "_corner", "block/cube_all")
                .texture("all", texture);

        generateFixed2x2x2(block, corner);
    }

    // =========================================================================
    // CODE-GENERIERTE TEMPLATES (MIT PARAMETERN FÜR SCALE & GLASS)
    // =========================================================================

    *//**
     * Standard-Aufruf (Fallback auf Default-Werte)
     *//*
    public void generateCubeFrame(Block block, ResourceLocation texture) {
        generateCubeFrame(block, texture, null, 1.0f, 1.0f, false);
    }

    *//**
     * Erweitert: Kompletten 3D-Rahmen (NxNxN) mit konfigurierbarer Dicke und Glas.
     * @param edgeThickness 1.0 = 2 Pixel dicke Kanten
     * @param cornerScale 1.0 = 2 Pixel große Ecken (kann z.B. dicker als edge sein)
     * @param glassTexture Kann null sein, wenn hasGlass false ist
     *//*
    public void generateCubeFrame(Block block, ResourceLocation texture, ResourceLocation glassTexture, float edgeThickness, float cornerScale, boolean hasGlass) {
        String name = block.builtInRegistryHolder().key().location().getPath();

        // 1.0f Scale entspricht 2 Pixeln (Minecraft Voxel-Maßstab)
        float t = edgeThickness * 2.0f;
        float c = cornerScale * 2.0f;

        // 1. Center / Glass Model
        var centerBuilder = provider.models().withExistingParent(name + "_center", "block/block")
                .texture("particle", texture);

        if (hasGlass) {
            centerBuilder.texture("glass", glassTexture != null ? glassTexture : texture);
            // Glasblock ist minimal kleiner (0.1 bis 15.9) um Z-Fighting mit dem Rahmen zu verhindern
            centerBuilder.element().from(0.1f, 0.1f, 0.1f).to(15.9f, 15.9f, 15.9f)
                    .face(f -> f.uvs(0, 0, 16, 16).texture("#glass")).end();
        } else {
            // Massiver Kern (füllt den Leerraum des Rahmens exakt aus)
            centerBuilder.texture("all", texture);
            centerBuilder.element().from(t, t, t).to(16f - t, 16f - t, 16f - t)
                    .face(f -> f.uvs(t, t, 16f - t, 16f - t).texture("#all")).end();
        }

        // 2. Horizontale Kante (Startet bei 'c' (Ecke) bis '16-c' um Überschneidungen zu meiden)
        ModelFile edgeHoriz = provider.models().withExistingParent(name + "_edge_horiz", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(c, 0, 0).to(16f - c, t, t)
                .face(f -> f.uvs(c, 0, 16f - c, t).texture("#all")).end();

        // 3. Vertikale Kante (Säule)
        ModelFile edgeVert = provider.models().withExistingParent(name + "_edge_vert", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(0, c, 0).to(t, 16f - c, t)
                .face(f -> f.uvs(0, c, t, 16f - c).texture("#all")).end();

        // 4. Ecke
        ModelFile corner = provider.models().withExistingParent(name + "_corner", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(0, 0, 0).to(c, c, c)
                .face(f -> f.uvs(0, 0, c, c).texture("#all")).end();

        generateCubeFrame(block, centerBuilder, edgeHoriz, edgeVert, corner);
    }

    public void generateHorizontalPlane(Block block, ResourceLocation texture) {
        generateHorizontalPlane(block, texture, null, 1.0f, 1.0f, false);
    }

    public void generateHorizontalPlane(Block block, ResourceLocation texture, ResourceLocation glassTexture, float edgeThickness, float cornerScale, boolean hasGlass) {
        String name = block.builtInRegistryHolder().key().location().getPath();

        float t = edgeThickness * 2.0f;
        float c = cornerScale * 2.0f;

        var centerBuilder = provider.models().withExistingParent(name + "_center", "block/block")
                .texture("particle", texture);

        if (hasGlass) {
            centerBuilder.texture("glass", glassTexture != null ? glassTexture : texture);
            // Flaches Glas, Y ist leicht nach innen versetzt (0.1 bis t-0.1)
            centerBuilder.element().from(0.1f, 0.1f, 0.1f).to(15.9f, t - 0.1f, 15.9f)
                    .face(f -> f.uvs(0, 0, 16, 16).texture("#glass")).end();
        } else {
            centerBuilder.texture("all", texture);
            centerBuilder.element().from(t, 0, t).to(16f - t, t, 16f - t)
                    .face(f -> f.uvs(t, t, 16f - t, 16f - t).texture("#all")).end();
        }

        ModelFile edge = provider.models().withExistingParent(name + "_edge", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(c, 0, 0).to(16f - c, t, t)
                .face(f -> f.uvs(c, 0, 16f - c, t).texture("#all")).end();

        // Ecke bei Horizontal hat die Breite 'c', aber die feste Höhe 't' der Plattform
        ModelFile corner = provider.models().withExistingParent(name + "_corner", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(0, 0, 0).to(c, t, c)
                .face(f -> f.uvs(0, 0, c, c).texture("#all")).end();

        generateHorizontalPlane(block, centerBuilder, edge, corner);
    }

    public void generateVerticalPlane(Block block, Direction.Axis axis, ResourceLocation texture) {
        generateVerticalPlane(block, axis, texture, null, 1.0f, 1.0f, false);
    }

    public void generateVerticalPlane(Block block, Direction.Axis axis, ResourceLocation texture, ResourceLocation glassTexture, float edgeThickness, float cornerScale, boolean hasGlass) {
        String name = block.builtInRegistryHolder().key().location().getPath() + "_" + axis.getName();

        float t = edgeThickness * 2.0f;
        float c = cornerScale * 2.0f;

        // Perfekt zentriert auf Z=8. Das ist wichtig, damit rotationY(180) die Tiefe nicht verschiebt!
        float zStart = 8f - (t / 2f);
        float zEnd = 8f + (t / 2f);

        var centerBuilder = provider.models().withExistingParent(name + "_center", "block/block")
                .texture("particle", texture);

        if (hasGlass) {
            centerBuilder.texture("glass", glassTexture != null ? glassTexture : texture);
            centerBuilder.element().from(0.1f, 0.1f, zStart + 0.1f).to(15.9f, 15.9f, zEnd - 0.1f)
                    .face(f -> f.uvs(0, 0, 16, 16).texture("#glass")).end();
        } else {
            centerBuilder.texture("all", texture);
            centerBuilder.element().from(t, t, zStart).to(16f - t, 16f - t, zEnd)
                    .face(f -> f.uvs(t, t, 16f - t, 16f - t).texture("#all")).end();
        }

        // Horizontale Kante (Oben: von Links 'c' bis Rechts '16-c')
        ModelFile edgeHoriz = provider.models().withExistingParent(name + "_edge_horiz", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(c, 16f - t, zStart).to(16f - c, 16f, zEnd)
                .face(f -> f.uvs(c, 0, 16f - c, t).texture("#all")).end();

        // Vertikale Kante (Links: von Unten 'c' bis Oben '16-c')
        ModelFile edgeVert = provider.models().withExistingParent(name + "_edge_vert", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(0, c, zStart).to(t, 16f - c, zEnd)
                .face(f -> f.uvs(0, c, t, 16f - c).texture("#all")).end();

        // Ecke (Oben Links)
        ModelFile corner = provider.models().withExistingParent(name + "_corner", "block/block")
                .texture("all", texture).texture("particle", texture)
                .element().from(0, 16f - c, zStart).to(c, 16f, zEnd)
                .face(f -> f.uvs(0, 0, c, c).texture("#all")).end();

        // Aufruf der korrigierten Basis-Methode
        generateVerticalPlane(block, axis, centerBuilder, edgeHoriz, edgeVert, corner);
    }*/
}