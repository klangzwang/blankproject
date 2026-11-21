# Workflow: Migration verifizieren

## Ziel
Nachweisen, ob die freigegebene Umsetzung korrekt ist.

## Prüfschritte
1. Diff gegen den Ausgangsstand kontrollieren.
2. Sicherstellen, dass nur freigegebene Dateien geändert wurden.
3. Kompilierung mit dem tatsächlich vorhandenen Gradle-Wrapper und passenden Tasks ausführen.
4. Vorhandene Tests ausführen, soweit sinnvoll.
5. Datagen-Tasks nur verwenden, wenn sie im Projekt existieren und für die Änderung relevant sind.
6. Ressourcenpfade, Registry-IDs, Model-/Blockstate-Verweise und Übersetzungen prüfen.
7. Client-/Server-Ladegrenzen und Netzwerk-/Persistenzpfade prüfen.
8. Manuelle In-Game-Schritte dokumentieren, falls automatisierte Prüfung nicht reicht.

## Ergebnisstatus
- PASS: Prüfung ausgeführt und bestanden.
- FAIL: Prüfung ausgeführt und fehlgeschlagen; relevante Meldungen nennen.
- NOT RUN: nicht ausgeführt; Grund nennen.
- PARTIAL: nur Teilprüfung möglich; offene Punkte nennen.

## Ausgabe
`walkthrough.md` mit tatsächlich ausgeführten Befehlen, Ergebnissen und offenen Punkten.

## Verbot
Keine Tests erfinden und keinen erfolgreichen Build behaupten, wenn er nicht gelaufen ist.
