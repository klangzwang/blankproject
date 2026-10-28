package geni.witherutils.client.render.layer;

import geni.witherutils.WitherUtils;
import geni.witherutils.client.entity.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ModelLayers {

    public static final ModelLayerLocation CHICKENNAKED = new ModelLayerLocation(WitherUtils.loc("chickennaked"), "main");
    public static final ModelLayerLocation CURSEDCREEPER = new ModelLayerLocation(WitherUtils.loc("cursedcreeper"), "main");
    public static final ModelLayerLocation CURSEDSKELETON = new ModelLayerLocation(WitherUtils.loc("cursedskeleton"), "main");
    public static final ModelLayerLocation CURSEDZOMBIE = new ModelLayerLocation(WitherUtils.loc("cursedzombie"), "main");
    public static final ModelLayerLocation CURSEDSPIDER = new ModelLayerLocation(WitherUtils.loc("cursedspider"), "main");
    public static final ModelLayerLocation CURSEDDRYHEAD = new ModelLayerLocation(WitherUtils.loc("curseddryhead"), "main");

    public static void init(IEventBus bus)
    {
        bus.addListener(ModelLayers::registerLayerDefinitions);
    }

    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(CHICKENNAKED, ChickenNakedModel::createBodyLayer);
        event.registerLayerDefinition(CURSEDCREEPER, CursedCreeperModel::createBodyLayer);
        event.registerLayerDefinition(CURSEDSKELETON, CursedSkeletonModel::createBodyLayer);
        event.registerLayerDefinition(CURSEDZOMBIE, CursedZombieModel::createBodyLayer);
        event.registerLayerDefinition(CURSEDSPIDER, CursedSpiderModel::createBodyLayer);
        event.registerLayerDefinition(CURSEDDRYHEAD, CursedDryHeadModel::createBodyLayer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onAddRenderLayers(EntityRenderersEvent.AddLayers event)
    {
//        for (Model skin : event.getSkins())
//        {
//            LivingEntityRenderer renderer = event.getSkin(skin);
//            assert renderer != null;
//            renderer.addLayer(new EquippedItemModelLayer(renderer, skin.equals("slim")));
//        }
//
//        for (EntityRenderer r : Minecraft.getInstance().getEntityRenderDispatcher().renderers.values())
//        {
//            if (r instanceof LivingEntityRenderer<?, ?> renderer && renderer.getModel() instanceof HumanoidModel)
//            {
//                renderer.addLayer(new EquippedItemModelLayer(renderer, false));
//            }
//        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void onAddLayers(EntityRenderersEvent.AddLayers event)
    {
//		List<EntityType<? extends LivingEntity>> entityTypes = ImmutableList.copyOf(BuiltInRegistries.ENTITY_TYPE.keySet()
//				.stream().filter(DefaultAttributes::hasSupplier)
//				.map(entityType -> (EntityType<? extends LivingEntity>) entityType).collect(Collectors.toList()));
//
//		entityTypes.forEach((entityType -> {
//			addLayerIfApplicable(entityType, event);
//		}));
//
//		for(Model skinType : event.getSkins())
//		{
//			event.getSkin(skinType).addLayer(new ArmorHelmetLayer(event.getSkin(skinType)));
//			event.getSkin(skinType).addLayer(new ArmorEyesLayer(event.getSkin(skinType)));
//			event.getSkin(skinType).addLayer(new ArmorCapeLayer(event.getSkin(skinType)));
//		}
    }

//	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
//	private static void addLayerIfApplicable(EntityType<? extends LivingEntity> entityType, EntityRenderersEvent.AddLayers event)
//	{
//		LivingEntityRenderer renderer = null;
//		if(entityType != EntityType.ENDER_DRAGON)
//		{
//			try
//			{
//				renderer = event.getRenderer(entityType);
//			}
//			catch(Exception e) {}
//			
//			if(renderer != null)
//			{
//				renderer.addLayer(new CursedModelLayer(renderer));
//				renderer.addLayer(new ArmorHelmetLayer(renderer));
//				renderer.addLayer(new ArmorCapeLayer(renderer));
//				renderer.addLayer(new ArmorEyesLayer(renderer));
//			}
//		}
//	}
}
