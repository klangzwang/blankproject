# Minecraft NeoForge to React/Three.js Prototyping Sandbox: Implementation Plan

High-performance browser-based sandbox environment for rapid creative testing of Minecraft 1.21.1 (NeoForge) mod mechanics, items, blocks, and animations using React, `@react-three/fiber`, and Three.js, bridging the gap between slow Java build cycles and instant visual iteration.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The architectural design for the Prototyping Sandbox will govern the implementation upon approval:

- **Confirmed Decision 1 (Java-Style TypeScript Architecture)**:
  - **Class-Based Logic**: Game mechanics, calculations, and state are encapsulated in strict TypeScript classes featuring explicit typing, `public`/`private` modifiers, constructors, and methods matching Java object-oriented design patterns.
  - **Separation of Concerns**: React components act purely as UI and rendering viewers, making the codebase intuitively readable for Java developers.
- **Confirmed Decision 2 (Modular Frontend Directory Structure)**:
  - Code is strictly separated into distinct modules under `src-frontend/` (`first/`, `orbit/`, `tools/`) to isolate modes and clean up component architecture.
- **Confirmed Decision 3 (Shared Global World State & Scene Synchronization)**:
  - **Single Source of Truth**: The 3D scene / World-Map state is shared globally between FirstPerson and Orbit modes. Changes made in FirstPerson (e.g., building) reflect instantly in Orbit, and vice versa.
  - **Structure-Based Initial World**: Instead of a generic empty flatmap, the sandbox starts by default by loading and rendering the selected initial Structure (30x30 Grass layout from `.nbt`).
  - **Orbit-Mode Structure Positioning (X, Y, Z Controls)**: Since loaded structures vary in height and can cause player clipping/stuck issues, the Orbit sidebar provides manual coordinate adjustment controls (X, Y, Z offset inputs) to shift structures precisely before entering FirstPerson mode.
- **Confirmed Decision 4 (True-to-Game Asset Pipeline & Structures)**:
  - **Pixel-Art Textures**: Enforces `THREE.NearestFilter` for both `magFilter` and `minFilter` on all mod textures to prevent blurring.
  - **Gui & Structure Integration**: Loads GUI textures from `public\resources\assets\minecraft\textures\gui\...` and reads Minecraft `.nbt` structure files from `public\resources\data\minecraft\structure\...` via a frontend NBT parser.
- **Confirmed Decision 5 (Interactive Modes & TAB Switching)**:
  - Supports switching between **FirstPerson**, **Orbit**, and **Tool** modes dynamically via the `TAB` key (starting in FirstPerson mode by default).
- **Confirmed Decision 6 (Two-Way Synchronization & Code Export)**:
  - Changes tested and validated in the browser live-preview automatically generate clean, copy-paste-ready Java code snippets tailored for IntelliJ IDEA (NeoForge 1.21.1 / Java 21).

---

### 1. Overview & Core Concept

- **What It Does**: Bypasses the multi-minute build/launch times of IntelliJ IDEA by simulating mod components in an instantaneous WebGL/Three.js sandbox. Allows creative experimentation with mechanics, numbers, and visuals in real time.
- **Target Audience**: Java mod developers who want lightning-fast visual feedback without needing to master functional, unformatted frontend React patterns.
- **Key Value**: Instant prototyping loop, zero loss of aesthetic fidelity (textures/models/structures), and direct translation back to production-ready Java code.

---

### 2. Gameplay Modes & Interaction Design

- **Default State**:
  - Upon starting the sandbox, **FirstPerson-Modus** is active by default.
  - Pressing the **TAB** key cycles through the modes (**FirstPerson** ➔ **Orbit** ➔ **Tool**).

- **1. FirstPerson-Modus (`src-frontend/first/`)**:
  - Uses the **global shared world state** (identical scene as Orbit). Building or modifying blocks here updates the global scene in real time.
  - Contains standard mouse and keyboard controls: `WSAD` movement, Jump, Crouch, and `E` to open the Player Inventory featuring container GUIs and slot textures (`public\resources\assets\minecraft\textures\gui\...`).
  - Player Hotbar located at the bottom of the screen.
  - Visual styling targets a shader-like aesthetic look with a Day Skybox, right-click block placement, and a 1.5-second digging/breaking action when holding the left mouse button.

- **2. Orbit-Modus (`src-frontend/orbit/`)**:
  - Shares the exact same 3D world scene as FirstPerson mode.
  - Camera allows smooth rotation, zooming, and panning around the scene.
  - Features two collapsible sidebars with settings, calculations, notes, buttons, and AI Studio tooling.
  - **Structure & Position Management**: 
    - Scrollable list of all `.nbt` structure files found in `public\resources\data\minecraft\structure\...`.
    - Includes **X, Y, Z coordinate adjustment inputs/sliders** to move/offset the loaded structure prior to gameplay to prevent height mismatches and player collision/stuck bugs.
    - Clicking "LoadStructure" parses the selected `.nbt` file and places it into the shared world state at the specified X, Y, Z offsets.

- **3. Tool-Modus (`src-frontend/tools/`)**:
  - Hides the 3D scene entirely.
  - Displays a centered, larger scrollable box containing a grid of utility and tool buttons.
  - The primary utility button is **"ExportToNeoforge 1.21.1"**, exemplifying the purpose of the Tool mode for future mod utility tasks.

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
     │         src-frontend         │              │    sandbox.asset.pipeline    │
     │ - first/ (1st Person, Hotbar)│              │ - Nearest-Neighbor Textures  │
     │ - orbit/ (Orbit, NBT + XYZ)  │              │ - GUI & Structure Parsers    │
     │ - tools/ (Utilities Grid)    │              │ - glTF Model Loaders         │
     └────────────┬─────────────────┘              └──────────────┬───────────────┘
                  │                                               │
                  └───────────────────────┬───────────────────────┘
                                          ▼
                         ┌─────────────────────────────────┐
                         │     sandbox.react.ui            │
                         │ - TAB-Key Mode Switcher         │
                         │ - Global World State Store      │
                         │ - Java Code Exporter UI         │
                         └─────────────────────────────────┘
						 
#### Detailed Module Allocation (`src-frontend/`):
1. **`src-frontend/first/`**:
   - First-person controller, shared world scene binding, movement physics, inventory screen container GUI renderer using assets from `public\resources\assets\minecraft\textures\gui\`, hotbar, and 1.5s block digging mechanics.
2. **`src-frontend/orbit/`**:
   - Orbit camera controls, AI Studio collapsable sidebars, structure selector dropdown/list sourced from `public\resources\data\minecraft\structure\`, **X, Y, Z offset positioning controls**, and the TypeScript NBT structure parser/loader.
3. **`src-frontend/tools/`**:
   - 3D-viewport suppression, centered scrollable button grid interface, and the **"ExportToNeoforge 1.21.1"** utility trigger.

---

### 4. Access Transformers & Sync Rules

- **Access Transformers**: N/A for frontend sandbox; mapped 1:1 to NeoForge equivalents during code export.
- **Two-Way Sync Protocol**: Every successful sandbox iteration produces a validated Java code counterpart for seamless integration into the mod source tree.