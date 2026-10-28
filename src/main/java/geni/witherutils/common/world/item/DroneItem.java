package geni.witherutils.common.world.item;

import geni.withercore.init.WUTComponents;
import geni.withercore.init.WUTEntities;
import geni.withercore.init.WUTSounds;
import geni.withercore.item.WitherItem;
import geni.witherutils.common.world.entity.drone.DroneDeliverEntity;
import geni.witherutils.common.world.level.block.entity.DronePlatformDockingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class DroneItem extends WitherItem {

    public DroneItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                startSetupMode(stack, level, player);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (player == null) {
            return InteractionResult.PASS;
        }

        Integer step = stack.get(WUTComponents.DRONE_SETUP_STEP.get());

        // Crouching / Shift + Right-click on a block starts or resets setup mode
        if (player.isSecondaryUseActive()) {
            if (!level.isClientSide()) {
                startSetupMode(stack, level, player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        // Programmiermodus: Step 1 - Docking Platform auswählen
        if (step != null && step == 1) {
            if (!level.isClientSide()) {
                if (level.getBlockEntity(clickedPos) instanceof DronePlatformDockingBlockEntity) {
                    stack.set(WUTComponents.DRONE_DOCK_POS.get(), clickedPos);
                    stack.set(WUTComponents.DRONE_SETUP_STEP.get(), 2);
                    level.playSound(null, clickedPos, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 1.0F, 1.4F);
                    player.displayClientMessage(
                            Component.literal("§a[Drone] Docking Platform gesetzt: [" + clickedPos.toShortString() + "]. §eBitte klicke nun mit Rechtsklick auf einen beliebigen Zielort-Block."),
                            true
                    );
                } else {
                    resetSetupMode(stack);
                    level.playSound(null, clickedPos, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8F, 1.4F);
                    player.displayClientMessage(
                            Component.literal("§c[Drone] Ungültiger Block! Kein Docking Platform. Programmiermodus abgebrochen."),
                            true
                    );
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        // Programmiermodus: Step 2 - Zielort auswählen
        if (step != null && step == 2) {
            if (!level.isClientSide()) {
                BlockPos dockPos = stack.get(WUTComponents.DRONE_DOCK_POS.get());
                if (level.getBlockEntity(clickedPos) instanceof DronePlatformDockingBlockEntity || (dockPos != null && clickedPos.equals(dockPos))) {
                    resetSetupMode(stack);
                    level.playSound(null, clickedPos, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8F, 1.4F);
                    player.displayClientMessage(
                            Component.literal("§c[Drone] Die Docking Platform kann nicht als Zielort gewählt werden! Programmiermodus abgebrochen."),
                            true
                    );
                } else {
                    stack.set(WUTComponents.DRONE_TARGET_POS.get(), clickedPos);
                    stack.remove(WUTComponents.DRONE_SETUP_STEP.get());
                    level.playSound(null, clickedPos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8F, 1.5F);
                    player.displayClientMessage(
                            Component.literal("§a[Drone] Zielort erfolgreich gesetzt: [" + clickedPos.toShortString() + "]! §bDu kannst die Drohne nun mit Rechtsklick auf der Docking Platform platzieren."),
                            true
                    );
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        // Normaler Modus: Platzieren der Drohne auf der Docking Platform
        if (!(level.getBlockEntity(clickedPos) instanceof DronePlatformDockingBlockEntity dockingBE)) {
            if (!level.isClientSide()) {
                level.playSound(null, clickedPos, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8F, 1.4F);
                player.displayClientMessage(
                        Component.literal("§e[Drone] Die Drohne muss auf einer Docking Platform platziert werden."),
                        true
                );
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        BlockPos dockPos = stack.get(WUTComponents.DRONE_DOCK_POS.get());
        BlockPos targetPos = stack.get(WUTComponents.DRONE_TARGET_POS.get());

        if (dockPos == null || targetPos == null) {
            if (!level.isClientSide()) {
                level.playSound(null, clickedPos, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8F, 1.4F);
                player.displayClientMessage(
                        Component.literal("§e[Drone] Diese Drohne hat noch keine Flugdaten! Shift + Rechtsklick in den Himmel zum Programmieren."),
                        true
                );
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        if (dockingBE.isDocked()) {
            return InteractionResult.FAIL;
        }

        BlockState clickedState = level.getBlockState(clickedPos);
        Direction clickedFace = context.getClickedFace();
        BlockPos spawnBlockPos = clickedState.canBeReplaced() ? clickedPos : clickedPos.relative(clickedFace);

        double spawnX = spawnBlockPos.getX() + 0.5D;
        double spawnY = spawnBlockPos.getY();
        double spawnZ = spawnBlockPos.getZ() + 0.5D;

        AABB placementBox = WUTEntities.DRONE_DELIVER.get().getSpawnAABB(spawnX, spawnY, spawnZ);
        if (!level.noCollision(null, placementBox) || !level.getEntities(null, placementBox).isEmpty()) {
            return InteractionResult.FAIL;
        }

        if (level instanceof ServerLevel serverLevel) {
            DroneDeliverEntity drone = WUTEntities.DRONE_DELIVER.get().create(serverLevel);
            if (drone == null) {
                return InteractionResult.FAIL;
            }

            drone.setPlatformPos(dockPos);
            drone.setTargetPos(targetPos);
            drone.moveTo(spawnX, spawnY, spawnZ, 45f, 0.0F);

            if (stack.has(DataComponents.CUSTOM_NAME)) {
                drone.setCustomName(stack.getHoverName());
            }

            drone.initializeOnPlacement(spawnBlockPos, 45f);

            serverLevel.addFreshEntity(drone);
            serverLevel.gameEvent(player, GameEvent.ENTITY_PLACE, spawnBlockPos);

            dockingBE.setDockedDrone(drone);

            serverLevel.playSound(
                    null,
                    spawnBlockPos.getX(),
                    spawnBlockPos.getY(),
                    spawnBlockPos.getZ(),
                    WUTSounds.DRONEBEEP.get(),
                    SoundSource.NEUTRAL,
                    0.9F,
                    1.15F
            );
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private void startSetupMode(ItemStack stack, Level level, Player player) {
        stack.set(WUTComponents.DRONE_SETUP_STEP.get(), 1);
        stack.remove(WUTComponents.DRONE_DOCK_POS.get());
        stack.remove(WUTComponents.DRONE_TARGET_POS.get());
        level.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.PLAYERS, 1.0F, 1.2F);
        player.displayClientMessage(
                Component.literal("§e[Drone] Programmiermodus gestartet! Bitte klicke mit Rechtsklick auf eine Docking Platform."),
                true
        );
    }

    private void resetSetupMode(ItemStack stack) {
        stack.remove(WUTComponents.DRONE_SETUP_STEP.get());
        stack.remove(WUTComponents.DRONE_DOCK_POS.get());
        stack.remove(WUTComponents.DRONE_TARGET_POS.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> infoList, TooltipFlag flags) {
        super.appendHoverText(stack, context, infoList, flags);

        Integer step = stack.get(WUTComponents.DRONE_SETUP_STEP.get());
        BlockPos dockPos = stack.get(WUTComponents.DRONE_DOCK_POS.get());
        BlockPos targetPos = stack.get(WUTComponents.DRONE_TARGET_POS.get());

        if (step != null && step == 1) {
            infoList.add(Component.literal("§eStatus: §fWarte auf Docking Platform..."));
        } else if (step != null && step == 2) {
            infoList.add(Component.literal("§eStatus: §fWarte auf Zielort..."));
        } else if (dockPos != null && targetPos != null) {
            infoList.add(Component.literal("§aStatus: §fBereit zum Platzieren"));
        } else {
            infoList.add(Component.literal("§cStatus: §fUnkonfiguriert"));
        }

        if (dockPos != null) {
            infoList.add(Component.literal("§8• Docking Platform: §b[" + dockPos.getX() + ", " + dockPos.getY() + ", " + dockPos.getZ() + "]"));
        }
        if (targetPos != null) {
            infoList.add(Component.literal("§8• Zielort: §b[" + targetPos.getX() + ", " + targetPos.getY() + ", " + targetPos.getZ() + "]"));
        }

        infoList.add(Component.literal("§8--------------------------------"));
        infoList.add(Component.literal("§7Shift + Rechtsklick (in die Luft): §fProgrammiermodus starten"));
    }
}