package geni.withercore.client.gui.widget;

import codechicken.lib.gui.modular.lib.GuiRender;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Specialized Dropdown Menu populated directly with Vanilla Registries or Class types
 * (such as Blocks, EntityTypes, Items). Displays purely text entries and features
 * an integrated real-time search input field for effortless selection.
 */
public class WitherDropdownClassMenu<T> extends WitherDropdownMenu<T> {

    protected final List<DropdownItem<T>> allItems = new ArrayList<>();
    protected String searchFilter = "";
    protected int searchBoxHeight = 16;
    protected boolean searchFocused = false;

    public WitherDropdownClassMenu(int x, int y, int width, int height, Component placeholder) {
        super(x, y, width, height, placeholder);
        this.maxVisibleItems = 7;
    }

    /**
     * Factory for Minecraft Blocks (BuiltInRegistries.BLOCK)
     */
    public static WitherDropdownClassMenu<Block> forBlocks(int x, int y, int width, int height, Consumer<Block> onSelect) {
        WitherDropdownClassMenu<Block> menu = new WitherDropdownClassMenu<>(x, y, width, height, Component.literal("Select Block..."));
        menu.populateFromRegistry(BuiltInRegistries.BLOCK, block -> {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
            String name = block.getName().getString();
            return Component.literal(name + " (" + id.getPath() + ")");
        });
        menu.setOnSelect(onSelect);
        return menu;
    }

    /**
     * Factory for Minecraft EntityTypes (BuiltInRegistries.ENTITY_TYPE)
     */
    public static WitherDropdownClassMenu<EntityType<?>> forEntityTypes(int x, int y, int width, int height, Consumer<EntityType<?>> onSelect) {
        WitherDropdownClassMenu<EntityType<?>> menu = new WitherDropdownClassMenu<>(x, y, width, height, Component.literal("Select Entity..."));
        menu.populateFromRegistry(BuiltInRegistries.ENTITY_TYPE, entityType -> {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            String name = entityType.getDescription().getString();
            return Component.literal(name + " (" + id.getPath() + ")");
        });
        menu.setOnSelect(onSelect);
        return menu;
    }

    /**
     * Factory for Minecraft Items (BuiltInRegistries.ITEM)
     */
    public static WitherDropdownClassMenu<Item> forItems(int x, int y, int width, int height, Consumer<Item> onSelect) {
        WitherDropdownClassMenu<Item> menu = new WitherDropdownClassMenu<>(x, y, width, height, Component.literal("Select Item..."));
        menu.populateFromRegistry(BuiltInRegistries.ITEM, item -> {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            String name = item.getDescription().getString();
            return Component.literal(name + " (" + id.getPath() + ")");
        });
        menu.setOnSelect(onSelect);
        return menu;
    }

    /**
     * Factory for an arbitrary Vanilla Registry
     */
    public static <E> WitherDropdownClassMenu<E> forRegistry(Registry<E> registry, int x, int y, int width, int height, Function<E, Component> labelProvider, Consumer<E> onSelect) {
        WitherDropdownClassMenu<E> menu = new WitherDropdownClassMenu<>(x, y, width, height, Component.literal("Select..."));
        menu.populateFromRegistry(registry, labelProvider);
        menu.setOnSelect(onSelect);
        return menu;
    }

    /**
     * Factory for a list of Java Classes
     */
    public static <C> WitherDropdownClassMenu<Class<? extends C>> forClasses(List<Class<? extends C>> classList, int x, int y, int width, int height, Consumer<Class<? extends C>> onSelect) {
        WitherDropdownClassMenu<Class<? extends C>> menu = new WitherDropdownClassMenu<>(x, y, width, height, Component.literal("Select Class..."));
        for (Class<? extends C> clazz : classList) {
            String simpleName = clazz.getSimpleName();
            menu.allItems.add(new DropdownItem<>(clazz, Component.literal(simpleName)));
        }
        menu.applyFilter();
        menu.setOnSelect(onSelect);
        return menu;
    }

