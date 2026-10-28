package geni.withercore.common.network.menu;

import geni.withercore.common.network.menu.payload.SlotPayload;
import net.minecraft.world.level.Level;

/**
 * Inspired by Mekanism's SyncableData system.
 */
public interface SyncSlot {
    ChangeType detectChanges();

    SlotPayload createPayload(Level level, ChangeType changeType);

    void unpackPayload(Level level, SlotPayload payload);

    enum ChangeType {
        NONE, PARTIAL, FULL
    }
}
