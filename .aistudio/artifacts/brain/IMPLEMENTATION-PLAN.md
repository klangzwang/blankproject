# Implementation Plan – Minecraft 1.21.1 Clone, React Frontend und WitherUtils

**Projekt:** WitherUtils / Minecraft 1.21.1 NeoForge + React Companion App  
**Planstatus:** Verbindliche Architektur- und Umsetzungsrichtlinie  
**Ziel:** Ein datengetriebenes Minecraft-1.21.1-Abbild in TypeScript mit Three.js-Rendering, ein darauf aufbauendes React-Frontend und – ausschließlich auf ausdrückliche Anforderung – die Erzeugung von WitherUtils-Release-Code in Java.

---

## 1. Verbindliche Grundentscheidung

Das Projekt hat drei klar getrennte Arbeitsbereiche. Jeder neue Inhalt muss anhand seines tatsächlichen Zwecks dem richtigen Bereich zugeordnet werden. Die Ordner sind keine austauschbaren Ablageorte.

| Bereich | Verbindlicher Pfad | Sprache / Technologie | Aufgabe |
|---|---|---|---|
| Minecraft-Abbild | `src/main/java/net/minecraft/` | TypeScript, Three.js | Minecraft-1.21.1-APIs und benötigte Spiel-/Client-Strukturen in TypeScript nachbilden; Rendering und Spielwelt bereitstellen |
| React-Frontend | `src/main/java/net/frontend/` | React, TypeScript/TSX | Neue React-Oberflächen, Komponenten und Frontend-Funktionen erstellen; das Minecraft-Abbild aus Bereich 1 wiederverwenden |
| WitherUtils-Release-Code | `src/main/java/geni/` | Java, NeoForge | Ausschließlich dann Java-Mod-Code für WitherUtils erzeugen oder ändern, wenn der Nutzer dies ausdrücklich als Release-Code für die Mod WitherUtils beauftragt |

Die `index.html` im Projektstamm bleibt der Einstiegspunkt der Web-App. Sie lädt `src/main/java/net/neoforged/neoforge/modloader.tsx`. Dieser Einstiegspunkt startet das React-Frontend; die eigentlichen React-Inhalte liegen in `net/frontend`.

**Wichtig:** Der Ordnername `src/main/java/net/minecraft` ist durch die bestehende Projektstruktur vorgegeben. Die dortigen Dateien sind TypeScript-Dateien (`.ts`) und bilden Minecraft in TypeScript ab. Dort wird kein Java-Mod-Code abgelegt. Umgekehrt wird React-UI nicht in `geni` oder in den Minecraft-Klassenordner geschrieben.

## 2. Routing-Regeln für jeden neuen Auftrag

Vor jeder Änderung ist zuerst die Art des Auftrags festzustellen:

1. **Minecraft-Kern, Spielwelt, Block-/Item-Daten, Darstellung oder Three.js-Rendering:** Änderungen ausschließlich im Minecraft-Abbild unter `src/main/java/net/minecraft/` vornehmen.
2. **Neuer React-Inhalt, eine neue Frontend-Komponente oder eine KI-gestützte Frontend-Funktion:** Inhalt unter `src/main/java/net/frontend/` erstellen. Dabei vorhandene Klassen, Blockdaten, Modelle und Renderer aus `net/minecraft` importieren und wiederverwenden. Die Minecraft-Logik nicht ein zweites Mal im React-Frontend implementieren.
3. **WitherUtils-Release-Code:** Nur wenn der Nutzer ausdrücklich sagt, dass jetzt Release-Code für die Mod WitherUtils erstellt werden soll, die Java-Architektur in `src/main/java/geni/` verwenden. In diesem Modus wird fertiger bzw. abgestimmter Frontend-/Minecraft-Inhalt in die passende Java-/NeoForge-Implementierung übertragen. Die TypeScript-Abbildung ersetzt nicht die Java-Mod-Implementierung.
4. **Test-Inhalt:** Die bestehende `.aistudio/SYSTEM-PROMPT.md` beschreibt `test.withertest` als Sandbox. Diesen Pfad nur benutzen, wenn der Nutzer ausdrücklich Test-Inhalt statt Release-Code verlangt. Im geprüften ZIP ist dieser Ordner derzeit nicht vorhanden; vor einer solchen Umsetzung muss seine tatsächliche Projektlage geprüft werden.

