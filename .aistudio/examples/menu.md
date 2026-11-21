## Container- & Menü-Klassen (`CollectorMenu.java`)

> \[!IMPORTANT\]
> Synchronisiert Inventar-Slots und Daten-Variablen sicher zwischen Server-Logik und Client-GUI.

* **Zweck**: Verwaltung der Slot-Positionen (Spieler-Inventar vs. Maschinen-Inventar) und Shift-Klick-Logik (`quickMoveStack`).

* **Besonderheiten**: Arbeitet eng mit `ContainerData` zusammen, um Fortschrittsbalken oder Energiestände ohne eigene Netzwerkpakete zu übertragen.

* **Architektur**: Wird auf dem Server über `MenuProvider` geöffnet und auf dem Client mit dem passenden `Screen` verknüpft.

```java
public class CollectorMenu extends AbstractContainerMenu {
    private final CollectorBlockEntity blockEntity;

    public CollectorMenu(int containerId, Inventory playerInv, FriendlyByteBuf extraData) {
        this(containerId, playerInv, (CollectorBlockEntity) playerInv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public CollectorMenu(int containerId, Inventory playerInv, CollectorBlockEntity blockEntity) {
        super(WUTMenus.COLLECTOR.get(), containerId);
        this.blockEntity = blockEntity;

        // Maschinen-Slots (3x3)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(blockEntity.getInventory(), col + row * 3, 62 + col * 18, 17 + row * 18));
            }
        }
        addPlayerInventory(playerInv);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), player, WUTBlocks.COLLECTOR.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // Standard Shift-Klick Transfer-Logik zwischen Inventar und Maschine
        return ItemStack.EMPTY;
    }
}
```
