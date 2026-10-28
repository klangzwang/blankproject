package geni.withercore.item;

import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Repräsentiert Zusatztasten (Modifier Keys), die während eines Klicks gehalten werden können.
 */
public enum ClickModifier {
    NONE,
    SHIFT,
    CTRL,
    ALT,
    ANY;

    public boolean isShift()
    {
        return this == SHIFT;
    }

    public boolean isCtrl()
    {
        return this == CTRL;
    }

    public boolean isAlt()
    {
        return this == ALT;
    }

    public boolean isNone()
    {
        return this == NONE;
    }

    public boolean isAny()
    {
        return this == ANY;
    }

    /**
     * Prüft, ob dieser Modifier mit einem erwarteten Modifier übereinstimmt.
     * ANY matcht jeden Modifier.
     */
    public boolean matches(ClickModifier required)
    {
        if (required == null || required == ANY || this == ANY)
        {
            return true;
        }
        return this == required;
    }

    /**
     * Ermittelt den aktuellen Modifier anhand des Spieler-Status (und Tastatureingaben auf dem Client).
     *
     * @param player der Spieler
     * @return der aktive ClickModifier
     */
    public static ClickModifier fromPlayer(Player player)
    {
        if (player == null)
        {
            return NONE;
        }

        // Auf dem Client die exakten Tastatur-Modifier über Screen prüfen
        if (FMLEnvironment.dist.isClient())
        {
            ClickModifier clientMod = ClientHelper.getClientModifier();
            if (clientMod != NONE)
            {
                return clientMod;
            }
        }

        // Serverseitige / plattformunabhängige Fallbacks
        if (player.isCrouching() || player.isShiftKeyDown())
        {
            return SHIFT;
        }
        if (player.isSprinting())
        {
            return CTRL;
        }
        return NONE;
    }

    public static boolean isShiftHeld(Player player)
    {
        return fromPlayer(player).isShift();
    }

    public static boolean isCtrlHeld(Player player)
    {
        return fromPlayer(player).isCtrl();
    }

    public static boolean isAltHeld(Player player)
    {
        return fromPlayer(player).isAlt();
    }

    private static class ClientHelper
    {
        @OnlyIn(Dist.CLIENT)
        static ClickModifier getClientModifier()
        {
            try
            {
                if (net.minecraft.client.gui.screens.Screen.hasShiftDown())
                {
                    return SHIFT;
                }
                if (net.minecraft.client.gui.screens.Screen.hasControlDown())
                {
                    return CTRL;
                }
                if (net.minecraft.client.gui.screens.Screen.hasAltDown())
                {
                    return ALT;
                }
            }
            catch (Throwable ignored)
            {
            }
            return NONE;
        }
    }
}
