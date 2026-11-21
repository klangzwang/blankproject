# 05 – Registrierungssystem

## Bekannte Orientierungspunkte

Der Snapshot dokumentiert die Register-Infrastruktur in `geni.withercore.common.registration` und zugehörige Implementierungen. Init-Klassen liegen unter `geni.withercore.init`, darunter `WUTBlocks`, `WUTItems`, `WUTBlockEntities`, `WUTMenus`, `WUTEntities`, `WUTFluids`, `WUTParticles`, `WUTSounds` (nur falls im Checkout vorhanden) und weitere.

## Vorgehen für neue Inhalte

1. Aktuelle Init-Klasse für den passenden Inhalt finden.
2. Bestehende Register-Instanz und deren Typen untersuchen.
3. Vorhandene Registry-Object-Klassen und Factory-/Supplier-Patterns nachvollziehen.
4. Prüfen, ob BlockItem, BlockEntityType, MenuType, Sound, Fluid oder Component zusätzlich registriert werden muss.
5. Konstruktoren und Referenzen prüfen, um Initialisierungsreihenfolge und zirkuläre Abhängigkeiten zu vermeiden.
6. Plan listet jede betroffene Registrierungsdatei einzeln auf.

## Verbote

- Keine direkte Vanilla-/NeoForge-Registrierung parallel zum bestehenden WUT-Register, sofern das Projektpattern dies nicht ausdrücklich verlangt.
- Keine zweite Register-Instanz nur für eine einzelne Klasse ohne Begründung.
- Keine Registry-IDs raten. Namespace und Pfad müssen aus der freigegebenen Anforderung abgeleitet sein.
- Keine existierenden Registry-Namen oder IDs ändern, ohne Kompatibilitätsanalyse und Freigabe.
