package geni.withercore.common.menu.blockentity.slot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Ermöglicht die gemeinsame Verwaltung (Aktivierung, Sperrung) mehrerer Slots.
 */
public class WitherSlotGroup {

    private final String name;
    private final List<WitherMachineSlot> slots = new ArrayList<>();
    private boolean active = true;
    private boolean locked = false;

    public WitherSlotGroup(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public WitherSlotGroup addSlot(WitherMachineSlot slot)
    {
        slots.add(slot);
        slot.setActive(this.active);
        slot.setLocked(this.locked);
        return this;
    }

    public List<WitherMachineSlot> getSlots()
    {
        return Collections.unmodifiableList(slots);
    }

    public void setActive(boolean active)
    {
        this.active = active;
        for (WitherMachineSlot slot : slots)
        {
            slot.setActive(active);
        }
    }

    public boolean isActive()
    {
        return active;
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
        for (WitherMachineSlot slot : slots)
        {
            slot.setLocked(locked);
        }
    }

    public boolean isLocked()
    {
        return locked;
    }

    public void forEach(Consumer<WitherMachineSlot> action)
    {
        slots.forEach(action);
    }
}

