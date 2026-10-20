package dev.azurite.libs.modules.nametags.adapter;

import dev.azurite.libs.modules.nametags.Nametag;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public interface NametagAdapter {

    void updateNametag(Nametag nametag, Player viewer, Player target);

}
