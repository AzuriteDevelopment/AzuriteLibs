package dev.azurite.libs.modules.tablist.adapter;

import dev.azurite.libs.modules.tablist.Tablist;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public interface TablistAdapter {

    String[] getHeader(Player player);

    String[] getFooter(Player player);

    void updateEntries(Player player, Tablist tablist);

}
