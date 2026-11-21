# Resources and assets — source of truth

## Authoritative locations

Vanilla resources: `src/main/resources/assets/minecraft/`.

Examples of resource IDs:
- `minecraft:blockstates/grass_block.json`
- `minecraft:models/block/grass_block.json`
- `minecraft:models/item/iron_pickaxe.json`
- `minecraft:textures/block/grass_block_top.png`
- `minecraft:textures/gui/sprites/hud/hotbar.png`
- `minecraft:font/default.json`

Mod resources use their own namespace, especially `assets/witherutils` for mod content.

## Prohibited duplication

Do not re-create blockstates, block models, item models, textures or GUI data as TypeScript objects, base64, pixel arrays or hardcoded replacement definitions. TypeScript code may provide generic parsers, resolvers, loaders, caches and renderers that read original resource files.

An automatically generated file index is acceptable only if it is reproducible from the source files and does not duplicate resource contents. Prefer runtime/build resolution.

## Vite access

The project needs one centralized asset access strategy for both dev server and production build. Preserve namespace/path logic, load JSON as data and image/shader/font assets in suitable formats, keep original files unchanged, report missing files and malformed references, and prevent path traversal outside the asset root.

See `source_references/IMPLEMENTATION-PLAN.original.md`, section 4, for the complete source requirements.
