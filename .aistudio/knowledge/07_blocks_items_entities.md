# 07 – Blöcke, Items und Block Entities

## Bekannte Basistypen

Im Snapshot dokumentiert:
- `WitherAbstractBlock`
- `WitherBlock`
- `WitherBlockEntity`
- `WitherMachineBlockEntity`
- `ItemBlock`
- `WitherItem`
- `WitherInventoryItem`
- `WitherEntity`
- `WitherInventoryEntity`

Beispiel: `TestBlock` erweitert `WitherAbstractBlock` und implementiert `WitherEntityBlock`; die Block Entity `TestBlockEntity` erweitert `WitherMachineBlockEntity`.

## Migrationsfragen

Für jedes Frontend-Element mit Spielweltbezug klären:
- Ist es ein Block, Item, Block Entity, Entity, Fluid oder nur GUI-Widget?
- Welche Interaktion löst es aus?
- Welche Daten müssen persistent gespeichert werden?
- Läuft die Logik auf dem Server, Client oder beiden?
- Braucht es Tick-Verhalten?
- Welche Capabilities/Provider/Interfaces werden bereits verwendet?
- Welche Loot-, Rezept-, Tag-, Modell- und Blockstate-Daten sind notwendig?

## Sicherheitsregeln

- Spielzustand serverautoritativ ändern.
- Client darf Vorschau und Darstellung steuern, aber nicht ungeprüft maßgebliche Spielzustände festlegen.
- NBT-/Component-Persistenz und Synchronisierung getrennt betrachten.
- Keine neue Block Entity für reine Darstellung oder einen bereits durch vorhandene Daten-/Menu-Objekte abgedeckten Zustand erzeugen.