Ein Auftrag für den Minecraft-Kern oder das React-Frontend ist **nicht automatisch** ein Auftrag zur Erstellung von Release-Code. Insbesondere darf aus dem Erstellen einer React-Komponente oder einer neuen Block-Visualisierung nicht eigenmächtig eine Java-Mod-Implementierung abgeleitet werden.

Wenn ein Auftrag mehrere Bereiche benötigt, gilt die Abhängigkeit: **Minecraft-Abbild / Datenmodell zuerst, React-Frontend danach, Java-Release-Code nur bei expliziter Freigabe.** Änderungen sind entsprechend getrennt und nachvollziehbar zu halten.

## 3. Zielarchitektur des Minecraft-Abbilds

### 3.1 Zweck und Kompatibilitätsprinzip

`src/main/java/net/minecraft/` wird zu einem TypeScript-Abbild derjenigen Minecraft-1.21.1-Klassen und -Verhaltensweisen, die für die gewünschte Welt, Interaktion, GUI und Mod-Vorschau benötigt werden.

- Bestehende Klassenpfade und passende Klassennamen beibehalten, soweit sie sinnvoll sind.
- Nicht jede Klasse des originalen Minecraft muss nachgebaut werden. Klassen werden nur dann implementiert, wenn ein konkreter Renderer, eine Spielmechanik oder ein Frontend-Feature sie benötigt.
- Vorhandene Stubs zuerst prüfen und dort gezielt implementieren, statt parallel eine zweite, konkurrierende API zu erfinden.
- Semantik und Datenfluss von Minecraft 1.21.1 als Referenz nehmen. Abweichungen sind zu dokumentieren und dürfen nicht als vollständige Kompatibilität ausgegeben werden.
- Three.js ist die Render-Engine. React ist nicht die 3D-Engine und darf keine eigene alternative Block-/Model-Rendering-Implementierung aufbauen.

### 3.2 Verantwortliche Schichten

Die Implementierung soll die vorhandene Paketstruktur nutzen und logisch folgende Verantwortlichkeiten abbilden:

- **Resources / ResourceLocation:** Namespaces und Ressourcenpfade auflösen, Dateien laden und cachen.
- **Registries:** Blöcke, Items, Blockstates, Modelle, Texturen und – soweit benötigt – Entitäten über stabile IDs auffindbar machen.
- **World / Level / Chunk:** Blockpositionen, Blockzustände, Chunk-Daten, Weltzugriff und die für die Darstellung erforderlichen Umgebungsdaten verwalten.
- **Block- und Item-Modell-Lader:** Minecraft-JSON-Dateien interpretieren; Modelle, Elternmodelle, Varianten und Texturbindungen auflösen.
- **Client / Renderer:** Three.js-Szene, Kamera, Render-Pipeline, Materialien, Chunk-Meshes, Block-/Item-Renderer und Effekte verwalten.
- **Player / Input / Interaction:** First-Person-Kamera, Maus-/Tastatursteuerung, Raycasting und die jeweils implementierten Interaktionen verbinden.
- **GUI / HUD / Font:** Minecraft-HUD und GUI-Elemente aus den vorhandenen Ressourcen darstellen.

Die existierenden Typen unter `net/minecraft/client`, `net/minecraft/core`, `net/minecraft/resources` und `net/minecraft/world` sind Ausgangspunkte. Neue Klassen sollen in die passende vorhandene Paketdomäne einsortiert werden. Keine Pflicht, alle derzeit angelegten Stub-Klassen sofort zu implementieren.

## 4. Ressourcen sind die alleinige Datenquelle für Minecraft-Inhalte

### 4.1 Priorität und Auflösung

Der Ressourcenbestand unter `src/main/resources/assets/minecraft/` hat bei Vanilla-Minecraft-Inhalten immer Vorrang und ist die maßgebliche Quelle. Ein Block, Item, Modell oder GUI-Element wird zuerst dort gesucht.

