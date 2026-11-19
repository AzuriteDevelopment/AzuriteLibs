package dev.azurite.libs.libs.board;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.framework.lib.ticking.TickingLib;
import dev.azurite.libs.libs.board.adapter.BoardAdapter;
import dev.azurite.libs.libs.board.listener.BoardListener;
import dev.azurite.libs.libs.board.runnable.BoardRunnable;
import dev.azurite.libs.libs.board.settings.BoardSettings;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class BoardLib extends TickingLib<AzuriteLibs, Board, BoardSettings, BoardAdapter> {

    public BoardLib(AzuriteLibs loader) {
        super(loader, "azurite_board");
    }

    @Override
    public void enable(BoardSettings settings) {
        this.checkEnabled();
        this.settings = settings;
        this.adapter = settings.getAdapter();
        this.loader.registerListener(new BoardListener(this));
        this.enableAndStartTicking(settings.getTickTime(), new BoardRunnable(this));
    }
}
