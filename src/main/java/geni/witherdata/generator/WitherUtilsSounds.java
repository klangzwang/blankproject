package geni.witherdata.generator;

import geni.withercore.init.WUTSounds;
import geni.witherutils.WitherUtils;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class WitherUtilsSounds extends SoundDefinitionsProvider {

    public WitherUtilsSounds(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, WitherUtils.MODID, existingFileHelper);
    }

    @Override
    public void registerSounds() {
        add(WUTSounds.DOOROPEN, SoundDefinition.definition()
                .with(sound("witherutils:dooropen", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.DRONEBEEPON, SoundDefinition.definition()
                .with(sound("witherutils:dronebeepon", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.DRONEBEEPOFF, SoundDefinition.definition()
                .with(sound("witherutils:dronebeepoff", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.DRONESTART, SoundDefinition.definition()
                .with(sound("witherutils:dronestart", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.DRONESTOP, SoundDefinition.definition()
                .with(sound("witherutils:dronestop", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.DRONEIDLE, SoundDefinition.definition()
                .with(sound("witherutils:droneidle", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.DOORCLOSE, SoundDefinition.definition()
                .with(sound("witherutils:doorclose", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.HAMMERHIT, SoundDefinition.definition()
                .with(sound("witherutils:hammer01", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.PLACELIQUID, SoundDefinition.definition()
                .with(sound("witherutils:place_liquid1", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:place_liquid2", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:place_liquid3", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.PLACESTEAM, SoundDefinition.definition()
                .with(sound("witherutils:place_steam1", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:place_steam2", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.PLACEVAR1, SoundDefinition.definition()
                .with(sound("witherutils:place_var11", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:place_var12", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:place_var13", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:place_var14", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.PLACEVAR2, SoundDefinition.definition()
                .with(sound("witherutils:place_var21", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:place_var22", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:place_var23", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:place_var24", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.PLACEVAR3, SoundDefinition.definition()
                .with(sound("witherutils:place_var31", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:place_var32", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:place_var33", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:place_var34", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.SHIELD_STRIKE, SoundDefinition.definition()
                .with(sound("witherutils:shieldstrike", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.BUZZ, SoundDefinition.definition()
                .with(sound("witherutils:buzz1", SoundDefinition.SoundType.SOUND),
                        sound("witherutils:buzz2", SoundDefinition.SoundType.SOUND))
        );
        add(WUTSounds.BUCKET, SoundDefinition.definition()
                .with(sound("witherutils:bucket", SoundDefinition.SoundType.SOUND).stream(true))
        );
        /*add(WUTSounds.BUCKET, SoundDefinition.definition()
                .with(sound("witherutils:bucket", SoundDefinition.SoundType.SOUND)
                        // Sets the volume. Also has a double counterpart.
                        .volume(1f)
                        // Sets the pitch. Also has a double counterpart.
                        .pitch(1f)
                        // Sets the weight.
                        .weight(2)
                        // Sets the attenuation distance.
                        .attenuationDistance(16)
                        // Enables streaming.
                        // Also has a parameterless overload that defers to stream(true).
                        .stream(true)
                        // Enables preloading.
                        // Also has a parameterless overload that defers to preload(true).
                        .preload(true))
        );*/
    }
}