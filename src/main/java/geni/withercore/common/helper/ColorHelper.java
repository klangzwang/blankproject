package geni.withercore.common.helper;

import codechicken.lib.colour.ColourRGBA;

public class ColorHelper extends ColourRGBA {

    public static final java.awt.Color white = new java.awt.Color(255, 255, 255);
    public static final java.awt.Color WHITE = white;

    public static final java.awt.Color lightGray = new java.awt.Color(192, 192, 192);
    public static final java.awt.Color LIGHT_GRAY = lightGray;

    public static final java.awt.Color gray = new java.awt.Color(128, 128, 128);
    public static final java.awt.Color GRAY = gray;

    public static final java.awt.Color darkGray = new java.awt.Color(64, 64, 64);
    public static final java.awt.Color DARK_GRAY = darkGray;

    public static final java.awt.Color black = new java.awt.Color(0, 0, 0);
    public static final java.awt.Color BLACK = black;

    public static final java.awt.Color red = new java.awt.Color(255, 0, 0);
    public static final java.awt.Color RED = red;

    public static final java.awt.Color pink = new java.awt.Color(255, 175, 175);
    public static final java.awt.Color PINK = pink;

    public static final java.awt.Color orange = new java.awt.Color(255, 128, 0);
    public static final java.awt.Color ORANGE = orange;

    public static final java.awt.Color yellow = new java.awt.Color(255, 255, 0);
    public static final java.awt.Color YELLOW = yellow;

    public static final java.awt.Color green = new java.awt.Color(0, 255, 0);
    public static final java.awt.Color GREEN = green;

    public static final java.awt.Color magenta = new java.awt.Color(255, 0, 255);
    public static final java.awt.Color MAGENTA = magenta;

    public static final java.awt.Color cyan = new java.awt.Color(0, 255, 255);
    public static final java.awt.Color CYAN = cyan;

    public static final java.awt.Color blue = new java.awt.Color(0, 0, 255);
    public static final java.awt.Color BLUE = blue;

    // --- Erweiterte Grautöne ---
    public static final java.awt.Color charcoal = new java.awt.Color(54, 69, 79);
    public static final java.awt.Color CHARCOAL = charcoal;

    public static final java.awt.Color slateGray = new java.awt.Color(112, 128, 144);
    public static final java.awt.Color SLATE_GRAY = slateGray;

    public static final java.awt.Color ashGray = new java.awt.Color(178, 190, 181);
    public static final java.awt.Color ASH_GRAY = ashGray;

    // --- Navy-Töne ---
    public static final java.awt.Color navy = new java.awt.Color(0, 0, 128);
    public static final java.awt.Color NAVY = navy;

    public static final java.awt.Color darkNavy = new java.awt.Color(0, 0, 80);
    public static final java.awt.Color DARK_NAVY = darkNavy;

    public static final java.awt.Color midnightBlue = new java.awt.Color(25, 25, 112);
    public static final java.awt.Color MIDNIGHT_BLUE = midnightBlue;

    // --- Petrol-Töne ---
    public static final java.awt.Color petrol = new java.awt.Color(0, 95, 106);
    public static final java.awt.Color PETROL = petrol;

    public static final java.awt.Color darkPetrol = new java.awt.Color(0, 60, 70);
    public static final java.awt.Color DARK_PETROL = darkPetrol;

    public static final java.awt.Color lightPetrol = new java.awt.Color(46, 139, 144);
    public static final java.awt.Color LIGHT_PETROL = lightPetrol;

    // --- Graublau-Töne ---
    public static final java.awt.Color grayBlue = new java.awt.Color(100, 149, 237);
    public static final java.awt.Color GRAY_BLUE = grayBlue;

    public static final java.awt.Color steelBlue = new java.awt.Color(70, 130, 180);
    public static final java.awt.Color STEEL_BLUE = steelBlue;

    public static final java.awt.Color iceBlue = new java.awt.Color(176, 196, 222);
    public static final java.awt.Color ICE_BLUE = iceBlue;

    public ColorHelper() {
        this(WHITE.getBlue());
    }

    public ColorHelper(int colour) {
        super(colour);
    }
}