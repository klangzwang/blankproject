package geni.withercore.init;

import geni.withercore.common.world.effect.FrostbiteEffect;
import geni.witherutils.WitherUtils;
import geni.withercore.common.world.effect.BlindEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WUTEffects {

	public static final DeferredRegister<MobEffect> EFFECT_TYPES = DeferredRegister.create(Registries.MOB_EFFECT, WitherUtils.MODID);

	public static final Holder<MobEffect> BLIND = EFFECT_TYPES.register("blind", () -> new BlindEffect(MobEffectCategory.HARMFUL, 0));
	public static final Holder<MobEffect> FROSTBITE = EFFECT_TYPES.register("frostbite", () -> new FrostbiteEffect(MobEffectCategory.HARMFUL, 0));
}
