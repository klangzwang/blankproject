package geni.witherapi.providers;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

@MethodsReturnNonnullByDefault
public interface ISoundEventProvider extends IBaseProvider {

    SoundEvent getSoundEvent();

    @Override
    default ResourceLocation getRegistryName()
    {
        return BuiltInRegistries.SOUND_EVENT.getKey(getSoundEvent());
    }
}
