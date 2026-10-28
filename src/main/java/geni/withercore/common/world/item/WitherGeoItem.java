package geni.withercore.common.world.item;

import software.bernie.geckolib.GeckoLibClient;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

public class WitherGeoItem extends WitherItem implements GeoAnimatable {

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public WitherGeoItem(Properties props)
	{
		super(props);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 0, state -> {
			return PlayState.CONTINUE;
		}));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public double getTick(Object object) {
		return RenderUtil.getCurrentTick();
	}
}
