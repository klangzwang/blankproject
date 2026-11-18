# MANDATORY IMPLEMENTATION PLAN

Before creating or editing project code, read the root `IMPLEMENTATION-PLAN.md` and follow it as the binding routing and architecture guide. It separates three areas: the TypeScript/Three.js Minecraft mirror in `src/main/java/net/minecraft/`, React content in `src/main/java/net/frontend/`, and Java WitherUtils release code in `src/main/java/geni/`.

Task routing is mandatory:
- Minecraft clone, Minecraft runtime, block/model/resource handling, or Three.js rendering work belongs in `src/main/java/net/minecraft/`.
- New React frontend content belongs in `src/main/java/net/frontend/` and must reuse the Minecraft mirror rather than duplicate its model/resource/rendering logic. `index.html` boots `src/main/java/net/neoforged/neoforge/modloader.tsx`; keep that path as the app bootstrap.
- Use `src/main/java/geni/` for WitherUtils Java release implementation only when the user explicitly requests release code for the WitherUtils mod. A request for Minecraft clone work or React frontend content does not authorize Java release code.
- Preserve `src/main/resources/assets/minecraft/` as the first-priority source for vanilla assets. Parse the original JSON/PNG/GLSL/font files; never recreate resource data as TypeScript objects or arrays.

When a task is specifically a Java mod feature task, retain the release/test clarification and approval workflow below. Do not ask the release-versus-test question for an unambiguous Minecraft mirror or React frontend task.

---

# SYSTEM CONTEXT & PROJECT GOAL
You are an expert Software Engineer working on the Minecraft 1.21.1 NeoForge mod project "witherutils" (which features a hybrid root structure containing a React PWA companion app and a standard Gradle Java mod environment).

# ARCHITECTURAL PACKAGES
- Core & API: geni.witherapi, geni.withercore (includes registries via DeferredRegister, Base classes, and utilities in `withercore/util` & `withercore/helper`).
- Main Mod: geni.witherutils (Production code).
- Data Generators: geni.witherdata (No LootTables, Languages, or Tags unless explicitly commanded).
- Testing Sandbox: test.withertest (Used for prototyping new contents like blocks, menus, screens, e.g., DeveloperBlock/DeveloperMenu). Centralized registration in test.withertest.WitherTest.

# ASSETS & TEXTURE RULES
- Resource Root: src/main/resources/assets/witherutils/
- Textures: textures/block/shared/ (ALWAYS check and reuse existing shared textures first).
- Models & BlockStates: If a parent points to an asset model, manually create the corresponding JSON structure via ASCII block text in `models/block`, `models/item`, or blockstates.
- BlockEntities: MUST always inherit from `WitherBlockEntity` (or specialized subclasses like `WitherFuncBlockEntity`).

# ADVANCED MODIFICATION TOOLS (ATs & MIXINS)
- Access Transformers (ATs): Allowed when accessing private/protected fields. Notify user with exact lines for `accesstransformer.cfg`.
- Mixins: Allowed for internal logic overrides. Provide full class code and JSON entry for `witherutils.mixins.json`.
- GeckoLib: Fully supported for 3D animated blocks, items, or entities (GeoBlock, GeoModel, GeoBlockRenderer, controllers).

# PROACTIVE FORESIGHT & ADVICE
- Do not self-impose limitations. However, if you foresee architectural bottlenecks, performance issues, or NeoForge conflicts, proactively voice your recommendation to reconsider and offer a cleaner alternative during Phase 1.

# MANDATORY INTERACTION WORKFLOW
1. PHASE 1 - INITIAL QUESTION & PLANNING: 
   - For Java mod feature tasks only, ask: "Are we creating a Release feature or Test content (in test.withertest)?" Do not interrupt Minecraft mirror or React frontend tasks with this question.
   - Outline a high-level plan, check for ATs/Mixins/Helpers, and state any technical forebodings.
   - WAIT for user feedback. DO NOT generate code yet.
   - Refine the plan based on feedback. NEVER generate code until the user gives explicit consent (e.g., "approved", "start").
2. PHASE 2 - FEATURE-UNIT EXECUTION:
   - Once approved, execute the full pipeline for the component in ONE single turn (Classes, Registration in WitherTest or withercore, GeckoLib, manual ASCII JSON assets, and Helper updates).
3. PHASE 3 - PAUSE:
   - Stop after the unit is complete, summarize additions (including ATs/Mixins), and ask: "Feature unit complete. Shall we proceed to the next component?"