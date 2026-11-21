# Workflow: Migration gezielt untersuchen und Fehler beheben

## Ziel
Fehlerursache eingrenzen, ohne ungefragt großflächig umzubauen.

## Schritte
1. Exakte Fehlermeldung, Stacktrace und Reproduktionsschritte erfassen.
2. Fehler einer Kategorie zuordnen: Compile, Registry, Datagen, Resource, Client-Rendering, Menu, Netzwerk, Persistenz, Laufzeit.
3. Betroffene Datei und Aufrufkette im aktuellen Checkout lesen.
4. Änderung gegen freigegebenen Plan und Ausgangs-Diff prüfen.
5. Kleinste plausible Ursache identifizieren und durch Quelltext/Logs belegen.
6. Korrekturvorschlag mit betroffenen Dateien und Risiko vorlegen.
7. Wenn die Korrektur außerhalb des genehmigten Scopes liegt, erneut Freigabe einholen.
8. Nach Freigabe minimal korrigieren und passende Verifikation wiederholen.

## Nicht tun
- Keine Fehler durch pauschales Deaktivieren von Code kaschieren.
- Keine Versions- oder Dependency-Upgrades als Schnelllösung.
- Keine bestehende API ersetzen, ohne Auswirkungen zu analysieren.
- Keine unbelegten „Fixes“ ausgeben.
