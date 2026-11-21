# 03 – Core-Modul

## Dokumentierte Bereiche

Der Snapshot beschreibt `geni.withercore` mit Client- und Common-Infrastruktur. Relevante Paketbereiche sind `common.helper`, `common.inventory`, `common.menu`, `common.network`, `common.registration`, `common.util`, `common.world`, `client` und `init`.

## Registrierungsinfrastruktur

Im Snapshot erscheinen unter anderem:
- `WUTDeferredRegister`
- `WUTDeferredHolder`
- `DatapackDeferredRegister`
- `DeferredMapCodecRegister`
- `DoubleDeferredRegister`
- `INamedEntry`

Sowie Implementierungen für Block-, Item-, EntityType-, SoundEvent- und BlockEntity-/TileEntity-Registrierungen, darunter `BlockDeferredRegister`, `BlockRegistryObject`, `ItemDeferredRegister`, `ItemRegistryObject`, `EntityTypeDeferredRegister`, `EntityTypeRegistryObject`, `SoundEventDeferredRegister`, `SoundEventRegistryObject`, `TileEntityTypeDeferredRegister` und `TileEntityTypeRegistryObject`.

## Init-Klassen im Snapshot

`WUTBlocks`, `WUTItems`, `WUTBlockEntities`, `WUTCapabilities`, `WUTComponents`, `WUTCreativeTab`, `WUTEffects`, `WUTEntities`, `WUTFluids`, `WUTMenus`, `WUTParticles`, `WUTShaders`.

Diese Namen sind Orientierungspunkte, keine Garantie, dass jede Funktion aktuell oder für jede Registrierung zuständig ist. Vor Änderung konkrete Datei und Register-Lifecycle prüfen.

## Basistypen und Helpers

Dokumentierte Basistypen umfassen unter anderem `WitherAbstractBlock`, `WitherBlock`, `WitherBlockEntity`, `WitherMachineBlockEntity`, `ItemBlock`, `WitherItem`, `WitherInventoryItem`, `WitherEntity`, `WitherInventoryEntity`, `WitherAbstractMenu`, `WitherMachineMenu` und `WitherMenu`.

Vor einer neuen Implementierung immer Konstruktoren, abstrakte Methoden, Lifecycle-Hooks und tatsächliche Vererbung im Checkout prüfen.
