package geni.witherutils.client;

import geni.withercore.block.WitherBlock;
import geni.withercore.helper.AtlasTextureHelper;
import geni.withercore.init.*;
import geni.witherutils.client.baked.model.CTMBakedModel;
import geni.witherutils.client.entity.render.*;
import geni.witherutils.client.events.ClientEventHandler;
import geni.witherutils.client.gui.screen.*;
import geni.witherutils.client.gui.screen.device.DeviceProgScreen;
import geni.witherutils.client.render.blockentity.*;
import geni.witherutils.client.render.blockentity.rack.CaseRenderer;
import geni.witherutils.client.render.blockentity.rack.ControllerEnergyRenderer;
import geni.witherutils.client.render.blockentity.rack.ControllerFluidRenderer;
import geni.witherutils.client.render.blockentity.rack.ControllerItemRenderer;
import geni.witherutils.client.render.effect.ComicPopupManager;
import geni.witherutils.client.render.effect.particle.*;
import geni.witherutils.client.render.hud.OverlayHandler;
import geni.witherutils.client.render.layer.ModelLayers;
import geni.witherutils.common.events.KeybindHandler;
import geni.witherutils.common.world.fluid.*;
import geni.witherutils.common.world.level.block.CutterBlock;
import geni.witherutils.common.world.level.block.DronePlatformBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.obj.ObjLoader;
import net.neoforged.neoforge.common.NeoForge;

public class ClientSetup {

    public static void onModConstruction(IEventBus modBus)
    {
        modBus.addListener(ClientSetup::onClientSetup);
        modBus.addListener(ClientSetup::onRegisterClientExtensions);
        modBus.addListener(ClientSetup::registerRenderers);
        modBus.addListener(ClientSetup::registerParticleFactories);
        modBus.addListener(ClientSetup::registerScreens);
        modBus.addListener(ClientSetup::registerLayerDefinition);
        modBus.addListener(ClientSetup::registerExtensions);
        modBus.addListener(ClientSetup::onRegisterShaders);
        modBus.addListener(ClientSetup::onModifyBakingResult);
        modBus.addListener(ModelLayers::registerLayerDefinitions);
        modBus.addListener(OverlayHandler::registerOverlays);

		AtlasTextureHelper.init(modBus);

		/*GuiTextures.CCL.init(modBus);*/
        /*modBus.addListener(AtlasTexturesGui::onResourceReload);*/
    }

