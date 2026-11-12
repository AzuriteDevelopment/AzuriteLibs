package dev.azurite.libs.modules.board.listener;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.board.Board;
import dev.azurite.libs.modules.board.BoardModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class BoardListener extends SubModule<AzuriteLibs, BoardModule> {

    public BoardListener(BoardModule module) {
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
