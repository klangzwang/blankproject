package geni.witherutils.common.menu;

import java.util.ArrayList;
import java.util.List;

import geni.withercore.init.WUTMenus;
import geni.withercore.init.WUTRecipes;
import geni.witherdata.recipes.CutterRecipe;
import geni.witherutils.common.world.item.CutterItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public class CutterMenu extends AbstractContainerMenu {

    public static final int INPUT_SLOT = 0;
    public static final int RESULT_SLOT = 1;
    private static final int INV_SLOT_START = 2;
    private static final int INV_SLOT_END = 29;
    private static final int USE_ROW_SLOT_START = 29;
    private static final int USE_ROW_SLOT_END = 38;

    private final ContainerLevelAccess access;
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final Level level;
    private List<RecipeHolder<CutterRecipe>> recipes = new ArrayList<>();
    private ItemStack input = ItemStack.EMPTY;
    long lastSoundTime;

    public final Slot inputSlot;
    public final Slot resultSlot;
    private Runnable slotUpdateListener = () -> {};

    public final Container container = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            CutterMenu.this.slotsChanged(this);
            CutterMenu.this.slotUpdateListener.run();
        }
    };

    public final ResultContainer resultContainer = new ResultContainer();

    public CutterMenu(int windowId, Inventory playerInventory) {
        this(windowId, playerInventory, ContainerLevelAccess.NULL);
    }

    public CutterMenu(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, extraData, ContainerLevelAccess.NULL);
    }

    public CutterMenu(int windowId, Inventory playerInventory, FriendlyByteBuf extraData, ContainerLevelAccess access) {
        this(windowId, playerInventory, access);
    }

    public CutterMenu(int windowId, Inventory playerInventory, ContainerLevelAccess access) {
        super(WUTMenus.CUTTER.get(), windowId);
        this.access = access;
        this.level = playerInventory.player.level();

        this.inputSlot = this.addSlot(new Slot(this.container, 0, 20, 33));

        this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player.level(), player, stack.getCount());
                CutterMenu.this.resultContainer.awardUsedRecipes(player, this.getRelevantItems());

                ItemStack inputStack = CutterMenu.this.inputSlot.remove(1);

                damageCutter(player);

                access.execute((lvl, pos) -> {
                    long gameTime = lvl.getGameTime();
                    if (CutterMenu.this.lastSoundTime != gameTime) {
                        lvl.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        CutterMenu.this.lastSoundTime = gameTime;
                    }
                });

                CutterMenu.this.setupResultSlot();

                super.onTake(player, stack);
            }

            private List<ItemStack> getRelevantItems() {
                return List.of(CutterMenu.this.inputSlot.getItem());
            }
        });

        // 2..28: Player Inventory (3 rows of 9 slots at x=8, y=84)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // 29..37: Player Hotbar (1 row of 9 slots at x=8, y=142)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addDataSlot(this.selectedRecipeIndex);
    }

    private void damageCutter(Player player) {
        if (player.level().isClientSide) return;
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        ItemStack cutter = ItemStack.EMPTY;
        if (main.getItem() instanceof CutterItem) {
            cutter = main;
        } else if (off.getItem() instanceof CutterItem) {
            cutter = off;
        } else {
            for (ItemStack s : player.getInventory().items) {
                if (s.getItem() instanceof CutterItem) {
                    cutter = s;
                    break;
                }
            }
        }
        if (!cutter.isEmpty() && player instanceof ServerPlayer sp) {
            cutter.hurtAndBreak(1, sp, sp.getEquipmentSlotForItem(cutter));
        }
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public List<RecipeHolder<CutterRecipe>> getRecipes() {
        return this.recipes;
    }

    public int getNumRecipes() {
        return this.recipes.size();
    }

    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.recipes.isEmpty();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (this.isValidRecipeIndex(id)) {
            this.selectedRecipeIndex.set(id);
            this.setupResultSlot();
        }
        return true;
    }

    private boolean isValidRecipeIndex(int index) {
        return index >= 0 && index < this.recipes.size();
    }

    @Override
    public void slotsChanged(Container container) {
        ItemStack itemstack = this.inputSlot.getItem();
        if (!ItemStack.isSameItemSameComponents(itemstack, this.input)) {
            this.input = itemstack.copy();
            this.setupRecipeList(container, itemstack);
        } else if (itemstack.isEmpty()) {
            this.input = ItemStack.EMPTY;
            this.setupRecipeList(container, itemstack);
        } else {
            this.setupResultSlot();
        }
    }

    public void findMatchingCutterRecipe(Container container) {
        ItemStack stack = this.inputSlot.getItem();
        if (!ItemStack.matches(stack, this.input)) {
            this.input = stack.copy();
            this.setupRecipeList(container, stack);
        }
    }

    private void setupRecipeList(Container container, ItemStack stack) {
        this.recipes.clear();
        if (!stack.isEmpty()) {
            this.recipes = this.level.getRecipeManager().getRecipesFor(
                    WUTRecipes.CUTTER.type().get(),
                    new CutterRecipe.RecipeIn(stack),
                    this.level
            );
            if (!this.recipes.isEmpty()) {
                if (this.selectedRecipeIndex.get() < 0 || this.selectedRecipeIndex.get() >= this.recipes.size()) {
                    this.selectedRecipeIndex.set(0);
                }
                this.setupResultSlot();
            } else {
                this.selectedRecipeIndex.set(-1);
                this.resultSlot.set(ItemStack.EMPTY);
            }
        } else {
            this.selectedRecipeIndex.set(-1);
            this.resultSlot.set(ItemStack.EMPTY);
        }
    }

    void setupResultSlot() {
        if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            RecipeHolder<CutterRecipe> recipeholder = this.recipes.get(this.selectedRecipeIndex.get());
            ItemStack itemstack = recipeholder.value().assemble(
                    new CutterRecipe.RecipeIn(this.inputSlot.getItem()),
                    this.level.registryAccess()
            );
            if (!itemstack.isEmpty()) {
                this.resultContainer.setRecipeUsed(recipeholder);
                this.resultSlot.set(itemstack);
            } else {
                this.resultSlot.set(ItemStack.EMPTY);
            }
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }
        this.broadcastChanges();
    }

    public void registerUpdateListener(Runnable listener) {
        this.slotUpdateListener = listener;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (index == RESULT_SLOT) {
                item.onCraftedBy(itemstack1, player.level(), player);
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index == INPUT_SLOT) {
                if (!this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= INV_SLOT_START && index < USE_ROW_SLOT_END) {
                List<RecipeHolder<CutterRecipe>> r = this.level.getRecipeManager().getRecipesFor(
                        WUTRecipes.CUTTER.type().get(),
                        new CutterRecipe.RecipeIn(itemstack1),
                        this.level
                );
                if (!r.isEmpty()) {
                    if (!this.moveItemStackTo(itemstack1, INPUT_SLOT, RESULT_SLOT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= INV_SLOT_START && index < USE_ROW_SLOT_START) {
                    if (!this.moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= USE_ROW_SLOT_START && index < USE_ROW_SLOT_END && !this.moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_START, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            }

            slot.setChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            this.broadcastChanges();
        }

        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(1);
        this.clearContainer(player, this.container);
    }
}
