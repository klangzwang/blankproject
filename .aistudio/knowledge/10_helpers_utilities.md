# 10 – Helpers und Utilities

## Im Snapshot dokumentierte Helpers

Unter `geni.withercore.common.helper` erscheinen unter anderem:
- `AtlasTextureHelper`
- `ColorHelper`
- `FieldReflectionHelper`
- `IOHelper`
- `NNList`
- `NullHelper`
- `RenderHelper`
- `TickHelper`
- `Ticker`

## Im Snapshot dokumentierte Utilities

Unter `geni.withercore.common.util` erscheinen unter anderem:
- `BlockEntityUtil`
- `BlockUtil`
- `ClientRegistrationUtil`
- `EnergyUtil`
- `FacingUtil`
- `FluidUtil`
- `GuiUtil`
- `ItemStackUtil`
- `ParticleUtil`
- `RelativeSide`
- `SoundUtil`
- `TranslationUtil`
- `Utils`

## Regeln

- Vor jeder Hilfsklasse nach gleichartiger Funktion suchen.
- Bestehende Utility nur erweitern, wenn Zuständigkeit und Sichtbarkeit passen.
- Keine generische Utility-Sammelklasse mit fachfremden Methoden aufbauen.
- Reflection nur verwenden, wenn bereits projektseitig vorgesehen und zwingend erforderlich.
- Nullability, Side-Safety und Threading berücksichtigen.
