# Project architecture and work-area routing

This project deliberately combines three distinct deliverables: a TypeScript/Three.js Minecraft 1.21.1 abstraction, a React frontend built on that abstraction, and an optional Java/NeoForge WitherUtils release mod. These are not interchangeable code areas.

## Exact locations

- Clone: `src/main/java/net/minecraft/` — TypeScript, Three.js.
- React: `src/main/java/net/frontend/` — React, TypeScript/TSX.
- Release mod: `src/main/java/geni/` — Java, NeoForge; only on explicit user request.
- Web entry: root `index.html` loads `src/main/java/net/neoforged/neoforge/modloader.tsx`.

The `src/main/java/net/minecraft/` path is intentional even though its files are TypeScript. Do not normalize the directory layout.

## Dependency direction

Build the Minecraft abstraction first; React consumes it. Java release migration is a separate, explicitly authorized activity. A frontend or clone task does not imply Java release work.

## Full source plan

The complete, unabridged project plan is preserved in `source_references/IMPLEMENTATION-PLAN.original.md`. See especially its sections 1–3, 8–10 and 13.
