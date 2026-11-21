## BlockEntity- & Maschinen-Klassen (`CollectorBlockEntity.java`)

> \[!IMPORTANT\]
> Server- und Client-Synchronisation muss explizit über `getUpdatePacket()` und `handleUpdateTag()` erfolgen.

* **Zweck**: Speicherung von Inventaren, Energie (FE), Flüssigkeiten sowie Ausführung der eigentlichen Maschinenlogik pro Tick.

* **Besonderheiten**: Nutzt NeoForge-Capabilities (`IItemHandler`, `IEnergyStorage`) und verwaltet NBT-Serialisierung (`saveAdditional` / `loadAdditional`).

* **Architektur**: Erbt oft von mod-internen Basisklassen (`WitherMachineBlockEntity`), um Energie- und Redstone-Logik wiederzuverwenden.

```java
public class CollectorBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler inventory = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public CollectorBlockEntity(BlockPos pos, BlockState state) {
        super(WUTBlockEntities.COLLECTOR.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CollectorBlockEntity be) {
        if (level.getGameTime() % 20 == 0) {
            be.collectNearbyItems();
        }
    }

    private void collectNearbyItems() {
        // Items im Umkreis einsammeln und in 'inventory' einfügen
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.witherutils.collector");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new CollectorMenu(id, playerInv, this);
    }
}
```
