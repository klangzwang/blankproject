package geni.withercore.render.tooltip;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import geni.witherutils.WitherUtils;
import geni.witherutils.client.ponder.WitherPonderRegistry;
import geni.witherutils.common.config.common.SolarConfig;
import geni.withercore.init.WUTCapabilities;
import geni.withercore.init.WUTComponents;
import geni.witherutils.common.world.level.block.SolarPanelBlockItem;
import geni.witherutils.common.world.level.block.network.solar.ISolarPowered;
import geni.witherutils.common.world.level.block.network.solar.SolarType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class Tooltips {

    private final static Minecraft mc = Minecraft.getInstance();
    private static long lastPonderFrameTime = 0L;
    private static float ponderHoldProgress = 0.0f;
    private static final float PONDER_HOLD_DURATION_MS = 650.0f;

    public static final class Tooltip
    {
        @OnlyIn(Dist.CLIENT)
        public static boolean shiftKeyDown()
        {
            return isShiftDown();
        }

        @OnlyIn(Dist.CLIENT)
        public static boolean wKeyDown()
        {
            return isWDown();
        }

        @OnlyIn(Dist.CLIENT)
        public static boolean add(ItemStack stack, TooltipContext pContext, List<Component> tooltip, TooltipFlag flag)
        {
            if (!shiftKeyDown())
            {
                tooltip.add(Component.literal("")
                        .append(Component.literal(">>> ").withStyle(ChatFormatting.DARK_GRAY)
                                .append(Component.literal("Hold SHift for Tooltips").withStyle(ChatFormatting.DARK_GRAY)
                                )));
            }
            else
            {
                tooltip.add(Component.literal("")
                        .append(Component.literal("■■■ ").withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY)
                                .append(Component.literal("Information").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY)
                                        .append(Component.literal(" ■■■■■■■■■■■").withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY)
                                        ))));

                if(!I18n.exists(stack.getDescriptionId() + ".desc")) {
                    tooltip.add(Component.literal("").append(Component.literal("no Description yet...").withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY)));
                    return true;
                }

                showDescriptionTooltip(stack, tooltip);

                if(WUTCapabilities.getItemEnergyHandler(stack).isPresent())
                    showEnergyTooltip(stack, tooltip);
                else if(stack.getItem() instanceof ISolarPowered)
                    showSolarPanelTooltip(stack, tooltip);
                else if(stack.getItem() != null)
                    displayTooltipInfo(stack, pContext, tooltip, flag);
            }

            if (WitherPonderRegistry.hasPonder(stack))
            {
                handlePonderTooltipAndHold(stack, tooltip);
            }

            return true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void handlePonderTooltipAndHold(ItemStack stack, List<Component> tooltip)
    {
        long now = System.currentTimeMillis();
        long delta = lastPonderFrameTime == 0L ? 16L : Math.min(100L, now - lastPonderFrameTime);
        lastPonderFrameTime = now;

        if (isWDown())
        {
            ponderHoldProgress = Math.min(1.0f, ponderHoldProgress + (delta / PONDER_HOLD_DURATION_MS));
            int totalBars = 10;
            int filledBars = Math.round(ponderHoldProgress * totalBars);

            MutableComponent barComp = Component.literal(">>> ").withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.translatable("tooltip.witherutils.ponder.holding").withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" [").withStyle(ChatFormatting.DARK_GRAY));

            for (int i = 0; i < totalBars; i++)
            {
                barComp.append(Component.literal("■").withStyle(i < filledBars ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY));
            }
            barComp.append(Component.literal("]").withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(barComp);

            if (ponderHoldProgress >= 1.0f)
            {
                ponderHoldProgress = 0.0f;
                lastPonderFrameTime = 0L;
                WitherPonderRegistry.openPonderScreen(stack);
            }
        }
        else
        {
            ponderHoldProgress = Math.max(0.0f, ponderHoldProgress - (delta / (PONDER_HOLD_DURATION_MS * 0.5f)));
            tooltip.add(Component.literal("")
                    .append(Component.literal(">>> ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.translatable("tooltip.witherutils.hint.ponder").withStyle(ChatFormatting.DARK_GRAY)));
        }
    }

    private static boolean showEnergyTooltip(ItemStack stack, List<Component> tooltip)
    {
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Energy:").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        Optional<IEnergyStorage> storage = WUTCapabilities.getItemEnergyHandler(stack);
        if (storage.isPresent())
        {
            NumberFormat fmt = NumberFormat.getInstance(Locale.ENGLISH);

            String energy = fmt.format(storage.get().getEnergyStored());
            String maxEnergy = fmt.format(storage.get().getMaxEnergyStored());

            if(stack.get(WUTComponents.HAS_ENERGY))
                tooltip.add(Component.literal(I18n.get("Charge") + ": " + energy + " / " + maxEnergy).withStyle(ChatFormatting.GRAY));
            else
                tooltip.add(Component.literal("Combine this Item, with a SoulBank in the Vanilla Anvil.").withStyle(ChatFormatting.GRAY));
        }
        return true;
    }

    private static boolean showSolarPanelTooltip(ItemStack stack, List<Component> tooltip)
    {
        if(stack.getItem() instanceof ISolarPowered iSolarItem)
        {
            if(iSolarItem instanceof SolarPanelBlockItem)
            {
                SolarPanelBlockItem solarBItem = (SolarPanelBlockItem) iSolarItem;
                SolarType type = solarBItem.getType();
                int prod = 0;
                switch(type)
                {
                    case BASIC:
                        prod = SolarConfig.SOLARBASICINPUTRF.get();
                        break;
                    case ADVANCED:
                        prod = SolarConfig.SOLARADVINPUTRF.get();
                        break;
                    case ULTRA:
                        prod = SolarConfig.SOLARULTRAINPUTRF.get();
                        break;
                    case NONE:
                        break;
                    default:
                        break;
                }

                tooltip.add(Component.empty());
                tooltip.add(Component.literal(ChatFormatting.GRAY + "Can produce " + ChatFormatting.WHITE + "" + prod + ChatFormatting.GRAY + " RF/t from" + ChatFormatting.YELLOW + " Sunlight"));
            }
        }
        return true;
    }

    /*
     *
     * ICON
     *
     */
/*    public static final Component ICON01 = Component.empty()
            .append(Tooltips.getIcon(FontIcons.ICON01))
            .append(CommonComponents.SPACE);
//            .append(Component.literal("icon01").plainCopy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY);

    public static final Component ICON02 = Component.empty()
            .append(Tooltips.getIcon(FontIcons.ICON02))
            .append(CommonComponents.SPACE);
//            .append(Component.literal("icon02").plainCopy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY);

    public static final Component ICON03 = Component.empty()
            .append(Tooltips.getIcon(FontIcons.ICON03))
            .append(CommonComponents.SPACE);
//            .append(Component.literal("icon03").plainCopy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY);

    public static final Component ICON04 = Component.empty()
            .append(Tooltips.getIcon(FontIcons.ICON04))
            .append(CommonComponents.SPACE);
//            .append(Component.literal("icon04").plainCopy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY);

    public static final Component ICON05 = Component.empty()
            .append(Tooltips.getIcon(FontIcons.ICON05))
            .append(CommonComponents.SPACE);
//            .append(Component.literal("icon05").plainCopy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY);

    public static MutableComponent getIcon(FontIcons icon)
    {
        MutableComponent component = Component.literal(String.valueOf((char) (33 + icon.ordinal())));
        component.setStyle(component.getStyle().withColor(ChatFormatting.WHITE).withFont(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "icons")));
        return component;
    }

    private static MutableComponent appendIcon(FormattedText content)
    {
    	Minecraft mc = Minecraft.getInstance();
        MutableComponent line = Component.empty();

        if (FMLEnvironment.dist.isClient())
        {
        	mc.font.getSplitter().splitLines(content, 150, Style.EMPTY).forEach(text -> {
                text.visit((style, s) -> {
                    line.append(Component.literal(s).withStyle(style));
                    return Optional.empty();
                }, Style.EMPTY);
            });
        }
        return line;
    }

    public enum FontIcons
    {
        ICON01,
        ICON02,
        ICON03,
        ICON04,
        ICON05
    }*/

    /*
     *
     * TOOLS
     *
     */
    @SuppressWarnings("unused")
    private static Component createTextComponent(TypedDataComponent<?> component)
    {
        var text = " " + StringUtil.truncateStringIfNecessary(component.toString(), 50, true);
        return Component.literal(text).withStyle(ChatFormatting.DARK_GRAY);
    }

    private static void displayTooltipInfo(ItemStack stack, TooltipContext pContext, List<Component> tooltip, TooltipFlag context)
    {
        final List<Component> additions = new ArrayList<>();

        if (stack.isDamageableItem())
        {
            if (stack.isDamaged())
            {
                additions.add(Component.translatable("item.durability", stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()).withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        if (!additions.isEmpty())
        {
            tooltip.addAll(getInsertOffset(context.isAdvanced(), tooltip.size(), stack), additions);
        }
    }

    private static int getInsertOffset(boolean advanced, int tooltipSize, ItemStack stack)
    {
        int offset = 0;
        if (advanced)
        {
            offset++;
        }
        return Math.max(0, tooltipSize - offset);
    }

    /*
     *
     * DESCRIPTION
     *
     */
    public static Component combineComponents(List<Component> components)
    {
        return components.stream().reduce((c1, c2) -> c1.copy().append("\n").append(c2)).orElse(Component.empty());
    }

    private static boolean showDescriptionTooltip(ItemStack stack, List<Component> tooltip)
    {
        @Nullable String descriptionIdKey = stack.getDescriptionId();
        final boolean tip_available = (descriptionIdKey != null) && Tooltips.hasTranslation(descriptionIdKey + ".desc");

        if(tip_available)
        {
            String tip_text = "";
            tip_text = localize(descriptionIdKey + ".desc");

            if (tip_text.isEmpty())
                return false;

            String[] tip_list = tip_text.split("\\r?\\n");
            for (String tip : tip_list)
            {
                tooltip.add(Component.literal(tip.replaceAll("\\s+$", "").replaceAll("^\\s+", "")).withStyle(ChatFormatting.GRAY));
            }
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isShiftDown()
    {
        return (InputConstants.isKeyDown(mc.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) ||
                InputConstants.isKeyDown(mc.getWindow().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT));
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isWDown()
    {
        return InputConstants.isKeyDown(mc.getWindow().getWindow(), GLFW.GLFW_KEY_W);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean hasTranslation(String key)
    {
        return I18n.exists(key);
    }

    @OnlyIn(Dist.CLIENT)
    public static String localize(String translationKey, Object... args)
    {
        Component tr = Component.translatable(translationKey, args);
        final String ft = tr.getString();
        if (ft.contains("${")) {
            Pattern pt = Pattern.compile("\\$\\{([^}]+)\\}");
            Matcher mt = pt.matcher(ft);
            StringBuffer sb = new StringBuffer();
            while (mt.find()) {
                String m = mt.group(1);
                if (m.contains("?")) {
                    String[] kv = m.split("\\?", 2);
                    String key = kv[0].trim();
                    boolean not = key.startsWith("!");
                    if (not)
                        key = key.replaceFirst("!", "");
                    m = kv[1].trim();
                }
                mt.appendReplacement(sb, Matcher.quoteReplacement((Component.translatable(m)).getString().trim()));
            }
            mt.appendTail(sb);
            return sb.toString();
        } else {
            return ft;
        }
    }

    public static MutableComponent translation(String category, String path, Object ... params)
    {
        return Component.translatable(String.format("%s.%s.%s", category, WitherUtils.MODID, path), params);
    }
}
