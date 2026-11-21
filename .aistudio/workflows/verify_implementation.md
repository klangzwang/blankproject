# Workflow: verification and handoff

1. Review the diff and confirm every changed file belongs to the authorized area.
2. Check for accidental duplicates, hardcoded resource content, unrelated refactors and unapproved Java edits.
3. Run the narrowest relevant tests/typecheck/build.
4. If a check cannot run, state why; never imply it passed.
5. Confirm resource references resolve and no source assets were silently replaced.
6. Summarize behavior, changed files, checks, limitations and next dependency.
