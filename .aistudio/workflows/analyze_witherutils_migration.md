# Workflow: analyze possible Java migration

This is analysis only; it does not authorize release-code changes.

1. Confirm the user asked for a migration assessment.
2. Inspect the current WitherUtils source tree, build configuration and dependency versions.
3. Locate existing API/Core/Main/Datagen abstractions and similar implemented features.
4. Map the approved prototype behavior to existing Java classes and extension points.
5. Identify missing pieces, risks, assets, registration and networking requirements.
6. Present a migration mapping and ask for explicit approval before editing `src/main/java/geni/`.