Ressourcen werden über ihre Namespace-ID und den Ressourcenpfad aufgelöst, zum Beispiel:

- `minecraft:blockstates/grass_block.json`
- `minecraft:models/block/grass_block.json`
- `minecraft:models/item/iron_pickaxe.json`
- `minecraft:textures/block/grass_block_top.png`
- `minecraft:textures/gui/sprites/hud/hotbar.png`
- `minecraft:font/default.json`

Bei mod-eigenen Ressourcen gilt der jeweilige Mod-Namespace, insbesondere `assets/witherutils`, sofern der Auftrag Java-/WitherUtils-Inhalte betrifft. Vanilla-Ressourcen dürfen dadurch nicht überschrieben oder unbemerkt ersetzt werden.

### 4.2 Keine Ressourcen als TypeScript-Daten nachbauen

**Das System schreibt niemals eigene Ressourceninhalte im TypeScript-Format.** Insbesondere verboten sind:

- Blockstates, Blockmodelle oder Itemmodelle als TypeScript-Objekte nachzuerstellen;
- Texturen als Base64-/Pixel-Arrays oder andere TS-Datenkopien zu hinterlegen;
- im Code erfundene Textur-, Modell- oder Blockdefinitionen als Ersatz für fehlende Dateien zu führen;
- Ressourcendateien ohne ausdrücklichen Auftrag zu überschreiben oder umzubenennen.

TypeScript-Code darf und soll generische **Parser, Resolver, Loader, Cache-Schichten und Renderer** enthalten. Diese lesen die Originaldateien aus `src/main/resources/assets/` und interpretieren deren Inhalt. Die Ressourcendaten selbst bleiben in ihrem Originalformat (JSON, PNG, GLSL, MCMeta usw.).

Ein automatisch erstellter Dateiindex ist nur zulässig, wenn er direkt aus den vorhandenen Dateien abgeleitet wird, keine Ressourceninhalte dupliziert und jederzeit reproduzierbar ist. Bevorzugt wird eine Laufzeit-/Build-Auflösung der Originaldateien statt einer manuell gepflegten Kopie.

### 4.3 Vite- und Build-Zugriff

Da `src/main/resources/assets` nicht automatisch dem üblichen Vite-`public`-Verzeichnis entspricht, muss ein zentraler Ressourcen-Zugriff für Entwicklungs- und Produktionsbuilds eingerichtet werden. Dieser muss:

- die Originaldateien unter einem stabilen Ressourcenpfad erreichbar machen;
- im Dev-Server und im Produktionsbuild dieselbe Namespace-/Pfadlogik verwenden;
- JSON als Daten und PNG/Shader/Font-Dateien als passende Binär- bzw. Textressourcen laden;
- Dateien unverändert durchreichen bzw. kopieren, ohne eine TS-Ressourcenfassung zu erzeugen;
- fehlende Dateien und fehlerhafte JSON-Referenzen verständlich melden;
- Pfad-Traversal und Zugriffe außerhalb des Asset-Roots verhindern.

Die Implementierung darf dafür eine Vite-Konfiguration bzw. ein dediziertes Vite-Plugin ergänzen. Es darf keine zweite manuell gepflegte Asset-Sammlung entstehen.

## 5. Blockstates, Blockmodelle, Itemmodelle und Texturen

### 5.1 Blockstates

Der Blockstate-Loader liest `assets/<namespace>/blockstates/<name>.json` und ermittelt anhand des tatsächlichen Zustands die passende Modellreferenz.

Mindestens schrittweise unterstützen:

- `variants` einschließlich Zustandsausdrücken wie `snowy=false`;
- einzelne Modellobjekte und Modell-Arrays mit Rotationsvarianten;
- `x`-/`y`-Rotation und `uvlock`, soweit im Datensatz vorhanden;
- später `multipart`, sobald Ressourcen oder Anforderungen es benötigen.

Der Loader darf keine feste Liste von Block-IDs voraussetzen. Neue gültige JSON-Dateien müssen ohne neue TypeScript-Ressourcendefinition auffindbar sein.

