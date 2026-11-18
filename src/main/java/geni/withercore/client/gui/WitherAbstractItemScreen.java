package geni.withercore.client.gui;

import geni.withercore.common.menu.item.WitherItemMenu;
import geni.withercore.common.world.item.WitherItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public abstract class WitherAbstractItemScreen<C extends WitherItemMenu<T>, T extends WitherItem>
        extends WitherAbstractScreen<C> {

    public final T itemInstance;
    protected final ItemStack itemStack;

    public WitherAbstractItemScreen(C menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.itemInstance = menu.getItemInstance();
        this.itemStack = playerInventory.getSelected();

        this.borderRadius = 4.0f;
        this.borderThickness = 0.8f;
        if (this.shaderConfig != null) {
            this.shaderConfig.setRadius(borderRadius);
            this.shaderConfig.setBorderThickness(borderThickness);
        }
    }

    @Override
    protected void init() {
        super.init();

        if (this.shaderConfig != null) {
            this.shaderConfig.setSize(this.imageWidth, this.imageHeight);
            this.shaderConfig.setRadius(borderRadius);
            this.shaderConfig.setBorderThickness(borderThickness);
        }
    }

    public T getItem() {
        return itemInstance;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
