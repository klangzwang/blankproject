package geni.withercore.init;

import geni.witherutils.WitherUtils;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WUTParticles
{
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, WitherUtils.MODID);

    /*public static final DeferredHolder<ParticleType<?>, DataParticleType> STARS = PARTICLE_TYPES.register("stars", () -> new DataParticleType());
    public static final DeferredHolder<ParticleType<?>, DataParticleType> SPRAY = PARTICLE_TYPES.register("spray", () -> new DataParticleType());

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIQUIDSPRAY = PARTICLE_TYPES.register("liquidspray", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EXPERIENCEORB = PARTICLE_TYPES.register("experience", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BUBBLE = PARTICLE_TYPES.register("bubble", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOULFLAKE = PARTICLE_TYPES.register("soulflake", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOULORB = PARTICLE_TYPES.register("soulorb", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOULFRAGSOFT = PARTICLE_TYPES.register("soulfragsoft", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SOULFRAGHARD = PARTICLE_TYPES.register("soulfraghard", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARK = PARTICLE_TYPES.register("spark", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BOOM = PARTICLE_TYPES.register("boom", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WISP = PARTICLE_TYPES.register("wisp", () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, MoltenParticleType> MOLTEN_EMBER = PARTICLE_TYPES.register("molten_ember", () -> new MoltenParticleType());
    public static final DeferredHolder<ParticleType<?>, UpdraftParticleType> UPDRAFT = PARTICLE_TYPES.register("updraft", () -> new UpdraftParticleType());
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ROTORAIR = PARTICLE_TYPES.register("rotorair", () -> new SimpleParticleType(false));
*/
}