### 5.2 Block- und Itemmodelle

Der Model-Resolver verarbeitet:

- `parent`-Vererbung und rekursive Texturvariablen wie `#all`, `#side` oder `#top`;
- `textures`, `elements`, `from`, `to`, `faces`, `uv`, `rotation`, `cullface` und `tintindex`;
- Blockmodelle auf dem Minecraft-16er-Koordinatensystem und die korrekte Skalierung auf Three.js-Welteinheiten;
- Items über `models/item`, einschließlich `parent`, `layer0` und den in den Ressourcen vorhandenen Display-/Override-Daten;
- fehlende Elternmodelle, Texturen und Zyklen in der Elternauflösung mit nachvollziehbaren Fehlern.

Modelle werden aus JSON in Three.js-Geometrie und Materialbindungen übersetzt. Das JSON bleibt die Datenquelle; die zur Laufzeit erzeugte `BufferGeometry` ist ein Render-Artefakt und keine neue Ressourcen-Datei.

### 5.3 Texturen und Materialverhalten

Für den originalen Minecraft-Look sind unter anderem wichtig:

- pixelgetreue Texturabbildung und passende UV-Orientierung;
- `NearestFilter` bzw. eine bewusst gewählte Minecraft-kompatible Sampling-Strategie statt unscharfer Standardfilterung;
- korrekte Transparenz: opaque, cutout/alpha-test und translucent;
- Tinting für `tintindex` (z. B. Gras), ohne die Originaltextur durch eine TS-Kopie zu ersetzen;
- Blockseiten-Culling und korrekte Flächenorientierung;
- Texturwiederverwendung und Caching statt Material-/Texture-Neuerstellung pro Block.

## 6. Three.js-Rendering: Look und Gefühl von Minecraft

Die visuelle Zielsetzung ist eine möglichst originalgetreue Minecraft-1.21.1-Darstellung. Das bereitgestellte Screenshot dient als Referenz für Perspektive, Maßstab, Pixeloptik, Weltatmosphäre und HUD. „1:1“ ist ein Abnahmekriterium und kein bereits erreichter Zustand.

### 6.1 Render-Grundlage

- Three.js `WebGLRenderer` mit einem zentralen Render-Lifecycle und sauberer Behandlung von Resize, Dispose und Context-Verlust.
- Minecraft-kompatibles Koordinatensystem: Y nach oben, Blockraster und konsistente Block-/Kameraeinheiten.
- Perspective Camera mit FOV und First-Person-Kameraposition, die anhand der Referenz und Minecraft-Verhalten abgestimmt werden.
- Pixeltreue Texturen, konsistente Blockmaße und korrekte UVs.
- Materialien mit korrekter Licht-, Schatten-, Alpha- und Fog-Behandlung.

### 6.2 Welt und Chunk-Rendering

- Weltzustände werden unabhängig von Three.js-Meshes gehalten.
- Blockdaten werden in Chunks bzw. Chunk-Sektionen zusammengefasst.
- Pro sichtbarer Blockfläche werden Geometrie und Material aus dem Blockstate-/Model-Ergebnis erzeugt.
- Verdeckte Nachbarflächen werden nicht unnötig gezeichnet.
- Chunk-Meshes werden bei Änderungen gezielt aktualisiert; keine komplette Welt-Neuerstellung bei jedem Frame.
- Frustum Culling, Geometrie-/Material-Caches und – wenn erforderlich – Instancing bzw. Greedy Meshing zur Leistungsoptimierung.

### 6.3 First-Person und Atmosphäre

Die Darstellung wird schrittweise um die sichtbaren Bestandteile der Referenz erweitert:

- First-Person-Kamera und sichtbare Spielerhand;
- Hotbar und Auswahlrahmen am unteren Bildschirmrand;
- Himmel, Horizont, Wolken, Umgebungslicht und Fog;
- Gelände mit klaren Blockkanten, Gras-/Erde-/Steinflächen und korrekter Texturorientierung;
- Vegetation und Blockobjekte, sobald die dazugehörigen Originalressourcen vorhanden und vom Loader unterstützt sind;
- Block- und Itemdarstellung in der Welt sowie in der Hotbar.

