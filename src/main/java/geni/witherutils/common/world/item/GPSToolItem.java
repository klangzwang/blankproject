package geni.witherutils.common.world.item;

import geni.witherapi.item.IPositionProvider;
import geni.withercore.init.WUTComponents;
import geni.withercore.init.WUTItems;
import geni.withercore.item.WitherItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.*;

public class GPSToolItem extends WitherItem implements IPositionProvider {

    public static final int MAX_WAYPOINTS = 48;

    public GPSToolItem(Properties props)
    {
        super(props.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx)
    {
        Player player = ctx.getPlayer();
        if (player == null)
            return InteractionResult.PASS;

        Level level = ctx.getLevel();
        ItemStack stack = ctx.getItemInHand();
        BlockPos clickedPos = ctx.getClickedPos();

        // 1. CROUCHING: Delete last waypoint
        if (player.isCrouching())
        {
            if (!level.isClientSide())
            {
                boolean removed = removeLastWaypoint(stack);
                if (removed)
                {
                    int remaining = getStoredPositions(stack).size();
                    level.playSound(null, clickedPos, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8F, 1.4F);
                    player.displayClientMessage(Component.literal("§e[GPS Tool] Letzter Wegpunkt entfernt. (Verbleibend: " + remaining + ")"), true);
                }
                else
                {
                    player.displayClientMessage(Component.literal("§c[GPS Tool] Keine Wegpunkte zum Löschen vorhanden."), true);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        // 2. STANDING: Add waypoint or toggle closed loop
        if (!level.isClientSide())
        {
            List<BlockPos> waypoints = getStoredPositions(stack);

            // Clicking on the first waypoint with at least 3 points toggles closed loop path
            if (waypoints.size() >= 3 && waypoints.get(0).equals(clickedPos))
            {
                boolean newLoop = !isClosedLoop(stack);
                setClosedLoop(stack, newLoop);
                level.playSound(null, clickedPos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.4F);
                player.displayClientMessage(Component.literal(newLoop ? "§a[GPS Tool] Pfad geschlossen (Rundkurs AN)!" : "§6[GPS Tool] Pfad geöffnet (Rundkurs AUS)!"), true);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            // Prevent duplicate consecutive waypoint clicks
            if (!waypoints.isEmpty() && waypoints.get(waypoints.size() - 1).equals(clickedPos))
            {
                player.displayClientMessage(Component.literal("§6[GPS Tool] Dieser Block ist bereits der aktuelle Endpunkt."), true);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            // Check capacity limit
            if (waypoints.size() >= MAX_WAYPOINTS)
            {
                player.displayClientMessage(Component.literal("§c[GPS Tool] Maximale Anzahl von 48 Wegpunkten erreicht!"), true);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }

            // Append new waypoint
            addWaypoint(stack, clickedPos);
            int newTotal = waypoints.size() + 1;
            level.playSound(null, clickedPos, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 1.0F, 1.6F);
            player.displayClientMessage(Component.literal("§b[GPS Tool] §aWegpunkt #" + newTotal + " gesetzt: [" + clickedPos.getX() + ", " + clickedPos.getY() + ", " + clickedPos.getZ() + "]"), true);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isCrouching())
        {
            if (!level.isClientSide())
            {
                boolean removed = removeLastWaypoint(stack);
                if (removed)
                {
                    int remaining = getStoredPositions(stack).size();
                    level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8F, 1.4F);
                    player.displayClientMessage(Component.literal("§e[GPS Tool] Letzter Wegpunkt entfernt. (Verbleibend: " + remaining + ")"), true);
                }
                else
                {
                    player.displayClientMessage(Component.literal("§c[GPS Tool] Keine Wegpunkte zum Löschen vorhanden."), true);
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        else
        {
            List<BlockPos> waypoints = getStoredPositions(stack);
            if (waypoints.size() >= 3)
            {
                if (!level.isClientSide())
                {
                    boolean newLoop = !isClosedLoop(stack);
                    setClosedLoop(stack, newLoop);
                    level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.4F);
                    player.displayClientMessage(Component.literal(newLoop ? "§a[GPS Tool] Pfad geschlossen (Rundkurs AN)!" : "§6[GPS Tool] Pfad geöffnet (Rundkurs AUS)!"), true);
                }
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> infoList, TooltipFlag flags)
    {
        super.appendHoverText(stack, context, infoList, flags);

        List<BlockPos> waypoints = getStoredPositions(stack);
        boolean loop = isClosedLoop(stack);

        infoList.add(Component.literal("§7Wegpunkte: §e" + waypoints.size() + " §7/ §e" + MAX_WAYPOINTS));
        infoList.add(Component.literal("§7Rundkurs: " + (loop ? "§aGeschlossen" : "§6Offen")));

        if (!waypoints.isEmpty())
        {
            BlockPos start = waypoints.get(0);
            BlockPos end = waypoints.get(waypoints.size() - 1);
            infoList.add(Component.literal("§8• Start #1: §b[" + start.getX() + ", " + start.getY() + ", " + start.getZ() + "]"));
            if (waypoints.size() > 1)
            {
                infoList.add(Component.literal("§8• Ende #" + waypoints.size() + ": §b[" + end.getX() + ", " + end.getY() + ", " + end.getZ() + "]"));
            }
        }

        infoList.add(Component.literal("§8--------------------------------"));
        infoList.add(Component.literal("§7Rechtsklick: §fWegpunkt hinzufügen"));
        infoList.add(Component.literal("§7Sneak + Rechtsklick: §fLetzten Wegpunkt löschen"));
        infoList.add(Component.literal("§7Klick auf #1 / Luft (>=3 WP): §fRundkurs umschalten"));
    }

    // ==========================================
    // IPositionProvider Implementation & Helpers
    // ==========================================

    @Nonnull
    @Override
    public List<BlockPos> getStoredPositions(@Nonnull ItemStack stack)
    {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null)
            return Collections.emptyList();

        CompoundTag tag = customData.copyTag();
        if (!tag.contains("Waypoints", Tag.TAG_LIST))
            return Collections.emptyList();

        ListTag listTag = tag.getList("Waypoints", Tag.TAG_LONG);
        List<BlockPos> list = new ArrayList<>(listTag.size());
        for (int i = 0; i < listTag.size(); i++)
        {
            list.add(BlockPos.of(((LongTag) listTag.get(i)).getAsLong()));
        }
        return list;
    }

    @Override
    public boolean isClosedLoop(@Nonnull ItemStack stack)
    {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null)
            return false;
        return customData.copyTag().getBoolean("ClosedLoop");
    }

    public static void setClosedLoop(ItemStack stack, boolean loop)
    {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = customData != null ? customData.copyTag() : new CompoundTag();
        tag.putBoolean("ClosedLoop", loop);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void addWaypoint(ItemStack stack, BlockPos pos)
    {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = customData != null ? customData.copyTag() : new CompoundTag();

        ListTag listTag = tag.getList("Waypoints", Tag.TAG_LONG);
        if (listTag.size() < MAX_WAYPOINTS)
        {
            listTag.add(LongTag.valueOf(pos.asLong()));
            tag.put("Waypoints", listTag);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public static boolean removeLastWaypoint(ItemStack stack)
    {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null)
            return false;

        CompoundTag tag = customData.copyTag();
        if (!tag.contains("Waypoints", Tag.TAG_LIST))
            return false;

        ListTag listTag = tag.getList("Waypoints", Tag.TAG_LONG);
        if (listTag.isEmpty())
            return false;

        listTag.remove(listTag.size() - 1);
        tag.put("Waypoints", listTag);

        if (listTag.size() < 3)
        {
            tag.putBoolean("ClosedLoop", false);
        }

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return true;
    }

    @Nonnull
    public static Optional<BlockPos> getGPSLocation(ItemStack stack) {
        return getGPSLocation(null, stack);
    }

    @Nonnull
    public static Optional<BlockPos> getGPSLocation(UUID playerId, ItemStack gpsTool) {
        /*BlockPos curPos = gpsTool.get(ModDataComponents.GPS_TOOL_POS);*/
        if (gpsTool.getItem() == WUTItems.GPS_TOOL.get() /*&& curPos != null*/) {
/*            String var = getVariable(gpsTool);
            if (!var.isEmpty()) {
                BlockPos pos = GlobalVariableHelper.getInstance().getPos(playerId, var);
                if (pos != null && !curPos.equals(pos)) {
                    curPos = pos;
                    setGPSLocation(playerId, gpsTool, pos, false);
                }
            }*/
            /*return Optional.of(curPos);*/
            return Optional.empty();
        } else {
            return Optional.empty();
        }
    }

    /*public static void setGPSLocation(UUID playerId, ItemStack gpsTool, BlockPos pos) {
        setGPSLocation(playerId, gpsTool, pos, true);
    }*/

    /*public static void setGPSLocation(UUID playerId, ItemStack gpsTool, BlockPos pos, boolean updateVarManager) {
        gpsTool.set(WUTComponents.GPS_TOOL_POS, pos);
        if (updateVarManager) {
            String var = getVariable(gpsTool);
            if (!var.isEmpty()) {
                GlobalVariableHelper.getInstance().setPos(playerId, var, pos);
            }
        }
    }*/
}
