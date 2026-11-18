package geni.withercore.common.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Hilfsklasse zum Auffinden und Befüllen von nahegelegenen Inventaren (Nearby Storage, Abschnitt 3.5).
 */
public class NearbyStorageHelper {

    public static final int DEFAULT_RADIUS = 6;

    /**
     * Verschiebt passende Items aus dem Spielerinventar in erreichbare Kisten/Inventare im Umkreis.
     * Nur Items, die im Ziel bereits vorhanden sind (Stacking), werden übertragen (Quick-Store).
     *
     * @return die Gesamtzahl der erfolgreich übertragenen Items.
     */
    public static int transferToNearbyStorages(ServerPlayer player, int radius) {
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();
        int transferredTotal = 0;

        List<IItemHandler> nearbyHandlers = findNearbyItemHandlers(level, playerPos, radius);
        if (nearbyHandlers.isEmpty()) {
            return 0;
        }

        Inventory inv = player.getInventory();
        // Nur Hauptinventar durchsuchen (Slot 9 bis 35, Hotbar 0 bis 8 optional geschützt)
        for (int i = 9; i < 36; i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            // Suche ein Zielinventar, das dieses Item bereits enthält
            for (IItemHandler handler : nearbyHandlers) {
                if (containsItem(handler, stack)) {
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack.copy(), false);
                    int moved = stack.getCount() - remainder.getCount();
                    if (moved > 0) {
                        transferredTotal += moved;
                        inv.setItem(i, remainder);
                        stack = remainder;
                        if (stack.isEmpty()) break;
                    }
                }
            }
        }

        if (transferredTotal > 0) {
            player.containerMenu.broadcastChanges();
        }

        return transferredTotal;
    }

    /**
     * Findet alle erreichbaren Blockinventare in geladenen Chunks innerhalb des Radius.
     */
    public static List<IItemHandler> findNearbyItemHandlers(Level level, BlockPos origin, int radius) {
        List<IItemHandler> result = new ArrayList<>();
        int r = Math.min(Math.max(1, radius), 16);

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);

                    if (!level.hasChunkAt(cursor)) continue;

                    for (Direction side : Direction.values()) {
                        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, cursor, side);
                        if (handler != null && !result.contains(handler)) {
                            result.add(handler);
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static boolean containsItem(IItemHandler handler, ItemStack match) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack inSlot = handler.getStackInSlot(i);
            if (!inSlot.isEmpty() && ItemStack.isSameItemSameComponents(inSlot, match)) {
                return true;
            }
        }
        return false;
    }
}