Die Screenshot-Referenz enthält unter anderem einen Notenblock und einen Hebel. Für diese Inhalte wurden in der geprüften ZIP keine entsprechenden Blockstate-/Modell-/Texturdateien gefunden. Diese Elemente dürfen deshalb nicht mit erfundenen TS-Ressourcendaten vorgetäuscht werden. Für pixelgenaue Darstellung werden die passenden Originalressourcen benötigt.

### 6.4 Gameplay und Interaktion

Die Kernabbildung soll die für die jeweils beauftragten Features notwendigen Minecraft-Verhaltensweisen bereitstellen. Dazu gehören je nach Umsetzungsphase:

- Maus-/Tastatursteuerung, Blickrichtung und Bewegung;
- Raycasting auf Blöcke und Auswahl des getroffenen Blocks;
- Block platzieren/abbauen und Blockzustand aktualisieren;
- Hotbar-Auswahl und Anzeige gehaltener Items;
- Kollisionen und einfache Weltphysik;
- später BlockEntities, Entitäten, Partikel, Animationen, Sounds und weitere Spielsysteme, sofern tatsächlich benötigt.

Nicht implementierte Mechaniken dürfen nicht als vollständig vorhanden dargestellt werden. Netzwerk-/Server-Parität oder vollständige Multiplayer-Kompatibilität wird nicht automatisch vorausgesetzt; sie muss als eigener Scope beauftragt und geplant werden.

## 7. Shader, Fonts und GUI-Ressourcen

### 7.1 Shader

Die vorhandenen Shader sollen genutzt werden, aber nicht alle 200 Dateien müssen sofort oder überhaupt eingebunden werden.

Vorgehen:

1. Shader-JSONs und zugehörige `.vsh`-/`.fsh`-Dateien inventarisieren und anhand der tatsächlich verwendeten Renderpfade auswählen.
2. Zuerst die notwendigen Grundpfade implementieren: solide/opaque Blöcke, Cutout, transparente Materialien, Textur-Blit/GUI sowie erforderliche Licht- und Fog-Funktionen.
3. Minecraft-spezifische `#moj_import`-Direktiven und Uniform-/Attributverträge gezielt auflösen bzw. in Three.js-kompatible Shader übersetzen.
4. Three.js-Standardmaterialien bevorzugen, wenn sie das Verhalten hinreichend korrekt abbilden; `ShaderMaterial` nur dort verwenden, wo es für die Parität erforderlich ist.
5. Weitere Shader erst aufnehmen, wenn ein konkreter Block-, Entity-, Partikel- oder GUI-Pfad sie benötigt.

Ein bloßes Kopieren von Mojang-GLSL in ein Three.js-Material gilt nicht als erfolgreiche Integration. Shader-Version, Attribute, Uniforms, Includes und Render-Reihenfolge müssen zusammenpassen.

### 7.2 Fonts und GUI

- Die vorhandenen `assets/minecraft/font/*.json`-Definitionen und ihre referenzierten Provider als Quelle verwenden.
- Minecraft-Glyphen, Abstände, Zeilenhöhe, Unicode-Fallback und Text-Skalierung schrittweise korrekt umsetzen.
- Die vorhandenen GUI-Sprites für Hotbar, Slot, Crosshair, Hearts, Hunger, Armor und weitere HUD-Elemente verwenden.
- GUI-Sprites nicht durch CSS-Nachzeichnungen ersetzen, wenn die entsprechende Originaldatei vorhanden ist.
- GUI-Skalierung und Positionierung müssen auf unterschiedliche Viewport-Größen reagieren und dabei die Minecraft-Pixeloptik bewahren.

## 8. React-Frontend und Integration

Alle neuen React-Frontend-Inhalte werden unter `src/main/java/net/frontend/` erstellt.

