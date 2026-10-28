package geni.witherutils.client.events;

import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Vector3;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import geni.witherapi.lib.Pair;
import geni.withercore.helper.ColorHelper;
import geni.withercore.helper.TickHelper;
import geni.withercore.init.WUTSounds;
import geni.withercore.util.SoundUtil;
import geni.witherutils.WitherUtils;
import geni.witherutils.client.render.BasicModels;
import geni.witherutils.client.render.effect.EffectLib;
import geni.witherutils.client.render.type.WUTRenderType;
import geni.witherutils.common.world.entity.portal.Portal;
import geni.witherutils.common.world.item.ShieldSteelItem;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.model.obj.ObjLoader;
import net.neoforged.neoforge.client.model.obj.ObjModel;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientEventHandler {

    public static Map<Player, Pair<Float, Integer>> playerShieldStatus = new HashMap<Player, Pair<Float, Integer>>();
    public static BakedModel shieldModel = BasicModels.SHIELD_SPHERE.getModel();
    public static volatile int elapsedTicks;
    private static CameraType previousCamera = CameraType.FIRST_PERSON;
    private static boolean wasSlamming = false;
    private static int cameraResetTimer = -1;

    @SubscribeEvent
    public void onClientPlayerTick(PlayerTickEvent.Post event)
    {
        Player player = event.getEntity();

        elapsedTicks++;
        playerShieldStatus.entrySet().removeIf(entry -> elapsedTicks - entry.getValue().value() > 5);

        // Nur für den lokalen Spieler auf Client-Seite ausführen
        if (!player.level().isClientSide() || player != Minecraft.getInstance().player)
            return;

        boolean isSlamming = player.getPersistentData().getBoolean("WUT_HammerSlam");

        // 1. Sprung gestartet: In 3rd Person schalten & Sitz-Pose erzwingen
        if (isSlamming)
        {
            if (!wasSlamming)
            {
                previousCamera = Minecraft.getInstance().options.getCameraType();
                Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
                wasSlamming = true;
                cameraResetTimer = -1;
            }
            player.setPose(Pose.SITTING);
        }
        // 2. Aufprall erfolgt: Timer für Kamera-Reset starten (15 Ticks = 0.75 Sek.)
        else if (wasSlamming)
        {
            wasSlamming = false;
            cameraResetTimer = 15;
        }

        // 3. Nachlauf-Timer herunterzählen
        if (cameraResetTimer > 0)
        {
            cameraResetTimer--;
            if (cameraResetTimer == 0)
            {
                Minecraft.getInstance().options.setCameraType(previousCamera);
                cameraResetTimer = -1;
            }
        }
    }

    public void renderShield(Player player, PoseStack poseStack, MultiBufferSource buffer) {

        shieldModel = BasicModels.SHIELD_SPHERE.getModel();

        if (!playerShieldStatus.containsKey(player)) {
            return;
        }

        // Sicherstellen, dass das Schildmodell geladen ist (Sollte idealerweise über Model-Events geschehen)
        if (shieldModel == null) {
            // Hinweis: Das Modell sollte im Client-Setup / Bake-Event geladen/gebacken werden.
            return;
        }

        // Farb- und Alpha-Berechnung
        float p = playerShieldStatus.get(player).key();
        int ticksLeft = 5 - (elapsedTicks - playerShieldStatus.get(player).value());
        float alpha = Math.max(0.0F, ticksLeft / 5.0F);

        float red = 1.0F - p;
        float green = 0.0F;
        float blue = p;

        poseStack.pushPose();

        // Der PoseStack ist bereits auf den Spieler zentriert!
        // Wir muessen nur noch die Verschiebung nach oben und die Skalierung anwenden.
        poseStack.translate(0.0D, 1.15D, 0.0D);
        poseStack.scale(1.0F, 1.5F, 1.0F);

        // Buffer fuer transparentes Rendering anfordern (Verwendet den Standard-Block-Atlas)
        VertexConsumer consumer = buffer.getBuffer(WUTRenderType.FORCEFIELD);
        // Quads des gebackenen OBJ-Modells direkt in den VertexConsumer schreiben
        List<BakedQuad> quads = shieldModel.getQuads(null, null, RandomSource.create(42));
        for (BakedQuad quad : quads) {
            consumer.putBulkData(
                    poseStack.last(),
                    quad,
                    ColorHelper.cyan.getRed() / 255.0f, ColorHelper.cyan.getGreen() / 255.0f, ColorHelper.cyan.getBlue() / 255.0f, alpha,
                    LightTexture.FULL_BRIGHT, // Volle Helligkeit / Emissiv
                    OverlayTexture.NO_OVERLAY
            );
        }

        poseStack.popPose();

        renderLightningEffectsOut(player, TickHelper.getClientTickCount(), poseStack, buffer);
        renderLightningEffectsIn(player, TickHelper.getClientTickCount(), poseStack, buffer);
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        // Prüfen, ob der Spieler gerade ein Item benutzt
        if (!player.isUsingItem()) return;

        ItemStack useStack = player.getUseItem();
        if (!(useStack.getItem() instanceof ShieldSteelItem.Energy shieldItem)) return;

        // Sicherstellen, dass das Event nur für die Hand ausgeführt wird, die das Schild WIRKLICH hält
        InteractionHand activeHand = player.getUsedItemHand();
        if (event.getHand() != activeHand) return;

        shieldModel = BasicModels.SHIELD_SPHERE.getModel();

        renderShield(player, event.getPoseStack(), event.getMultiBufferSource());

        /*float partialTick = event.getPartialTick();
        float radius = shieldItem.getRadius(partialTick);
        float alpha = shieldItem.getAlpha(partialTick);

        // Rendern nur wenn sichtbar
        if (radius > 0.01f && alpha > 0.01f) {
            shieldItem.renderShieldForcefield(
                    event.getPoseStack(),
                    event.getMultiBufferSource(),
                    player,
                    partialTick,
                    radius,
                    alpha
            );
        }*/
    }

    @SubscribeEvent
    public void renderPlayerEvent(RenderPlayerEvent.Post event) {
        renderShield(event.getEntity(), event.getPoseStack(), event.getMultiBufferSource());
    }

    // Wenn der Schild einen Angriff erfolgreich abblockt
    @SubscribeEvent
    public void onShieldBlock(LivingShieldBlockEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack useItem = player.getUseItem();
            if (useItem.getItem() instanceof ShieldSteelItem.Energy shield) {
                shield.triggerHitFlash();
            }
        }
    }

    // Wenn der Spieler trotz Blocken getroffen wird
    @SubscribeEvent
    public void onPlayerDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player player && player.isBlocking()) {
            ItemStack useItem = player.getUseItem();
            if (useItem.getItem() instanceof ShieldSteelItem.Energy shield) {
                shield.triggerHitFlash();
            }
        }
    }

    private void renderLightningEffectsOut(Player player, float partialTicks, PoseStack matrix, MultiBufferSource buffer)
    {
        matrix.pushPose();

        int i = 0;
        i++;

        if (TickHelper.getClientTickCount() % 2 == 0)
        {
            if(player.level().random.nextFloat() < 0.2f)
            {
                int pos = player.level().random.nextInt(4);
                for (i = 0; i < 4; i++)
                {
                    if (i != pos)
                        continue;

                    if(player.level().random.nextFloat() < 0.1f)
                    {
                        SoundUtil.playSoundDistrib(player.level(), player.blockPosition(), WUTSounds.BUZZ.get(), 0.5f, 1.0f, false, true);
                    }

                    float loopOffset = ((i / 4F) * (float) Math.PI * 2F) + TickHelper.getClientTickCount() / 100F;
                    float rot = ((7 / 64F) * (float) Math.PI * 2F) + (TickHelper.getClientTickCount() / 10F) + loopOffset;
                    double x = Mth.sin(rot) * 0.45;
                    double z = Mth.cos(rot) * 0.45;
                    double y = Mth.cos(rot - loopOffset) * 0.75;
                    EffectLib.renderLightningP2PRotate(matrix, buffer, new Vector3(x, y, z), new Vector3(0, 0, 0), 2 + player.level().random.nextInt(12), TickHelper.getClientTickCount() / 2, 0.03F * player.level().random.nextInt(4), 0.030F, false, 0, 0x6300BD);
                }
            }
        }

        matrix.popPose();
    }

    private void renderLightningEffectsIn(Player player, float partialTicks, PoseStack matrix, MultiBufferSource buffer)
    {
        matrix.pushPose();

        int i = 0;
        i++;

        if (TickHelper.getClientTickCount() % 2 == 0)
        {
            if(player.level().random.nextFloat() < 0.75f)
            {
                int pos = player.level().random.nextInt(4);
                for (i = 0; i < 4; i++)
                {
                    if (i != pos)
                        continue;

                    if(player.level().random.nextFloat() < 0.1f)
                    {
                        SoundUtil.playSoundDistrib(player.level(), player.blockPosition(), WUTSounds.BUZZ.get(), 0.5f, 1.0f, false, true);
                    }

                    float loopOffset = ((i / 4F) * (float) Math.PI * 2F) + TickHelper.getClientTickCount() / 100F;
                    float rot = ((7 / 64F) * (float) Math.PI * 2F) + (TickHelper.getClientTickCount() / 10F) + loopOffset;
                    double x = Mth.sin(rot) * 0.45;
                    double z = Mth.cos(rot) * 0.45;
                    double y = Mth.cos(rot - loopOffset) * 0.75;

                    // Gleichmäßige Zufallsrichtung auf der Kugeloberfläche erzeugen
                    double dx = player.level().random.nextGaussian();
                    double dy = player.level().random.nextGaussian();
                    double dz = player.level().random.nextGaussian();
                    double norm = Math.sqrt(dx * dx + dy * dy + dz * dz);

                    if (norm == 0) {
                        norm = 1.0;
                    }

                    Vector3 startPos = new Vector3(
                            (dx / norm) * 1,
                            (dy / norm) * 2,
                            (dz / norm) * 1
                    );

                    /*System.out.println(z);*/

                    EffectLib.renderLightningP2PRotate(matrix, buffer,
                            new Vector3(dx, 1, dz),
                            startPos,
                            2 + player.level().random.nextInt(12),
                            TickHelper.getClientTickCount() / 2,
                            0.03F * player.level().random.nextInt(4),
                            0.030F,
                            false,
                            0,
                            0x6300BD);
                }
            }
        }

        matrix.popPose();
    }
}