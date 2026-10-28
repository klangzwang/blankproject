package geni.withercore.common.inventory.blockentity.item;

import geni.withercore.init.WUTComponents;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;

public class MachineInventoryLayout {

    private final List<SlotConfig> slots;

    private final int soulBankSlot;

    private MachineInventoryLayout(Builder builder)
    {
        this.slots = List.copyOf(builder.slots);
        this.soulBankSlot = builder.soulBankSlot;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getSlotCount() {
        return slots.size();
    }

    public boolean supportsSoulBank() {
        return soulBankSlot >= 0;
    }

    public int getSoulBankSlot() {
        return soulBankSlot;
    }

    public int getStackLimit(int slot) {
        return slots.get(slot).stackLimit;
    }

    public boolean canInsert(int slot) {
        return slots.get(slot).insert;
    }

    public boolean canExtract(int slot) {
        return slots.get(slot).extract;
    }

    public boolean guiCanInsert(int slot) {
        return slots.get(slot).guiInsert;
    }

    public boolean guiCanExtract(int slot) {
        return slots.get(slot).guiExtract;
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return slots.get(slot).filter.test(slot, stack);
    }

    public static class Builder {

        private final ArrayList<SlotConfig> slots = new ArrayList<>();
        private int currentStackLimit = 64;
        private int soulBankSlot = -1;
        
        private SlotAdditionInfo additionInfo = new SlotAdditionInfo(Integer.MIN_VALUE, Integer.MIN_VALUE);

        public Builder slot(UnaryOperator<SlotBuilder> builder) {
            additionInfo = new SlotAdditionInfo(slots.size(), 1);
            slots.add(builder.apply(new SlotBuilder()).build());
            return this;
        }

        public Builder setStackLimit(int limit) {
            currentStackLimit = limit;
            return this;
        }

        public Builder inputSlot() {
            return inputSlot(1, (i,s) -> true);
        }

        public Builder inputSlot(int count) {
            return inputSlot(count, (i,s) -> true);
        }

        public Builder inputSlot(BiPredicate<Integer, ItemStack> filter) {
            return inputSlot(1, filter);
        }

        public Builder inputSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            SlotAdditionInfo info = new SlotAdditionInfo(slots.size(), count);
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiInsert().guiExtract().insert().filter(filter).stackLimit(currentStackLimit));
            }
            additionInfo = info;
            return this;
        }

        public Builder outputSlot() {
            return outputSlot(1, (i,s) -> true);
        }

        public Builder outputSlot(int count) {
            return outputSlot(count, (i,s) -> true);
        }

        public Builder outputSlot(BiPredicate<Integer, ItemStack> filter) {
            return outputSlot(1, filter);
        }

        public Builder outputSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            SlotAdditionInfo info = new SlotAdditionInfo(slots.size(), count);
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiExtract().extract().filter(filter).stackLimit(currentStackLimit));
            }
            additionInfo = info;
            return this;
        }

        public Builder storageSlot() {
            return storageSlot(1, (i,s) -> true);
        }

        public Builder storageSlot(int count) {
            return storageSlot(count, (i,s) -> true);
        }

        public Builder storageSlot(BiPredicate<Integer, ItemStack> filter) {
            return storageSlot(1, filter);
        }

        public Builder storageSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            SlotAdditionInfo info = new SlotAdditionInfo(slots.size(), count);
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiInsert().guiExtract().insert().extract().filter(filter).stackLimit(currentStackLimit));
            }
            additionInfo = info;
            return this;
        }

        public Builder ghostSlot() {
            return ghostSlot(1, (i,s) -> true);
        }

        public Builder ghostSlot(int count) {
            return ghostSlot(count, (i,s) -> true);
        }

        public Builder ghostSlot(BiPredicate<Integer, ItemStack> filter) {
            return ghostSlot(1, filter);
        }

        public Builder ghostSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            SlotAdditionInfo info = new SlotAdditionInfo(slots.size(), count);
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiInsert().filter(filter).stackLimit(currentStackLimit));
            }
            additionInfo = info;
            return this;

        }

        public Builder soulbank()
        {
            if (soulBankSlot >= 0) {
                throw new IllegalStateException("A machine inventory may not have more than one capacitor slot!");
            }

            slot(slot -> slot.guiInsert().guiExtract().filter((i, s) -> s.has(WUTComponents.SOULBANK_DATA)).stackLimit(1));
            soulBankSlot = slots.size() - 1;
            return this;
        }
        
        public Builder previewSlot() {
            return previewSlot(1);
        }

        public Builder previewSlot(int count) {
            SlotAdditionInfo info = new SlotAdditionInfo(slots.size(), count);
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.stackLimit(currentStackLimit));
            }
            additionInfo = info;
            return this;
        }

        public Builder slotAccess(SingleSlotAccess access) {
            if (additionInfo.size == 1) {
                access.init(additionInfo.index);
            } else {
                throw new IllegalStateException("try to get a single slot access for multiple slots");
            }
            return this;
        }

        public Builder slotAccess(MultiSlotAccess multiSlotAccess) {
            if (additionInfo.size > 1) {
                multiSlotAccess.init(additionInfo.index, additionInfo.size);
            } else {
                throw new IllegalStateException("try to get multi slot access for a single slot");
            }
            return this;
        }

        public MachineInventoryLayout build() {
            return new MachineInventoryLayout(this);
        }

        public static class SlotBuilder {
            private boolean insert;
            private boolean extract;
            private boolean guiInsert;
            private boolean guiExtract;
            private int stackLimit = 64;

            private BiPredicate<Integer, ItemStack> filter = (i,s) -> true;

            private SlotBuilder() {}

            public SlotBuilder insert() {
                insert = true;
                return this;
            }

            public SlotBuilder extract() {
                extract = true;
                return this;
            }

            public SlotBuilder guiInsert() {
                guiInsert = true;
                return this;
            }

            public SlotBuilder guiExtract() {
                guiExtract = true;
                return this;
            }

            public SlotBuilder filter(BiPredicate<Integer, ItemStack> filter) {
                this.filter = filter;
                return this;
            }

            public SlotBuilder stackLimit(int limit) {
                this.stackLimit = Math.max(Math.min(limit, 64), 0);
                return this;
            }

            private SlotConfig build() {
                return new SlotConfig(insert, extract, guiInsert, guiExtract, stackLimit, filter);
            }
        }
    }

    private record SlotAdditionInfo(int index, int size) {}
    private record SlotConfig(boolean insert, boolean extract, boolean guiInsert, boolean guiExtract, int stackLimit, BiPredicate<Integer, ItemStack> filter) {}
}