- `Frontend.tsx` ist der React-App-Inhalt und darf in Komponenten/Features aufgeteilt werden.
- React-Komponenten importieren die Minecraft-Abbildung und deren Daten-/Render-APIs, anstatt Blockgeometrie, Texturauflösung oder Minecraft-Regeln eigenständig zu duplizieren.
- Für eine React-Komponente, die einen Minecraft-Block, ein Item oder eine Weltvorschau zeigt, wird derselbe Model-/Blockstate-/Texture-Resolver und derselbe Three.js-Renderer verwendet wie im Minecraft-Abbild.
- UI-Zustand und Minecraft-Weltzustand bleiben getrennt. Eine React-Auswahl darf keine zweite, widersprüchliche Blockdefinition erzeugen.
- Gemeinsame Frontend-Zustände können mit den bereits vorhandenen Projektwerkzeugen umgesetzt werden; eine neue State-Library ist nicht ohne begründeten Bedarf einzuführen.

Der Bootstrap bleibt in `src/main/java/net/neoforged/neoforge/modloader.tsx`. `index.html` lädt weiterhin genau diesen Einstieg. `net/neoforged/neoforge` enthält nur die dafür nötige Start-/Integrationsschicht und globale Styles, nicht den eigentlichen Feature-Content.

## 9. WitherUtils-Release-Code in `geni`

Dieser Bereich wird nur auf ausdrücklichen Auftrag zur Erstellung von Release-Code aktiviert.

- Bestehende Architektur in `geni.witherapi`, `geni.withercore` und `geni.witherutils` zuerst analysieren und verwenden.
- Vorhandene Registrierungs-, Basisklassen-, Helper-, GUI-, BlockEntity-, Netzwerk- und Renderer-Muster bevorzugen, statt ein konkurrierendes Framework zu bauen.
- Registrierungen und Java-Klassen folgen der existierenden NeoForge-1.21.1-Projektstruktur.
- Für neue Blöcke, Items, Menüs, Screens oder BlockEntities passende vorhandene Basisklassen und Registries verwenden.
- Mod-eigene Ressourcen gehören in den jeweiligen Mod-Namespace, insbesondere `src/main/resources/assets/witherutils/`; vorhandene `assets/minecraft`-Dateien bleiben Vanilla-Quelle und werden nicht als WitherUtils-Ablage missbraucht.
- Das TypeScript-Abbild ist Referenz für Verhalten und Darstellung. Java-Code muss trotzdem valide NeoForge-Java-Implementierung sein; TS-Klassen dürfen nicht mechanisch als Java ausgegeben werden.
- ATs, Mixins, GeckoLib und weitere bestehende Projektmechanismen entsprechend der vorhandenen `.aistudio/SYSTEM-PROMPT.md` und Codebasis einsetzen und Änderungen ausdrücklich benennen.
- Für Java-Release-Arbeiten die vorhandene Interaktionsregel beibehalten: erst Release/Test-Modus klären, Plan und technische Risiken vorstellen, auf Freigabe warten und erst danach implementieren.

Bestandshinweis: Das ZIP enthält eine umfangreiche `geni/withercore`-Codebasis. `geni/witherutils/client` und `geni/witherutils/common` enthalten in diesem Snapshot nur Platzhalterdateien; vor der ersten Release-Implementierung ist daher zu prüfen, welche konkreten WitherUtils-Klassen und Registrierungen fehlen.

## 10. Umsetzungsphasen und Meilensteine

Die Reihenfolge reduziert doppelte Arbeit und stellt sicher, dass das Frontend nicht auf nicht vorhandenen Minecraft-Daten aufbaut.

### Phase 0 – Projekt- und Ressourcen-Audit

- Projektpfade, bestehende Stubs, Einstiegspunkt, Build-Konfiguration und Ressourcendateien prüfen.
- Ressourcen über Namespace und Typ indizieren, ohne die Dateien in TS-Daten umzuwandeln.
- Fehlende Dateien und nicht abgedeckte Screenshot-Elemente dokumentieren.
- Ergebnis: belastbare Liste der tatsächlich unterstützten Blöcke, Items, Modelle, Texturen, Fonts und Shader.

**Abnahme:** Jeder unterstützte Inhalt lässt sich auf eine konkrete Ressourcendatei zurückführen; fehlende Inhalte sind sichtbar als fehlend markiert.

