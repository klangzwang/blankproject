package geni.witherutils.client;

import codechicken.lib.util.ClientUtils;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import geni.withercore.client.shader.BaseShader;
import geni.withercore.common.world.level.block.WitherBlock;
import geni.withercore.common.helper.AtlasTextureHelper;
import geni.withercore.init.*;
import geni.witherutils.client.block.render.entity.*;
import geni.witherutils.client.block.render.entity.rack.CaseRenderer;
import geni.witherutils.client.block.render.entity.rack.ControllerEnergyRenderer;
import geni.witherutils.client.block.render.entity.rack.ControllerFluidRenderer;
import geni.witherutils.client.block.render.entity.rack.ControllerItemRenderer;
import geni.witherutils.client.block.render.fluid.LiquidRendererPlus;
import geni.witherutils.client.entity.render.CursedLightningBoltRenderer;
import geni.witherutils.client.entity.render.DroneRenderer;
import geni.witherutils.client.events.KeybindMappingEvents;
import geni.witherutils.client.gui.screen.CutterScreen;
import geni.witherutils.client.render.hud.OverlayHandler;
import geni.witherutils.common.world.fluid.FluidColdSlush;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
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
        modBus.addListener(OverlayHandler::registerOverlays);

		AtlasTextureHelper.init(modBus);

        WASP_LASER_SHADER.register(modBus);
    }

    public static final BaseShader WASP_LASER_SHADER = new BaseShader("wasp_laser", DefaultVertexFormat.NEW_ENTITY)
            .onShaderApplied(e -> e.getTimeUniform().glUniform1f((float) (ClientUtils.getRenderTime() / 20)));

    public static void onRegisterShaders(RegisterShadersEvent event) {
        WUTShaders.register(event);
    }

    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event)
    {
    }

    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(FluidColdSlush.RENDER_PROPS, WUTFluids.COLDSLUSH_FLUID_TYPE.get());
    }

    public static void registerScreens(RegisterMenuScreensEvent event)
    {
        event.register(WUTMenus.CUTTER.get(), CutterScreen::new);
    }

    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
    }

    public static void onRegisterItemProperties(FMLClientSetupEvent event) {
    }

    public static void onClientSetup(FMLClientSetupEvent event)
	{
        NeoForge.EVENT_BUS.register(new KeybindMappingEvents());

        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.ANVIL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.STEEL_CATWALK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.STEEL_RAILING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.ANGEL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.GREENHOUSE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.FLOORSENSOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WUTBlocks.WALL_SENSOR.get(), RenderType.cutout());
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

        LiquidRendererPlus.onClientSetup(event);
    }

	@OnlyIn(Dist.CLIENT)
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
	    event.registerBlockEntityRenderer(WUTBlockEntities.ANVIL.get(), AnvilRenderer::new);
        event.registerBlockEntityRenderer(WUTBlockEntities.FLOORSENSOR.get(), FloorSensorRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.WALLSENSOR.get(), WallSensorRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.SOLARBASIC_BE.get(), SolarPanelRenderer.Basic::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.SOLARADV_BE.get(), SolarPanelRenderer.Advanced::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.SOLARULTRA_BE.get(), SolarPanelRenderer.Ultra::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.CREATIVE_GENERATOR.get(), CreativeGeneratorRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.RACK_CASE.get(), CaseRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.RACKITEM_CONTROLLER.get(), ControllerItemRenderer::new);
	    event.registerBlockEntityRenderer(WUTBlockEntities.RACKFLUID_CONTROLLER.get(), ControllerFluidRenderer::new);
        event.registerBlockEntityRenderer(WUTBlockEntities.RACKENERGY_CONTROLLER.get(), ControllerEnergyRenderer::new);

        event.registerBlockEntityRenderer(WUTBlockEntities.HYDRO_TURBINE.get(), HydroTurbineGeoRenderer::new);

        event.registerEntityRenderer(WUTEntities.CURSEDBOLT.get(), CursedLightningBoltRenderer::new);
        event.registerEntityRenderer(WUTEntities.GUARD_DRONE.get(), DroneRenderer::new);
    }

	@OnlyIn(Dist.CLIENT)
    public static void registerParticleFactories(RegisterParticleProvidersEvent event)
    {
        /*Minecraft.getInstance().particleEngine.register(WUTParticles.SOULFLAKE.get(), SoulFlakeParticle.Provider::new);
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
        Minecraft.getInstance().particleEngine.register(WUTParticles.ROTORAIR.get(), DownwashParticle.Provider::new);*/
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
