/*
package geni.witherutils.client;

import geni.withercore.network.packet.ShieldBashPacket;
import geni.withercore.network.packet.ShieldThrowPacket;
import geni.witherutils.common.world.item.ShieldSteelItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class ClientShieldHandler {

    private boolean wasBlocking = false;
    private float anchorYaw = 0.0F;
    private float anchorPitch = 0.0F;

    // Charge & Dash-Bash State
    private static boolean isCharging = false;
    private static long chargeStartTime = 0L;
    private static final long CHARGE_TIME_MS = 500L;
    private static int dashBrakeTicks = -1;

    public static boolean isCharging() {
        return isCharging;
    }

    public static float getChargeRatio() {
        if (!isCharging) {
            return 0.0F;
        }
        return Math.min(1.0F, (System.currentTimeMillis() - chargeStartTime) / (float) CHARGE_TIME_MS);
    }

    @SubscribeEvent
    public void onMovementInputUpdate(MovementInputUpdateEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;

        boolean holdsShield = player.getMainHandItem().getItem() instanceof ShieldSteelItem.Energy;
        if (!holdsShield)
            return;

        if (player.isUsingItem()) {
            // Active blocking: player cannot walk, auto-crouched, WSAD, sprint, and jump disabled
            event.getInput().forwardImpulse = 0.0F;
            event.getInput().leftImpulse = 0.0F;
            event.getInput().jumping = false;
            event.getInput().shiftKeyDown = true;
            player.setSprinting(false);
        } else {
            // Passive carrying: heavy shield slows player by 1/3
            event.getInput().forwardImpulse *= 0.67F;
            event.getInput().leftImpulse *= 0.67F;
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;

        boolean isBlockingWithShield = player.isUsingItem() && player.getUseItem().getItem() instanceof ShieldSteelItem.Hero;

        if (isBlockingWithShield) {
            if (!wasBlocking) {
                anchorYaw = player.getYRot();
                anchorPitch = player.getXRot();
                wasBlocking = true;
            } else {
                // Clamp camera to a narrow stationary view cone
                player.setYRot(Mth.clamp(player.getYRot(), anchorYaw - 25.0F, anchorYaw + 25.0F));
                player.setXRot(Mth.clamp(player.getXRot(), anchorPitch - 20.0F, anchorPitch + 20.0F));
            }
        } else {
            wasBlocking = false;
        }

        // Cancel charge if player releases RMB block or switches item
        if (isCharging) {
            boolean stillBlocking = player.isUsingItem() && player.getUseItem().getItem() instanceof ShieldSteelItem.Hero;
            if (!stillBlocking) {
                isCharging = false;
            }
        }

        // Abrupt brake after dash sprint
        if (dashBrakeTicks > 0) {
            dashBrakeTicks--;
            if (dashBrakeTicks == 0) {
                player.setDeltaMovement(player.getDeltaMovement().multiply(0.04D, 1.0D, 0.04D));
                dashBrakeTicks = -1;
            }
        }
    }

    @SubscribeEvent
    public void onMouseButton(InputEvent.MouseButton.Pre event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;

        // Button 0 = Left click
        if (event.getButton() == 0) {
            // Action 1 = Press
            if (event.getAction() == 1) {
                // Case A: Blocking with shield -> start 500ms charge!
                if (player.isUsingItem() && player.getUseItem().getItem() instanceof ShieldSteelItem.Basic) {
                    isCharging = true;
                    chargeStartTime = System.currentTimeMillis();
                    event.setCanceled(true);
                }
                // Case B: Shield in Main Hand (not blocking) -> Boomerang Frisbee Throw!
                else if (player.getMainHandItem().getItem() instanceof ShieldSteelItem.Basic && !player.isUsingItem()) {
                    if (!player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem())) {
                        PacketDistributor.sendToServer(new ShieldThrowPacket());
                        player.swing(InteractionHand.MAIN_HAND);
                        event.setCanceled(true);
                    }
                }
            }
            // Action 0 = Release
            else if (event.getAction() == 0) {
                if (isCharging) {
                    long duration = System.currentTimeMillis() - chargeStartTime;
                    isCharging = false;

                    // Fully charged after 500ms: trigger Dash + Bash!
                    if (duration >= CHARGE_TIME_MS && player.isUsingItem() && player.getUseItem().getItem() instanceof ShieldSteelItem.Basic) {
                        Vec3 look = player.getLookAngle();

                        // Short forward dash sprint
                        player.setDeltaMovement(new Vec3(look.x * 1.55D, 0.08D, look.z * 1.55D));
                        dashBrakeTicks = 4; // Abrupt stop in 4 ticks (~80ms)

                        // Send dash bash to server
                        PacketDistributor.sendToServer(new ShieldBashPacket(false));
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.getMainHandItem().getItem() instanceof ShieldSteelItem.Basic && !player.isUsingItem()) {
            if (!player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem())) {
                PacketDistributor.sendToServer(new ShieldThrowPacket());
                player.swing(InteractionHand.MAIN_HAND);
            }
        }
    }
}
*/
