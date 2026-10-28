# Minecraft NeoForge Sandbox: Interactive Modes & Structure Pipeline Implementation Plan

Detailed architecture and execution roadmap for integrating the Three-Mode Sandbox (First-Person, Orbit, and Tool Mode), asset bindings for GUI textures and `.nbt` structures from `public/resources/data/<modid>/structure/`, and the TypeScript-based NBT structure parser.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The architectural design for the Sandbox Modes and Structure Pipeline will govern the implementation upon approval:

- **Confirmed Decision 1 (Three-Mode Architecture & TAB Switching)**:
  - **First-Person Mode**: Authentic Minecraft 1.21.1 flat map experience (30x30 grass blocks), WASD movement, jump, crouch, shader-style rendering, day skybox, inventory GUI container with slot textures, bottom hotbar, right-click block placement, and 1.5-second left-click block breaking (without visual breaking percentage progress bars).
  - **Orbit Mode**: Standard AI Studio development layout featuring camera orbit/zoom/pan controls, two collapsible sidebars for settings, calculations, and hints, plus a scrollable list of available `.nbt` structures from the structure folder with a "LoadStructure" button powered by a frontend NBT parser.
  - **Tool Mode**: Dedicated utility grid hiding the 3D scene to display a central scrollable box of utility buttons (starting with the "ExportToNeoForge 1.21.1" action).
  - **Default State**: Sandbox launches directly into **First-Person Mode**. TAB key cycles through modes.
- **Confirmed Decision 2 (Modular Frontend Directory Structure)**:
  - Code strictly separated into dedicated directories under `src-frontend/` (`first/`, `orbit/`, `tools/`).
- **Confirmed Decision 3 (Asset & Structure Pipeline Integration)**:
  - GUI Textures mapped from `public/resources/assets/minecraft/textures/gui/...`.
  - Structure files loaded from `public/resources/data/minecraft/structure/...`.

---

### 1. Overview & Core Concept

- **What It Does**: Transforms the static Three.js prototype into an interactive, multi-mode development sandbox. Allows developers to test gameplay mechanics in an authentic First-Person Minecraft view, manage structures and AI Studio tools in Orbit Mode, and execute utilities in Tool Mode.
- **Target Audience**: Mod developers testing 1.21.1 NeoForge mechanics, block placing/breaking logic, GUI containers, and custom `.nbt` world structures in real time.
- **Key Value**: Seamless transition between player experience and developer tooling without breaking immersion or build pipelines.

---

### 2. Gameplay Experience & Visual Design

- **User Workflow**:
  1. **Startup**: Sandbox launches in **First-Person Mode** on a 30x30 grass flat map.
  2. **Movement & Action**: Move with WASD, jump, crouch, open player inventory with 'E', place blocks with right-click, and mine blocks over 1.5 seconds with held left-click.
  3. **Mode Switching**: Press **TAB** to switch to **Orbit Mode** to inspect structures, pick an `.nbt` file from the sidebar list, and click "LoadStructure" to render it. Press **TAB** again to enter **Tool Mode** for utilities like "ExportToNeoForge 1.21.1".

---

### 3. Technical Architecture & Package Structure

						 ┌─────────────────────────────────┐
                         │       src-frontend.core         │
                         │  ModeManager, InputController   │
                         └────────────────┬────────────────┘
                                          │
                  ┌───────────────────────┴───────────────────────┐
                  ▼                                               ▼
     ┌──────────────────────────────┐              ┌──────────────────────────────┐
     │       src-frontend.first     │              │     src-frontend.orbit       │
     │ - 30x30 Flat Map & Skybox    │              │ - Collapsible AI Studio UIs  │
     │ - WASD, Jump, Crouch         │              │ - NBT Structure Parser       │
     │ - Inventory GUI & Hotbar     │              │ - Structure Selector List    │
     └────────────┬─────────────────┘              └──────────────┬───────────────┘
                  │                                               │
                  └───────────────────────┬───────────────────────┘
                                          ▼
                         ┌─────────────────────────────────┐
                         │       src-frontend.tools        │
                         │ - Central Utility Button Grid   │
                         │ - ExportToNeoForge 1.21.1 Action│
                         └─────────────────────────────────┘
						 
#### Detailed Module Allocation:
1. **`src-frontend.first`**:
   - First-person camera physics, movement controllers, flat map renderer, block placing/mining logic, and HUD/Inventory UI.
2. **`src-frontend.orbit`**:
   - Orbit controls, collapsible AI Studio sidebars, `.nbt` file scanner, NBT-to-JSON frontend parser, and structure visualization.
3. **`src-frontend.tools`**:
   - Tool-Mode view container, utility button grid, and export command triggers.

---

### 4. Access Transformers & Constraints

- **Access Transformers**: N/A.
- **Strict Implementation Constraint**: No additional features (such as breaking percentage bars) are permitted in this phase. Additional mechanics will be introduced in subsequent prompts.
