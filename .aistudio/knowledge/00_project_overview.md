# 00 – Projektüberblick

## Bestätigter Projektkontext

- Projektname: **WitherUtils**
- Mod-ID im dokumentierten Hauptmod-Einstieg: `witherutils`
- Hauptklasse im Snapshot: `src/main/java/geni/witherutils/WitherUtils.java`
- ResourceLocation-Helfer: `WitherUtils.loc(String path)`, der den Namespace `witherutils` verwendet und den Pfad kleinschreibt.
- Zielkontext laut Nutzer: Minecraft **1.21.1** mit **NeoForge**.
- Der bereitgestellte Code-Snapshot enthält Java-Pakete unter `geni.witherapi`, `geni.withercore`, `geni.witherdata` und `geni.witherutils`.

## Grobe Modulverantwortung

| Bereich | Namespace | Verantwortung laut Snapshot |
|---|---|---|
| API | `geni.witherapi` | Schnittstellen und Verträge, die von Mod-Komponenten verwendet werden |
| Core | `geni.withercore` | wiederverwendbare Basisklassen, Registrierung, Menüs, Inventare, Netzwerk, Client- und Render-Infrastruktur |
| Datagen | `geni.witherdata` | Rezepte und Daten-/Ressourcen-Provider |
| Hauptmod | `geni.witherutils` | konkrete WitherUtils-Inhalte, Blöcke, Block Entities, Items, Screens und Client-Setup |

Die genaue Gradle-Multi-Project-Konfiguration und die aktuelle Abhängigkeitsrichtung müssen vor Änderungen im echten Checkout geprüft werden. Aus Paketnamen allein keine Modulabhängigkeit ableiten.

## Projekt-Snapshot als Wissensbasis

Der Snapshot `WitherUtils-Data.md` enthält dokumentierte Pfade und Quelltextauszüge. Er ist nicht notwendigerweise vollständig oder aktuell. Er kann außerdem inaktive, auskommentierte oder experimentelle Codebereiche enthalten. Vor Verwendung immer die aktuelle Datei im Repository öffnen.

## Migrationsziel

Frontend-/Live-Preview-Ergebnisse aus AI Studio werden als Spezifikation gelesen. Ziel ist eine native Umsetzung in Java und Minecraft-GUI-/Spielmechaniken, ohne die bestehende Projektstruktur zu umgehen.
