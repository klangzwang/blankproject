# Minecraft 1.21.1 clone / abstraction

The goal is a data-driven TypeScript abstraction of the Minecraft 1.21.1 classes and behaviors required by the desired world, interaction, GUI and mod preview. It is not a requirement to recreate every Minecraft class.

## Architecture responsibilities

- Resources / ResourceLocation: namespace IDs, paths, loading and caching.
- Registries: stable lookup for blocks, items, blockstates, models, textures and required entities.
- World / Level / Chunk: block positions, states, chunk data and world access.
- Model loaders: Minecraft JSON parsing, parent models, variants and texture bindings.
- Client / Renderer: Three.js scene, camera, render pipeline, materials, chunk meshes, block/item renderers and effects.
- Player / Input / Interaction: first-person camera, controls, raycasting and implemented interactions.
- GUI / HUD / Font: render Minecraft HUD and GUI from existing resources.

Existing types under `net/minecraft/client`, `core`, `resources` and `world` are starting points. Extend existing stubs where possible; do not create parallel APIs. Implement classes only when a concrete feature requires them.

## Compatibility discipline

Use Minecraft 1.21.1 semantics as the reference. Record deviations and supported subsets. Never describe partial coverage as full compatibility. Three.js is the rendering engine; React must not implement a competing 3D/block renderer.

## Source material

The complete implementation plan is in `source_references/IMPLEMENTATION-PLAN.original.md`, sections 3, 6, 7, 10 and 11.
