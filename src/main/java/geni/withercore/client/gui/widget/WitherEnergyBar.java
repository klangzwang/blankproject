package geni.withercore.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import geni.withercore.common.helper.TickHelper;
import geni.witherutils.WitherUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class WitherEnergyBar extends AbstractWidget {

    private final Supplier<IEnergyStorage> energyStorageSupplier;
    private final Supplier<Boolean> hasSoulBankSupplier;

    public WitherEnergyBar(int x, int y, int width, int height, Supplier<IEnergyStorage> storageSupplier, Supplier<Boolean> hasSoulBank) {
        super(x, y, width, height, Component.empty());
        this.energyStorageSupplier = storageSupplier;
        this.hasSoulBankSupplier = hasSoulBank;
    }

    public WitherEnergyBar(int x, int y, Supplier<IEnergyStorage> storageSupplier) {
        this(x, y, 16, 40, storageSupplier, () -> true);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        IEnergyStorage storage = energyStorageSupplier != null ? energyStorageSupplier.get() : null;
        boolean hasSoulBank = hasSoulBankSupplier == null || hasSoulBankSupplier.get();

        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();

        if (storage == null || storage.getMaxEnergyStored() <= 0 || !hasSoulBank) {
            // Render locked / missing SoulBank state
            guiGraphics.blit(WitherUtils.loc("textures/gui/energy_bar_locked.png"), x, y, 0, 0, w, h, 16, 40);

            // Floating SoulBank item hint
            float time = TickHelper.getClientTickCount();
            double offset = Math.sin(time * 4.0D / 12.0D) * 2.0D;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0D, offset, 0.0D);
            guiGraphics.blit(WitherUtils.loc("textures/item/soulbank/soulbank.png"), x, y + h - 16, 0, 0, 16, 16, 16, 16);
            guiGraphics.pose().popPose();
            return;
        }

        // Render Energy Bar Background and Filled level
        float pct = Math.min((float) storage.getEnergyStored() / (float) storage.getMaxEnergyStored(), 1.0F);

        // Filled portion from texture
        guiGraphics.blit(WitherUtils.loc("textures/gui/energy_bar.png"), x, y, 16, 0, w, h, 32, h);
        int emptyHeight = h - (int) (h * pct);
        if (emptyHeight > 0) {
            guiGraphics.blit(WitherUtils.loc("textures/gui/energy_bar.png"), x, y, 0, 0, w, emptyHeight, 32, h);
        }

        // Render Tooltip on hover
        if (isHovered()) {
            renderEnergyTooltip(guiGraphics, mouseX, mouseY, storage, hasSoulBank);
        }
    }

    private void renderEnergyTooltip(GuiGraphics gg, int mouseX, int mouseY, IEnergyStorage storage, boolean hasSoulBank) {
        List<Component> list = new ArrayList<>();
        NumberFormat fmt = NumberFormat.getInstance(Locale.ENGLISH);

        if (!hasSoulBank) {
            list.add(Component.literal("Energy Storage: Offline").withStyle(ChatFormatting.RED));
            list.add(Component.literal("Requires SoulBank upgrade").withStyle(ChatFormatting.GRAY));
        } else {
            list.add(Component.literal("Stored Energy:").withStyle(ChatFormatting.AQUA));
            list.add(Component.literal(fmt.format(storage.getEnergyStored()) + " / " + fmt.format(storage.getMaxEnergyStored()) + " FE")
                    .withStyle(ChatFormatting.WHITE));
            int pct = (int) (((double) storage.getEnergyStored() / (double) storage.getMaxEnergyStored()) * 100);
            list.add(Component.literal("Charge: " + pct + "%").withStyle(ChatFormatting.GRAY));
        }

        Minecraft mc = Minecraft.getInstance();
        gg.renderComponentTooltip(mc.font, list, mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
