package geni.withercore.init;

import java.util.HashMap;

import geni.witherutils.WitherUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WUTSounds {

	public static final DeferredRegister<SoundEvent> SOUND_TYPES = DeferredRegister.create(Registries.SOUND_EVENT, WitherUtils.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> HAMMERHIT = SOUND_TYPES.register("hammerhit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "hammerhit")));
    public static final DeferredHolder<SoundEvent, SoundEvent> BUCKET = SOUND_TYPES.register("bucket", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "bucket")));
    public static final DeferredHolder<SoundEvent, SoundEvent> PLACEVAR1 = SOUND_TYPES.register("placevar1", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "placevar1")));
    public static final DeferredHolder<SoundEvent, SoundEvent> PLACEVAR2 = SOUND_TYPES.register("placevar2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "placevar2")));
    public static final DeferredHolder<SoundEvent, SoundEvent> PLACEVAR3 = SOUND_TYPES.register("placevar3", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "placevar3")));
    public static final DeferredHolder<SoundEvent, SoundEvent> PLACELIQUID = SOUND_TYPES.register("placeliquid", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "placeliquid")));
    public static final DeferredHolder<SoundEvent, SoundEvent> PLACESTEAM = SOUND_TYPES.register("placesteam", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "placesteam")));
	public static final DeferredHolder<SoundEvent, SoundEvent> DOOROPEN = SOUND_TYPES.register("dooropen", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "dooropen")));
	public static final DeferredHolder<SoundEvent, SoundEvent> DOORCLOSE = SOUND_TYPES.register("doorclose", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "doorclose")));
	public static final DeferredHolder<SoundEvent, SoundEvent> DRONEBEEP = SOUND_TYPES.register("dronebeep", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "dronebeep")));
	public static final DeferredHolder<SoundEvent, SoundEvent> DRONESTART = SOUND_TYPES.register("dronestart", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "dronestart")));
	public static final DeferredHolder<SoundEvent, SoundEvent> DRONEIDLE = SOUND_TYPES.register("droneidle", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "droneidle")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SHIELD_STRIKE = SOUND_TYPES.register("shieldstrike", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "shieldstrike")));
	public static final DeferredHolder<SoundEvent, SoundEvent> BUZZ = SOUND_TYPES.register("buzz", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "buzz")));
	public static final DeferredHolder<SoundEvent, SoundEvent> DRONENEAR = SOUND_TYPES.register("dronenear", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "dronenear")));
	public static final DeferredHolder<SoundEvent, SoundEvent> DRONEFAR = SOUND_TYPES.register("dronefar", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(WitherUtils.MODID, "dronefar")));

	public static DeferredSoundType STANDARD = new DeferredSoundType(1.0F, 1.0F,
			() -> SoundEvents.STONE_BREAK,
			() -> SoundEvents.STONE_STEP,
			() -> SoundEvents.STONE_PLACE,
			() -> SoundEvents.STONE_HIT,
			() -> SoundEvents.STONE_FALL
			);
	
	public static DeferredSoundType VARIANT1 = new DeferredSoundType(1.0F, 1.0F,
			() -> WUTSounds.PLACEVAR2.get(),
			() -> SoundEvents.NETHERITE_BLOCK_STEP,
			() -> WUTSounds.PLACEVAR1.get(),
			() -> WUTSounds.PLACEVAR2.get(),
			() -> SoundEvents.NETHERITE_BLOCK_FALL
			);
	
	public static DeferredSoundType VARIANT2 = new DeferredSoundType(1.0F, 1.0F,
			() -> WUTSounds.PLACEVAR1.get(),
			() -> SoundEvents.NETHERITE_BLOCK_STEP,
			() -> WUTSounds.PLACEVAR2.get(),
			() -> WUTSounds.PLACEVAR1.get(),
			() -> SoundEvents.NETHERITE_BLOCK_FALL
			);
	
	public static DeferredSoundType VARIANT3 = new DeferredSoundType(1.0F, 1.0F,
			() -> WUTSounds.PLACEVAR2.get(),
			() -> SoundEvents.NETHERITE_BLOCK_STEP,
			() -> WUTSounds.PLACEVAR3.get(),
			() -> WUTSounds.PLACEVAR3.get(),
			() -> SoundEvents.NETHERITE_BLOCK_FALL
			);
    public static DeferredSoundType GLASS = new DeferredSoundType(1.0F, 1.0F,
			() -> SoundEvents.GLASS_BREAK,
			() -> SoundEvents.NETHERITE_BLOCK_STEP,
			() -> WUTSounds.PLACEVAR3.get(),
			() -> SoundEvents.GLASS_HIT,
			() -> SoundEvents.NETHERITE_BLOCK_FALL
    		);

	private static final HashMap<String, SoundEvent> lookupStringToEvent = new HashMap<String, SoundEvent>();

	public static void putAllSounds()
	{
		for(var sound : WUTSounds.SOUND_TYPES.getEntries())
		{
			var sndstring = sound.getRegisteredName();
			lookupStringToEvent.put(sndstring, sound.get());
		}
	}
	
	public static SoundEvent get(String soundPath)
	{
		return lookupStringToEvent.get(soundPath);
	}
}
