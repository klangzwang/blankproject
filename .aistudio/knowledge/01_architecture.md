# 01 – Architektur und Modulgrenzen

## API – `geni.witherapi`

Im Snapshot sind unter anderem diese Bereiche dokumentiert:
- `geni.witherapi.block`
- `geni.witherapi.block.entity`
- `geni.witherapi.client`
- `geni.witherapi.item`
- `geni.witherapi.providers`
- `geni.witherapi.tags`

Beispiele aus dem Snapshot: `IBlock`, `WitherEntityBlock`, `ISerializableTanks`, `UseOnly`, `IHudOverlay`, `IFluidCapProvider`, `IWrenchable`, `IBaseProvider`, `IBlockProvider`, `IItemProvider`, `IEntityTypeProvider`, `ISoundEventProvider`, `WitherTags`.

**Regel:** API-Typen nur ergänzen, wenn ein tatsächlich benötigter, wiederverwendbarer Vertrag vorliegt. Konkrete UI-Logik gehört nicht automatisch in die API.

## Core – `geni.withercore`

Dokumentierte Bereiche umfassen:
- `client` mit Block-/Entity-/Item-Rendering, Shadern und GUI-Bausteinen
- `common.helper`
- `common.inventory`
- `common.menu`
- `common.network`
- `common.registration`
- `common.util`
- `common.world`
- `init`
- `math`

Core enthält wiederverwendbare Infrastruktur. Bevor eine neue generische Klasse erstellt wird, prüfen, ob bereits ein passender Core-Typ existiert.

## Datagen – `geni.witherdata`

Im Snapshot sind Generatoren unter `src/main/java/geni/witherdata/generator/` dokumentiert. Siehe `06_datagen.md`.

## Hauptmod – `geni.witherutils`

Dokumentierte Bereiche:
- `client`
- `client.block.render`
- `client.entity.render`
- `client.gui.screen`
- `common.world.item`
- `common.world.level.block`
- `common.world.level.block.entity`

Konkrete WitherUtils-Funktionen gehören grundsätzlich in den Main-Mod, sofern der aktuelle Modulaufbau nichts anderes vorgibt.

## Entscheidung für neue Klassen

Jede geplante neue Klasse muss beantworten:
1. Ist sie konkret oder generisch?
2. Wird sie von mehreren Modulen/Features benötigt?
3. Gibt es bereits eine Basisklasse oder ein Interface?
4. Welches Modul besitzt die fachliche Verantwortung?
5. Ist die Klasse wirklich notwendig oder kann eine bestehende Klasse erweitert werden?

Keine Modulverschiebung ohne explizite Freigabe.
