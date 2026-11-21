# 11 – Coding-Konventionen und Qualitätsregeln

## Grundsatz

Der aktuelle Quellcode ist die Stilreferenz. Die Wissensdateien enthalten nur Orientierung, keine vollständige Style-Spezifikation.

## Verbindliche Arbeitsregeln

- Bestehende Einrückung, Klammerstil, Namensgebung und Annotationen der betroffenen Datei beibehalten.
- Imports aus tatsächlich verfügbaren Abhängigkeiten beziehen.
- Keine unnötigen `@SuppressWarnings`, unchecked casts oder Reflection hinzufügen.
- Keine leeren Catch-Blöcke.
- Keine Fehler verschlucken.
- Keine Debug-Ausgaben oder temporären Testklassen im finalen Scope belassen.
- Öffentliche API-Änderungen begründen.
- Client-only Code nicht in Common-Code laden.
- Serverautorität und Thread-Kontext beachten.
- Bestehende Save-/NBT-/DataComponent-Kompatibilität erhalten.

## Versionen und APIs

Minecraft 1.21.1 / NeoForge ist der vom Nutzer genannte Zielkontext. Vor dem Codegen die konkrete NeoForge-Version, Gradle-Plugins, Java Toolchain, Mappings und API-Signaturen aus dem Checkout lesen. Keine APIs aus anderen Minecraft-Versionen übernehmen.

## Tests

Nur Tests und Gradle-Tasks nennen, die im Projekt tatsächlich existieren. Wenn kein passender automatisierter Test existiert, dies offen angeben und einen manuellen Verifikationsschritt vorschlagen.
