package dev.azurite.libs.libs.board.listener;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.framework.lib.sub.SubLib;
import dev.azurite.libs.framework.module.sub.SubModule;
import dev.azurite.libs.libs.board.Board;
import dev.azurite.libs.libs.board.BoardLib;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class BoardListener extends SubLib<AzuriteLibs, BoardLib> {

    public BoardListener(BoardLib module) {
        super(module);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (module.isEnabled()) {
            Board board = new Board(module, player);
            module.getCachedData().put(player.getUniqueId(), board);
        }
    }
}
