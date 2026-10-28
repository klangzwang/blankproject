package geni.witherutils.client;

import geni.witherutils.WitherUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import javax.annotation.Nullable;
import java.util.*;

@EventBusSubscriber(modid = WitherUtils.MODID, value = Dist.CLIENT)
public enum BasicModels
{
    STEEL_POLE("block/pole/steel_pole"),
    STEEL_FOOT("block/pole/steel_foot"),

    STEEL_CASE("wither/steel/case"),
	STEEL_GEAR("wither/steel/gear"),

	ANVIL_HOT("wither/block/anvil/hot"),

	SKULLUP("wither/skull/witherskull_up"),
	SKULLLW("wither/skull/witherskull_lw"),
	SKULLEM("wither/skull/witherskull_em"),
	
	LOGO_EYES("wither/entity/dryhead/eyes"),
	LOGO_HAIR("wither/entity/dryhead/hair"),
	LOGO_HEAD("wither/entity/dryhead/head"),
	LOGO_JAW("wither/entity/dryhead/jaw"),

    MULTICENTER("block/rack/multi/center"),
    MULTICORNER("block/rack/casing/case_corner"),

    EMMULTI("wither/emissive/em_multi"),
    EMWALLSENSOR("wither/emissive/em_wallsensor");

    private final ModelResourceLocation modelLocation;
    private BakedModel cachedModel;

    private static final Map<String, ModelResourceLocation> LOOKUP_MAP = new HashMap<>();
    private static final Set<ModelResourceLocation> EXTERNAL_MODELS = new HashSet<>();

    static {
        for (BasicModels model : values()) {
            LOOKUP_MAP.put(model.modelLocation.id().getPath(), model.modelLocation);
            LOOKUP_MAP.put(model.modelLocation.id().toString(), model.modelLocation);
        }
    }

    BasicModels(String model)
    {
        this.modelLocation = ModelResourceLocation.standalone(WitherUtils.loc(model));
    }

    public BakedModel getModel()
    {
        if(this.cachedModel == null)
        {
            this.cachedModel = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
        }
        return this.cachedModel;
    }

    public ModelResourceLocation getModelLoc()
    {
        return this.modelLocation;
    }

    public static void addModel(ModelResourceLocation modelresloc)
    {
        EXTERNAL_MODELS.add(modelresloc);
        LOOKUP_MAP.put(modelresloc.id().getPath(), modelresloc);
        LOOKUP_MAP.put(modelresloc.id().toString(), modelresloc);
    }

    /**
     * Sucht ein ModelResourceLocation anhand von Pfad oder vollem ResourceLocation-String.
     * Beispiel: "wither/emissive/em_multi" oder "witherutils:wither/emissive/em_multi"
     */
    @Nullable
    public static ModelResourceLocation getModelResLoc(String path)
    {
        return LOOKUP_MAP.get(path);
    }

    /**
     * Überladene Methode für direkte Suche per ResourceLocation
     */
    @Nullable
    public static ModelResourceLocation getModelResLoc(ResourceLocation location)
    {
        return LOOKUP_MAP.get(location.toString());
    }

    @SubscribeEvent
    public static void registerAdditional(ModelEvent.RegisterAdditional event)
    {
        for (BasicModels model : values())
        {
            event.register(model.modelLocation);
        }
        for (ModelResourceLocation modelresloc : EXTERNAL_MODELS)
        {
            event.register(modelresloc);
        }
    }

    @SubscribeEvent
    public static void onBake(ModelEvent.BakingCompleted event)
    {
        for (BasicModels model : values())
        {
            model.cachedModel = null;
        }
    }
}
