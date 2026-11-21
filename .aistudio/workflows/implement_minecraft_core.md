# Workflow: implement Minecraft abstraction

1. Confirm the request belongs to `src/main/java/net/minecraft/`.
2. Inspect existing package structure and relevant stubs.
3. Identify the precise Minecraft 1.21.1 behavior required; do not implement unrelated APIs.
4. Trace the required resource data to `src/main/resources/assets/`.
5. Implement the smallest generic abstraction that reads the original data.
6. Avoid hardcoded content, duplicate loaders and resource copies.
7. Add focused tests or validation where the project supports them.
8. Report compatibility scope and known gaps.
