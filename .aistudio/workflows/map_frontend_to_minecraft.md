# Workflow: Frontend auf Minecraft abbilden

## Ziel
Für jedes Element des Frontends eine nachvollziehbare native Minecraft-/NeoForge-Entsprechung finden.

## Mapping-Kategorien
- Darstellung → Screen, Widget, GuiGraphics, vorhandene Wither-GUI-Komponenten
- Eingabe → Widget-Aktion, Menu-Aktion, serverseitiger Handler
- Zustand → Menu-/BlockEntity-/Item-/Entity-Daten
- Persistenz → vorhandenes NBT-/DataComponent-/Save-Pattern
- Synchronisierung → vorhandene SyncSlot-/Payload-/Menu-Sync-Infrastruktur
- Weltinhalt → Block, Item, BlockEntity, Entity oder Fluid mit bestehender Registrierung
- Assets → vorhandene Textur-/Model-/Blockstate-/Lang-/Datagen-Pipeline

## Für jede Zuordnung dokumentieren
1. Frontend-Element und Quellstelle.
2. Exaktes beobachtetes Verhalten.
3. Vorgeschlagene Java-Klasse bzw. vorhandener Erweiterungspunkt.
4. Zuständige Modulgrenze.
5. Client-/Server-Verantwortung.
6. Persistenz und Netzwerkbedarf.
7. Ressourcen/Datagen.
8. Verifikationskriterium.
9. Unsicherheit und offene Frage.

## Regel
Wenn keine sichere Entsprechung gefunden wird, `OFFEN` markieren und fragen. Nicht automatisch eine neue Basisklasse, ein neues Netzwerkprotokoll oder ein neues Framework entwerfen.

## Ausgabe
`migration_mapping.md` anhand der Vorlage.
