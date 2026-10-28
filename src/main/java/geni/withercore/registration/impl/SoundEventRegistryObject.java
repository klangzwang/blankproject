package geni.withercore.registration.impl;

import org.jetbrains.annotations.NotNull;

import geni.witherapi.providers.ISoundEventProvider;
import geni.withercore.registration.WUTDeferredHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;

public class SoundEventRegistryObject extends WUTDeferredHolder<SoundEvent, SoundEvent> implements ISoundEventProvider {

    public SoundEventRegistryObject(ResourceKey<SoundEvent> key)
    {
        super(key);
    }

    @NotNull
    @Override
    public SoundEvent getSoundEvent()
    {
        return value();
    }
}
