## Haupt- & Initialisierungsklassen (`WitherUtils.java`)

> \[!IMPORTANT\]
> Steuert den gesamten Lebenszyklus der Mod und fungiert als zentraler Einstiegspunkt für NeoForge.

* **Zweck**: Registrierung von Event-Bus-Listenern, Konfigurationen und Netzwerk-Paketen.

* **Besonderheiten**: Nutzt die `@Mod`-Annotation und verknüpft die `DeferredRegister`-Instanzen mit dem Mod-Event-Bus.

* **Architektur**: Dient als Brücke zwischen der NeoForge-API und den internen Mod-Systemen.

```java
@Mod(WitherUtils.MODID)
public class WitherUtils {
    public static final String MODID = "witherutils";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WitherUtils(IEventBus modEventBus, ModContainer modContainer) {
        WUTBlocks.BLOCKS.register(modEventBus);
        WUTItems.ITEMS.register(modEventBus);
        WUTBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        WUTMenus.MENUS.register(modEventBus);
        WUTRecipes.SERIALIZERS.register(modEventBus);

        modEventBus.addListener(WUTNetwork::register);
        modContainer.registerConfig(ModConfig.Type.COMMON, WUTConfig.SPEC);
    }
}
```