    public <E extends T> void populateFromRegistry(Registry<E> registry, Function<E, Component> labelProvider) {
        this.allItems.clear();
        for (E entry : registry) {
            Component label = labelProvider != null ? labelProvider.apply(entry) : Component.literal(entry.toString());
            this.allItems.add(new DropdownItem<>(entry, label));
        }
        applyFilter();
    }

    public void setSearchFilter(String filter) {
        this.searchFilter = filter != null ? filter : "";
        applyFilter();
    }

    private void applyFilter() {
        this.items.clear();
        String lower = searchFilter.trim().toLowerCase(Locale.ROOT);
        if (lower.isEmpty()) {
            this.items.addAll(allItems);
        } else {
            for (DropdownItem<T> item : allItems) {
                String labelStr = item.label.getString().toLowerCase(Locale.ROOT);
                if (labelStr.contains(lower)) {
                    this.items.add(item);
                }
            }
        }
        this.scrollOffset = 0;
    }

    @Override
    public int getPopupHeight() {
        int visibleCount = Math.min(items.size(), maxVisibleItems);
        return searchBoxHeight + (visibleCount * itemHeight) + 4;
    }

    @Override
    public void setOpen(boolean open) {
        super.setOpen(open);
        if (open) {
            this.searchFocused = true;
        } else {
            this.searchFilter = "";
            this.searchFocused = false;
            applyFilter();
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (isOpen && searchFocused && (Character.isLetterOrDigit(codePoint) || codePoint == '_' || codePoint == ':' || codePoint == ' ' || codePoint == '-')) {
            this.searchFilter += codePoint;
            applyFilter();
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isOpen) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (!searchFilter.isEmpty()) {
                    searchFilter = searchFilter.substring(0, searchFilter.length() - 1);
                    applyFilter();
                }
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                if (!items.isEmpty()) {
                    DropdownItem<T> first = items.get(0);
                    this.selectedValue = first.value;
                    setOpen(false);
                    if (this.onSelect != null) {
                        this.onSelect.accept(first.value);
                    }
                }
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                setOpen(false);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (!this.active) return;

        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        // Click on Trigger Box
        if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
            setOpen(!this.isOpen);
            return;
        }

        // Click inside Popup Area
        if (isOpen && isMouseOverPopup(mouseX, mouseY)) {
            int py = y + h + 2;
            int searchY = py + 2;

            // Clicked inside Search Filter field
            if (mouseY >= searchY && mouseY <= searchY + searchBoxHeight) {
                this.searchFocused = true;
                return;
            }

            // Clicked inside List of entries
            int listY = py + searchBoxHeight + 2;
            int relY = (int) (mouseY - listY);
            if (relY >= 0) {
                int clickedIdx = scrollOffset + (relY / itemHeight);
                if (clickedIdx >= 0 && clickedIdx < items.size()) {
                    DropdownItem<T> item = items.get(clickedIdx);
                    if (!item.disabled) {
                        this.selectedValue = item.value;
                        setOpen(false);
                        if (this.onSelect != null) {
                            this.onSelect.accept(item.value);
                        }
                    }
                }
            }
            return;
        }

        // Clicked outside
        if (isOpen) {
            setOpen(false);
        }
    }

    @Override
    protected void renderPopup(GuiGraphics guiGraphics, GuiRender render, Font font, int mouseX, int mouseY) {
        int px = getX();
        int py = getY() + getHeight() + 2;
        int pw = getWidth();
        int ph = getPopupHeight();

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 300);

        // Main Popup Frame
        guiGraphics.fill(px, py, px + pw, py + ph, 0xF810121A);
        guiGraphics.fill(px, py, px + pw, py + 1, 0xFF2C2F3D);
        guiGraphics.fill(px, py + ph - 1, px + pw, py + ph, 0xFF2C2F3D);
        guiGraphics.fill(px, py + 1, px + 1, py + ph - 1, 0xFF2C2F3D);
        guiGraphics.fill(px + pw - 1, py + 1, px + pw, py + ph - 1, 0xFF2C2F3D);

