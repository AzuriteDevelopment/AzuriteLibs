package dev.azurite.libs.modules.board.adapter;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public interface BoardAdapter {

    String getTitle(Player player);

    List<String> getLines(Player player);

}
