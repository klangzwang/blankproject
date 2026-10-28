package geni.withercore.common.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FrostbiteEffect extends MobEffect {

    public FrostbiteEffect(MobEffectCategory typeIn, int ColorIn) {
        super(MobEffectCategory.HARMFUL, 0x80D8FF);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        int currentFreeze = livingEntity.getTicksFrozen();
        livingEntity.setTicksFrozen(Math.min(livingEntity.getTicksRequiredToFreeze() + 100, currentFreeze + 12 * (amplifier + 1)));

        if (livingEntity.isFullyFrozen()) {
            livingEntity.hurt(livingEntity.damageSources().freeze(), 1.5F + (amplifier * 1.0F));
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int interval = Math.max(10, 40 >> amplifier);
        return duration % interval == 0;
    }
}