### Phase 1 – Ressourcen-Lader und Modellpipeline

- Zentralen Asset-Resolver für Vite Dev und Build umsetzen.
- JSON-Parser/Resolver für Blockstates, Modell-Eltern und Texturvariablen umsetzen.
- PNG-Lader und Material-/Texture-Caches erstellen.
- Erste Block- und Itemgeometrien aus Original-JSON erzeugen.

**Abnahme:** Beispielsweise `grass_block.json` wird mit beiden `snowy`-Zuständen und den Rotationsvarianten korrekt aufgelöst; Modellflächen, UVs, Texturen und Gras-Tinting werden aus den vorhandenen Originaldateien abgeleitet.

### Phase 2 – Three.js-Render-Vertical-Slice

- Szene, Kamera, Renderer und Ressourcenlebenszyklus aufbauen.
- Einen kleinen Testbereich ausschließlich datengetrieben aus vorhandenen Blöcken rendern.
- Blockkanten, Perspektive, Texturschärfe, Transparenz, Licht und Fog an der Referenz ausrichten.

**Abnahme:** Mindestens ein repräsentativer Bereich wird ohne handcodierte Blocktextur-/Modelldaten aus den Ressourcen gerendert; Resize und wiederholtes Laden erzeugen keine dauerhaft wachsenden GPU-Ressourcen.

### Phase 3 – Welt-/Chunk-System und First-Person

- Blockzustände und Chunk-Speicherung von Render-Meshes trennen.
- Chunk-Mesh-Erstellung, sichtbare Flächen und gezielte Aktualisierung implementieren.
- First-Person-Steuerung, Raycasting, Spielerhand und die benötigte einfache Interaktion ergänzen.

**Abnahme:** Der Nutzer kann sich in einer datengetriebenen Blockwelt umsehen; Änderungen an Blockzuständen aktualisieren nur die betroffenen Renderbereiche.

### Phase 4 – Minecraft-Look, HUD und ausgewählte Shader

- Himmel, Wolken, Umgebungslicht, Schatten/Fog und Vegetation gemäß verfügbarem Asset-Bestand ergänzen.
- Hotbar, Auswahlrahmen, Crosshair, Items und erforderliche Font-Provider einbinden.
- Die für diese Renderpfade notwendigen Shader auswählen und kompatibel integrieren.

**Abnahme:** Die Szene erreicht die visuelle Referenzqualität für alle durch vorhandene Ressourcen abgedeckten Elemente. Nicht vorhandene Ressourcen bleiben ausdrücklich als offene Abhängigkeit ausgewiesen.

### Phase 5 – React-Frontend auf dem Minecraft-Abbild

- React-Features in `net/frontend` implementieren.
- Minecraft-Blöcke, Items oder Weltvorschauen über dieselbe Daten- und Renderer-Pipeline anzeigen.
- `index.html` → `modloader.tsx` → `Frontend.tsx` als Startkette erhalten.

**Abnahme:** Eine neue React-Komponente kann einen vorhandenen Block/Item darstellen, ohne eigene Modelldaten oder einen separaten Renderer zu definieren.

### Phase 6 – Explizite WitherUtils-Release-Übertragung (optional)

- Nur nach ausdrücklichem Release-Auftrag starten.
- Die abgestimmte Funktion in Java-/NeoForge-Strukturen von `geni` umsetzen.
- Notwendige Registrierungen, Mod-Ressourcen, Helfer und ggf. ATs/Mixins ergänzen.

**Abnahme:** Java-Code kompiliert mit der vorgesehenen Projektkonfiguration und folgt den bestehenden WitherUtils-Konventionen. Diese Phase ist nicht Voraussetzung für die React-App oder das TypeScript-Minecraft-Abbild.

## 11. Qualitätssicherung und Abnahmekriterien

Jede Phase wird separat validiert:

