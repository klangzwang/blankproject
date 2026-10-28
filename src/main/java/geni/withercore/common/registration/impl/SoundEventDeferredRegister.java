package geni.withercore.common.registration.impl;

import geni.withercore.common.registration.WUTDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundEventDeferredRegister extends WUTDeferredRegister<SoundEvent> {

    public SoundEventDeferredRegister(String modid)
    {
        super(Registries.SOUND_EVENT, modid, SoundEventRegistryObject::new);
    }

    public SoundEventRegistryObject register(String name)
    {
        return (SoundEventRegistryObject) register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(getNamespace(), name)));
    }

    public SoundEventRegistryObject registerFixed(String name, float range)
    {
        return (SoundEventRegistryObject) register(name, () -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath(getNamespace(), name), range));
    }
}