        // 1. Integrated Search Filter Box ("einfach ein normales Text Feld")
        int sfX = px + 2;
        int sfY = py + 2;
        int sfW = pw - 4;
        int sfH = searchBoxHeight - 2;

        guiGraphics.fill(sfX, sfY, sfX + sfW, sfY + sfH, 0xFF141620);
        int sfBorder = searchFocused ? 0xFF00E5FF : 0xFF2A2D3B;
        guiGraphics.fill(sfX, sfY, sfX + sfW, sfY + 1, sfBorder);
        guiGraphics.fill(sfX, sfY + sfH - 1, sfX + sfW, sfY + sfH, sfBorder);
        guiGraphics.fill(sfX, sfY + 1, sfX + 1, sfY + sfH - 1, sfBorder);
        guiGraphics.fill(sfX + sfW - 1, sfY + 1, sfX + sfW, sfY + sfH - 1, sfBorder);

        String displayFilter = searchFilter.isEmpty() ? (searchFocused ? "" : "Search...") : searchFilter;
        int filterColor = searchFilter.isEmpty() ? 0xFF52525B : 0xFFE4E4E7;

        // Scissor for search text
        guiGraphics.enableScissor(sfX + 2, sfY + 1, sfX + sfW - 2, sfY + sfH - 1);
        guiGraphics.drawString(font, displayFilter, sfX + 4, sfY + (sfH - 8) / 2, filterColor, false);

        // Blinking cursor in search field
        if (searchFocused && ((System.currentTimeMillis() / 450) % 2 == 0)) {
            int curX = sfX + 4 + font.width(searchFilter);
            guiGraphics.fill(curX, sfY + 2, curX + 1, sfY + sfH - 2, 0xFF00E5FF);
        }
        guiGraphics.disableScissor();

        // 2. Purely Text-based List items
        int listY = py + searchBoxHeight + 2;
        int visibleCount = Math.min(items.size(), maxVisibleItems);

        if (items.isEmpty()) {
            guiGraphics.drawString(font, "No matches found", px + 6, listY + 3, 0xFF71717A, false);
        } else {
            int itemY = listY;
            for (int i = 0; i < visibleCount; i++) {
                int itemIdx = scrollOffset + i;
                if (itemIdx >= items.size()) break;

                DropdownItem<T> item = items.get(itemIdx);
                boolean itemHovered = mouseX >= px && mouseX <= px + pw && mouseY >= itemY && mouseY < itemY + itemHeight;
                boolean isItemActive = (this.selectedValue != null && this.selectedValue.equals(item.value));

                if (itemHovered) {
                    guiGraphics.fill(px + 1, itemY, px + pw - 1, itemY + itemHeight, 0xFF222634);
                }

                if (isItemActive) {
                    guiGraphics.fill(px + 1, itemY, px + 3, itemY + itemHeight, 0xFF00E5FF);
                }

                int itemTextCol = itemHovered ? 0xFFFFFFFF : (isItemActive ? 0xFF00E5FF : 0xFFD4D4D8);

                // Scissor each text line inside dropdown width
                guiGraphics.enableScissor(px + 4, itemY, px + pw - (items.size() > maxVisibleItems ? 8 : 4), itemY + itemHeight);
                guiGraphics.drawString(font, item.label, px + 6, itemY + (itemHeight - 8) / 2, itemTextCol, false);
                guiGraphics.disableScissor();

                itemY += itemHeight;
            }

            // Scrollbar for large registries
            if (items.size() > maxVisibleItems) {
                int sbW = 3;
                int sbX = px + pw - sbW - 1;
                int listH = visibleCount * itemHeight;
                int maxScroll = items.size() - maxVisibleItems;
                int thumbH = Math.max(6, (listH * maxVisibleItems) / items.size());
                int thumbY = listY + (scrollOffset * (listH - thumbH)) / maxScroll;

                guiGraphics.fill(sbX, listY, sbX + sbW, listY + listH, 0x22FFFFFF);
                guiGraphics.fill(sbX, thumbY, sbX + sbW, thumbY + thumbH, 0xFF00E5FF);
            }
        }

        guiGraphics.pose().popPose();
    }
}
