package geni.withercore.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

/**
 * Akustisches Feedback für GUI-Aktionen (Abschnitt 5.6).
 * Spielt UI-Sounds für Klicks, Transfers, Drop und Trash ab.
 */
public class WitherGuiSounds {

    public static void playSound(SoundEvent sound, float pitch) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch));
    }

    public static void playClick() {
        playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f);
    }

    public static void playTransfer() {
        playSound(SoundEvents.ITEM_PICKUP, 1.3f);
    }

    public static void playDrop() {
        playSound(SoundEvents.ITEM_PICKUP, 0.7f);
    }

    public static void playTrash() {
        playSound(SoundEvents.LAVA_EXTINGUISH, 1.4f);
    }

    public static void playSuccess() {
        playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.2f);
    }

    public static void playFail() {
        playSound(SoundEvents.DISPENSER_FAIL, 1.0f);
    }
}

