package geni.witherutils;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import geni.withercore.init.*;
import geni.witherdata.generator.*;
import geni.witherdata.generator.provider.WitherUtilsDataProvider;
import geni.witherdata.recipes.AnvilRecipeProvider;
import geni.witherdata.recipes.CutterRecipeProvider;
import geni.witherdata.recipes.VanillaAnvilRecipe;
import geni.witherutils.client.ClientSetup;
import geni.witherutils.common.config.BaseConfig;
import geni.witherutils.common.events.*;
import geni.witherutils.common.world.level.block.AngelBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mod(WitherUtils.MODID)
public class WitherUtils {

    public static final String MODID = "witherutils";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation loc(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(MODID, path.toLowerCase(Locale.ROOT));
    }

    public static final UUID defaultFakePlayerUUID = UUID.nameUUIDFromBytes("witherutils.common".getBytes(StandardCharsets.UTF_8));
    public static final GameProfile gameProfile = new GameProfile(defaultFakePlayerUUID, "[WitherFakePlayer]");

    public WitherUtils(ModContainer modContainer, IEventBus modEventBus, Dist dist)
    {
        try
        {
            Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve(MODID));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        modContainer.registerConfig(ModConfig.Type.COMMON, BaseConfig.COMMON_SPEC, "witherutils/base-common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, BaseConfig.CLIENT_SPEC, "witherutils/base-client.toml");

        IEventBus forgeBus = NeoForge.EVENT_BUS;

        if (dist.isClient())
        {
            ClientSetup.onModConstruction(modEventBus);
            modEventBus.addListener(KeybindHandler::onRegisterKeyMappings);
        }

        WUTCreativeTab.init(modEventBus);
        WUTSounds.SOUND_TYPES.register(modEventBus);
        WUTParticles.PARTICLE_TYPES.register(modEventBus);
        WUTBlocks.BLOCKS.register(modEventBus);
        WUTBlockEntities.TILE_ENTITY_TYPES.register(modEventBus);
        WUTMenus.MENUS.register(modEventBus);
        WUTItems.ITEMS.register(modEventBus);
        WUTEntities.ENTITY_TYPES.register(modEventBus);
        WUTComponents.DATACOMP_TYPES.register(modEventBus);
        WUTRecipes.RECIPE_TYPES.register(modEventBus);
        WUTRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        WUTEffects.EFFECT_TYPES.register(modEventBus);
        WUTFluids.FLUIDS.register(modEventBus);
        WUTFluids.FLUID_TYPES.register(modEventBus);

        modEventBus.addListener(WUTCapabilities::register);
        modEventBus.addListener(EventPriority.LOWEST, this::onGatherData);

        forgeBus.register(new VanillaAnvilRecipe());
        forgeBus.register(new PlayerInteractEvents());
        forgeBus.register(new ItemEventHandler());
        forgeBus.register(new WitherEarthRitualHandler());
        forgeBus.register(new WitherEarthMobHandler());

        NeoForge.EVENT_BUS.register(new ShieldEventHandler());

        NeoForge.EVENT_BUS.addListener(
                (InputEvent.Key e) -> AngelBlock.handleInput(PacketDistributor::sendToServer)
        );
    }

    public void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

        WitherUtilsBlockTags blockTags = new WitherUtilsBlockTags(packOutput, registries, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new WitherUtilsItemTags(generator, registries, blockTags.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new WitherUtilsEntityTags(generator, registries, existingFileHelper));

        generator.addProvider(event.includeServer(), new WitherUtilsBlockStates(generator.getPackOutput(), existingFileHelper));
        generator.addProvider(event.includeServer(), new WitherUtilsItemModels(generator, existingFileHelper));
        generator.addProvider(event.includeServer(), new WitherUtilsCraftingRecipes(generator.getPackOutput(), registries));
        generator.addProvider(event.includeClient(), new WitherUtilsLanguages(packOutput, WitherUtils.MODID, "en_us"));
        generator.addProvider(event.includeClient(), new WitherUtilsSounds(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), new WitherUtilsLootTables(generator, registries));

        //
        // EXTRA
        //
        WitherUtilsDataProvider provider = new WitherUtilsDataProvider("witherutils");
        provider.addSubProvider(event.includeServer(), new AnvilRecipeProvider(packOutput, registries));
        provider.addSubProvider(event.includeServer(), new CutterRecipeProvider(packOutput, registries));
        generator.addProvider(true, provider);
    }
}
