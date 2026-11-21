# Workflow: WitherUtils Java release implementation

Use only after the user explicitly authorizes creation/modification of WitherUtils Java/NeoForge release code.

1. Reconfirm the authorized feature and scope.
2. Inspect current source and versioned APIs; treat examples as patterns, not authority.
3. Reuse existing API/Core/Main/Datagen classes and registrations.
4. Implement only the approved feature in the appropriate `geni` package.
5. Preserve client/server separation, registration conventions, serialization and sync patterns.
6. Add/update datagen and assets only where required by the approved feature.
7. Run Gradle compile/test tasks available in the repository.
8. Report all changed files, build result and unresolved runtime checks.
