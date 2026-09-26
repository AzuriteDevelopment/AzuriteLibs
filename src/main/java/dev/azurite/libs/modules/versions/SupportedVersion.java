package dev.azurite.libs.modules.versions;

import org.bukkit.Bukkit;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public enum SupportedVersion {

    V1_7_10,
    V1_8_8,
    V26_2;

    public boolean isLegacy() {
        return this == V1_7_10 || this == V1_8_8;
    }

    public boolean isModern() {
        return !isLegacy();
    }

    public static SupportedVersion getSupportedVersion() {
        try {
            String[] split = Bukkit.getVersion().split("-");
            return SupportedVersion.valueOf(split[0].replace(".", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
