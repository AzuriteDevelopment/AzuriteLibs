package dev.azurite.libs.modules.nametags.enums;

import lombok.Getter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public enum NametagColor {

    BLACK('0', 0, "black"),
    DARK_BLUE('1', 1, "dark_blue"),
    DARK_GREEN('2', 2, "dark_green"),
    DARK_AQUA('3', 3, "dark_aqua"),
    DARK_RED('4', 4, "dark_red"),
    DARK_PURPLE('5', 5, "dark_purple"),
    GOLD('6', 6, "gold"),
    GRAY('7', 7, "gray"),
    DARK_GRAY('8', 8, "dark_gray"),
    BLUE('9', 9, "blue"),
    GREEN('a', 10, "green"),
    AQUA('b', 11, "aqua"),
    RED('c', 12, "red"),
    LIGHT_PURPLE('d', 13, "light_purple"),
    YELLOW('e', 14, "yellow"),
    WHITE('f', 15, "white"),
    NONE('-', -1, "none");

    private final char colorCode;
    private final int legacyCode;
    private final String name;

    NametagColor(char colorCode, int legacyCode, String name) {
        this.colorCode = colorCode;
        this.legacyCode = legacyCode;
        this.name = name;
    }

    public String toColorString() {
        return "§" + colorCode;
    }
}
