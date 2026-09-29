package dev.azurite.libs.modules.versions.utils;

import com.viaversion.viaversion.api.Via;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class VersionUtils {

    public static int getProtocolVersion(Player player) {
        if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
            return Via.getAPI().getPlayerVersion(player.getUniqueId());
        }
        return -1;
    }
}
