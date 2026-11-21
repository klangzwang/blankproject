# 08 – Menüs und GUI

## Dokumentierte Menüstruktur

Der Snapshot enthält unter anderem:
- `WitherAbstractMenu`
- `WitherMachineMenu`
- `WitherMenu`
- `WitherContainerEntityMenu`
- `WitherEntityMenu`
- `WitherContainerItemMenu`
- `WitherItemMenu`

Clientseitig sind GUI-Bausteine und Screens unter `geni.withercore.client` sowie `geni.witherutils.client.gui.screen` dokumentiert. Es gibt außerdem Helpers/Widgets wie `GuiUtil`, `WitherSlotGroup`, `WitherSlotDecorator` und verschiedene Wither-Widgets (konkrete Existenz und Signatur vor Nutzung verifizieren).

## Übersetzung von Frontend-UI

| Frontend-Konzept | Mögliche Minecraft-Entsprechung |
|---|---|
| Panel / Fenster | Screen oder vorhandenes Screen-Layout |
| Inventar-Slot | Slot im Menu und zugehöriger Container |
| Button | Minecraft GUI Widget |
| Slider | vorhandenes oder natives Slider-Widget |
| Toggle / Checkbox | passendes Widget plus serverseitige Aktion |
| Tooltip | Component/Tooltip |
| Tab | bestehendes Screen-/Widget-Pattern, sofern vorhanden |
| Statusanzeige | aus Menu-/BlockEntity-Zustand gespeiste Anzeige |

Das Mapping ist eine Prüfliste, keine automatische Architekturentscheidung.

## Visuelle Treue

Erfasse aus dem Prototyp:
- Canvas-/Fenstergröße und Skalierung
- Panelgrößen, Abstände, Ränder und Ausrichtung
- Farben, Transparenz, Fonts und Schriftgrößen
- Icons, Texturen, Slot-Darstellung und Tooltips
- Hover-, Disabled-, Selected-, Loading- und Error-Zustände
- Animationen und Übergänge
- Scrollbereiche und Eingabeverhalten

Danach auf Minecraft GUI Koordinaten, `GuiGraphics`, Widgets und vorhandene Wither-GUI-Komponenten abbilden. Keine Pixelwerte ungeprüft übernehmen, wenn Minecraft GUI-Skalierung oder Bildschirmauflösung sie verändert.

## Menü-/Screen-Grenze

Screen ist Darstellung und Eingabe. Menu/BlockEntity bzw. serverseitige Systeme verwalten maßgebliche Daten und Aktionen. Keine Spielweltänderung ausschließlich im Client-Screen implementieren.
