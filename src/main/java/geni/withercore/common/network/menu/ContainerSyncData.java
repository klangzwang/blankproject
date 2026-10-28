package geni.withercore.common.network.menu;

import java.util.List;

public interface ContainerSyncData {
    List<SyncSlot> syncSlots();

    default List<SyncSlot> updatableSyncSlots() {
        return List.of();
    }
}
