# Three.js rendering

Three.js owns the 3D scene and rendering. React is not a replacement renderer.

The intended implementation includes a render foundation, world/chunk rendering, first-person camera and atmosphere, gameplay interaction, plus selected shaders and Minecraft GUI/HUD resources. Rendering should be built in verifiable vertical slices, not as a speculative all-at-once engine.

Player/input/interaction responsibilities include mouse and keyboard control, raycasting and only the interactions actually implemented. Chunk rendering should use the world/chunk data model and resource-backed block models/materials.

Consult `source_references/IMPLEMENTATION-PLAN.original.md`, sections 6–7 and phases 2–4 for detailed goals and acceptance criteria.