- `npm run lint` für TypeScript-Typprüfung;
- `npm run build` für den Vite-Produktionsbuild;
- gezielte Tests für Ressourcenauflösung, JSON-Parsing, Parent-Vererbung, Blockstate-Auswahl, UV-Berechnung, Alpha-Modi und Tinting;
- visuelle Referenzprüfung mit identischer Kameraposition, vergleichbarer Auflösung und denselben geladenen Ressourcen;
- Prüfung auf fehlende Dateien, defekte Referenzen und unhandled JSON-Formen;
- bei Java-Release-Arbeiten zusätzlich `./gradlew compileJava` und die für das Feature passenden Gradle-/NeoForge-Checks.

**Definition of Done für einen Minecraft-Inhalt:**

1. Der Inhalt ist einer korrekten Block-/Item-/Resource-ID zugeordnet.
2. Die Daten stammen aus den Originalressourcen, sofern diese vorhanden sind.
3. Blockstate, Modell, Textur, Material und Renderer sind über die gemeinsame Pipeline verbunden.
4. Es existieren keine parallel gepflegten TS-Ressourcendaten.
5. Darstellung, Fehlerzustände und unterstützte Zustände sind geprüft und dokumentiert.

**Definition of Done für einen Frontend-Inhalt:**

1. Der neue React-Code liegt unter `net/frontend`.
2. Minecraft-Inhalte nutzen die gemeinsame Implementierung aus `net/minecraft`.
3. Der bestehende Einstieg über `index.html` und `modloader.tsx` bleibt funktionsfähig.
4. Es wurde kein unnötiger Java-Release-Code in `geni` erzeugt.

## 12. Erkannter Ist-Zustand des bereitgestellten ZIP-Snapshots

Die folgenden Angaben beziehen sich auf die untersuchte `AiStudio.zip`, nicht auf einen später veränderten Projektstand:

- `src/main/java/net/minecraft/` enthält **203 TypeScript-Dateien**. Im Snapshot sind diese Dateien sehr kleine Platzhalter (jeweils höchstens 110 Bytes), keine fertigen Implementierungen.
- `src/main/java/net/frontend/Frontend.tsx` rendert derzeit nur ein leeres schwarzes `<main>`.
- `src/main/java/net/neoforged/neoforge/modloader.tsx` startet React und importiert `Frontend.tsx` sowie `index.css`.
- `index.html` verweist bereits auf `/src/main/java/net/neoforged/neoforge/modloader.tsx`.
- `src/main/java/geni/` enthält eine umfangreiche bestehende Java-Codebasis in `withercore`; `geni/witherutils/client` und `geni/witherutils/common` sind im Snapshot noch Platzhalter.
- `src/main/resources/assets/minecraft/` enthält **387 Dateien**: 11 Blockstates, 36 Modell-JSONs, 7 Font-JSONs, 200 Shader-Dateien und 133 Textur-/Metadateien. Darunter befinden sich 16 Blocktexturen und 13 Itemtexturen.
- Die vorhandenen Blockstate-/Modelldateien decken nur einen Teil der Vanilla-Inhalte ab. Die Ressourcenbasis ist eine Grundlage, noch kein vollständiger Minecraft-1.21.1-Assetsatz.

Daraus folgt: Die erste technische Aufgabe ist nicht, sämtliche Minecraft-Klassen gleichzeitig zu implementieren, sondern eine funktionierende, getestete Ressourcen- und Modellpipeline aufzubauen und daran einen kleinen vertikalen Rendering-Slice nachzuweisen.

---

## 13. Verbindliche Arbeitsregel für die KI

Vor jeder Implementierung ist dieser Plan zu lesen und die Zielablage anhand des Task-Routings in Abschnitt 2 auszuwählen. Der existierende `.aistudio/SYSTEM-PROMPT.md` bleibt für WitherUtils-/NeoForge-Konventionen, ATs/Mixins, Test-/Release-Abläufe und bestehende Codebeispiele maßgeblich, wird aber durch diesen Plan hinsichtlich der Trennung von Minecraft-Abbild, React-Frontend und Java-Release-Code konkretisiert.

Keine Pfadvermischung, keine erfundenen TypeScript-Ressourcendaten, keine eigenmächtige Java-Release-Implementierung und keine Behauptung vollständiger 1:1-Parität ohne nachgewiesene Implementierung und Prüfung.
