# WitherUtils Collector Drone: Automated Item & XP Vacuum Framework

Autonomous retrieval and salvage drone framework for Minecraft 1.21.1 (NeoForge) designed to sweep designated areas, vacuum scattered item drops (`ItemEntity`) and experience orbs (`ExperienceOrb`) using an underslung magnetic intake funnel, transport goods with an 18-slot internal buffer, deposit sorted items into the `DroneDockBlock`, and store accumulated player experience.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The recommended design for the Collector Drone will govern the implementation upon approval:

- **Confirmed Decision 1 (Item & XP Dual Vacuuming)**:
  - **Item Retrieval**: Actively locates all loose item entities on the ground within its assigned zone, flies overhead, and pulls items upward into its intake funnel via smooth magnetic velocity interpolation.
  - **XP Absorption**: Collects experience orbs, storing accumulated XP points in an internal experience buffer (up to 1,000 XP / ~30 levels) which can be retrieved by players or discharged at the dock.
- **Confirmed Decision 2 (18-Slot High-Capacity Buffer)**:
  - Equipped with an 18-slot internal inventory (`ItemStackHandler`) optimized for mob farm collection, quarry cleanup, and tree harvesting byproduct retrieval.
  - Returns to the `DroneDockBlock` immediately when ≥ 14 slots are occupied or when the operational area is cleared of drops.
- **Confirmed Decision 3 (GeckoLib Modular Attachment & Vortex VFX)**:
  - Uses the unified quadrocopter airframe with the `collector_attachment.geo.json` module parented to the underslung `attachment_mount` bone.
  - High-RPM intake turbine that spins during collection, accompanied by vacuum suction audio (`SoundEvents.BEACON_ACTIVATE` pitch-shifted / `SoundEvents.EXPERIENCE_ORB_PICKUP`) and swirling purple/cyan energy vortex particles (`ParticleTypes.PORTAL`, `ParticleTypes.ENCHANTED_HIT`).
- **Confirmed Decision 4 (Dock Unloading & Sorting)**:
  - Unloads items systematically into the dock's inventory or adjacent storage blocks (chests, barrels, pipes).
  - Can discharge stored XP to nearby players or transfer it to dock storage.

---

### 1. Overview & Core Concept

- **What It Does**: Eliminates dropped-item despawning and server lag in high-throughput areas (mob grinders, quarries, boss arenas, tree plantations). The Collector Drone continuously scans its assigned perimeter, swoops down over scattered loot, vacuums items and XP orbs into its belly, and delivers them directly into the base's logistic network.
- **Target Audience**: Modded players running automated farms and mining rigs who want a mobile, animated cleanup robot instead of massive hopper floors or stationary vacuum hoppers.
- **Key Value**: Lag reduction through active entity pickup, zero ground item clutter, and effortless mob farm collection.

---

### 2. Gameplay Experience & Visual Design

- **Player Workflow**:
  1. **Deployment**: Place a `DroneDockBlock` near a mob farm, quarry, or arena.
  2. **Launch**: Right-click the dock with a `CollectorDroneItem`.
  3. **Operation**:
     - The drone takes off with its signature rotor sound and downwash dust.
     - Detects item drops or XP orbs within its operational zone.
     - Flies 2.0 blocks above the target loot, activates its vacuum turbine, and draws items and orbs smoothly upward into its chassis.
     - Swirling vacuum vortex particles indicate active suction.
  4. **Dock Delivery**:
     - When inventory reaches capacity (or all drops are collected), it returns to dock.
     - Deposits all collected loot into the dock's buffer.
     - Right-clicking the drone with an empty hand allows the player to claim any absorbed XP directly.
- **GeckoLib Visuals**:
  - Unified slate alloy chassis.
  - Underslung attachment: Inverted aerodynamic conical vacuum funnel with glowing energy collector fins.
  - Animated turbine rotor that spins rapidly during vacuum operations (`ANIM_VACUUM`).

---

### 3. Key Technical Decisions & Entity Attraction

- **Decision 1: Smooth Vector Attraction vs Instant Despawn/Teleport**
  - *Chosen Approach*: When within 4.5 blocks, target items and XP orbs receive upward and inward velocity vectors toward the drone's intake point (`drone.position().add(0, -0.3, 0)`), before being inserted into the inventory upon contact.
  - *Why*: Creates an authentic, cinematic tractor-beam vacuum effect rather than items simply vanishing from the ground.

- **Decision 2: Staggered Entity Radar Scanning**
  - *Chosen Approach*: Scans for `ItemEntity` and `ExperienceOrb` every 10 ticks instead of every tick.
  - *Why*: Ensures pristine server TPS even in mob farms with hundreds of simultaneous drops.

---

### 4. Technical Architecture & Package Structure

```
                             ┌─────────────────────────────────┐
                             │       geni.witherapi.drone       │
                             │  IDrone, IDroneDock, DroneState │
                             └────────────────┬────────────────┘
                                              │
                      ┌───────────────────────┴───────────────────────┐
                      ▼                                               ▼
         ┌──────────────────────────────┐              ┌──────────────────────────────┐
         │     geni.withercore.drone    │              │    geni.withercore.sound     │
         │ - AbstractDroneEntity        │              │ - DroneFlightSoundInstance   │
         │ - UniversalDroneModel        │              │ - WitherRegistries           │
         └────────────┬─────────────────┘              └──────────────┬───────────────┘
                      │                                               │
                      └───────────────────────┬───────────────────────┘
                                              ▼
                             ┌─────────────────────────────────┐
                             │     geni.witherutils.drone      │
                             │ - CollectorDroneEntity (Geo)    │
                             │ - CollectorDroneModel & Renderer│
                             │ - VacuumItemsAndXPSkill (Prio 80│
                             │ - CollectorDockSkill (Prio 100) │
                             │ - ScanDropsSkill (Priority 50)  │
                             └────────────────┬────────────────┘
                                              ▼
                             ┌─────────────────────────────────┐
                             │        geni.witherdata          │
                             │ - ItemModels (Collector Drone)  │
                             │ - English & German Lang         │
                             │ - Tags & Recipes                │
                             └─────────────────────────────────┘
```

#### Detailed Class Allocation:
1. **`geni.witherutils.drone.collector`**:
   - `CollectorDroneEntity`: 18-slot inventory, XP point accumulator, empty-hand XP claim interaction, synched vacuum status.
   - `CollectorDroneModel` & `CollectorDroneRenderer`: GeckoLib 4 model utilizing the universal chassis and modular collector attachment.
   - Skills:
     - `VacuumItemsAndXPSkill` (Priority 80): Magnetic attraction physics, entity collection, vortex particles, audio.
     - `CollectorDockSkill` (Priority 100): Unloads items at dock; stands by when area is clear.
     - `ScanDropsSkill` (Priority 50): Periodic radar scan for item/XP drops.
2. **`geni.withercore.registration`**:
   - Register `COLLECTOR_DRONE` EntityType and `COLLECTOR_DRONE_ITEM`.
3. **`geni.witherdata`**:
   - Generate item model, English & German localizations, and drone tags.

---

### 5. Access Transformers & Mixins Assessment

- **Access Transformers**: None required.
- **Mixins**: None required.
