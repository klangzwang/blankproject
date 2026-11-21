# Workflow: Freigegebene Migration ausführen

## Eintrittsbedingung
Nur starten, wenn der Nutzer eindeutig die Umsetzung des aktuellen `implementation_plan.md` freigegeben hat.

## Vor Start
1. Freigabe und Planversion prüfen.
2. Arbeitsbaum erneut prüfen; zwischenzeitliche Änderungen beachten.
3. Betroffene Dateien erneut öffnen.
4. Prüfen, ob der Scope noch unverändert ist.

## Umsetzung
1. Nur genehmigte Dateien und Schritte bearbeiten.
2. Bestehende Patterns verwenden.
3. Änderungen in kleinen, logisch getrennten Schritten vornehmen.
4. Keine zusätzlichen Features oder Refactorings.
5. Keine Build-Dateien oder Dependencies ändern, sofern nicht ausdrücklich genehmigt.
6. Wenn neue Anforderungen oder unerwartete Architekturprobleme auftreten: stoppen und Plan aktualisieren.

## Nach jedem Schritt
- betroffene Datei prüfen,
- Imports und Referenzen prüfen,
- Registrierungs- und Ressourcenbezüge prüfen,
- `task.md` aktualisieren.

## Abschluss
Noch keine Erfolgsaussage ohne Verifikation. Übergib an `verify_migration.md`.
