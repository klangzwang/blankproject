## Datengenerierungs-Klassen (`WUTBlockStateProvider.java`)

> \[!IMPORTANT\]
> Wird nicht im regulären Spiel ausgeführt, sondern über den `runData`-Gradle-Task gestartet.

* **Zweck**: Automatische Generierung von JSON-Dateien für BlockStates, Modelle, Loot-Tables, Rezepte und Tags.

* **Besonderheiten**: Verhindert Tippfehler in hunderten JSON-Dateien durch programmatische Erstellung während der Entwicklung.

* **Architektur**: Wird über das `GatherDataEvent` registriert und schreibt die fertigen Ressourcen direkt in den `src/generated`-Ordner.

```java
public class WUTBlockStateProvider extends BlockStateProvider {
    public WUTBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WitherUtils.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(WUTBlocks.COLLECTOR.get(), cubeAll(WUTBlocks.COLLECTOR.get()));
    }
}
```
