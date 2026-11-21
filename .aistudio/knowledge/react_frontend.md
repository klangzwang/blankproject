# React frontend

React code belongs in `src/main/java/net/frontend/`. The root `index.html` remains the web entry and loads `src/main/java/net/neoforged/neoforge/modloader.tsx`, which starts the frontend.

React consumes the Minecraft abstraction for world, block, item, model and renderer behavior. Do not duplicate block/model parsing, rendering, simulation or game logic in React. The frontend is presentation and interaction on top of the shared abstraction.

The full integration direction is described in `source_references/IMPLEMENTATION-PLAN.original.md`, sections 1, 2 and 8.
