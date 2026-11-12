package dev.azurite.libs.modules.board.runnable;

import dev.azurite.libs.modules.board.Board;
import dev.azurite.libs.modules.board.BoardModule;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class BoardRunnable implements Runnable {

    private final BoardModule module;

    public BoardRunnable(BoardModule module) {
        this.module = module;
    }

    @Override
    public void run() {
        for (Board board : module.getCachedData().values()) {
            board.update();
        }
    }
}
