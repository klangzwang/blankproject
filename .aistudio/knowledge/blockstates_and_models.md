# Blockstates, block models, item models and textures

The loader resolves `assets/<namespace>/blockstates/<name>.json` against the actual block state. Required staged behavior includes:
- `variants`, including state expressions such as `snowy=false`;
- model objects and model arrays with rotations;
- `x`/`y` rotations and `uvlock` where present;
- `multipart` when required by the resource set or task.

Do not maintain a hardcoded list of block IDs. New valid JSON resources should become available through the generic loader without adding TS resource definitions.

Block/item model loading must resolve parent models and texture references using Minecraft resource semantics. Textures and material behavior must be driven by the actual resource data, not invented substitutes.

The full requirements are in `source_references/IMPLEMENTATION-PLAN.original.md`, section 5.
