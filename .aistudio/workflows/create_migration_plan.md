# Workflow: Implementierungsplan erstellen (Plan Mode)

## Ziel
Einen vollständigen, prüfbaren und begrenzten Plan erstellen. Dieser Plan ist die Freigabeschranke vor jeder Änderung.

## Voraussetzungen
- Frontend-Analyse liegt vor.
- Analyse des aktuellen WitherUtils-Codes liegt vor.
- Mapping liegt vor.
- Offene Fragen und Risiken sind sichtbar.

## Plan muss enthalten
1. Ziel und ausdrücklich nicht enthaltene Ziele.
2. Nutzeranforderungen als überprüfbare Akzeptanzkriterien.
3. Dateien, die erstellt werden sollen.
4. Dateien, die geändert werden sollen.
5. Für jede Datei: konkrete Änderung und Begründung.
6. Registrierungs- und Datagen-Schritte.
7. Datenfluss, Persistenz und Netzwerk.
8. Client-/Server-Sicherheit.
9. Ressourcen und Übersetzungen.
10. Build-/Test-/Manuellprüfungen.
11. Risiken, Rückwärtskompatibilität und offene Fragen.

## Scope-Regel
Jede Datei muss im Plan stehen. „Weitere nötige Dateien“ ist keine pauschale Erlaubnis. Wenn während der Umsetzung eine zusätzliche Datei erforderlich wird, stoppen, Plan ergänzen und erneut Freigabe einholen.

## Ausgabe
`implementation_plan.md` anhand der Vorlage.

## Ende
Plan vorlegen und ausdrücklich fragen, ob genau dieser Plan umgesetzt werden soll. Danach warten. Keine Änderungen vornehmen.
