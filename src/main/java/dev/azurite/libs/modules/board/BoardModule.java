package dev.azurite.libs.modules.board;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.ticking.TickingModule;
import dev.azurite.libs.modules.board.adapter.BoardAdapter;
import dev.azurite.libs.modules.board.listener.BoardListener;
import dev.azurite.libs.modules.board.runnable.BoardRunnable;
import dev.azurite.libs.modules.board.settings.BoardSettings;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class BoardModule extends TickingModule<AzuriteLibs, Board, BoardSettings, BoardAdapter> {

    public BoardModule(AzuriteLibs loader) {
        super(loader, "azurite_board");
        new BoardListener(this).register();
    }

    @Override
    public void enable(BoardSettings settings) {
        this.settings = settings;
        this.adapter = settings.getAdapter();
        this.enableAndStartTicking(settings.getTickTime(), new BoardRunnable(this));
    }
}
