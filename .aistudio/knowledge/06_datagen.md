# 06 – Datagen und Ressourcen

## Dokumentierte Generatoren

Im Snapshot unter `geni.witherdata.generator`:
- `WitherUtilsDataProvider`
- `WitherUtilsBlockStates`
- `WitherUtilsBlockTags`
- `WitherUtilsCraftingRecipes`
- `WitherUtilsEntityTags`
- `WitherUtilsItemModels`
- `WitherUtilsItemTags`
- `WitherUtilsLootTables`

Der Snapshot zeigt unter anderem `WitherUtilsBlockStates` mit Helfern für Cube-All-Modelle und vorhandene Modelle sowie `WitherUtilsLootTables`, das über `WUTBlocks.BLOCKS.getPrimaryEntries()` iteriert und `dropSelf` verwendet. Die konkreten Implementierungen im aktuellen Checkout sind maßgeblich.

## Ressourcen-Mapping

Bei einem Frontend-Feature prüfen, ob Minecraft-Ressourcen benötigt werden:
- `assets/witherutils/blockstates/`
- `assets/witherutils/models/block/`
- `assets/witherutils/models/item/`
- `assets/witherutils/textures/`
- `assets/witherutils/lang/`
- `data/witherutils/recipes/`
- `data/witherutils/loot_tables/`
- `data/witherutils/tags/`

Diese Pfade sind typische Minecraft-Ressourcenorte und müssen mit der tatsächlichen Repository-Struktur und den bestehenden Assets abgeglichen werden. Nicht jede Aufgabe benötigt alle Kategorien.

## Regeln

- Keine vorhandenen Texturen überschreiben, ohne sie zu prüfen.
- Dateinamen, Registry-IDs und Model-Referenzen konsistent halten.
- Blockstates, Modelle, Item-Modelle, Loot Tables, Rezepte und Tags aufeinander abstimmen.
- Datagen-Output und manuell gepflegte Ressourcen nicht unkontrolliert doppeln.
- Generierte Dateien nicht von Hand editieren, wenn der Projektworkflow sie aus Provider-Code erzeugt.
- Keine erfundenen Texturen oder Platzhalter als final ausgeben.
