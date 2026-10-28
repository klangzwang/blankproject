package geni.withercore.common.inventory.entity.item;

import geni.withercore.init.WUTComponents;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;

public class EntityInventoryLayout {

    private final List<SlotConfig> slots;
    private final int soulBankSlot;

    private EntityInventoryLayout(Builder builder) {
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

        public Builder slot(UnaryOperator<SlotBuilder> builder) {
            slots.add(builder.apply(new SlotBuilder()).build());
            return this;
        }

        public Builder setStackLimit(int limit) {
            currentStackLimit = limit;
            return this;
        }

        public Builder inputSlot() {
            return inputSlot(1, (i, s) -> true);
        }

        public Builder inputSlot(int count) {
            return inputSlot(count, (i, s) -> true);
        }

        public Builder inputSlot(BiPredicate<Integer, ItemStack> filter) {
            return inputSlot(1, filter);
        }

        public Builder inputSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiInsert().guiExtract().insert().filter(filter).stackLimit(currentStackLimit));
            }
            return this;
        }

        public Builder outputSlot() {
            return outputSlot(1, (i, s) -> true);
        }

        public Builder outputSlot(int count) {
            return outputSlot(count, (i, s) -> true);
        }

        public Builder outputSlot(BiPredicate<Integer, ItemStack> filter) {
            return outputSlot(1, filter);
        }

        public Builder outputSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiExtract().extract().filter(filter).stackLimit(currentStackLimit));
            }
            return this;
        }

        public Builder storageSlot() {
            return storageSlot(1, (i, s) -> true);
        }

        public Builder storageSlot(int count) {
            return storageSlot(count, (i, s) -> true);
        }

        public Builder storageSlot(BiPredicate<Integer, ItemStack> filter) {
            return storageSlot(1, filter);
        }

        public Builder storageSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiInsert().guiExtract().insert().extract().filter(filter).stackLimit(currentStackLimit));
            }
            return this;
        }

        public Builder ghostSlot() {
            return ghostSlot(1, (i, s) -> true);
        }

        public Builder ghostSlot(int count) {
            return ghostSlot(count, (i, s) -> true);
        }

        public Builder ghostSlot(BiPredicate<Integer, ItemStack> filter) {
            return ghostSlot(1, filter);
        }

        public Builder ghostSlot(int count, BiPredicate<Integer, ItemStack> filter) {
            for (int i = 0; i < count; i++) {
                slot(slot -> slot.guiInsert().filter(filter).stackLimit(currentStackLimit));
            }
            return this;
        }

        public Builder soulbank() {
            if (soulBankSlot >= 0) {
                throw new IllegalStateException("An entity inventory may not have more than one soulbank slot!");
            }
            slot(slot -> slot.guiInsert().guiExtract().filter((i, s) -> s.has(WUTComponents.SOULBANK_DATA)).stackLimit(1));
            soulBankSlot = slots.size() - 1;
            return this;
        }

        public EntityInventoryLayout build() {
            return new EntityInventoryLayout(this);
        }
    }

    public static class SlotBuilder {

        private int stackLimit = 64;
        private boolean insert = false;
        private boolean extract = false;
        private boolean guiInsert = false;
        private boolean guiExtract = false;
        private BiPredicate<Integer, ItemStack> filter = (i, s) -> true;

        public SlotBuilder stackLimit(int stackLimit) {
            this.stackLimit = stackLimit;
            return this;
        }

        public SlotBuilder insert() {
            this.insert = true;
            return this;
        }

        public SlotBuilder extract() {
            this.extract = true;
            return this;
        }

        public SlotBuilder guiInsert() {
            this.guiInsert = true;
            return this;
        }

        public SlotBuilder guiExtract() {
            this.guiExtract = true;
            return this;
        }

        public SlotBuilder filter(BiPredicate<Integer, ItemStack> filter) {
            this.filter = filter;
            return this;
        }

        SlotConfig build() {
            return new SlotConfig(stackLimit, insert, extract, guiInsert, guiExtract, filter);
        }
    }

    public record SlotConfig(
            int stackLimit,
            boolean insert,
            boolean extract,
            boolean guiInsert,
            boolean guiExtract,
            BiPredicate<Integer, ItemStack> filter
    ) {
    }
}
