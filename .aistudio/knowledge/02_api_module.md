# 02 – API-Modul

## Dokumentierte Typen

Der Snapshot enthält unter anderem:
- `geni.witherapi.block.IBlock`
- `geni.witherapi.block.WitherEntityBlock`
- `geni.witherapi.block.entity.ISerializableTanks`
- `geni.witherapi.block.entity.UseOnly`
- `geni.witherapi.client.IHudOverlay`

`WitherEntityBlock` stellt im Snapshot einen Ticker bereit, der bei `WitherBlockEntity` zwischen `clientTick()` und `serverTick()` anhand der Level-Seite unterscheidet. `ISerializableTanks` kapselt die Serialisierung registrierter Fluid-Tanks über Data Components und NBT.

## Regeln

- Bestehende Interfaces zuerst verwenden.
- Keine konkrete WitherUtils-Implementierung in API verschieben, nur um einen neuen Typ bequem erreichbar zu machen.
- Keine Abhängigkeit der API auf Main-Mod-Klassen hinzufügen.
- Client-only-Typen nicht unbedacht in common/serverseitig geladenen Code ziehen.
- Generische API-Erweiterungen müssen einen klaren, realen Wiederverwendungsfall besitzen.
- Signaturen, Nullability, Seitentrennung und Abhängigkeiten am aktuellen Quelltext verifizieren.

## Vor einer Änderung

Suche nach Implementierungen und Aufrufern des betroffenen Interfaces. Prüfe außerdem, ob das Interface bereits in anderen Modulen verwendet wird und ob eine Signaturänderung Kompatibilität brechen würde.
