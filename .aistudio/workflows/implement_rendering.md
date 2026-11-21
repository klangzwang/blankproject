# Workflow: Three.js rendering slice

1. Confirm the requested output is part of the Minecraft clone.
2. Inspect the existing renderer, scene setup, camera, chunk/world data and asset loader.
3. Define one visible vertical slice and its acceptance criteria.
4. Reuse loaded Minecraft model/state/texture data; do not hardcode block visuals.
5. Keep rendering responsibilities in Three.js and the clone layer.
6. Validate performance-sensitive work and verify dev/build asset access.
7. Report exactly what can be rendered and what is not yet supported.
