# 04 – Hauptmod

## Paketbereiche

Der Snapshot enthält unter `geni.witherutils`:
- `client`
- `client.block.render`
- `client.entity.render`
- `client.gui.screen`
- `common.world.item`
- `common.world.level.block`
- `common.world.level.block.entity`

Die Hauptklasse `geni.witherutils.WitherUtils` definiert im Snapshot:
- `MODID = "witherutils"`
- Logger
- `loc(String path)` mit `ResourceLocation.fromNamespaceAndPath(MODID, path.toLowerCase(Locale.ROOT))`
- Mod-Konstruktor mit ModContainer, IEventBus und Dist; clientseitiges Setup wird an `ClientSetup` delegiert.

## Konkrete Inhalte

Beispiel im Snapshot: `TestBlock` erweitert `WitherAbstractBlock` und implementiert `WitherEntityBlock`; `TestBlockEntity` erweitert `WitherMachineBlockEntity`. Das ist ein Hinweis auf vorhandene Patterns, aber kein Muster, das ohne Prüfung blind kopiert werden darf.

## Regeln

- Konkrete Blöcke, Items, Block Entities und Screens in den vorhandenen Main-Mod-Bereichen platzieren.
- Registrierung über bestehende Core-Register und Init-Klassen untersuchen.
- Kein zweiter Mod-Einstiegspunkt und keine neue Mod-ID.
- `WitherUtils.loc(...)` nur verwenden, wenn es im aktuellen Checkout weiterhin die vorgesehene ResourceLocation-Factory ist.
- Clientklassen strikt von Common-/Dedicated-Server-Code trennen.
