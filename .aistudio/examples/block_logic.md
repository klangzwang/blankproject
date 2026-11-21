## Block-Logik-Klassen (`BlockCollector.java`)

> \[!IMPORTANT\]
> Blöcke selbst sind Singletons; zustandsbehaftete Daten dürfen niemals direkt in der Block-Klasse gespeichert werden.

* **Zweck**: Definition der physischen Eigenschaften, Hitboxen, BlockStates und Interaktionen (z. B. Rechtsklick).

* **Besonderheiten**: Implementiert häufig `EntityBlock`, um eine zugehörige `BlockEntity` zu instanziieren.

* **Architektur**: Delegiert komplexe Logik und Tick-Berechnungen an die verknüpfte `BlockEntity`.

```java
public class BlockCollector extends BaseEntityBlock {
    public static final MapCodec<BlockCollector> CODEC = simpleCodec(BlockCollector::new);

    public BlockCollector(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CollectorBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MenuProvider provider) {
            player.openMenu(provider, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, WUTBlockEntities.COLLECTOR.get(), CollectorBlockEntity::serverTick);
    }
}
```
