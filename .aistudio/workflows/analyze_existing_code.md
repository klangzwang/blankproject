# Workflow: Vorhandenen WitherUtils-Code untersuchen (read-only)

## Ziel
Vor einer Migration vorhandene Klassen, Registrierungen und Datenflüsse finden, damit keine parallele Architektur entsteht.

## Schritte
1. Aktuellen Repository-Baum, Gradle-Projekte und Source Sets prüfen.
2. Minecraft-/NeoForge-/Java-Version aus den Build-Dateien bestätigen.
3. Nach semantisch passenden Klassen, Methoden, Widgets, Menüs, Payloads, Registraren und Providern suchen.
4. Vererbung, Konstruktoren, Aufrufer, Implementierungen und Lifecycle-Hooks lesen.
5. Client-/Server-Seitigkeit, Threading, Persistenz und Sync-Verhalten ermitteln.
6. Ressourcen- und Datagen-Pipeline prüfen.
7. Abweichungen zum Snapshot `.aistudio/knowledge/` dokumentieren.

## Ausgabe
`existing_code_analysis.md` mit:
- bereits vorhandene Lösung,
- geeignete Erweiterungspunkte,
- nicht geeignete Ansätze,
- konkrete Pfade und Gründe,
- noch zu klärende Punkte.

## Grenzen
Keine Dateien ändern. Snapshot-Namen nicht als Beweis ansehen, dass die Klasse im aktuellen Checkout existiert.
