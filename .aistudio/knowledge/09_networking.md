# 09 – Netzwerk und Synchronisierung

## Dokumentierte Bereiche

Der Snapshot enthält `geni.withercore.common.network` mit unter anderem:
- `CoreNetwork`
- `NetworkDataSlot`
- `SyncSlot`
- `ContainerSyncData`
- `ClientPacketSender`
- `ClientPayloadHandler`
- `ServerPayloadHandler`
- Payload- und Menu-Payload-Klassen

Ein dokumentierter `SyncSlot`-Vertrag unterscheidet `NONE`, `PARTIAL` und `FULL` und stellt Methoden zum Erkennen von Änderungen, Erzeugen und Entpacken von Payloads bereit. Der Snapshot zeigt außerdem Handler, die Arbeiten über `context.enqueueWork(...)` ausführen und Menu-/Container-IDs prüfen. Konkrete Klassen und Paketnamen können sich im Checkout unterscheiden.

## Regeln für neue UI-Aktionen

1. Prüfen, ob eine vorhandene Menu-Aktion, Payload, SyncSlot oder DataSlot passt.
2. Datenrichtung (Client → Server, Server → Client) ausdrücklich dokumentieren.
3. Serverseitige Validierung und Berechtigungsprüfung vorsehen.
4. Container-ID, Menu-Typ und Gültigkeit der Ziel-BlockEntity prüfen.
5. Netzwerk-Payload klein halten und Typ-/Codec-Pattern des Projekts nutzen.
6. Client-Vorschau von bestätigtem Serverzustand unterscheiden.
7. Keine sensiblen oder manipulierbaren Spielzustände allein vom Client übernehmen.

## Nicht tun

- Keine neue Netzwerkbibliothek hinzufügen.
- Keine direkte Client-Manipulation als dauerhafte Wahrheit verwenden.
- Keine Payload-ID oder Codec-Struktur erfinden.
- Bestehende Synchronisierungsmechanismen nicht parallel duplizieren.
