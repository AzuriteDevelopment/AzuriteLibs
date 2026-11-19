package dev.azurite.libs.libs.nametags.adapter;

import dev.azurite.libs.libs.nametags.update.NametagUpdate;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public interface NametagAdapter {

    NametagUpdate getNametagUpdate(Player viewer, Player target);

    List<String> getLunarNametags(Player viewer, Player target);

}
