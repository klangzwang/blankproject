# Workflow: Frontend analysieren (read-only)

## Ziel
Den AI-Studio-Frontend-/Live-Preview-Prototyp vollständig als Anforderungsspezifikation erfassen, ohne den WitherUtils-Code zu verändern.

## Schritte
1. Projekt-/Preview-Einstiegspunkt identifizieren.
2. Komponentenbaum und visuelle Hierarchie erfassen.
3. Alle sichtbaren Zustände dokumentieren: initial, hover, focus, selected, disabled, loading, success, error, empty.
4. Interaktionen dokumentieren: Klick, Drag, Eingabe, Tastatur, Scroll, Navigation, Kontextaktionen.
5. Datenmodell und Zustandsübergänge aus dem tatsächlich vorhandenen Code nachvollziehen.
6. Assets, Icons, Fonts, Farben, Abstände, Maße und Animationen erfassen.
7. Responsive Verhalten und Randfälle erfassen.
8. Unklare oder nur implizit angenommene Anforderungen als offene Fragen markieren.

## Ausgabe
`frontend_analysis.md` anhand `.aistudio/templates/frontend_analysis.md`.

## Grenzen
- Kein Java schreiben.
- Keine Projektdateien ändern.
- Keine Funktionalität ergänzen, die nicht im Prototyp oder Nutzerauftrag nachweisbar ist.
- Keine Annahmen als Fakten formulieren.
