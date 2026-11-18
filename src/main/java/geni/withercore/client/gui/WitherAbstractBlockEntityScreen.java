package geni.withercore.client.gui;

import geni.withercore.client.gui.widget.WitherTitle;
import geni.withercore.common.helper.ColorHelper;
import geni.withercore.common.menu.blockentity.WitherMachineMenu;
import geni.withercore.common.world.level.block.entity.WitherMachineBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

public abstract class WitherAbstractBlockEntityScreen<C extends WitherMachineMenu<T>, T extends WitherMachineBlockEntity>
        extends WitherAbstractScreen<C> {

    @Nullable
    public final T te;
    protected WitherTitle title;

    public WitherAbstractBlockEntityScreen(C menu, Inventory playerInventory) {
        this(menu, playerInventory, Component.empty());
    }

    public WitherAbstractBlockEntityScreen(C menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.te = menu.getBlockEntity();
    }

    @Override
    protected void init() {
        super.init();

        if (this.shaderConfig != null) {
            this.shaderConfig.setSize(this.imageWidth, this.imageHeight);
            this.shaderConfig.setRadius(4.0f);
            this.shaderConfig.setBorderThickness(1.0f);

            this.shaderConfig.setBgColorTop(0.0f, 0.0f, 0.0f, 0.0f);
            this.shaderConfig.setBgColorBottom(0.0f, 0.0f, 0.0f, 0.0f);

            this.shaderConfig.setBorderColor(ColorHelper.ICE_BLUE.getRGB());

            this.shaderConfig.setShadowOffset(0.0f, 0.0f);
            this.shaderConfig.setShadowBlur(0.0f);
            this.shaderConfig.setShadowColor(0.0f, 0.0f, 0.0f, 0.0f);
        }

        this.title = new WitherTitle(leftPos, topPos, width, height, Component.literal(getScreenName()));
        this.title.setLetterSpacing(-0.5f);
        this.title.setBoxBounds(width / 2, 0, 0, 0);

        addRenderableWidget(title);
    }

    @Nullable
    public T getBlockEntity() {
        return te;
    }

    protected abstract String getScreenName();
}
