# Minecraft NeoForge to React/Three.js Prototyping Sandbox: Implementation Plan

High-performance browser-based sandbox environment for rapid creative testing of Minecraft 1.21.1 (NeoForge) mod mechanics, items, blocks, and animations using React, `@react-three/fiber`, and Three.js, bridging the gap between slow Java build cycles and instant visual iteration.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The architectural design for the Prototyping Sandbox will govern the implementation upon approval:

- **Confirmed Decision 1 (Java-Style TypeScript Architecture)**:
  - **Class-Based Logic**: Game mechanics, calculations, and state are encapsulated in strict TypeScript classes featuring explicit typing, `public`/`private` modifiers, constructors, and methods matching Java object-oriented design patterns.
  - **Separation of Concerns**: React components act purely as UI and rendering viewers, making the codebase intuitively readable for Java developers.
- **Confirmed Decision 2 (True-to-Game Asset Pipeline)**:
  - **Pixel-Art Textures**: Enforces `THREE.NearestFilter` for both `magFilter` and `minFilter` on all mod textures to prevent blurring.
  - **3D Models & Audio**: Loads converted glTF/GLB models (from Blockbench/GeckoLib assets) and handles `.ogg` sound effects via standard Web Audio APIs.
- **Confirmed Decision 3 (Exact Formula & Logic Mirroring)**:
  - Translates core Java math (damage formulas, energy consumption, inventory constraints from `BlockEntity` and item classes) directly into synchronous TypeScript utility functions.
- **Confirmed Decision 4 (Two-Way Synchronization & Code Export)**:
  - Changes tested and validated in the browser live-preview automatically generate clean, copy-paste-ready Java code snippets tailored for IntelliJ IDEA (NeoForge 1.21.1 / Java 21).

---

### 1. Overview & Core Concept

- **What It Does**: Bypasses the multi-minute build/launch times of IntelliJ IDEA by simulating mod components in an instantaneous WebGL/Three.js sandbox. Allows creative experimentation with mechanics, numbers, and visuals in real time.
- **Target Audience**: Java mod developers who want lightning-fast visual feedback without needing to master functional, unformatted frontend React patterns.
- **Key Value**: Instant prototyping loop, zero loss of aesthetic fidelity (textures/models), and direct translation back to production-ready Java code.

---

### 2. Gameplay Experience & Visual Design

- **Developer Workflow**:
  1. **Initialization**: Mod assets (`assets/`, models, textures, sounds) and core logic references are linked to the sandbox.
  2. **Sandbox Prototyping**: Interact with the 3D world in the browser, tweak numbers, test custom block entities or items live.
  3. **Verification**: Observe smooth Three.js rendering, precise pixel-art scaling, and live animation/logic feedback.
  4. **Code Export**: Request the AI to output the verified logic as an immaculate Java snippet ready for IntelliJ IDEA insertion.

---

### 3. Technical Architecture & Package Structure

                         ┌─────────────────────────────────┐
                         │       sandbox.core.config       │
                         │  ModConfig, BalancingData, Math │
                         └────────────────┬────────────────┘
                                          │
                  ┌───────────────────────┴───────────────────────┐
                  ▼                                               ▼
     ┌──────────────────────────────┐              ┌──────────────────────────────┐
     │     sandbox.logic.classes    │              │    sandbox.asset.pipeline    │
     │ - Java-Style TS Services     │              │ - Nearest-Neighbor Textures  │
     │ - Combat & Energy Calculators│              │ - glTF Model Loaders         │
     └────────────┬─────────────────┘              └──────────────┬───────────────┘
                  │                                               │
                  └───────────────────────┬───────────────────────┘
                                          ▼
                         ┌─────────────────────────────────┐
                         │     sandbox.react.ui            │
                         │ - Three.js Canvas / R3F         │
                         │ - Live Parameter Tweaks         │
                         │ - Java Code Exporter UI         │
                         └─────────────────────────────────┘

#### Detailed Module Allocation:
1. **`sandbox.logic.classes`**:
   - Class-based TypeScript service layers replicating Java items, block entities, and container mechanics.
2. **`sandbox.asset.pipeline`**:
   - Loaders configured for crisp pixel-art rendering and spatial audio playback.
3. **`sandbox.react.ui`**:
   - Declarative React wrapper providing immediate visual feedback and code-export utilities.

---

### 4. Access Transformers & Sync Rules

- **Access Transformers**: N/A for frontend sandbox; mapped 1:1 to NeoForge equivalents during code export.
- **Two-Way Sync Protocol**: Every successful sandbox iteration produces a validated Java code counterpart for seamless integration into the mod source tree.