    public static void onRegisterShaders(RegisterShadersEvent event) {
        WUTShaders.register(event);
    }

    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event)
    {
        for(var blocks : BuiltInRegistries.BLOCK.entrySet())
        {
            var block = blocks.getValue();
            if(block instanceof CutterBlock blk)
            {
                for (BlockState state : blk.getStateDefinition().getPossibleStates())
                {
                    ModelResourceLocation mrl = BlockModelShaper.stateToModelLocation(state);
                    BakedModel original = event.getModels().get(mrl);
                    if (original != null && !(original instanceof CTMBakedModel))
                    {
                        event.getModels().put(mrl, new CTMBakedModel(original));
                    }
                }
            }
        }
    }

    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(FluidBlueLimbo.RENDER_PROPS, WUTFluids.BLUELIMBO_FLUID_TYPE.get());
        event.registerFluidType(FluidColdSlush.RENDER_PROPS, WUTFluids.COLDSLUSH_FLUID_TYPE.get());
        event.registerFluidType(FluidExperience.RENDER_PROPS, WUTFluids.EXPERIENCE_FLUID_TYPE.get());
        event.registerFluidType(FluidFertilizer.RENDER_PROPS, WUTFluids.FERTILIZER_FLUID_TYPE.get());
        event.registerFluidType(FluidPortium.RENDER_PROPS, WUTFluids.PORTIUM_FLUID_TYPE.get());
        event.registerFluidType(FluidRedResin.RENDER_PROPS, WUTFluids.REDRESIN_FLUID_TYPE.get());
        event.registerFluidType(FluidSoulful.RENDER_PROPS, WUTFluids.SOULFUL_FLUID_TYPE.get());
        event.registerFluidType(FluidWitherWater.RENDER_PROPS, WUTFluids.WITHERWATER_FLUID_TYPE.get());
    }

    public static void registerScreens(RegisterMenuScreensEvent event)
    {
        event.register(WUTMenus.DRONE_DELIVER.get(), DroneScreen::new);
        event.register(WUTMenus.BLOCKCARD.get(), CardScreen::new);
        event.register(WUTMenus.CUTTER.get(), CutterScreen::new);
        event.register(WUTMenus.DEVICE_PROG.get(), DeviceProgScreen::createScreen);
    }

    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
    }

    public static void onRegisterItemProperties(FMLClientSetupEvent event) {

        /*ItemProperties.register(WUTItems.BATTERY.get(), WitherUtils.loc("charge"), (stack, world, entity, seed) -> {
            long energy = BatteryItem.getStoredEnergy(stack);
            long maxEnergy = BatteryItem.MAX_ENERGY;
            return energy > 0 ? (float) energy / (float) maxEnergy : 0.0f;
        });

        ItemProperties.register(WUTItems.WRENCH.get(), WitherUtils.loc("using"), (stack, world, entity, seed) -> {
            if (entity == null) return 0.0F;
            boolean isHeld = entity.getMainHandItem() == stack || entity.getOffhandItem() == stack;
            boolean isSwinging = entity.swinging && isHeld;
            boolean isUsing = entity.isUsingItem() && entity.getUseItem() == stack;
            return (isSwinging || isUsing) ? 1.0F : 0.0F;
        });

        ItemProperties.register(WUTItems.CARD.get(), WitherUtils.loc("bound"), (stack, world, entity, i) -> {
            CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            if (!customData.isEmpty()) {
                CompoundTag nbt = customData.copyTag();
                if (nbt.hasUUID("bound_playerid") || nbt.contains("bound_mobname") || nbt.contains("bound_item")) {
                    return 1.0F;
                }
            }
            return 0.0F;
        });*/
    }

    public static void onClientSetup(FMLClientSetupEvent event)
	{
        NeoForge.EVENT_BUS.register(new KeybindHandler());
        NeoForge.EVENT_BUS.register(new ClientCursedEarthHandler());
        /*NeoForge.EVENT_BUS.register(new ClientShieldHandler());*/
        NeoForge.EVENT_BUS.register(new ClientEventHandler());
        NeoForge.EVENT_BUS.register(new ClientGPSRenderer());
        NeoForge.EVENT_BUS.register(new ComicPopupManager());

        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.ANVIL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.STEEL_CATWALK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.STEEL_RAILING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.METAL_DOOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.COLLECTOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.ANGEL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.GREENHOUSE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.FLOORSENSOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.WALL_SENSOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.RESERVOIR.get(), RenderType.cutout());
    	ItemBlockRenderTypes.setRenderLayer(WUTBlocks.SOULFIRE.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(WUTBlocks.SOLARBASIC.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(WUTBlocks.SOLARADV.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(WUTBlocks.SOLARULTRA.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(WUTBlocks.CREATIVE_GENERATOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(WUTBlocks.RACK_CASE.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(WUTBlocks.RACK_TERMINAL.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(WUTBlocks.RACKITEM_CONTROLLER.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(WUTBlocks.RACKFLUID_CONTROLLER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.RACKENERGY_CONTROLLER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.DEVICE.get(), RenderType.cutout());
    }

	@OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
	    event.registerBlockEntityRenderer(WUTBlockEntities.ANVIL.get(), AnvilRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.METALDOOR.get(), MetalDoorRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.COLLECTOR.get(), CollectorRenderer::new);
        event.registerBlockEntityRenderer(WUTBlockEntities.FLOORSENSOR.get(), FloorSensorRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.WALLSENSOR.get(), WallSensorRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.RESERVOIR.get(), TankReservoirRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.SOLARBASIC_BE.get(), SolarPanelRenderer.Basic::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.SOLARADV_BE.get(), SolarPanelRenderer.Advanced::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.SOLARULTRA_BE.get(), SolarPanelRenderer.Ultra::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.CREATIVE_GENERATOR.get(), CreativeGeneratorRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.RACK_CASE.get(), CaseRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.RACKITEM_CONTROLLER.get(), ControllerItemRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.RACKFLUID_CONTROLLER.get(), ControllerFluidRenderer::new);
        event.registerBlockEntityRenderer(WUTBlockEntities.RACKENERGY_CONTROLLER.get(), ControllerEnergyRenderer::new);
        event.registerBlockEntityRenderer(WUTBlockEntities.VANISH_BLOCK_ENTITY.get(), VanishBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(WUTBlockEntities.DEVICE.get(), DeviceRenderer::new);
        event.registerBlockEntityRenderer(WUTBlockEntities.DRONEPLATFORM.get(), DronePlatformRenderer::new);
        /*event.registerBlockEntityRenderer(WUTBlockEntities.DRONE_REDSTONE_EMITTER.get(), DronePlatformDockingRenderer::new);*/

        event.registerEntityRenderer(WUTEntities.WORM.get(), WormRenderer::new);
        event.registerEntityRenderer(WUTEntities.SOULORB.get(), SoulOrbRenderer::new);
        event.registerEntityRenderer(WUTEntities.SOULORBPRO.get(), SoulOrbProjectileRenderer::new);
        event.registerEntityRenderer(WUTEntities.CURSEDBOLT.get(), CursedLightningBoltRenderer::new);
        event.registerEntityRenderer(WUTEntities.CHICKENNAKED.get(), ChickenNakedRenderer::new);
        event.registerEntityRenderer(WUTEntities.CURSEDZOMBIE.get(), CursedZombieRenderer::new);
        event.registerEntityRenderer(WUTEntities.CURSEDCREEPER.get(), CursedCreeperRenderer::new);
        event.registerEntityRenderer(WUTEntities.CURSEDSKELETON.get(), CursedSkeletonRenderer::new);
        event.registerEntityRenderer(WUTEntities.CURSEDSPIDER.get(), CursedSpiderRenderer::new);
        event.registerEntityRenderer(WUTEntities.CURSEDDRYHEAD.get(), CursedDryHeadRenderer::new);
        event.registerEntityRenderer(WUTEntities.PORTAL.get(), PortalRenderer::new);
        event.registerEntityRenderer(WUTEntities.DRONE_DELIVER.get(), DroneRenderer::new);
        event.registerEntityRenderer(WUTEntities.THROWN_STEEL_SHIELD.get(), ThrownSteelShieldRenderer::new);
    }

	@OnlyIn(Dist.CLIENT)
    public static void registerParticleFactories(RegisterParticleProvidersEvent event)
    {
        Minecraft.getInstance().particleEngine.register(WUTParticles.SOULFLAKE.get(), SoulFlakeParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(WUTParticles.SOULORB.get(), SoulOrbParticle.SmallNodeProvider::new);
        Minecraft.getInstance().particleEngine.register(WUTParticles.SOULFRAGSOFT.get(), SoulFragParticle.SoftProvider::new);
        Minecraft.getInstance().particleEngine.register(WUTParticles.SOULFRAGHARD.get(), SoulFragParticle.HardProvider::new);
        Minecraft.getInstance().particleEngine.register(WUTParticles.SPARK.get(), SparkParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(WUTParticles.STARS.get(), StarsParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(WUTParticles.SPRAY.get(), SprayParticle::createWaterSprayParticle);
        Minecraft.getInstance().particleEngine.register(WUTParticles.BUBBLE.get(), BubbleParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(WUTParticles.EXPERIENCEORB.get(), ExperienceParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(WUTParticles.BOOM.get(), new ParticleBoom.Factory());
        Minecraft.getInstance().particleEngine.register(WUTParticles.WISP.get(), new ParticleWisp.Factory());
        Minecraft.getInstance().particleEngine.register(WUTParticles.LIQUIDSPRAY.get(), LiquidSprayParticle.WaterSplashProvider::new);
        Minecraft.getInstance().particleEngine.register(WUTParticles.MOLTEN_EMBER.get(), MoltenEmberParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(WUTParticles.UPDRAFT.get(), UpdraftParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(WUTParticles.ROTORAIR.get(), DownwashParticle.Provider::new);
    }

    public static final IClientBlockExtensions PARTICLE_HANDLER = new IClientBlockExtensions()
    {
        @Override
        public boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager)
        {
            state.getShape(Level, pos).forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                double xDif = Math.min(1, maxX - minX);
                double yDif = Math.min(1, maxY - minY);
                double zDif = Math.min(1, maxZ - minZ);
                int xCount = Mth.ceil(xDif / 0.25);
                int yCount = Mth.ceil(yDif / 0.25);
                int zCount = Mth.ceil(zDif / 0.25);
                if (xCount > 0 && yCount > 0 && zCount > 0)
                {
                    for (int x = 0; x < xCount; x++) {
                        for (int y = 0; y < yCount; y++) {
                            for (int z = 0; z < zCount; z++) {
                                double d4 = (x + 0.5) / xCount;
                                double d5 = (y + 0.5) / yCount;
                                double d6 = (z + 0.5) / zCount;
                                double d7 = d4 * xDif + minX;
                                double d8 = d5 * yDif + minY;
                                double d9 = d6 * zDif + minZ;
                                manager.add(new TerrainParticle((ClientLevel) Level, pos.getX() + d7, pos.getY() + d8,
                                        pos.getZ() + d9, d4 - 0.5, d5 - 0.5, d6 - 0.5, state).updateSprite(state, pos));
                            }
                        }
                    }
                }
            });
            return true;
        }
    };

    public static void registerExtensions(RegisterClientExtensionsEvent event)
    {
        for(var blocks : BuiltInRegistries.BLOCK.entrySet())
        {
            var block = blocks.getValue();
            if(block instanceof WitherBlock blk)
            {
                event.registerBlock(ClientSetup.PARTICLE_HANDLER, blk);
            }
        }
    }
}
