# AI Studio Agent Instructions — WitherUtils / Minecraft 1.21.1

This file is the primary operating contract for this workspace. Read it before acting, then read the relevant knowledge and workflow documents. The original source documents are preserved in `source_references/`.

## 1. Mandatory project separation

The project has three independent implementation areas. Never blur their responsibilities.

| Area | Exact project path | Technology | Scope |
|---|---|---|---|
| Minecraft clone / abstraction | `src/main/java/net/minecraft/` | TypeScript, Three.js | Minecraft 1.21.1-inspired APIs, resources, world, models, rendering and interaction |
| React frontend | `src/main/java/net/frontend/` | React, TypeScript/TSX | UI and frontend features; consumes the Minecraft abstraction |
| WitherUtils release mod | `src/main/java/geni/` | Java, NeoForge | Actual WitherUtils mod implementation, only after explicit user authorization |

The unusual `src/main/java/net/minecraft/` path is intentional. Files there are TypeScript (`.ts`/`.tsx` as applicable), not Java. Do not move or rename it based on convention.

`index.html` remains the web entry point and loads `src/main/java/net/neoforged/neoforge/modloader.tsx`, which starts the React frontend. React implementation belongs in `net/frontend`.

## 2. Explicit authorization gate for Java release code

Creating or changing code under `src/main/java/geni/` is forbidden unless the user explicitly requests WitherUtils Java/NeoForge release code. A request to create a Minecraft clone feature, a React screen, a preview, or a prototype is not permission to implement the equivalent mod feature in Java.

When multiple areas are needed, proceed in this order:
1. Minecraft abstraction/data/rendering.
2. React frontend using that abstraction.
3. Java release migration only after explicit approval.

Test/sandbox work is separate. The source plan mentions `test.withertest` as a sandbox described by an existing `SYSTEM-PROMPT.md`; verify that the folder and prompt actually exist before relying on them. Never invent a sandbox path.

## 3. No invention / no speculative implementation

- Do not invent features, classes, APIs, dependencies, architecture, resource files, or project conventions.
- Do not redesign or refactor architecture merely because another approach seems cleaner.
- Do not create duplicate classes, duplicate registries, parallel loaders, shadow resource sets, or competing implementations.
- Search the existing project before creating anything. Extend existing stubs and types where appropriate.
- Use exact APIs supported by the installed Minecraft/NeoForge/Three.js/React versions. If an API or version-specific signature is uncertain, inspect project dependencies and existing usage; if still uncertain, ask the user.
- Do not silently resolve conflicting instructions. Explain the conflict and ask.
- Do not claim full Minecraft 1.21.1 compatibility when only a subset is implemented. Document supported behavior and known gaps.
- Do not delete, rename, overwrite, or mass-format existing files without a direct requirement.
- Keep changes minimal and scoped to the requested task.

## 4. Resource source-of-truth

Vanilla Minecraft content is sourced from `src/main/resources/assets/minecraft/`. Mod resources use their own namespace, notably `assets/witherutils` for WitherUtils content.

Never duplicate resource content as TypeScript objects, arrays, base64, pixel data, or hardcoded definitions. TypeScript may implement generic parsers, resolvers, loaders, caches, and renderers that read the original JSON/PNG/GLSL/MCMeta resources. Do not create manually maintained duplicate asset trees.

Centralize Vite development and production resource access. Preserve original asset files and namespace/path semantics. Validate paths and report missing assets or malformed references clearly.

## 5. Implementation discipline

For every task:
1. Identify the target area and exact files.
2. Inspect the relevant existing code, configuration, dependencies, assets, and tests.
3. State the smallest viable implementation plan.
4. If required information is missing or the requested behavior is ambiguous, ask one focused question before changing files.
5. Implement only the approved scope.
6. Run the narrowest relevant checks, then broader checks if practical.
7. Report changed files, behavior, checks, limitations, and anything not verified.

Do not create a second architecture document or overwrite the source plan. Use `knowledge/` as the distilled navigation layer and `source_references/` as the preserved original material.

## 6. Area-specific constraints

### Minecraft clone
- Use TypeScript and Three.js under `src/main/java/net/minecraft/`.
- Reuse existing package/class structure and stubs.
- Resource JSON is authoritative for blockstates and models; no hardcoded block ID catalogue.
- Implement only classes needed by concrete use cases.
- React is not the 3D engine.

### React frontend
- Implement under `src/main/java/net/frontend/`.
- Consume the Minecraft abstraction for world, block, item, model and renderer behavior.
- Do not duplicate model parsing, block rendering, world state, or game logic in React.
- Keep UI state and presentation separate from simulation/rendering responsibilities.

### WitherUtils Java release
- Only enter this area after explicit user authorization.
- Inspect the current checkout, not just old snapshots or examples.
- Follow existing WitherUtils API/Core/Main/Datagen/registration/menu/network conventions.
- Treat `EXAMPLES.md` as patterns and reference examples, not guaranteed drop-in code. Verify every class, import, method, and API against the current project and target dependency versions.
- Avoid adding new base classes or helper frameworks when existing project abstractions cover the need.

## 7. Required response style

Be precise and transparent. Distinguish:
- confirmed project facts,
- facts from the preserved planning documents,
- assumptions/inferences,
- unverified details.

If the request cannot be completed safely without a decision, stop and ask rather than guessing.
