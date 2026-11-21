# Workflow: React frontend

1. Confirm the task is frontend UI/interaction.
2. Inspect `src/main/java/net/frontend/` and the app entrypoint.
3. Find the existing Minecraft abstraction API needed by the screen.
4. Consume that API; do not reimplement block models, 3D rendering or world logic in React.
5. Keep UI concerns in React and simulation/render concerns in the clone.
6. Run relevant type/build checks and report any integration gaps.
7. Do not create Java mod code as a side effect.
