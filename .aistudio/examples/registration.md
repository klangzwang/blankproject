## Registrierungs- & Setup-Klassen (`WUTBlocks.java`)

> \[!IMPORTANT\]
> Alle Spielobjekte müssen über `DeferredRegister` registriert werden, um Lade-Reihenfolge-Konflikte zu vermeiden.

* **Zweck**: Zentrale Deklaration und Bereitstellung von Blöcken, BlockEntities, Items oder Rezepten.

* **Besonderheiten**: Kapselt die Erstellung von `BlockItem`-Instanzen und die Zuweisung zu Creative-Tabs.

* **Architektur**: Verwendet statische `DeferredHolder`-Referenzen für den typsicheren Zugriff im gesamten Projekt.

```java
public class WUTBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WitherUtils.MODID);

    public static final DeferredBlock<BlockCollector> COLLECTOR = registerBlockWithItem(
        "collector",
        () -> new BlockCollector(BlockBehaviour.Properties.of().strength(3.5F).requiresCorrectToolForDrops())
    );

    private static <T extends Block> DeferredBlock<T> registerBlockWithItem(String name, Supplier<T> blockSupplier) {
        DeferredBlock<T> block = BLOCKS.register(name, blockSupplier);
        WUTItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
```
