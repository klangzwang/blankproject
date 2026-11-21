# Integrated AI Studio workspace

This package integrates the supplied `IMPLEMENTATION-PLAN.md` and `EXAMPLES.md` into one `.aistudio` workspace.

## Contents
- `AGENTS.md` — primary agent contract, area routing and strict change controls.
- `knowledge/` — concise, navigable project knowledge for clone, resources, rendering, React and WitherUtils.
- `workflows/` — repeatable task workflows.
- `examples/` — extracted WitherUtils class examples grouped by responsibility.
- `templates/` — planning, task, walkthrough and migration templates.
- `source_references/` — verbatim copies of both original documents, retained to prevent information loss.

## Integration / cleanup
The original documents have **not** been discarded. After copying/merging this package into the project and reviewing the resulting `.aistudio` tree, the originals may be archived or removed from their former locations if desired. Keep `source_references/` unless you intentionally maintain another canonical archive.

## Install
Merge the `.aistudio/` directory from this archive into the project's existing `.aistudio/` directory. Review filename conflicts before overwriting anything. Do not replace the whole project `.aistudio` directory blindly; preserve existing artifacts such as `.aistudio/artifacts/brain/`.
