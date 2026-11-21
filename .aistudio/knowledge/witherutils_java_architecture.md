# WitherUtils Java / NeoForge release architecture

This area is for actual WitherUtils release code under `src/main/java/geni/`, and may only be modified when the user explicitly requests Java/NeoForge release implementation.

The supplied project snapshot describes distinct layers:
- API: `geni/witherapi/` contracts and provider interfaces.
- Core: `geni/withercore/` reusable client/common systems, registration, inventory, menus, network and utilities.
- Main mod: `geni/witherutils/` concrete mod content and client implementations.
- Datagen: `geni/witherdata/generator/` data providers.

The snapshot may be stale. Always inspect the current checkout and build files before relying on package names, class names, signatures or dependencies. Prefer existing project abstractions over new helpers/frameworks.

This knowledge file is a routing summary, not a substitute for current source inspection. The detailed planning notes and class examples are preserved in `source_references/` and `examples/`.
