package geni.withercore.common.registration.impl;

import geni.witherapi.providers.ISoundEventProvider;
import geni.withercore.common.registration.WUTDeferredHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

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
